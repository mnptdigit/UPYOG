package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.PurchaseOrder;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.PurchaseOrderModel;
import org.egov.finance.master.model.request.PurchaseOrderRequest;
import org.egov.finance.master.repository.PurchaseOrderRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.PurchaseOrderValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PurchaseOrderService {

	@Autowired
	private PurchaseOrderRepository repo;

	@Autowired
	private PurchaseOrderValidation validation;

	@Autowired
	private CacheEvictionService cacheEvictionService;

	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_KEY_GENERATOR)
	public List<PurchaseOrderModel> search(PurchaseOrderModel criteria) {
		Specification<PurchaseOrder> spec = Specification.where(null);

		if (criteria.getId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("id"), criteria.getId()));
		if (criteria.getOrderNumber() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("orderNumber"), criteria.getOrderNumber()));
		if (criteria.getSupplierId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.join("supplier").get("id"), criteria.getSupplierId()));
		if (criteria.getFundId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.join("fund").get("id"), criteria.getFundId()));
		if (criteria.getActive() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("active"), criteria.getActive()));

		return repo.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(PurchaseOrderModel::getId)).toList();
	}

	public PurchaseOrderModel save(PurchaseOrderRequest req) {
		PurchaseOrderModel m = req.getPurchaseOrder();

		if (m.getId() != null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG));

		validation.validateCreate(m);
		PurchaseOrder entity = validation.modelToEntity(m);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(repo.save(entity));
	}

	public PurchaseOrderModel update(PurchaseOrderRequest req) {
		PurchaseOrder newEntity = validation.modelToEntity(req.getPurchaseOrder());

		if (newEntity.getId() == null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));

		PurchaseOrder existing = repo.findById(newEntity.getId()).orElseThrow(() -> new MasterServiceException(
				Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE)));

		List<String> updatedFields = commonUtils.applyNonNullFields(newEntity, existing);
		Set<String> updated = updatedFields.stream().map(String::toLowerCase).collect(Collectors.toSet());

		PurchaseOrderModel model = validation.entityToModel(existing);

		if (!updated.isEmpty()) {
			validation.validateUpdate(model, updated);
		}

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_CACHE_NAME);

		return validation.entityToModel(repo.save(existing));
	}
}
