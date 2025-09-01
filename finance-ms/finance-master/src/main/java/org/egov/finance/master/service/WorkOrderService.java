package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.WorkOrder;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.WorkOrderModel;
import org.egov.finance.master.model.request.WorkOrderRequest;
import org.egov.finance.master.repository.WorkOrderRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.WorkOrderValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkOrderService {

	@Autowired
	private WorkOrderRepository repo;
	@Autowired
	private WorkOrderValidation validation;
	@Autowired
	private CacheEvictionService cacheEvictionService;
	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.WORK_ORDER_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.WORK_ORDER_SEARCH_REDIS_KEY_GENERATOR)
	public List<WorkOrderModel> search(WorkOrderModel criteria) {
		Specification<WorkOrder> spec = Specification.where(null);
		if (criteria.getId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("id"), criteria.getId()));
		if (criteria.getOrderNumber() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("orderNumber"), criteria.getOrderNumber()));
		if (criteria.getContractorId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.join("contractor").get("id"), criteria.getContractorId()));
		if (criteria.getFundId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.join("fund").get("id"), criteria.getFundId()));
		if (criteria.getActive() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("active"), criteria.getActive()));
		return repo.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(WorkOrderModel::getId)).toList();
	}

	public WorkOrderModel save(WorkOrderRequest req) {
		WorkOrderModel m = req.getWorkOrder();
		if (m.getId() != null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG));
		validation.validateCreate(m);
		WorkOrder e = validation.modelToEntity(m);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.WORK_ORDER_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.WORK_ORDER_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(e));
	}

	public WorkOrderModel update(WorkOrderRequest req) {
		WorkOrder mReq = validation.modelToEntity(req.getWorkOrder());
		if (mReq.getId() == null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));
		WorkOrder existing = repo.findById(mReq.getId()).orElseThrow(() -> new MasterServiceException(
				Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE)));
		List<String> updated = commonUtils.applyNonNullFields(mReq, existing);
		Set<String> updSet = updated.stream().map(String::toLowerCase).collect(Collectors.toSet());
		WorkOrderModel model = workOrderToModel(existing);
		if (!updSet.isEmpty())
			validation.validateUpdate(model, updSet);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.WORK_ORDER_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.WORK_ORDER_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(existing));
	}

	private WorkOrderModel workOrderToModel(WorkOrder e) {
		return validation.entityToModel(e);
	}
}
