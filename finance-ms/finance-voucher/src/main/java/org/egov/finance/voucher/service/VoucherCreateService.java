package org.egov.finance.voucher.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.finance.voucher.daoimpl.VouchermisHibernateDAO;
import org.egov.finance.voucher.entity.AccountDetailType;
import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.Fund;
import org.egov.finance.voucher.entity.Voucher;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.exception.TaskFailedException;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.AccountDetailModel;
import org.egov.finance.voucher.model.EgModules;
import org.egov.finance.voucher.model.SubledgerDetailModel;
import org.egov.finance.voucher.model.VoucherDetails;
import org.egov.finance.voucher.model.VoucherTypeBean;
import org.egov.finance.voucher.model.WorkflowBean;
import org.egov.finance.voucher.model.request.VoucherRequest;
import org.egov.finance.voucher.model.response.VoucherResponse;
import org.egov.finance.voucher.util.CommonUtils;
import org.egov.finance.voucher.util.FinancialConstants;
import org.egov.finance.voucher.util.MicroserviceUtils;
import org.egov.finance.voucher.util.VoucherConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherCreateService {

	@Autowired
	private CreateVoucher createVoucher;

	@Autowired
	private ChartOfAccountDetailService chartOfAccountDetailService;
	@Autowired
	private AppConfigValueService appConfigValuesService;

	@Autowired
	private EgModulesService egModuleService;

	@Autowired
	private CommonUtils commonUtils;

	@Autowired
	MicroserviceUtils microserviceUtils;

	@Autowired
	private VouchermisHibernateDAO vmisHibernateDao;
	@Autowired
	private JournalVoucherActionHelper journalVoucherActionHelper;

	public transient CVoucherHeader voucherHeader = new CVoucherHeader();

	public VoucherResponse processVoucherCreate(VoucherRequest voucherRequest) {
		VoucherResponse response = VoucherResponse.builder().build();

		// Step 1: Populate workflowBean from request if not already
		WorkflowBean workflowBean = voucherRequest.getWorkflowBean();
		populateWorkflowBeanFromRequest(workflowBean, voucherRequest);

		for (Voucher voucher : voucherRequest.getVouchers()) {
			try {
				Map<String, Object> headerDetails = buildHeaderDetails(voucher);
				List<Map<String, Object>> accountdetails = new ArrayList<>();
				List<Map<String, Object>> subledgerDetails = new ArrayList<>();

				for (AccountDetailModel ac : voucher.getLedgers()) {
					Map<String, Object> detailMap = new HashMap<>();
					detailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
					// Safe conversion
					detailMap.put(VoucherConstant.DEBITAMOUNT,
							ac.getDebitAmount() != null ? BigDecimal.valueOf(ac.getDebitAmount()) : BigDecimal.ZERO);
					detailMap.put(VoucherConstant.CREDITAMOUNT,
							ac.getCreditAmount() != null ? BigDecimal.valueOf(ac.getCreditAmount()) : BigDecimal.ZERO);

					if (ac.getFunctionId() != null)
						detailMap.put(VoucherConstant.FUNCTIONCODE, ac.getFunctionId());

					accountdetails.add(detailMap);

					for (SubledgerDetailModel sl : ac.getSubledgerDetails()) {
						if (chartOfAccountDetailService.getByGlcodeAndDetailTypeId(ac.getGlcode(),
								sl.getAccountDetailTypeId()) != null) {

							Map<String, Object> subledgertDetailMap = new HashMap<>();
							subledgertDetailMap.put(VoucherConstant.GLCODE, ac.getGlcode());
							subledgertDetailMap.put(VoucherConstant.DETAILAMOUNT, sl.getAmount());
							subledgertDetailMap.put(VoucherConstant.DETAIL_TYPE_ID, sl.getAccountDetailTypeId());
							subledgertDetailMap.put(VoucherConstant.DETAIL_KEY_ID, sl.getDetailKeyId());
							subledgerDetails.add(subledgertDetailMap);
						}
					}
				}

//				// Workflow logic (FORWARD or CREATEANDAPPROVE)
//				if (workflowBean != null && (FinancialConstants.BUTTONFORWARD
//						.equalsIgnoreCase(workflowBean.getWorkFlowAction())
//						|| FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction()))) {
//
//					// Validate approver
//					// CVoucherHeader tempHeader = convertToVoucherHeader(headerDetails);
//					if (!commonUtils.isValidApprover(voucherHeader, workflowBean.getApproverPositionId())) {
//						throw new ValidationException("Invalid Approver", "Selected approver is not valid");
//					}
//
//					// Create and transition state
//					voucherHeader = createVoucher.createVoucher(headerDetails, accountdetails, subledgerDetails);
//					voucherHeader = journalVoucherActionHelper.transitionWorkFlow(voucherHeader, workflowBean);
//
//				} else {
//					// Regular creation without workflow
//					voucherHeader = createVoucher.createVoucher(headerDetails, accountdetails, subledgerDetails);
//				}

				// Delegate complete creation + workflow inside CreateVoucher
				voucherHeader = createVoucher.createVoucher(headerDetails, accountdetails, subledgerDetails,
						voucherRequest.getRequestInfo(), workflowBean);

				// Step 3: Add message based on cutoff date and workflow action
				String message = buildSuccessMessage(voucherHeader, workflowBean, voucherRequest.getCutOffDate());
				response.setMessage(message);

				voucher.setId(voucherHeader.getId());
				voucher.setVoucherNumber(voucherHeader.getVoucherNumber());
				response.getVouchers().add(voucher);

			} catch (ValidationException | ApplicationRuntimeException e) {
				throw e;
			} catch (ParseException e) {
				throw new ApplicationRuntimeException(e.getMessage());
			} catch (TaskFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return response;
	}

	private String buildSuccessMessage(CVoucherHeader voucherHeader, WorkflowBean workflowBean, String cutOffDateStr) {
		String voucherDate = new SimpleDateFormat("dd-MM-yyyy").format(voucherHeader.getVoucherDate());
		String formattedCutoffDate = null;

		try {
			if (cutOffDateStr != null && !cutOffDateStr.isEmpty()) {
				Date cutoff = new SimpleDateFormat("dd-MM-yyyy").parse(cutOffDateStr);
				formattedCutoffDate = new SimpleDateFormat("dd-MM-yyyy").format(cutoff);
			}
		} catch (ParseException e) {
			// Ignore cutoff parsing error; just skip cutoff logic
		}

		boolean withinCutoff = formattedCutoffDate != null && voucherDate.compareTo(formattedCutoffDate) <= 0;
		boolean isCreateAndApprove = workflowBean != null
				&& FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction());

		StringBuilder msg = new StringBuilder("Voucher " + voucherHeader.getVoucherNumber() + " Created Successfully");

		if (withinCutoff && isCreateAndApprove) {
			if (voucherHeader.getVouchermis() != null
					&& voucherHeader.getVouchermis().getBudgetaryAppnumber() != null) {
				msg.append("\nAnd budget check successful: ")
						.append(voucherHeader.getVouchermis().getBudgetaryAppnumber());
			}
		} else {
			if (voucherHeader.getVouchermis() != null
					&& voucherHeader.getVouchermis().getBudgetaryAppnumber() != null) {
				msg.append("\nAnd budget check successful: ")
						.append(voucherHeader.getVouchermis().getBudgetaryAppnumber());
			}

			if (voucherHeader.getState() != null && voucherHeader.getState().getCreatedBy() != null) {
				msg.append("\nApproved by: ").append(getEmployeeName(voucherHeader.getState().getCreatedBy()));
			}
		}

		return msg.toString();
	}

	public String getEmployeeName(Long empId) {

		return microserviceUtils.getEmployee(empId, null, null, null).get(0).getUser().getName();
	}

	private void populateWorkflowBeanFromRequest(WorkflowBean workflowBean, VoucherRequest request) {
		if (workflowBean == null)
			return;

		workflowBean.setApproverPositionId(request.getWorkflowBean().getApproverPositionId());
		workflowBean.setApproverComments(request.getWorkflowBean().getApproverComments());
		workflowBean.setWorkFlowAction(request.getWorkflowBean().getWorkFlowAction());
		workflowBean.setCurrentState(request.getWorkflowBean().getCurrentState());
	}

	private Map<String, Object> buildHeaderDetails(Voucher voucher) throws ParseException {
		Map<String, Object> headerDetails = new HashMap<>();
		SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
		Date vDate = fm.parse(voucher.getVoucherDate());

		headerDetails.put(VoucherConstant.DEPARTMENTCODE, voucher.getDepartment());
		headerDetails.put(VoucherConstant.VOUCHERNAME, voucher.getName());
		headerDetails.put(VoucherConstant.VOUCHERTYPE, voucher.getType());
		headerDetails.put(VoucherConstant.VOUCHERNUMBER, voucher.getVoucherNumber());
		headerDetails.put(VoucherConstant.VOUCHERDATE, vDate);
		headerDetails.put(VoucherConstant.DESCRIPTION, voucher.getDescription());
		headerDetails.put(VoucherConstant.MODULEID, voucher.getModuleId());

		if (voucher.getSource() != null)
			headerDetails.put(VoucherConstant.SOURCEPATH, voucher.getSource());

		if (voucher.getReferenceDocument() != null && !voucher.getReferenceDocument().isEmpty())
			headerDetails.put(VoucherConstant.REFERENCEDOC, voucher.getReferenceDocument());

		if (voucher.getServiceName() != null && !voucher.getServiceName().isEmpty())
			headerDetails.put(VoucherConstant.SERVICE_NAME, voucher.getServiceName());

		if (voucher.getFund() != null)
			headerDetails.put(VoucherConstant.FUNDCODE, voucher.getFund().getCode());

		if (voucher.getFunction() != null)
			headerDetails.put(VoucherConstant.FUNCTIONCODE, voucher.getFunction().getCode());

		if (voucher.getFunctionary() != null)
			headerDetails.put(VoucherConstant.FUNCTIONARYCODE, voucher.getFunctionary().getCode());

		if (voucher.getScheme() != null)
			headerDetails.put(VoucherConstant.SCHEMECODE, voucher.getScheme().getCode());

		if (voucher.getSubScheme() != null)
			headerDetails.put(VoucherConstant.SUBSCHEMECODE, voucher.getSubScheme().getCode());

		return headerDetails;
	}

	public List<CVoucherHeader> getVoucherByServiceNameAndReferenceDocument(String serviceName,
			String referenceDocument) {
		return vmisHibernateDao.getRecentVoucherByServiceNameAndReferenceDoc1(serviceName, referenceDocument);
	}

	public EgModules getModulesIdByName(String name) {
		List<EgModules> egModuleServiceByName = egModuleService.getEgModuleServiceByName(name);
		return !egModuleServiceByName.isEmpty() ? egModuleServiceByName.get(0) : null;
	}

	public AppConfigValues isManualReceiptDateEnabledForVoucher() {
		List<AppConfigValues> appConfigValue = appConfigValuesService.getConfigValuesByModuleAndKey(
				FinancialConstants.MODULE_NAME_APPCONFIG, "IsManualReceiptDateConsideredForVoucher");
		return appConfigValue.isEmpty() ? null : appConfigValue.get(0);
	}

}
