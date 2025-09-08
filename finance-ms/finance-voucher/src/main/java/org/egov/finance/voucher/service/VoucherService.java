package org.egov.finance.voucher.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.EgBillPayeedetails;
import org.egov.finance.voucher.entity.EgBillSubType;
import org.egov.finance.voucher.entity.EgBilldetails;
import org.egov.finance.voucher.entity.EgBillregister;
import org.egov.finance.voucher.entity.EgBillregistermis;
import org.egov.finance.voucher.entity.EgwStatus;
import org.egov.finance.voucher.enumeration.FinanceEventType;
import org.egov.finance.voucher.exception.ValidationError;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.VoucherDetails;
import org.egov.finance.voucher.model.VoucherTypeBean;
import org.egov.finance.voucher.repository.EgBillSubTypeRepository;
import org.egov.finance.voucher.repository.EgBillregisterRepository;
import org.egov.finance.voucher.repository.EgwStatusRepository;
import org.egov.finance.voucher.repository.VoucherRepository;
import org.egov.finance.voucher.workflow.entity.State;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherService {

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private EgwStatusRepository egwStatusRepository;

	@Autowired
	private EgBillSubTypeRepository egBillSubTypeRepository;

	@Autowired
	private EgBillregisterRepository egBillregisterRepository;

	@Autowired
	private AutonumberServiceBeanResolver beanResolver;

	@Autowired
	private FinanceDashboardService finDashboardService;

	@PersistenceContext
	protected EntityManager entityManager;

	// No need to manually apply auditing
	@Transactional
	public void persist(CVoucherHeader voucher) {
		voucherRepository.save(voucher);
	}

	@Transactional
	public void update(CVoucherHeader voucher) {
		voucherRepository.save(voucher);
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

//	public void createBillForVoucherSubType(List<VoucherDetails> billDetailslist, List<VoucherDetails> subLedgerlist,
//			CVoucherHeader voucherHeader, VoucherTypeBean voucherTypeBean, BigDecimal bigDecimal) {
//		// TODO Auto-generated method stub
//		
//	}

	@Transactional
	public EgBillregister createBillForVoucherSubType(final List<VoucherDetails> billDetailslist,
			final List<VoucherDetails> subLedgerlist, final CVoucherHeader voucherHeader,
			final VoucherTypeBean voucherTypeBean, final BigDecimal totalBillAmount) {

		log.debug("VoucherService | createBillForVoucherSubType | Start");

		EgBillregister egBillregister = new EgBillregister();
		egBillregister.setBillstatus("APPROVED");

		String voucherName = voucherTypeBean.getVoucherName();
		String moduletype = null;
		String expendituretype = null;

		if ("Contractor Journal".equalsIgnoreCase(voucherName)) {
			EgwStatus egwstatus = egwStatusRepository
					.findByModuleTypeIgnoreCaseAndDescriptionIgnoreCase("CONTRACTORBILL", "APPROVED")
					.orElseThrow(() -> new ValidationException(
							List.of(new ValidationError("status", "No status found for moduletype: CONTRACTORBILL"))));
			egBillregister.setStatus(egwstatus);
			egBillregister.setExpendituretype("Works");
		} else if ("Supplier Journal".equalsIgnoreCase(voucherName)) {
			EgwStatus egwstatus = egwStatusRepository
					.findByModuleTypeIgnoreCaseAndDescriptionIgnoreCase("SBILL", "APPROVED")
					.orElseThrow(() -> new ValidationException(
							List.of(new ValidationError("status", "No status found for moduletype: SBILL"))));
			egBillregister.setStatus(egwstatus);
			egBillregister.setExpendituretype("Purchase");
		} else if ("Salary Journal".equalsIgnoreCase(voucherName)) {
			EgwStatus egwstatus = egwStatusRepository
					.findByModuleTypeIgnoreCaseAndDescriptionIgnoreCase("SALBILL", "APPROVED")
					.orElseThrow(() -> new ValidationException(
							List.of(new ValidationError("status", "No status found for moduletype: SALBILL"))));
			egBillregister.setStatus(egwstatus);
			egBillregister.setExpendituretype("Salary");
		} else if ("Expense Journal".equalsIgnoreCase(voucherName)) {
			EgwStatus egwstatus = egwStatusRepository
					.findByModuleTypeIgnoreCaseAndDescriptionIgnoreCase("EXPENSEBILL", "APPROVED")
					.orElseThrow(() -> new ValidationException(
							List.of(new ValidationError("status", "No status found for moduletype: EXPENSEBILL"))));
			egBillregister.setStatus(egwstatus);
			egBillregister.setExpendituretype("Expense");
		}

		else if ("Pension Journal".equalsIgnoreCase(voucherName)) {
			EgwStatus egwstatus = egwStatusRepository
					.findByModuleTypeIgnoreCaseAndDescriptionIgnoreCase("PENSIONBILL", "APPROVED")
					.orElseThrow(() -> new ValidationException(
							List.of(new ValidationError("status", "No status found for moduletype: PENSIONBILL"))));
			egBillregister.setStatus(egwstatus);
			egBillregister.setExpendituretype("Pension");
		}

		egBillregister.setBilldate(
				voucherTypeBean.getBillDate() != null ? voucherTypeBean.getBillDate() : voucherHeader.getVoucherDate());

		if (voucherHeader.getVouchermis().getDivisionid() != null) {
			egBillregister.setFieldid(BigDecimal.valueOf(voucherHeader.getVouchermis().getDivisionid().getId()));
		}

		egBillregister.setNarration(voucherHeader.getDescription());
		egBillregister.setIsactive(true);
		egBillregister.setBilltype("Final Bill");
		egBillregister.setPassedamount(totalBillAmount);
		egBillregister.setBillamount(totalBillAmount);

		EgBillregistermis egBillregistermis = new EgBillregistermis();
		egBillregistermis.setFund(voucherHeader.getFundId());
		egBillregistermis.setDepartmentcode(voucherHeader.getVouchermis().getDepartmentcode());
		egBillregistermis.setFunctionaryid(voucherHeader.getVouchermis().getFunctionary());
		egBillregistermis.setFunction(voucherHeader.getVouchermis().getFunction());
		egBillregistermis.setFundsource(voucherHeader.getVouchermis().getFundsource());
		egBillregistermis.setScheme(voucherHeader.getVouchermis().getSchemeid());
		egBillregistermis.setSubScheme(voucherHeader.getVouchermis().getSubschemeid());
		egBillregistermis.setNarration(voucherHeader.getDescription());
		egBillregistermis.setPartyBillDate(voucherTypeBean.getPartyBillDate());
		egBillregistermis.setPayto(voucherTypeBean.getPartyName());
		egBillregistermis.setPartyBillNumber(voucherTypeBean.getPartyBillNum());
		egBillregistermis.setFieldid(voucherHeader.getVouchermis().getDivisionid());
		egBillregistermis.setVoucherHeader(voucherHeader);
		egBillregistermis.setLastupdatedtime(new Date());

		// Fixed Asset bill subtype mapping
		if ("fixedassetjv".equalsIgnoreCase(voucherTypeBean.getVoucherNumType())) {
			EgBillSubType billSubType = egBillSubTypeRepository.findByExpenditureTypeAndName("Fixed Asset", "Purchase")
					.orElse(null);
			egBillregistermis.setEgBillSubType(billSubType);
		}

		// Set generated bill number if not provided
		if (StringUtils.isNotBlank(voucherTypeBean.getBillNum())) {
			egBillregister.setBillnumber(voucherTypeBean.getBillNum());
		} else {
			JVBillNumberGenerator billNumberGenerator = beanResolver
					.getAutoNumberServiceFor(JVBillNumberGenerator.class);
			String billNumber = billNumberGenerator.getNextNumber(egBillregister);
			egBillregister.setBillnumber(billNumber);
			log.debug("Generated Bill Number: {}", billNumber);
		}

		// Validate unique bill number
		if (!isBillNumUnique(egBillregister.getBillnumber())) {
			throw new ValidationException(List.of(
					new ValidationError("bill number", "Duplicate Bill Number : " + egBillregister.getBillnumber())));
		}

		egBillregister.setEgBillregistermis(egBillregistermis);
		egBillregistermis.setEgBillregister(egBillregister);

		// Prepare bill details and set
		Set<EgBilldetails> egBilldetailsSet = prepareBillDetails(egBillregister, billDetailslist, subLedgerlist,
				voucherHeader, new HashSet<>());
		egBillregister.setEgBilldetailes(egBilldetailsSet);

		// egBillregisterRepository.applyAuditing(egBillregister);
		egBillregisterRepository.save(egBillregister); // Replaces persist + flush

		voucherHeader.getVouchermis()
				.setSourcePath("/services/EGF/voucher/journalVoucherModify-beforeModify.action?voucherHeader.id="
						+ voucherHeader.getId());

		update(voucherHeader);

		// Publish event for indexing
		finDashboardService.publishEvent(FinanceEventType.billCreateOrUpdate, egBillregister);

		log.debug("VoucherService | createBillForVoucherSubType | End | Bill Number: {}",
				egBillregister.getBillnumber());

		return egBillregister;
	}

	private boolean isBillNumUnique(final String billNumber) {
		return !egBillregisterRepository.existsByBillnumberIgnoreCase(billNumber);
	}

	private Set<EgBilldetails> prepareBillDetails(final EgBillregister egBillregister,
			final List<VoucherDetails> billDetailslist, final List<VoucherDetails> subLedgerlist,
			final CVoucherHeader voucherHeader, final Set<EgBilldetails> egBilldetailes) {

		if (log.isDebugEnabled())
			log.debug("Voucher Service | prepareBillDetails | Start");
		for (final VoucherDetails accountDetail : billDetailslist) {

			final EgBilldetails egBilldetail = new EgBilldetails();
			egBilldetail.setEgBillregister(egBillregister);
			egBilldetail.setGlcodeid(new BigDecimal(accountDetail.getGlcodeIdDetail().toString()));
			egBilldetail.setDebitamount(accountDetail.getDebitAmountDetail());
			egBilldetail.setCreditamount(accountDetail.getCreditAmountDetail());
			if (null != accountDetail.getFunctionIdDetail())
				egBilldetail.setFunctionid(new BigDecimal(accountDetail.getFunctionIdDetail()));
			if (voucherHeader.getVouchermis().getFunction() != null
					&& !voucherHeader.getVouchermis().getFunction().equals("0"))
				egBilldetail.setFunctionid(new BigDecimal(voucherHeader.getVouchermis().getFunction().getId()));
			egBilldetail.setNarration(voucherHeader.getDescription());
			Set<EgBillPayeedetails> egBillPaydetailes = null;

			for (final VoucherDetails subledgerDetail : subLedgerlist)
				if (accountDetail.getGlcodeIdDetail().equals(subledgerDetail.getGlcode().getId())) {
					if (null == egBillPaydetailes)
						egBillPaydetailes = new HashSet<>();
					final EgBillPayeedetails egBillPaydetail = new EgBillPayeedetails();
					egBillPaydetail.setEgBilldetailsId(egBilldetail);
					egBillPaydetail.setAccountDetailTypeId(subledgerDetail.getDetailType().getId().intValue());
					egBillPaydetail.setAccountDetailKeyId(subledgerDetail.getDetailKeyId());
					if (egBilldetail.getDebitamount().compareTo(BigDecimal.ZERO) > 0)
						egBillPaydetail.setDebitAmount(subledgerDetail.getAmount());
					else
						egBillPaydetail.setCreditAmount(subledgerDetail.getAmount());
					egBillPaydetail.setNarration(voucherHeader.getDescription());
					egBillPaydetail.setLastUpdatedTime(new Date());
					egBillPaydetailes.add(egBillPaydetail);
				}
			egBilldetail.setEgBillPaydetailes(egBillPaydetailes);
			egBilldetail.setLastupdatedtime(new Date());
			egBilldetailes.add(egBilldetail);
		}
		if (log.isDebugEnabled())
			log.debug("Voucher Service | prepareBillDetails | End");
		return egBilldetailes;
	}

	public void applyAuditing(State state) {
		// TODO Auto-generated method stub

	}

}
