package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Bankaccount;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.BankaccountModel;
import org.egov.finance.master.model.request.BankaccountRequest;
import org.egov.finance.master.repository.BankaccountRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.BankAccountValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BankAccountService {

	@Autowired
	private BankaccountRepository bankaccountRepository;

	@Autowired
	private BankAccountValidation validation;

	@Autowired
	private CacheEvictionService cacheEvictionService;

	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_KEY_GENERATOR)
	public List<BankaccountModel> search(BankaccountModel searchRequest) {
		Specification<Bankaccount> spec = Specification.where(null);

		if (!ObjectUtils.isEmpty(searchRequest.getId()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("id"), searchRequest.getId()));
		if (!ObjectUtils.isEmpty(searchRequest.getBankbranchId()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("branchid"), searchRequest.getBankbranchId()));
		if (!ObjectUtils.isEmpty(searchRequest.getAccountnumber()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("accountnumber"), searchRequest.getAccountnumber()));
		if (!ObjectUtils.isEmpty(searchRequest.getIsactive()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("isactive"), searchRequest.getIsactive()));

		return bankaccountRepository.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(BankaccountModel::getId)).collect(Collectors.toList());
	}

	public BankaccountModel save(BankaccountRequest request) {
		BankaccountModel model = request.getBankaccount();
		Map<String, String> errorMap = new HashMap<>();

		if (!ObjectUtils.isEmpty(model.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG);
			throw new MasterServiceException(errorMap);
		}

		Bankaccount entity = validation.modelToEntity(model);
		validation.bankAccountCreateValidation(model);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(bankaccountRepository.save(entity));
	}

	public BankaccountModel update(BankaccountRequest request) {
		Bankaccount modelEntity = validation.modelToEntity(request.getBankaccount());
		Map<String, String> errorMap = new HashMap<>();

		if (ObjectUtils.isEmpty(modelEntity.getId())) {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		}

		Bankaccount existingEntity = bankaccountRepository.findById(modelEntity.getId()).orElseThrow(() -> {
			errorMap.put(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE);
			throw new MasterServiceException(errorMap);
		});

		List<String> updatedFields = commonUtils.applyNonNullFields(modelEntity, existingEntity);
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

		BankaccountModel model = new BankaccountModel();
		model.setId(existingEntity.getId());
		model.setAccountnumber(existingEntity.getAccountnumber());
		model.setBankbranchId(existingEntity.getBankbranch().getId());
		model.setIsactive(existingEntity.getIsactive());

		if (!ObjectUtils.isEmpty(updatedSet)) {
			validation.bankAccountUpdateValidation(model, updatedSet);
		}

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(bankaccountRepository.save(existingEntity));
	}
}
