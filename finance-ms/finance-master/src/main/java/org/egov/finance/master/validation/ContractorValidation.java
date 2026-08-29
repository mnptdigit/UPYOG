package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Bank;
import org.egov.finance.master.entity.Contractor;
import org.egov.finance.master.entity.EgwStatus;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.ContractorModel;
import org.egov.finance.master.repository.BankRepository;
import org.egov.finance.master.repository.ContractorRepository;
import org.egov.finance.master.repository.EgwStatusRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ContractorValidation {

	@Autowired
	private ContractorRepository contractorRepository;

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private EgwStatusRepository statusRepository;

	public Contractor modelToEntity(ContractorModel m) {
		Contractor e = new Contractor();
		e.setId(m.getId());
		e.setCode(m.getCode());
		e.setName(m.getName());
		e.setCorrespondenceAddress(m.getCorrespondenceAddress());
		e.setPaymentAddress(m.getPaymentAddress());
		e.setContactPerson(m.getContactPerson());
		e.setEmail(m.getEmail());
		e.setNarration(m.getNarration());
		e.setPanNumber(m.getPanNumber());
		e.setTinNumber(m.getTinNumber());

		if (m.getBankId() != null) {
			e.setBank(validateAndGetBank(m.getBankId()));
		}

		e.setIfscCode(m.getIfscCode());
		e.setBankAccount(m.getBankAccount());
		e.setMobileNumber(m.getMobileNumber());
		e.setRegistrationNumber(m.getRegistrationNumber());
		e.setEpfNumber(m.getEpfNumber());
		e.setEsiNumber(m.getEsiNumber());
		e.setGstRegisteredState(m.getGstRegisteredState());
		e.setContractorType(m.getContractorType());

		if (m.getStatusId() != null) {
			e.setStatus(validateAndGetStatus(m.getStatusId()));
		}

		e.setCreatedBy(m.getCreatedBy());
		e.setCreatedDate(m.getCreatedDate());
		e.setLastModifiedBy(m.getLastModifiedBy());
		e.setLastModifiedDate(m.getLastModifiedDate());
		return e;
	}

	public ContractorModel entityToModel(Contractor e) {
		return ContractorModel.builder().id(e.getId()).code(e.getCode()).name(e.getName())
				.correspondenceAddress(e.getCorrespondenceAddress()).paymentAddress(e.getPaymentAddress())
				.contactPerson(e.getContactPerson()).email(e.getEmail()).narration(e.getNarration())
				.panNumber(e.getPanNumber()).tinNumber(e.getTinNumber())
				.bankId(e.getBank() != null ? e.getBank().getId() : null).ifscCode(e.getIfscCode())
				.bankAccount(e.getBankAccount()).mobileNumber(e.getMobileNumber())
				.registrationNumber(e.getRegistrationNumber()).epfNumber(e.getEpfNumber()).esiNumber(e.getEsiNumber())
				.gstRegisteredState(e.getGstRegisteredState()).contractorType(e.getContractorType())
				.statusId(e.getStatus() != null ? e.getStatus().getId() : null).createdBy(e.getCreatedBy())
				.createdDate(e.getCreatedDate()).lastModifiedBy(e.getLastModifiedBy())
				.lastModifiedDate(e.getLastModifiedDate()).build();
	}

	public void contractorCreateValidation(ContractorModel m) {
		Map<String, String> errs = new HashMap<>();

		if (isCodeExists(m.getCode())) {
			errs.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		}

		if (isTinExists(m.getTinNumber())) {
			errs.put(MasterConstants.TIN_NUMBER_NOT_UNIQUE, MasterConstants.TIN_NUMBER_IS_ALREADY_EXISTS_MSG);
		}

		if (!errs.isEmpty()) {
			throw new MasterServiceException(errs);
		}
	}

	public void contractorUpdateValidation(ContractorModel m, Set<String> updatedFields) {
		Map<String, String> errs = new HashMap<>();

		if (updatedFields.contains("code") && isCodeExistsExcludeId(m.getCode(), m.getId())) {
			errs.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		}

		if (updatedFields.contains("tinNumber") && isTinExistsExcludeId(m.getTinNumber(), m.getId())) {
			errs.put(MasterConstants.TIN_NUMBER_NOT_UNIQUE, MasterConstants.TIN_NUMBER_IS_ALREADY_EXISTS_MSG);
		}

		if (!errs.isEmpty()) {
			throw new MasterServiceException(errs);
		}
	}

	// ---- SPECIFICATION BASED CHECKS ----

	private boolean isCodeExists(String code) {
		if (!StringUtils.hasText(code))
			return false;
		Specification<Contractor> spec = SpecificationHelper.<Contractor>equalIgnoreCase("code", code.trim());
		return contractorRepository.count(spec) > 0;
	}

	private boolean isTinExists(String tin) {
		if (!StringUtils.hasText(tin))
			return false;
		Specification<Contractor> spec = SpecificationHelper.<Contractor>equalIgnoreCase("tinNumber", tin.trim());
		return contractorRepository.count(spec) > 0;
	}

	private boolean isCodeExistsExcludeId(String code, Long id) {
		if (!StringUtils.hasText(code) || id == null)
			return false;
		Specification<Contractor> spec = SpecificationHelper.<Contractor>equalIgnoreCase("code", code.trim())
				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));
		return contractorRepository.count(spec) > 0;
	}

	private boolean isTinExistsExcludeId(String tin, Long id) {
		if (!StringUtils.hasText(tin) || id == null)
			return false;
		Specification<Contractor> spec = SpecificationHelper.<Contractor>equalIgnoreCase("tinNumber", tin.trim())
				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));
		return contractorRepository.count(spec) > 0;
	}

	// ---- VALIDATION HELPERS ----
	private Bank validateAndGetBank(Long id) {
		return bankRepository.findById(id).orElseThrow(() -> {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(MasterConstants.INVALID_BANKID, MasterConstants.INVALID_BANKID_ASSOCIATED_MSG);
			return new MasterServiceException(errorMap);
		});
	}

	private EgwStatus validateAndGetStatus(Long id) {
		return statusRepository.findById(id).orElseThrow(() -> {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(MasterConstants.INVALID_STATUSID, MasterConstants.INVALID_STATUSID_ASSOCIATED_MSG);
			return new MasterServiceException(errorMap);
		});
	}
}
