package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Bank;
import org.egov.finance.master.entity.EgwStatus;
import org.egov.finance.master.entity.Supplier;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.SupplierModel;
import org.egov.finance.master.repository.BankRepository;
import org.egov.finance.master.repository.EgwStatusRepository;
import org.egov.finance.master.repository.SupplierRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class SupplierValidation {

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private EgwStatusRepository egwStatusRepository;

	@Autowired
	private SupplierRepository supplierRepository;

	public Supplier modelToEntity(SupplierModel model) {
		Supplier supplier = new Supplier();
		supplier.setId(model.getId());
		supplier.setCode(model.getCode());
		supplier.setName(model.getName());
		supplier.setCorrespondenceAddress(model.getCorrespondenceAddress());
		supplier.setPaymentAddress(model.getPaymentAddress());
		supplier.setContactPerson(model.getContactPerson());
		supplier.setEmail(model.getEmail());
		supplier.setNarration(model.getNarration());
		supplier.setPanNumber(model.getPanNumber());
		supplier.setTinNumber(model.getTinNumber());
		supplier.setIfscCode(model.getIfscCode());
		supplier.setBankAccount(model.getBankAccount());
		supplier.setMobileNumber(model.getMobileNumber());
		supplier.setRegistrationNumber(model.getRegistrationNumber());
		supplier.setEpfNumber(model.getEpfNumber());
		supplier.setEsiNumber(model.getEsiNumber());
		supplier.setGstRegisteredState(model.getGstRegisteredState());
		supplier.setSupplierType(model.getSupplierType());

		supplier.setBank(validateAndGetBank(model.getBankId()));
		supplier.setStatus(validateAndGetStatus(model.getStatusId()));

		return supplier;
	}

	public SupplierModel entityToModel(Supplier supplier) {
		SupplierModel model = new SupplierModel();
		model.setId(supplier.getId());
		model.setCode(supplier.getCode());
		model.setName(supplier.getName());
		model.setCorrespondenceAddress(supplier.getCorrespondenceAddress());
		model.setPaymentAddress(supplier.getPaymentAddress());
		model.setContactPerson(supplier.getContactPerson());
		model.setEmail(supplier.getEmail());
		model.setNarration(supplier.getNarration());
		model.setPanNumber(supplier.getPanNumber());
		model.setTinNumber(supplier.getTinNumber());
		model.setIfscCode(supplier.getIfscCode());
		model.setBankAccount(supplier.getBankAccount());
		model.setMobileNumber(supplier.getMobileNumber());
		model.setRegistrationNumber(supplier.getRegistrationNumber());
		model.setEpfNumber(supplier.getEpfNumber());
		model.setEsiNumber(supplier.getEsiNumber());
		model.setGstRegisteredState(supplier.getGstRegisteredState());
		model.setSupplierType(supplier.getSupplierType());
		model.setBankId(supplier.getBank() != null ? supplier.getBank().getId() : null);
		model.setStatusId(supplier.getStatus() != null ? supplier.getStatus().getId() : null);
		model.setCreatedBy(supplier.getCreatedBy());
		model.setCreatedDate(supplier.getCreatedDate());
		model.setLastModifiedBy(supplier.getLastModifiedBy());
		model.setLastModifiedDate(supplier.getLastModifiedDate());
		return model;
	}

	public void validateCreate(SupplierModel model) {
		Map<String, String> errorMap = new HashMap<>();

		if (!StringUtils.hasText(model.getCode()) || !StringUtils.hasText(model.getName())) {
			errorMap.put(MasterConstants.INVALID_PARAMETERS, MasterConstants.INVALID_PARAMETERS_MSG);
			throw new MasterServiceException(errorMap);
		}

		if (isCodeExists(model.getCode())) {
			errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		}

		if (isRegNumberExists(model.getRegistrationNumber())) {
			errorMap.put(MasterConstants.REGISTRATION_NUMBER_NOT_UNIQUE,
					MasterConstants.REGISTRATION_NUMBER_IS_ALREADY_EXISTS_MSG);
		}

		if (!CollectionUtils.isEmpty(errorMap))
			throw new MasterServiceException(errorMap);

		validateAndGetBank(model.getBankId());
		validateAndGetStatus(model.getStatusId());
	}

	public void validateUpdate(SupplierModel model, Set<String> updatedFields) {
		Map<String, String> errorMap = new HashMap<>();

		Supplier existing = supplierRepository.findById(model.getId()).orElseThrow(() -> {
			Map<String, String> e = new HashMap<>();
			e.put("id", "Supplier with given ID not found");
			return new MasterServiceException(e);
		});

		if (updatedFields.contains("code") && !existing.getCode().equalsIgnoreCase(model.getCode())
				&& isCodeExistsExcludeId(model.getCode(), model.getId())) {
			errorMap.put(MasterConstants.CODE_NOT_UNIQUE, MasterConstants.CODE_IS_ALREADY_EXISTS_MSG);
		}

		if (updatedFields.contains("registrationNumber")
				&& isRegNumberExistsExcludeId(model.getRegistrationNumber(), model.getId())) {
			errorMap.put(MasterConstants.REGISTRATION_NUMBER_NOT_UNIQUE,
					MasterConstants.REGISTRATION_NUMBER_IS_ALREADY_EXISTS_MSG);
		}

		if (!errorMap.isEmpty())
			throw new MasterServiceException(errorMap);

		if (updatedFields.contains("bankId"))
			validateAndGetBank(model.getBankId());

		if (updatedFields.contains("statusId"))
			validateAndGetStatus(model.getStatusId());
	}

	// ============ Helper Methods ============

	private Bank validateAndGetBank(Long id) {
		if (id == null)
			return null;
		return bankRepository.findById(id).orElseThrow(() -> {
			Map<String, String> error = new HashMap<>();
			error.put(MasterConstants.INVALID_BANKID, MasterConstants.INVALID_BANKID_ASSOCIATED_MSG);
			return new MasterServiceException(error);
		});
	}

	private EgwStatus validateAndGetStatus(Long id) {
		if (id == null)
			return null;
		return egwStatusRepository.findById(id).orElseThrow(() -> {
			Map<String, String> error = new HashMap<>();
			error.put(MasterConstants.INVALID_STATUSID, MasterConstants.INVALID_STATUSID_ASSOCIATED_MSG);
			return new MasterServiceException(error);
		});
	}

	private boolean isCodeExists(String code) {
		if (!StringUtils.hasText(code))
			return false;
		Specification<Supplier> spec = SpecificationHelper.equalIgnoreCase("code", code.trim());
		return supplierRepository.count(spec) > 0;
	}

//	private boolean isCodeExistsExcludeId(String code, Long id) {
//		if (!StringUtils.hasText(code) || id == null) return false;
//		Specification<Supplier> spec = SpecificationHelper.equalIgnoreCase("code", code.trim())
//				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));
//		return supplierRepository.count(spec) > 0;
//	}

	private boolean isRegNumberExists(String regNumber) {
		if (!StringUtils.hasText(regNumber))
			return false;
		Specification<Supplier> spec = SpecificationHelper.equalIgnoreCase("registrationNumber", regNumber.trim());
		return supplierRepository.count(spec) > 0;
	}

//	private boolean isRegNumberExistsExcludeId(String regNumber, Long id) {
//		if (!StringUtils.hasText(regNumber) || id == null) return false;
//		Specification<Supplier> spec = SpecificationHelper.equalIgnoreCase("registrationNumber", regNumber.trim())
//				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));
//		return supplierRepository.count(spec) > 0;
//	}

	private boolean isCodeExistsExcludeId(String code, Long id) {
		if (!StringUtils.hasText(code) || id == null)
			return false;

		Specification<Supplier> spec = SpecificationHelper.<Supplier>equalIgnoreCase("code", code.trim())
				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));

		return supplierRepository.count(spec) > 0;
	}

	private boolean isRegNumberExistsExcludeId(String regNumber, Long id) {
		if (!StringUtils.hasText(regNumber) || id == null)
			return false;

		Specification<Supplier> spec = SpecificationHelper
				.<Supplier>equalIgnoreCase("registrationNumber", regNumber.trim())
				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));

		return supplierRepository.count(spec) > 0;
	}

}
