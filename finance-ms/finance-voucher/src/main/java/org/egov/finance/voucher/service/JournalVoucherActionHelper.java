package org.egov.finance.voucher.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.User;
import org.egov.finance.voucher.exception.ValidationError;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.EmployeeInfo;
import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.VoucherDetails;
import org.egov.finance.voucher.model.VoucherTypeBean;
import org.egov.finance.voucher.model.WorkflowBean;
import org.egov.finance.voucher.repository.EgBillSubTypeRepository;
import org.egov.finance.voucher.repository.EgwStatusRepository;
import org.egov.finance.voucher.repository.FunctionRepository;
import org.egov.finance.voucher.util.FinancialConstants;
import org.egov.finance.voucher.util.MicroserviceUtils;
import org.egov.finance.voucher.util.SecurityUtils;
import org.egov.finance.voucher.util.VoucherConstant;
import org.egov.finance.voucher.workflow.entity.WorkFlowMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class JournalVoucherActionHelper {

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	private SimpleWorkflowService<CVoucherHeader> voucherHeaderWorkflowService;

	@Autowired
	private VoucherService voucherService;

//	@Autowired
//	private CreateVoucher createVoucher;

	@Autowired
	private ChartOfAccountService chartOfAccountsService;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	@Autowired
	private FunctionRepository functionRepository;

	@Autowired
	private EgwStatusRepository egwStatusRepository;

	@Autowired
	private EgBillSubTypeRepository egBillSubTypeRepository;

	public CVoucherHeader createVoucher(List<VoucherDetails> billDetailslist, List<VoucherDetails> subLedgerlist,
			CVoucherHeader voucherHeader, VoucherTypeBean voucherTypeBean, WorkflowBean workflowBean) {
		try {
			voucherHeader.setName(voucherTypeBean.getVoucherName());
			voucherHeader.setType(voucherTypeBean.getVoucherType());
			voucherHeader.setVoucherSubType(voucherTypeBean.getVoucherSubType());

			voucherHeader = createVoucherAndledger(billDetailslist, subLedgerlist, voucherHeader);

			if (!"JVGeneral".equalsIgnoreCase(voucherTypeBean.getVoucherName())) {
				log.debug("Journal Voucher Action | Bill create | voucher name = {}", voucherTypeBean.getVoucherName());

				voucherService.createBillForVoucherSubType(billDetailslist, subLedgerlist, voucherHeader,
						voucherTypeBean, new BigDecimal(voucherTypeBean.getTotalAmount()));
			}

			if (FinancialConstants.CREATEANDAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())
					&& voucherHeader.getState() == null) {
				voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
			} else {
				// voucherHeader = transitionWorkFlow(voucherHeader, workflowBean);
				// voucherService.applyAuditing(voucherHeader.getState());
			}

			voucherService.persist(voucherHeader);
			return voucherHeader;

		} catch (ValidationException e) {
			throw new ValidationException(
					Collections.singletonList(new ValidationError("exp", e.getErrors().get(0).getMessage())));
		}
	}

//	@Transactional
//	public CVoucherHeader transitionWorkFlow(final CVoucherHeader voucherHeader, WorkflowBean workflowBean) {
//		final Date currentDate = new Date();
//		final User user = securityUtils.getCurrentUser();
//		EmployeeInfo info = null;
//		if (user != null && user.getId() != null)
//			info = microserviceUtils.getEmployeeById(user.getId());
//
//		if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
//			final String stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
//			voucherHeader.transition().progressWithStateCopy().withSenderName(user.getName())
//					.withComments(workflowBean.getApproverComments()).withStateValue(stateValue)
//					.withDateInfo(currentDate).withOwner(voucherHeader.getState().getInitiatorPosition())
//					.withNextAction(FinancialConstants.WF_STATE_EOA_Approval_Pending);
//
//		} else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
//			final WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(), null,
//					null, null, voucherHeader.getCurrentState().getValue(), null);
//			voucherHeader.transition().end().withSenderName(user.getName())
//					.withComments(workflowBean.getApproverComments())
//					.withStateValue(wfmatrix.getCurrentDesignation() + " Approved").withDateInfo(currentDate)
//					.withOwner((info != null && info.getAssignments() != null && !info.getAssignments().isEmpty())
//							? info.getAssignments().get(0).getPosition()
//							: null)
//					.withNextAction(wfmatrix.getNextAction());
//
//			voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
//		} else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workflowBean.getWorkFlowAction())) {
//			voucherHeader.setStatus(FinancialConstants.CANCELLEDVOUCHERSTATUS);
//			voucherHeader.transition().end().withStateValue(FinancialConstants.WORKFLOW_STATE_CANCELLED)
//					.withSenderName(user.getName()).withComments(workflowBean.getApproverComments())
//					.withDateInfo(currentDate);
//		} else {
//			if (null == voucherHeader.getState()) {
//				final WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(),
//						null, null, null, workflowBean.getCurrentState(), null);
//				voucherHeader.transition().start().withSenderName(user.getName())
//						.withComments(workflowBean.getApproverComments()).withStateValue(wfmatrix.getNextState())
//						.withDateInfo(currentDate).withOwner(workflowBean.getApproverPositionId())
//						.withNextAction(wfmatrix.getNextAction()).withInitiator(
//								(info != null && info.getAssignments() != null && !info.getAssignments().isEmpty())
//										? info.getAssignments().get(0).getPosition()
//										: null);
//			} else if (voucherHeader.getCurrentState().getNextAction().equalsIgnoreCase("END"))
//				voucherHeader.transition().end().withSenderName(user.getName())
//						.withComments(workflowBean.getApproverComments()).withDateInfo(currentDate);
//			else {
//				if (!voucherHeader.getCurrentState().getValue().equalsIgnoreCase(workflowBean.getCurrentState())) {
//					return voucherHeader;
//				}
//				final WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(),
//						null, null, null, voucherHeader.getCurrentState().getValue(), null);
//				voucherHeader.transition().progressWithStateCopy().withSenderName(user.getName())
//						.withComments(workflowBean.getApproverComments()).withStateValue(wfmatrix.getNextState())
//						.withDateInfo(currentDate).withOwner(workflowBean.getApproverPositionId())
//						.withNextAction(wfmatrix.getNextAction());
//			}
//		}
//		return voucherHeader;
//	}

	@Transactional
	public CVoucherHeader transitionWorkFlow(CVoucherHeader voucherHeader, WorkflowBean workflowBean,
			RequestInfo requestInfo) {
		final Date currentDate = new Date();
		// final User user = securityUtils.getCurrentUser();
		final User user = requestInfo.getUserInfo();
		EmployeeInfo info = (user != null && user.getId() != null) ? microserviceUtils.getEmployeeById(user.getId())
				: null;

		switch (workflowBean.getWorkFlowAction()) {
		case FinancialConstants.BUTTONREJECT:
			voucherHeader.transition().progressWithStateCopy().withSenderName(user.getName())
					.withComments(workflowBean.getApproverComments())
					.withStateValue(FinancialConstants.WORKFLOW_STATE_REJECTED).withDateInfo(currentDate)
					.withOwner(voucherHeader.getState().getInitiatorPosition())
					.withNextAction(FinancialConstants.WF_STATE_EOA_Approval_Pending);
			break;

		case FinancialConstants.BUTTONAPPROVE:
			WorkFlowMatrix approveMatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(), null,
					null, null, voucherHeader.getCurrentState().getValue(), null);
			voucherHeader.transition().end().withSenderName(user.getName())
					.withComments(workflowBean.getApproverComments())
					.withStateValue(approveMatrix.getCurrentDesignation() + " Approved").withDateInfo(currentDate)
					.withOwner(info != null && !info.getAssignments().isEmpty()
							? info.getAssignments().get(0).getPosition()
							: null)
					.withNextAction(approveMatrix.getNextAction());
			voucherHeader.setStatus(FinancialConstants.CREATEDVOUCHERSTATUS);
			break;

		case FinancialConstants.BUTTONCANCEL:
			voucherHeader.setStatus(FinancialConstants.CANCELLEDVOUCHERSTATUS);
			voucherHeader.transition().end().withSenderName(user.getName())
					.withComments(workflowBean.getApproverComments())
					.withStateValue(FinancialConstants.WORKFLOW_STATE_CANCELLED).withDateInfo(currentDate);
			break;

		default:
			// Forward or initiate
			if (voucherHeader.getState() == null) {
				WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(), null,
						null, null, workflowBean.getCurrentState(), null);
				voucherHeader.transition().start().withSenderName(user.getName())
						.withComments(workflowBean.getApproverComments()).withStateValue(wfmatrix.getNextState())
						.withDateInfo(currentDate).withOwner(workflowBean.getApproverPositionId())
						.withNextAction(wfmatrix.getNextAction())
						.withInitiator(info != null && !info.getAssignments().isEmpty()
								? info.getAssignments().get(0).getPosition()
								: null);
			} else if ("END".equalsIgnoreCase(voucherHeader.getCurrentState().getNextAction())) {
				voucherHeader.transition().end().withSenderName(user.getName())
						.withComments(workflowBean.getApproverComments()).withDateInfo(currentDate);
			} else {
				if (!voucherHeader.getCurrentState().getValue().equalsIgnoreCase(workflowBean.getCurrentState())) {
					return voucherHeader;
				}
				WorkFlowMatrix wfmatrix = voucherHeaderWorkflowService.getWfMatrix(voucherHeader.getStateType(), null,
						null, null, voucherHeader.getCurrentState().getValue(), null);
				voucherHeader.transition().progressWithStateCopy().withSenderName(user.getName())
						.withComments(workflowBean.getApproverComments()).withStateValue(wfmatrix.getNextState())
						.withDateInfo(currentDate).withOwner(workflowBean.getApproverPositionId())
						.withNextAction(wfmatrix.getNextAction());
			}
			break;
		}

		return voucherHeader;
	}

	public CVoucherHeader createVoucherAndledger(List<VoucherDetails> billDetailslist,
			List<VoucherDetails> subLedgerlist, CVoucherHeader voucherHeader) {

		try {
			Map<String, Object> headerDetails = createHeaderAndMisDetails(voucherHeader);
			List<Map<String, Object>> accountdetails = new ArrayList<>();
			List<Map<String, Object>> subledgerDetails = new ArrayList<>();
			Map<String, String> glcodeMap = new HashMap<>();

			for (VoucherDetails detail : billDetailslist) {
				Map<String, Object> detailMap = new HashMap<>();
				if (voucherHeader.getIsRestrictedtoOneFunctionCenter()) {
					detailMap.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
				} else if (detail.getFunctionIdDetail() != null) {
					functionRepository.findById(detail.getFunctionIdDetail())
							.ifPresent(func -> detailMap.put(VoucherConstant.FUNCTIONCODE, func.getCode()));
				}

				detailMap.put(VoucherConstant.GLCODE, detail.getGlcodeDetail());

				if (detail.getCreditAmountDetail().compareTo(BigDecimal.ZERO) == 0) {
					detailMap.put(VoucherConstant.DEBITAMOUNT, detail.getDebitAmountDetail().toPlainString());
					detailMap.put(VoucherConstant.CREDITAMOUNT, "0");
					glcodeMap.put(detail.getGlcodeDetail(), VoucherConstant.DEBIT);
				} else {
					detailMap.put(VoucherConstant.CREDITAMOUNT, detail.getCreditAmountDetail().toPlainString());
					detailMap.put(VoucherConstant.DEBITAMOUNT, "0");
					glcodeMap.put(detail.getGlcodeDetail(), VoucherConstant.CREDIT);
				}

				accountdetails.add(detailMap);
			}

			for (VoucherDetails subDetail : subLedgerlist) {
				Map<String, Object> subledgerMap = new HashMap<>();

				String amountType = glcodeMap.get(subDetail.getSubledgerCode());

				if (subDetail.getFunctionDetail() != null && !subDetail.getFunctionDetail().trim().isEmpty()) {
					Long functionId = Long.parseLong(subDetail.getFunctionDetail());
					functionRepository.findById(functionId)
							.ifPresent(func -> subledgerMap.put(VoucherConstant.FUNCTIONCODE, func.getCode()));
				}

				if (VoucherConstant.DEBIT.equalsIgnoreCase(amountType)) {
					subledgerMap.put(VoucherConstant.DEBITAMOUNT, subDetail.getAmount());
				} else {
					subledgerMap.put(VoucherConstant.CREDITAMOUNT, subDetail.getAmount());
				}

				subledgerMap.put(VoucherConstant.DETAILTYPEID, subDetail.getDetailType().getId());
				subledgerMap.put(VoucherConstant.DETAILKEYID, subDetail.getDetailKeyId());
				subledgerMap.put(VoucherConstant.GLCODE, subDetail.getSubledgerCode());

				subledgerDetails.add(subledgerMap);
			}

			return null;// createVoucher.createPreApprovedVoucher(headerDetails, accountdetails,
						// subledgerDetails);

		} catch (Exception e) {
			log.error("Error in createVoucherAndledger", e);
			throw new ValidationException(
					Collections.singletonList(new ValidationError("Voucher Creation Failed", e.getMessage())));
		}
	}

	private Map<String, Object> createHeaderAndMisDetails(CVoucherHeader voucherHeader) {
		final HashMap<String, Object> headerdetails = new HashMap<String, Object>();
		headerdetails.put(VoucherConstant.VOUCHERNAME, voucherHeader.getName());
		headerdetails.put(VoucherConstant.VOUCHERTYPE, voucherHeader.getType());
		headerdetails.put((String) VoucherConstant.VOUCHERSUBTYPE, voucherHeader.getVoucherSubType());
		headerdetails.put(VoucherConstant.VOUCHERNUMBER, voucherHeader.getVoucherNumber());
		headerdetails.put(VoucherConstant.VOUCHERDATE, voucherHeader.getVoucherDate());
		headerdetails.put(VoucherConstant.DESCRIPTION, voucherHeader.getDescription());
		if (voucherHeader.getVouchermis().getDepartmentcode() != null)
			headerdetails.put(VoucherConstant.DEPARTMENTCODE, voucherHeader.getVouchermis().getDepartmentcode());
		if (voucherHeader.getFundId() != null)
			headerdetails.put(VoucherConstant.FUNDCODE, voucherHeader.getFundId().getCode());
		if (voucherHeader.getVouchermis().getSchemeid() != null)
			headerdetails.put(VoucherConstant.SCHEMECODE, voucherHeader.getVouchermis().getSchemeid().getCode());
		if (voucherHeader.getVouchermis().getSubschemeid() != null)
			headerdetails.put(VoucherConstant.SUBSCHEMECODE, voucherHeader.getVouchermis().getSubschemeid().getCode());
		if (voucherHeader.getVouchermis().getFundsource() != null)
			headerdetails.put(VoucherConstant.FUNDSOURCECODE, voucherHeader.getVouchermis().getFundsource().getCode());
		if (voucherHeader.getVouchermis().getDivisionid() != null)
			headerdetails.put(VoucherConstant.DIVISIONID, voucherHeader.getVouchermis().getDivisionid().getId());
		if (voucherHeader.getVouchermis().getFunctionary() != null)
			headerdetails.put(VoucherConstant.FUNCTIONARYCODE,
					voucherHeader.getVouchermis().getFunctionary().getCode());
		if (voucherHeader.getVouchermis().getFunction() != null)
			headerdetails.put(VoucherConstant.FUNCTIONCODE, voucherHeader.getVouchermis().getFunction().getCode());
		return headerdetails;
	}

}
