package org.egov.finance.master.validation;

/**
 * BankValidation.java
 * 
 * @author mmavuluri
 * @date 21 Jul 2025
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Bank;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.BankModel;
import org.egov.finance.master.repository.BankRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class BankValidation {

	@Autowired
	private BankRepository bankRepository;

	public Bank modelToEntity(BankModel model) {
		Bank bank = new Bank();
		bank.setId(model.getId());
		bank.setCode(model.getCode());
		bank.setName(model.getName());
		bank.setIsactive(model.getIsactive());

		bank.setCreatedBy(model.getCreatedBy());
		bank.setCreatedDate(model.getCreatedDate());
		bank.setLastModifiedBy(model.getLastModifiedBy());
		bank.setLastModifiedDate(model.getLastModifiedDate());
		return bank;
	}

	public BankModel entityToModel(Bank bank) {
		BankModel model = new BankModel();
		model.setId(bank.getId());
		model.setCode(bank.getCode());
		model.setName(bank.getName());
		model.setIsactive(bank.getIsactive());

		model.setCreatedBy(bank.getCreatedBy());
		model.setCreatedDate(bank.getCreatedDate());
		model.setLastModifiedBy(bank.getLastModifiedBy());
		model.setLastModifiedDate(bank.getLastModifiedDate());
		return model;
	}

	public void bankCreateValidation(BankModel bankModel) {
		Map<String, String> errorMap = new HashMap<>();

		if (!StringUtils.hasText(bankModel.getCode()) || !StringUtils.hasText(bankModel.getName())) {
			errorMap.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
			throw new MasterServiceException(errorMap);
		}

		boolean codeExists = bankRepository
				.exists((root, query, cb) -> cb.equal(cb.lower(root.get("code")), bankModel.getCode().toLowerCase()));

		boolean nameExists = bankRepository
				.exists((root, query, cb) -> cb.equal(cb.lower(root.get("name")), bankModel.getName().toLowerCase()));

		if (codeExists)
			errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		if (nameExists)
			errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);

		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}

	public void bankUpdateValidation(BankModel bankModel, Set<String> updatedSet) {
		Map<String, String> errorMap = new HashMap<>();

		if (updatedSet.contains("code")) {
			boolean codeExists = bankRepository.exists(
					(root, query, cb) -> cb.and(cb.equal(cb.lower(root.get("code")), bankModel.getCode().toLowerCase()),
							cb.notEqual(root.get("id"), bankModel.getId())));
			if (codeExists)
				errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		}

		if (updatedSet.contains("name")) {
			boolean nameExists = bankRepository.exists(
					(root, query, cb) -> cb.and(cb.equal(cb.lower(root.get("name")), bankModel.getName().toLowerCase()),
							cb.notEqual(root.get("id"), bankModel.getId())));
			if (nameExists)
				errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		}

		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);
	}
}
