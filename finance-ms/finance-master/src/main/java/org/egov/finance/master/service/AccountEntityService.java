package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.egov.finance.master.entity.AccountEntity;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.AccountEntityModel;
import org.egov.finance.master.model.request.AccountEntityRequest;
import org.egov.finance.master.repository.AccountEntityRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.AccountEntityValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountEntityService {

	@Autowired
	private AccountEntityRepository repo;

	@Autowired
	private AccountEntityValidation validation;

	@Autowired
	private CacheEvictionService cacheEvictionService;

	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_KEY_GENERATOR)
	public List<AccountEntityModel> search(AccountEntityModel criteria) {
		Specification<AccountEntity> spec = Specification.where(null);
		if (criteria.getId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("id"), criteria.getId()));
		if (criteria.getCode() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("code"), criteria.getCode()));
		if (criteria.getName() != null)
			spec = spec
					.and((r, q, cb) -> cb.like(cb.lower(r.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
		if (criteria.getIsactive() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("isactive"), criteria.getIsactive()));
		return repo.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparing(AccountEntityModel::getId)).toList();
	}

	public AccountEntityModel save(AccountEntityRequest req) {
		AccountEntityModel m = req.getAccountEntity();
		Map<String, String> errorMap = new HashMap<>();
		if (m.getId() != null) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new MasterServiceException(errorMap);
		}
		validation.validateCreate(m);
		AccountEntity e = validation.modelToEntity(m);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(e));
	}

	public AccountEntityModel update(AccountEntityRequest req) {
		AccountEntityModel m = req.getAccountEntity();
		Map<String, String> errorMap = new HashMap<>();

		if (m.getId() == null) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}
		AccountEntity existing = repo.findById(m.getId()).orElseThrow(() -> {

			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			return new MasterServiceException(errorMap);
		});

		AccountEntity updatedEntity = validation.modelToEntity(m);
		List<String> changedFields = commonUtils.applyNonNullFields(updatedEntity, existing);
		validation.validateUpdate(m, new HashSet<>(changedFields));
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(existing));
	}
}
