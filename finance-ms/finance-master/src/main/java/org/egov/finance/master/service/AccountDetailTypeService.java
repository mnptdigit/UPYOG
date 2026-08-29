package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.AccountDetailType;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.AccountDetailTypeModel;
import org.egov.finance.master.model.request.AccountDetailTypeRequest;
import org.egov.finance.master.repository.AccountDetailTypeRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.AccountDetailTypeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountDetailTypeService {

	@Autowired
	private AccountDetailTypeRepository accountDetailTypeRepository;

	@Autowired
	private AccountDetailTypeValidation validation;

	@Autowired
	private CacheEvictionService cacheEvictionService;

	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_KEY_GENERATOR)
	public List<AccountDetailTypeModel> search(AccountDetailTypeModel searchRequest) {
		Specification<AccountDetailType> spec = Specification.where(null);

		if (!ObjectUtils.isEmpty(searchRequest.getId()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), searchRequest.getId()));
		if (!ObjectUtils.isEmpty(searchRequest.getName()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("name"), searchRequest.getName()));
		if (!ObjectUtils.isEmpty(searchRequest.getAttributename()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("attributename"), searchRequest.getAttributename()));
		if (!ObjectUtils.isEmpty(searchRequest.getActive()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), searchRequest.getActive()));

		return accountDetailTypeRepository.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(AccountDetailTypeModel::getId)).collect(Collectors.toList());
	}

	public AccountDetailTypeModel save(AccountDetailTypeRequest request) {
		AccountDetailTypeModel model = request.getAccountDetailType();
		Map<String, String> errorMap = new HashMap<>();

		if (!ObjectUtils.isEmpty(model.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new MasterServiceException(errorMap);
		}

		AccountDetailType entity = validation.modelToEntity(model);
		validation.accountDetailTypeValidateCreate(model);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(accountDetailTypeRepository.save(entity));
	}

	public AccountDetailTypeModel update(AccountDetailTypeRequest request) {
		AccountDetailType entityToUpdate = validation.modelToEntity(request.getAccountDetailType());
		Map<String, String> errorMap = new HashMap<>();

		if (ObjectUtils.isEmpty(entityToUpdate.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}

		AccountDetailType existing = accountDetailTypeRepository.findById(entityToUpdate.getId()).orElseThrow(() -> {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		});

		List<String> updatedFields = commonUtils.applyNonNullFields(entityToUpdate, existing);
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

		AccountDetailTypeModel model = validation.entityToModel(existing);
		if (!ObjectUtils.isEmpty(updatedSet)) {
			validation.accountDetailTypeValidateUpdate(model, updatedSet);
		}

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(accountDetailTypeRepository.save(existing));
	}
}
