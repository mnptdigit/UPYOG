package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Bank;
import org.egov.finance.master.entity.Bankbranch;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.BankbranchModel;
import org.egov.finance.master.repository.BankRepository;
import org.egov.finance.master.repository.BankbranchRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BankBranchValidation {

	@Autowired
	private BankbranchRepository bankbranchRepository;

	@Autowired
	private BankRepository bankRepository;

	public Bankbranch modelToEntity(BankbranchModel model) {
		Bankbranch entity = new Bankbranch();

		entity.setId(model.getId());
		entity.setBranchcode(model.getBranchcode());
		entity.setBranchname(model.getBranchname());
		entity.setBranchaddress1(model.getBranchaddress1());
		entity.setBranchaddress2(model.getBranchaddress2());
		entity.setBranchcity(model.getBranchcity());
		entity.setBranchstate(model.getBranchstate());
		entity.setBranchpin(model.getBranchpin());
		entity.setBranchphone(model.getBranchphone());
		entity.setBranchfax(model.getBranchfax());
		entity.setContactperson(model.getContactperson());
		entity.setIsactive(model.getIsactive());
		entity.setNarration(model.getNarration());
		entity.setBranchMICR(model.getBranchMICR());
		entity.setCreatedBy(model.getCreatedBy());
		entity.setCreatedDate(model.getCreatedDate());
		entity.setLastModifiedBy(model.getLastModifiedBy());
		entity.setLastModifiedDate(model.getLastModifiedDate());

		if (model.getBankId() != null) {
			Bank bank = bankRepository.findById(model.getBankId().longValue()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_BANKID, MasterConstants.INVALID_BANKID_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			});

			entity.setBank(bank);
		}

		return entity;
	}

	public BankbranchModel entityToModel(Bankbranch entity) {
		BankbranchModel model = new BankbranchModel();

		model.setId(entity.getId());

		model.setBranchcode(entity.getBranchcode());
		model.setBranchname(entity.getBranchname());
		model.setBranchaddress1(entity.getBranchaddress1());
		model.setBranchaddress2(entity.getBranchaddress2());
		model.setBranchcity(entity.getBranchcity());
		model.setBranchstate(entity.getBranchstate());
		model.setBranchpin(entity.getBranchpin());
		model.setBranchphone(entity.getBranchphone());
		model.setBranchfax(entity.getBranchfax());
		model.setContactperson(entity.getContactperson());
		model.setIsactive(entity.getIsactive());
		model.setNarration(entity.getNarration());
		model.setBranchMICR(entity.getBranchMICR());
		model.setCreatedBy(entity.getCreatedBy());
		model.setCreatedDate(entity.getCreatedDate());
		model.setLastModifiedBy(entity.getLastModifiedBy());
		model.setLastModifiedDate(entity.getLastModifiedDate());

		if (entity.getBank() != null) {
			model.setBankId(entity.getBank().getId());
		}

		return model;
	}

//	public void bankBranchCreateValidation(BankbranchModel model) {
//		Map<String, String> errors = new HashMap<>();
//
//		if (bankbranchRepository.existsByBranchcodeAndBank_Id(model.getBranchcode(), model.getBankId().longValue())) {
//			// errors.put("branchcode", "Branch code already exists for the given bank.");
//			errors.put(MasterConstants.BRANCHCODE_NOT_UNIQUE, MasterConstants.BRANCHCODE_IS_ALREADY_EXISTS_MSG);
//		}
//
//		if (!errors.isEmpty()) {
//			throw new MasterServiceException(errors);
//		}
//	}

	// ============ CREATE VALIDATION ============ //
	public void bankBranchCreateValidation(BankbranchModel model) {
		Map<String, String> errors = new HashMap<>();

		Specification<Bankbranch> spec = Specification
				.where(SpecificationHelper.<Bankbranch>equalIgnoreCase("branchcode", model.getBranchcode()))
				.and(SpecificationHelper.equal("bank.id", model.getBankId().longValue()));

		if (bankbranchRepository.findOne(spec).isPresent()) {
			errors.put(MasterConstants.BRANCHCODE_NOT_UNIQUE, MasterConstants.BRANCHCODE_IS_ALREADY_EXISTS_MSG);
		}

		if (!errors.isEmpty()) {
			throw new MasterServiceException(errors);
		}
	}

//	public void bankBranchUpdateValidation(BankbranchModel model, Set<String> updatedFields) {
//		Map<String, String> errors = new HashMap<>();
//
//		if (updatedFields.contains("branchcode")) {
//			if (bankbranchRepository.existsByBranchcodeAndBank_IdAndIdNot(model.getBranchcode(),
//					model.getBankId().longValue(), model.getId().longValue())) {
//				// errors.put("branchcode", "Branch code already exists for the given bank.");
//				errors.put(MasterConstants.BRANCHCODE_NOT_UNIQUE, MasterConstants.BRANCHCODE_IS_ALREADY_EXISTS_MSG);
//			}
//		}
//
//		if (!errors.isEmpty()) {
//			throw new MasterServiceException(errors);
//		}
//	}

	// ============ UPDATE VALIDATION ============ //
	public void bankBranchUpdateValidation(BankbranchModel model, Set<String> updatedFields) {
		Map<String, String> errors = new HashMap<>();

		if (updatedFields.contains("branchcode")) {
			Specification<Bankbranch> spec = Specification
					.where(SpecificationHelper.<Bankbranch>equalIgnoreCase("branchcode", model.getBranchcode()))
					.and(SpecificationHelper.equal("bank.id", model.getBankId().longValue()))
					.and(SpecificationHelper.notEqual("id", model.getId().longValue()));

			if (bankbranchRepository.findOne(spec).isPresent()) {
				errors.put(MasterConstants.BRANCHCODE_NOT_UNIQUE, MasterConstants.BRANCHCODE_IS_ALREADY_EXISTS_MSG);
			}
		}

		if (!errors.isEmpty()) {
			throw new MasterServiceException(errors);
		}
	}
}
