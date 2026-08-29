package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Bankbranch;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.BankbranchModel;
import org.egov.finance.master.model.request.BankbranchRequest;
import org.egov.finance.master.repository.BankbranchRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.BankBranchValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BankBranchService {

	private final BankbranchRepository bankBranchRepository;
	private final BankBranchValidation validation;
	private final CacheEvictionService cacheEvictionService;
	private final CommonUtils commonUtils;

	@Autowired
	public BankBranchService(BankbranchRepository bankBranchRepository, BankBranchValidation validation,
			CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.bankBranchRepository = bankBranchRepository;
		this.validation = validation;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	@Cacheable(value = MasterConstants.BANK_BRANCH_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.BANK_BRANCH_SEARCH_REDIS_KEY_GENERATOR)
	public List<BankbranchModel> search(@Valid BankbranchModel searchRequest) {
		Specification<Bankbranch> spec = Specification.where(null);

		if (!ObjectUtils.isEmpty(searchRequest.getBranchcode()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("branchcode"), searchRequest.getBranchcode()));

		if (!ObjectUtils.isEmpty(searchRequest.getBranchname()))
			spec = spec.and(
					(root, query, cb) -> cb.like(root.get("branchname"), "%" + searchRequest.getBranchname() + "%"));

//		if (!ObjectUtils.isEmpty(searchRequest.getBankId()))
//			spec = spec.and((root, query, cb) -> cb.equal(root.get("bankid"), searchRequest.getBankId()));

		if (!ObjectUtils.isEmpty(searchRequest.getId()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), searchRequest.getId()));

		if (!ObjectUtils.isEmpty(searchRequest.getIsactive()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("isactive"), searchRequest.getIsactive()));

		return bankBranchRepository.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(BankbranchModel::getId)).toList();
	}

	public BankbranchModel save(BankbranchRequest request) {
		BankbranchModel model = request.getBankbranch();
		Map<String, String> errorMap = new HashMap<>();

		if (!ObjectUtils.isEmpty(model.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new MasterServiceException(errorMap);
		}

		Bankbranch entity = validation.modelToEntity(model);
		validation.bankBranchCreateValidation(model);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.BANK_BRANCH_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.BANK_BRANCH_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(bankBranchRepository.save(entity));
	}

	public BankbranchModel update(BankbranchRequest request) {
		Bankbranch modelEntity = validation.modelToEntity(request.getBankbranch());
		Map<String, String> errorMap = new HashMap<>();

		if (ObjectUtils.isEmpty(modelEntity.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}

		Bankbranch existingEntity = bankBranchRepository.findById(modelEntity.getId()).orElseThrow(() -> {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		});

		List<String> updatedFields = commonUtils.applyNonNullFields(modelEntity, existingEntity);
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

		BankbranchModel model = new BankbranchModel();
		model.setId(existingEntity.getId());

		if (updatedSet.contains("code"))
			model.setBranchcode(existingEntity.getBranchcode());
		if (updatedSet.contains("name"))
			model.setBranchname(existingEntity.getBranchname());
		if (updatedSet.contains("isactive"))
			model.setIsactive(existingEntity.getIsactive());

		if (!ObjectUtils.isEmpty(updatedSet))
			validation.bankBranchUpdateValidation(model, updatedSet);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.BANK_BRANCH_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.BANK_BRANCH_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(bankBranchRepository.save(existingEntity));
	}
}