package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Bankaccount;
import org.egov.finance.master.entity.Bankbranch;
import org.egov.finance.master.entity.CChartOfAccounts;
import org.egov.finance.master.entity.ChequeFormat;
import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.BankaccountModel;
import org.egov.finance.master.repository.BankaccountRepository;
import org.egov.finance.master.repository.BankbranchRepository;
import org.egov.finance.master.repository.ChartOfAccountsRepository;
import org.egov.finance.master.repository.ChequeFormatRepository;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BankAccountValidation {

	@Autowired
	private BankaccountRepository bankaccountRepository;

	@Autowired
	private BankbranchRepository bankbranchRepository;

	@Autowired
	private FundRepository fundRepository;

	@Autowired
	private ChartOfAccountsRepository chartOfAccountsRepository;

	@Autowired
	private ChequeFormatRepository chequeFormatRepository;

	public Bankaccount modelToEntity(BankaccountModel model) {
		Bankaccount entity = new Bankaccount();

		entity.setId(model.getId());
		entity.setAccountnumber(model.getAccountnumber());
		entity.setAccounttype(model.getAccounttype());
		entity.setNarration(model.getNarration());
		entity.setIsactive(model.getIsactive());
		entity.setPayTo(model.getPayTo());
		entity.setType(model.getType());
		entity.setCreatedBy(model.getCreatedBy());
		entity.setCreatedDate(model.getCreatedDate());
		entity.setLastModifiedBy(model.getLastModifiedBy());
		entity.setLastModifiedDate(model.getLastModifiedDate());

		if (model.getBankbranchId() != null) {
			Bankbranch branch = bankbranchRepository.findById(model.getBankbranchId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_BANKBRANCHID, MasterConstants.INVALID_BANKBRANCHID_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			});

			entity.setBankbranch(branch);
		}

		if (model.getGlcodeId() != null) {
			CChartOfAccounts coa = chartOfAccountsRepository.findById(model.getGlcodeId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_GLCODEID, MasterConstants.INVALID_GLCODEID_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			});

			entity.setChartofaccounts(coa);
		}

		if (model.getFundId() != null) {
			Fund fund = fundRepository.findById(model.getFundId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_FUND, MasterConstants.INVALID_FUND_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			});
			entity.setFund(fund);
		}

		if (model.getChequeformatId() != null) {
			ChequeFormat format = chequeFormatRepository.findById(model.getChequeformatId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_CHEQUEFORMATID,
						MasterConstants.INVALID_CHEQUEFORMATID_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			});

			entity.setChequeformat(format);
		}

		return entity;
	}

	public BankaccountModel entityToModel(Bankaccount entity) {
		BankaccountModel model = new BankaccountModel();

		model.setId(entity.getId());
		model.setAccountnumber(entity.getAccountnumber());
		model.setAccounttype(entity.getAccounttype());
		model.setNarration(entity.getNarration());
		model.setIsactive(entity.getIsactive());
		model.setPayTo(entity.getPayTo());
		model.setType(entity.getType());
		model.setCreatedBy(entity.getCreatedBy());
		model.setCreatedDate(entity.getCreatedDate());
		model.setLastModifiedBy(entity.getLastModifiedBy());
		model.setLastModifiedDate(entity.getLastModifiedDate());

		if (entity.getBankbranch() != null)
			model.setBankbranchId(entity.getBankbranch().getId());
		if (entity.getChartofaccounts() != null)
			model.setGlcodeId(entity.getChartofaccounts().getId());
		if (entity.getFund() != null)
			model.setFundId(entity.getFund().getId());
		if (entity.getChequeformat() != null)
			model.setChequeformatId(entity.getChequeformat().getId());

		return model;
	}

//	public void bankAccountCreateValidation(BankaccountModel model) {
//		Map<String, String> errors = new HashMap<>();
//
//		if (bankaccountRepository.existsByAccountnumberAndBankbranch_Id(model.getAccountnumber(),
//				model.getBankbranchId())) {
//			// errors.put("accountnumber", "Account number already exists for the given
//			// branch.");
//			errors.put(MasterConstants.ACCOUNTNUMBER_NOT_UNIQUE, MasterConstants.ACCOUNT_IS_ALREADY_EXISTS_MSG);
//		}
//
//		if (!errors.isEmpty()) {
//			throw new MasterServiceException(errors);
//		}
//	}

	public void bankAccountCreateValidation(BankaccountModel model) {
		Map<String, String> errors = new HashMap<>();

		// Uniqueness check: account number + branch
		Specification<Bankaccount> spec = Specification
				.where(SpecificationHelper.<Bankaccount>equalIgnoreCase("accountnumber", model.getAccountnumber()))
				.and(SpecificationHelper.equal("bankbranch.id", model.getBankbranchId()));

		if (bankaccountRepository.count(spec) > 0) {
			errors.put(MasterConstants.ACCOUNTNUMBER_NOT_UNIQUE, MasterConstants.ACCOUNT_IS_ALREADY_EXISTS_MSG);
		}

		if (!errors.isEmpty()) {
			throw new MasterServiceException(errors);
		}
	}

	public void bankAccountUpdateValidation(BankaccountModel model, Set<String> updatedFields) {
		Map<String, String> errors = new HashMap<>();

		if (updatedFields.contains("accountnumber")) {
			// Uniqueness check: account number + branch (excluding current ID)
			Specification<Bankaccount> spec = Specification
					.where(SpecificationHelper.<Bankaccount>equalIgnoreCase("accountnumber", model.getAccountnumber()))
					.and(SpecificationHelper.equal("bankbranch.id", model.getBankbranchId()))
					.and(SpecificationHelper.notEqual("id", model.getId()));

			if (bankaccountRepository.count(spec) > 0) {
				errors.put(MasterConstants.ACCOUNTNUMBER_NOT_UNIQUE, MasterConstants.ACCOUNT_IS_ALREADY_EXISTS_MSG);
			}
		}

		if (!errors.isEmpty()) {
			throw new MasterServiceException(errors);
		}
	}

//	public void bankAccountUpdateValidation(BankaccountModel model, Set<String> updatedFields) {
//		Map<String, String> errors = new HashMap<>();
//
//		if (updatedFields.contains("accountnumber")) {
//			if (bankaccountRepository.existsByAccountnumberAndBankbranch_IdAndIdNot(model.getAccountnumber(),
//					model.getBankbranchId(), model.getId())) {
//				// errors.put("accountnumber", "Account number already exists for the given
//				// branch.");
//				errors.put(MasterConstants.ACCOUNTNUMBER_NOT_UNIQUE, MasterConstants.ACCOUNT_IS_ALREADY_EXISTS_MSG);
//			}
//		}
//
//		if (!errors.isEmpty()) {
//			throw new MasterServiceException(errors);
//		}
//	}
}
