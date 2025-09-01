package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Supplier;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.SupplierModel;
import org.egov.finance.master.model.request.SupplierRequest;
import org.egov.finance.master.repository.SupplierRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.SupplierValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupplierService {

	private final SupplierRepository supplierRepository;
	private final SupplierValidation validation;
	private final CacheEvictionService cacheEvictionService;
	private final CommonUtils commonUtils;

	@Autowired
	public SupplierService(SupplierRepository supplierRepository, SupplierValidation validation,
			CacheEvictionService cacheEvictionService, CommonUtils commonUtils) {
		this.supplierRepository = supplierRepository;
		this.validation = validation;
		this.cacheEvictionService = cacheEvictionService;
		this.commonUtils = commonUtils;
	}

	@Cacheable(value = MasterConstants.SUPPLIER_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.SUPPLIER_SEARCH_REDIS_KEY_GENERATOR)
	public List<SupplierModel> search(SupplierModel criteria) {
		Specification<Supplier> spec = Specification.where(null);

		if (StringUtils.hasText(criteria.getCode()))
			spec = spec.and((root, query, cb) -> cb.equal(root.get("code"), criteria.getCode()));

		if (StringUtils.hasText(criteria.getName()))
			spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + criteria.getName() + "%"));

		if (criteria.getBankId() != null)
			spec = spec.and((root, query, cb) -> cb.equal(root.join("bank").get("id"), criteria.getBankId()));

		if (criteria.getStatusId() != null)
			spec = spec.and((root, query, cb) -> cb.equal(root.join("status").get("id"), criteria.getStatusId()));

		return supplierRepository.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(SupplierModel::getId)).collect(Collectors.toList());
	}

	public SupplierModel save(SupplierRequest request) {
		SupplierModel model = request.getSupplier();

		if (model.getId() != null) {
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG));
		}

		validation.validateCreate(model);
		Supplier entity = validation.modelToEntity(model);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.SUPPLIER_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.SUPPLIER_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(supplierRepository.save(entity));
	}

	public SupplierModel update(SupplierRequest request) {
		Supplier supplierFromReq = validation.modelToEntity(request.getSupplier());

		if (supplierFromReq.getId() == null) {
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));
		}

		Supplier supplierDb = supplierRepository.findById(supplierFromReq.getId()).orElseThrow(() -> {
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));
		});

		List<String> updatedFields = commonUtils.applyNonNullFields(supplierFromReq, supplierDb);
		Set<String> updatedSet = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

		SupplierModel model = new SupplierModel();
		model.setId(supplierDb.getId());
		if (updatedSet.contains("code"))
			model.setCode(supplierDb.getCode());
		if (updatedSet.contains("registrationNumber"))
			model.setRegistrationNumber(supplierDb.getRegistrationNumber());

		validation.validateUpdate(model, updatedSet);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.SUPPLIER_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.SUPPLIER_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(supplierRepository.save(supplierDb));
	}
}