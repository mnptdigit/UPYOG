package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Bank;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.BankModel;
import org.egov.finance.master.model.request.BankRequest;
import org.egov.finance.master.repository.BankRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.BankValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BankService {

	private final BankRepository bankRepository;
	private final BankValidation validation;
	private final CacheEvictionService cacheEvictionService;
	private final CommonUtils commonUtils;

	@Autowired
	public BankService(BankRepository bankRepository, BankValidation validation,
			CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.bankRepository = bankRepository;
		this.validation = validation;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	@Cacheable(value = MasterConstants.BANK_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.BANK_SEARCH_REDIS_KEY_GENERATOR)
	public List<BankModel> search(@Valid BankModel searchRequest) {
		Specification<Bank> spec = Specification.where(null);

		if (!ObjectUtils.isEmpty(searchRequest.getCode()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("code"), searchRequest.getCode()));

		if (!ObjectUtils.isEmpty(searchRequest.getName()))
			spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + searchRequest.getName() + "%"));

		if (!ObjectUtils.isEmpty(searchRequest.getId()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), searchRequest.getId()));

		if (!ObjectUtils.isEmpty(searchRequest.getIsactive()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("isactive"), searchRequest.getIsactive()));

		return bankRepository.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(BankModel::getId)).toList();
	}

	public BankModel save(BankRequest request) {
		BankModel model = request.getBank();
		Map<String, String> errorMap = new HashMap<>();

		if (!ObjectUtils.isEmpty(model.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new MasterServiceException(errorMap);
		}

		Bank entity = validation.modelToEntity(model);
		validation.bankCreateValidation(model);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.BANK_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.BANK_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(bankRepository.save(entity));
	}

	public BankModel update(BankRequest request) {
		Bank modelEntity = validation.modelToEntity(request.getBank());
		Map<String, String> errorMap = new HashMap<>();

		if (ObjectUtils.isEmpty(modelEntity.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}

		Bank existingEntity = bankRepository.findById(modelEntity.getId()).orElseThrow(() -> {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		});

		List<String> updatedFields = commonUtils.applyNonNullFields(modelEntity, existingEntity);
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

		BankModel model = new BankModel();
		model.setId(existingEntity.getId());

		if (updatedSet.contains("code"))
			model.setCode(existingEntity.getCode());
		if (updatedSet.contains("name"))
			model.setName(existingEntity.getName());
		if (updatedSet.contains("isactive"))
			model.setIsactive(existingEntity.getIsactive());

		if (!ObjectUtils.isEmpty(updatedSet))
			validation.bankUpdateValidation(model, updatedSet);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.BANK_SEARCH_REDIS_CACHE_VERSION_KEY, MasterConstants.BANK_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(bankRepository.save(existingEntity));
	}
}
