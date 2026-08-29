package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Contractor;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.ContractorModel;
import org.egov.finance.master.model.request.ContractorRequest;
import org.egov.finance.master.repository.ContractorRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.ContractorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContractorService {
	private final ContractorRepository repo;
	private final ContractorValidation validation;
	private final CacheEvictionService cacheEvictionService;
	private final CommonUtils commonUtils;

	@Autowired
	public ContractorService(ContractorRepository repo, ContractorValidation validation,
			CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.repo = repo;
		this.validation = validation;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	@Cacheable(value = MasterConstants.CONTRACTOR_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.CONTRACTOR_SEARCH_REDIS_KEY_GENERATOR)
	public List<ContractorModel> search(ContractorModel c) {
		Specification<Contractor> spec = Specification.where(null);
		if (c.getId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("id"), c.getId()));
		if (StringUtils.hasText(c.getCode()))
			spec = spec.and((r, q, cb) -> cb.equal(cb.lower(r.get("code")), c.getCode().toLowerCase()));
		if (StringUtils.hasText(c.getName()))
			spec = spec.and((r, q, cb) -> cb.like(cb.lower(r.get("name")), "%" + c.getName().toLowerCase() + "%"));
		if (c.getStatusId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.join("status").get("id"), c.getStatusId()));
		return repo.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(ContractorModel::getId)).collect(Collectors.toList());
	}

	public ContractorModel save(ContractorRequest req) {
		ContractorModel m = req.getContractor();
		if (m.getId() != null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG));
		Contractor e = validation.modelToEntity(m);
		validation.contractorCreateValidation(m);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.CONTRACTOR_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.CONTRACTOR_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(e));
	}

	public ContractorModel update(ContractorRequest req) {
		ContractorModel m = req.getContractor();
		if (m.getId() == null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));
		Contractor existing = repo.findById(m.getId()).orElseThrow(() -> new MasterServiceException(
				Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE)));
		Contractor e = validation.modelToEntity(m);
		List<String> updated = commonUtils.applyNonNullFields(e, existing);
		Set<String> updSet = updated.stream().map(String::toLowerCase).collect(Collectors.toSet());
		ContractorModel tmp = ContractorModel.builder().id(existing.getId()).code(existing.getCode())
				.tinNumber(existing.getTinNumber()).statusId(existing.getStatus().getId()).build();
		if (!updSet.isEmpty())
			validation.contractorUpdateValidation(tmp, updSet);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.CONTRACTOR_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.CONTRACTOR_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(existing));
	}
}
