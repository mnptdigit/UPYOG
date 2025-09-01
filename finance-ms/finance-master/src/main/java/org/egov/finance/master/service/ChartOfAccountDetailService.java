package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.egov.finance.master.entity.CChartOfAccountDetail;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.ChartOfAccountDetailModel;
import org.egov.finance.master.model.request.CChartOfAccountDetailRequest;
import org.egov.finance.master.repository.ChartOfAccountDetailRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.ChartOfAccountDetailValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartOfAccountDetailService {

	@Autowired
	private ChartOfAccountDetailRepository repo;
	@Autowired
	private ChartOfAccountDetailValidation validation;
	@Autowired
	private CacheEvictionService cacheEvictionService;
	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.COA_DETAIL_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.COA_DETAIL_SEARCH_REDIS_KEY_GENERATOR)
	public List<ChartOfAccountDetailModel> search(ChartOfAccountDetailModel criteria) {
		Specification<CChartOfAccountDetail> spec = Specification.where(null);
		if (criteria.getId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("id"), criteria.getId()));
		if (criteria.getGlcodeId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.join("glCodeId").get("id"), criteria.getGlcodeId()));
		
		if (criteria.getDetailTypeId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.join("detailTypeId").get("id"), criteria.getDetailTypeId()));

		return repo.findAll(spec).stream().map(validation::entityToModel)
				.sorted(Comparator.comparingLong(ChartOfAccountDetailModel::getId)).toList();
	}

	public ChartOfAccountDetailModel save(CChartOfAccountDetailRequest req) {
		ChartOfAccountDetailModel m = req.getChartOfAccountDetail();
		if (m.getId() != null)
			throw new MasterServiceException(Map.of(MasterConstants.INVALID_ID_PASSED, "ID should not be passed"));
		validation.validateCreate(m);
		CChartOfAccountDetail e = validation.modelToEntity(m);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.COA_DETAIL_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.COA_DETAIL_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(e));
	}

	public ChartOfAccountDetailModel update(CChartOfAccountDetailRequest req) {
		ChartOfAccountDetailModel m = req.getChartOfAccountDetail();
		if (m.getId() == null)
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));
		validation.validateUpdate(m);
		CChartOfAccountDetail eReq = validation.modelToEntity(m);
		CChartOfAccountDetail existing = repo.findById(eReq.getId()).orElseThrow(() -> new MasterServiceException(
				Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE)));
		List<String> updated = commonUtils.applyNonNullFields(eReq, existing);
		if (!updated.isEmpty())
			validation.validateUpdate(m);
		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.COA_DETAIL_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.COA_DETAIL_SEARCH_REDIS_CACHE_NAME);
		return validation.entityToModel(repo.save(existing));
	}
}
