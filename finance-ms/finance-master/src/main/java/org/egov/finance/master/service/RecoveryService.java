package org.egov.finance.master.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.master.entity.Recovery;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.RecoveryModel;
import org.egov.finance.master.model.request.RecoveryRequest;
import org.egov.finance.master.repository.RecoveryRepository;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.validation.RecoveryValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecoveryService {

	@Autowired
	private RecoveryRepository recoveryRepository;

	@Autowired
	private RecoveryValidation recoveryValidation;

	@Autowired
	private CacheEvictionService cacheEvictionService;

	@Autowired
	private CommonUtils commonUtils;

	@Cacheable(value = MasterConstants.RECOVERY_SEARCH_REDIS_CACHE_NAME, keyGenerator = MasterConstants.RECOVERY_SEARCH_REDIS_KEY_GENERATOR)
	public List<RecoveryModel> search(RecoveryModel criteria) {
		Specification<Recovery> spec = Specification.where(null);

		if (criteria.getType() != null)
			spec = spec.and((r, q, cb) -> cb.equal(cb.lower(r.get("type")), criteria.getType().toLowerCase()));

		if (criteria.getIsActive() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("isactive"), criteria.getIsActive()));

		if (criteria.getGlcodeId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("chartofaccounts").get("id"), criteria.getGlcodeId()));

		if (criteria.getBankId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("bank").get("id"), criteria.getBankId()));

		if (criteria.getPartyTypeId() != null)
			spec = spec.and((r, q, cb) -> cb.equal(r.get("egPartytype").get("id"), criteria.getPartyTypeId()));

		return recoveryRepository.findAll(spec).stream().map(recoveryValidation::entityToModel)
				.sorted(Comparator.comparingLong(RecoveryModel::getId)).toList();
	}

	public RecoveryModel save(RecoveryRequest req) {
		RecoveryModel model = req.getRecovery();
		if (model.getId() != null) {
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.ID_CANNOT_BE_PASSED_IN_CREATION_MSG));
		}

		recoveryValidation.validateCreate(model);
		Recovery entity = recoveryValidation.modelToEntity(model);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.RECOVERY_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.RECOVERY_SEARCH_REDIS_CACHE_NAME);

		return recoveryValidation.entityToModel(recoveryRepository.save(entity));
	}

	public RecoveryModel update(RecoveryRequest req) {
		RecoveryModel model = req.getRecovery();
		if (model.getId() == null) {
			throw new MasterServiceException(
					Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE));
		}

		Recovery incoming = recoveryValidation.modelToEntity(model);
		Recovery existing = recoveryRepository.findById(model.getId()).orElseThrow(() -> new MasterServiceException(
				Map.of(MasterConstants.INVALID_ID_PASSED, MasterConstants.INVALID_ID_PASSED_MESSAGE)));

		List<String> updated = commonUtils.applyNonNullFields(incoming, existing);
		Set<String> updSet = updated.stream().map(String::toLowerCase).collect(Collectors.toSet());

		if (!updSet.isEmpty())
			recoveryValidation.validateUpdate(model, updSet);

		cacheEvictionService.incrementVersionForTenant(ApplicationThreadLocals.getTenantID(),
				MasterConstants.RECOVERY_SEARCH_REDIS_CACHE_VERSION_KEY,
				MasterConstants.RECOVERY_SEARCH_REDIS_CACHE_NAME);

		return recoveryValidation.entityToModel(recoveryRepository.save(existing));
	}
}
