package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.CChartOfAccounts;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.ChartOfAccountsModel;
import org.egov.finance.master.model.request.ChartOfAccountsRequest;
import org.egov.finance.master.repository.ChartOfAccountsRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.ChartOfAccountsValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartOfAccountsService {

	@Autowired
	private ChartOfAccountsRepository repo;
	@Autowired
	private ChartOfAccountsValidation validation;
	@Autowired
	private CacheEvictionService cacheEvictionService;
	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.COA_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.COA_SEARCH_REDIS_KEY_GENERATOR)
	public List<ChartOfAccountsModel> search(ChartOfAccountsModel criteria) {
		Specification<CChartOfAccounts> spec = Specification.where(null);
		if (criteria.getId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("id"), criteria.getId()));
		if (criteria.getGlcode() != null)
			spec = spec.and((r, q, cb) -> cb.equal(cb.lower(r.get("glcode")), criteria.getGlcode().toLowerCase()));
		if (criteria.getName() != null)
			spec = spec
					.and((r, q, cb) -> cb.like(cb.lower(r.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
		return repo.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(ChartOfAccountsModel::getId)).toList();
	}

	public ChartOfAccountsModel save(ChartOfAccountsRequest req) {
		ChartOfAccountsModel m = req.getChartOfAccount();
		if (m.getId() != null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG));
		validation.validateCreate(m);
		CChartOfAccounts e = validation.modelToEntity(m);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.COA_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.COA_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(e));
	}

	public ChartOfAccountsModel update(ChartOfAccountsRequest req) {
		ChartOfAccountsModel m = req.getChartOfAccount();
		if (m.getId() == null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));
		CChartOfAccounts eReq = validation.modelToEntity(m);
		CChartOfAccounts existing = repo.findById(eReq.getId()).orElseThrow(() -> new MasterServiceException(
				Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE)));
		List<String> updated = commonUtils.applyNonNullFields(eReq, existing);
		Set<String> updSet = updated.stream().map(String::toLowerCase).collect(Collectors.toSet());
		if (!updSet.isEmpty())
			validation.validateUpdate(m, updSet);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.COA_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.COA_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(existing));
	}
}
