package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.AccountEntity;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.AccountEntityModel;
import org.egov.finance.master.repository.AccountDetailTypeRepository;
import org.egov.finance.master.repository.AccountEntityRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class AccountEntityValidation {

	@Autowired
	private AccountEntityRepository repo;

	@Autowired
	private AccountDetailTypeRepository detailTypeRepo;

	public AccountEntity modelToEntity(AccountEntityModel m) {
		AccountEntity e = new AccountEntity();
		e.setId(m.getId());
		e.setName(m.getName());
		e.setCode(m.getCode());
		e.setNarration(m.getNarration());
		e.setIsactive(m.getIsactive());
		e.setCreatedBy(m.getCreatedBy());
		e.setCreatedDate(m.getCreatedDate());
		e.setLastModifiedBy(m.getLastModifiedBy());
		e.setLastModifiedDate(m.getLastModifiedDate());

		if (m.getDetailTypeId() != null) {
			e.setAccountDetailType(detailTypeRepo.findById(m.getDetailTypeId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_DETAILTYPEID, MasterConstants.INVALID_DETAILTYPEID_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			}));

		}

		return e;
	}

	public AccountEntityModel entityToModel(AccountEntity e) {
		return AccountEntityModel.builder().id(e.getId()).name(e.getName()).code(e.getCode())
				.narration(e.getNarration()).isactive(e.getIsactive()).createdBy(e.getCreatedBy())
				.createdDate(e.getCreatedDate()).lastModifiedBy(e.getLastModifiedBy())
				.lastModifiedDate(e.getLastModifiedDate())
				.detailTypeId(e.getAccountDetailType() != null ? e.getAccountDetailType().getId() : null).build();
	}

	/**
	 * Create validation (checks for code, name, and code+name uniqueness).
	 */
	public void validateCreate(AccountEntityModel m) {
		Map<String, String> errors = new HashMap<>();

		if (!StringUtils.hasText(m.getCode()) || !StringUtils.hasText(m.getName())) {
			errors.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
			throw new MasterServiceException(errors);
		}

		// Code uniqueness
		Specification<AccountEntity> codeSpec = SpecificationHelper.equalIgnoreCase("code", m.getCode());
		if (repo.count(codeSpec) > 0) {
			errors.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		}

		// Name uniqueness
		Specification<AccountEntity> nameSpec = SpecificationHelper.equalIgnoreCase("name", m.getName());
		if (repo.count(nameSpec) > 0) {
			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		}

		// Code + Name combination uniqueness
		Specification<AccountEntity> codeNameSpec = SpecificationHelper
				.<AccountEntity>equalIgnoreCase("code", m.getCode())
				.and(SpecificationHelper.equalIgnoreCase("name", m.getName()));
		if (repo.count(codeNameSpec) > 0) {
			errors.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
		}

		if (!CollectionUtils.isEmpty(errors)) {
			throw new MasterServiceException(errors);
		}
	}

//	public void validateCreate(AccountEntityModel m) {
//		Map<String, String> errors = new HashMap<>();
//
//		if (repo.existsByCodeIgnoreCase(m.getCode())) {
//			// errors.put("code", "Code already exists");
//			errors.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
//		}
//
//		if (repo.existsByNameIgnoreCase(m.getName())) {
//			// errors.put("name", "Name already exists");
//			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
//		}
//
//		if (repo.existsByCodeIgnoreCaseAndNameIgnoreCase(m.getCode(), m.getName())) {
//			// errors.put("code+name", "Code and Name combination already exists");
//			errors.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
//
//		}
//
//		if (!errors.isEmpty()) {
//			throw new MasterServiceException(errors);
//		}
//	}

//	public void validateUpdate(AccountEntityModel m, Set<String> updatedFields) {
//		Map<String, String> errors = new HashMap<>();
//
//		if (updatedFields.contains("code") && repo.existsByCodeIgnoreCaseAndIdNot(m.getCode(), m.getId())) {
//			errors.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
//		}
//
//		if (updatedFields.contains("name") && repo.existsByNameIgnoreCaseAndIdNot(m.getName(), m.getId())) {
//			// errors.put("name", "Name already exists for another entity");
//			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
//		}
//
//		if ((updatedFields.contains("code") || updatedFields.contains("name"))
//				&& repo.existsByCodeIgnoreCaseAndNameIgnoreCaseAndIdNot(m.getCode(), m.getName(), m.getId())) {
//			// errors.put("code+name", "Code and Name combination already exists for another
//			// entity");
//			errors.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
//		}
//
//		if (!errors.isEmpty()) {
//			throw new MasterServiceException(errors);
//		}
//	}

	/**
	 * Update validation (checks for code, name, and code+name uniqueness excluding
	 * current record).
	 */
	public void validateUpdate(AccountEntityModel m, Set<String> updatedFields) {
		Map<String, String> errors = new HashMap<>();

		if (updatedFields.contains("code") && StringUtils.hasText(m.getCode())) {
			Specification<AccountEntity> codeSpec = SpecificationHelper
					.<AccountEntity>equalIgnoreCase("code", m.getCode())
					.and((root, query, cb) -> cb.notEqual(root.get("id"), m.getId()));
			if (repo.count(codeSpec) > 0) {
				errors.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
			}
		}

		if (updatedFields.contains("name") && StringUtils.hasText(m.getName())) {
			Specification<AccountEntity> nameSpec = SpecificationHelper
					.<AccountEntity>equalIgnoreCase("name", m.getName())
					.and((root, query, cb) -> cb.notEqual(root.get("id"), m.getId()));
			if (repo.count(nameSpec) > 0) {
				errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
			}
		}

		if (updatedFields.contains("code") || updatedFields.contains("name")) {
			Specification<AccountEntity> codeNameSpec = SpecificationHelper
					.<AccountEntity>equalIgnoreCase("code", m.getCode())
					.and(SpecificationHelper.equalIgnoreCase("name", m.getName()))
					.and((root, query, cb) -> cb.notEqual(root.get("id"), m.getId()));
			if (repo.count(codeNameSpec) > 0) {
				errors.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
			}
		}

		if (!CollectionUtils.isEmpty(errors)) {
			throw new MasterServiceException(errors);
		}
	}
}
