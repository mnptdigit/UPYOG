package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.AccountDetailType;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.AccountDetailTypeModel;
import org.egov.finance.master.repository.AccountDetailTypeRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class AccountDetailTypeValidation {

	@Autowired
	private AccountDetailTypeRepository accountdetailtypeRepository;

	public AccountDetailType modelToEntity(AccountDetailTypeModel model) {
		AccountDetailType entity = new AccountDetailType();

		entity.setId(model.getId());
		entity.setName(model.getName());
		entity.setDescription(model.getDescription());
		entity.setTablename(model.getTablename());
		entity.setAttributename(model.getAttributename());
		entity.setColumnname(model.getColumnname());
		entity.setNbroflevels(model.getNbroflevels());
		entity.setFullyQualifiedName(model.getFullyQualifiedName());
		entity.setActive(model.getActive());
		entity.setCreatedBy(model.getCreatedBy());
		entity.setCreatedDate(model.getCreatedDate());
		entity.setLastModifiedBy(model.getLastModifiedBy());
		entity.setLastModifiedDate(model.getLastModifiedDate());
		return entity;
	}

	public AccountDetailTypeModel entityToModel(AccountDetailType entity) {
		AccountDetailTypeModel model = new AccountDetailTypeModel();
		model.setId(entity.getId());
		model.setName(entity.getName());
		model.setDescription(entity.getDescription());
		model.setTablename(entity.getTablename());

		model.setAttributename(entity.getAttributename());
		model.setColumnname(entity.getColumnname());
		model.setNbroflevels(entity.getNbroflevels());
		model.setFullyQualifiedName(entity.getFullyQualifiedName());
		model.setActive(entity.getActive());
		model.setCreatedBy(entity.getCreatedBy());
		model.setCreatedDate(entity.getCreatedDate());
		model.setLastModifiedBy(entity.getLastModifiedBy());
		model.setLastModifiedDate(entity.getLastModifiedDate());
		return model;
	}

//	public void accountDetailTypeValidateCreate(AccountDetailTypeModel model) {
//		Map<String, String> errors = new HashMap<>();
//
//		if (accountdetailtypeRepository.existsByNameIgnoreCase(model.getName())) {
//			// errors.put("name", "Account Detail Type with this name already exists.");
//			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
//		}
//
//		if (!errors.isEmpty()) {
//			throw new MasterServiceException(errors);
//		}
//	}

	/**
	 * Validation for create (Name uniqueness check).
	 */
	public void accountDetailTypeValidateCreate(AccountDetailTypeModel model) {
		Map<String, String> errorMap = new HashMap<>();

		if (!StringUtils.hasText(model.getName())) {
			errorMap.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
			throw new MasterServiceException(errorMap);
		}

		Specification<AccountDetailType> spec = SpecificationHelper.equalIgnoreCase("name", model.getName());

		if (accountdetailtypeRepository.count(spec) > 0) {
			errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		}

		if (!CollectionUtils.isEmpty(errorMap)) {
			throw new MasterServiceException(errorMap);
		}
	}

	/**
	 * Validation for update (Name uniqueness check with exclusion of current id).
	 */
	public void accountDetailTypeValidateUpdate(AccountDetailTypeModel model, Set<String> updatedFields) {
		Map<String, String> errorMap = new HashMap<>();

		if (updatedFields.contains("name") && StringUtils.hasText(model.getName())) {
			Specification<AccountDetailType> spec = SpecificationHelper
					.<AccountDetailType>equalIgnoreCase("name", model.getName())
					.and((root, query, cb) -> cb.notEqual(root.get("id"), model.getId()));

			if (accountdetailtypeRepository.count(spec) > 0) {
				errorMap.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
			}
		}

		if (!CollectionUtils.isEmpty(errorMap)) {
			throw new MasterServiceException(errorMap);
		}
	}

//	public void accountDetailTypeValidateUpdate(AccountDetailTypeModel model, Set<String> updatedFields) {
//	Map<String, String> errors = new HashMap<>();
//
//	if (updatedFields.contains("name")) {
//		if (accountdetailtypeRepository.existsByNameIgnoreCaseAndIdNot(model.getName(), model.getId())) {
//			// errors.put("name", "Another Account Detail Type with this name already
//			// exists.");
//			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
//		}
//	}
//
//	if (!errors.isEmpty()) {
//		throw new MasterServiceException(errors);
//	}
//}

}
