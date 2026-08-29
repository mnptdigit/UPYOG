package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Bank;
import org.egov.finance.master.entity.CChartOfAccounts;
import org.egov.finance.master.entity.EgPartytype;
import org.egov.finance.master.entity.Recovery;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.RecoveryModel;
import org.egov.finance.master.repository.BankRepository;
import org.egov.finance.master.repository.ChartOfAccountsRepository;
import org.egov.finance.master.repository.EgPartytypeRepository;
import org.egov.finance.master.repository.RecoveryRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RecoveryValidation {

	@Autowired
	private RecoveryRepository recoveryRepository;

	@Autowired
	private ChartOfAccountsRepository chartOfAccountsRepository;

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private EgPartytypeRepository egPartytypeRepository;

	public Recovery modelToEntity(RecoveryModel m) {
		Recovery r = new Recovery();
		r.setId(m.getId());
		r.setType(m.getType());
		r.setIsactive(m.getIsActive());
		r.setRate(m.getRate());
		r.setRemitted(m.getRemitted());
		r.setDescription(m.getDescription());
		r.setCaplimit(m.getCapLimit());
		r.setRecoveryName(m.getRecoveryName());
		r.setCalculationType(m.getCalculationType());
		r.setIfscCode(m.getIfscCode());
		r.setAccountNumber(m.getAccountNumber());
		r.setRecoveryMode(m.getRecoveryMode());
		r.setRemittanceMode(m.getRemittanceMode());

		r.setChartofaccounts(validateAndGetCOA(m.getGlcodeId()));
		r.setBank(validateAndGetBank(m.getBankId()));
		r.setEgPartytype(validateAndGetPartyType(m.getPartyTypeId()));

		return r;
	}

	public RecoveryModel entityToModel(Recovery r) {
		return RecoveryModel.builder().id(r.getId())
				.glcodeId(r.getChartofaccounts() != null ? r.getChartofaccounts().getId() : null).type(r.getType())
				.isActive(r.getIsactive()).rate(r.getRate()).remitted(r.getRemitted()).description(r.getDescription())
				.partyTypeId(r.getEgPartytype() != null ? r.getEgPartytype().getId().longValue() : null)
				.bankId(r.getBank() != null ? r.getBank().getId() : null).capLimit(r.getCaplimit())
				.recoveryName(r.getRecoveryName()).calculationType(r.getCalculationType()).ifscCode(r.getIfscCode())
				.accountNumber(r.getAccountNumber()).recoveryMode(r.getRecoveryMode())
				.remittanceMode(r.getRemittanceMode()).createdBy(r.getCreatedBy()).createdDate(r.getCreatedDate())
				.lastModifiedBy(r.getLastModifiedBy()).lastModifiedDate(r.getLastModifiedDate()).build();
	}

	public void validateCreate(RecoveryModel m) {
		Map<String, String> errors = new HashMap<>();

		if (isTypeExists(m.getType()))
			errors.put("type", "Recovery type already exists");

		if (isGlcodeUsed(m.getGlcodeId()))
			errors.put("glcodeId", "GL Code is already associated with another recovery");

		if (!errors.isEmpty())
			throw new MasterServiceException(errors);

		validateAndGetCOA(m.getGlcodeId());
		validateAndGetBank(m.getBankId());
		validateAndGetPartyType(m.getPartyTypeId());
	}

	public void validateUpdate(RecoveryModel m, Set<String> updatedFields) {
		Map<String, String> errors = new HashMap<>();

		Recovery existing = recoveryRepository.findById(m.getId())
				.orElseThrow(() -> new MasterServiceException(Map.of("id", "Recovery with given ID not found")));

		if (updatedFields.contains("type") && !existing.getType().equalsIgnoreCase(m.getType())
				&& isTypeExists(m.getType())) {
			errors.put("type", "Another Recovery with same type already exists");
		}

		if (updatedFields.contains("glcodeId")
				&& recoveryRepository.existsByChartofaccounts_IdAndIdNot(m.getGlcodeId(), m.getId())) {
			errors.put("glcodeId", "Another Recovery with same GL Code already exists");
		}

		if (!errors.isEmpty())
			throw new MasterServiceException(errors);

		if (updatedFields.contains("glcodeId"))
			validateAndGetCOA(m.getGlcodeId());

		if (updatedFields.contains("bankId"))
			validateAndGetBank(m.getBankId());

		if (updatedFields.contains("partyTypeId"))
			validateAndGetPartyType(m.getPartyTypeId());
	}

	// -------------------------------
	// Reusable helper methods below
	// -------------------------------

	private CChartOfAccounts validateAndGetCOA(Long coaId) {
		if (coaId == null)
			return null;
		return chartOfAccountsRepository.findById(coaId).orElseThrow(() -> new MasterServiceException(
				Map.of(MasterConstants.INVALID_GLCODEID, MasterConstants.INVALID_GLCODEID_ASSOCIATED_MSG)));
	}

	private Bank validateAndGetBank(Long bankId) {
		if (bankId == null)
			return null;
		return bankRepository.findById(bankId)
				.orElseThrow(() -> new MasterServiceException(Map.of("invalid.bankId", "Invalid Bank ID provided")));
	}

	private EgPartytype validateAndGetPartyType(Long id) {
		if (id == null)
			return null;
		return egPartytypeRepository.findById(id).orElseThrow(
				() -> new MasterServiceException(Map.of("invalid.partyTypeId", "Invalid Party Type ID provided")));
	}

	private boolean isTypeExists(String type) {
		if (type == null)
			return false;
		Specification<Recovery> spec = SpecificationHelper.equal("type", type.trim());
		return recoveryRepository.count(spec) > 0;
	}

	private boolean isGlcodeUsed(Long glcodeId) {
		if (glcodeId == null)
			return false;
		Specification<Recovery> spec = SpecificationHelper.equal("chartofaccounts.id", glcodeId);
		return recoveryRepository.count(spec) > 0;
	}
}
