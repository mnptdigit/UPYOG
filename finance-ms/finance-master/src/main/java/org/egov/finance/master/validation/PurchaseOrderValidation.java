package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.entity.PurchaseOrder;
import org.egov.finance.master.entity.Scheme;
import org.egov.finance.master.entity.SubScheme;
import org.egov.finance.master.entity.Supplier;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.PurchaseOrderModel;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.repository.PurchaseOrderRepository;
import org.egov.finance.master.repository.SchemeRepository;
import org.egov.finance.master.repository.SubSchemeRepository;
import org.egov.finance.master.repository.SupplierRepository;
import org.egov.finance.master.util.MasterConstants;
import org.egov.finance.master.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderValidation {

	@Autowired
	private PurchaseOrderRepository repo;

	@Autowired
	private SupplierRepository supplierRepo;

	@Autowired
	private FundRepository fundRepo;

	@Autowired
	private SchemeRepository schemeRepo;

	@Autowired
	private SubSchemeRepository subSchemeRepo;

	public PurchaseOrder modelToEntity(PurchaseOrderModel m) {
		PurchaseOrder e = new PurchaseOrder();
		e.setId(m.getId());
		e.setOrderNumber(m.getOrderNumber());
		e.setName(m.getName());
		e.setOrderDate(m.getOrderDate());
		e.setOrderValue(m.getOrderValue());
		e.setAdvancePayable(m.getAdvancePayable());
		e.setDescription(m.getDescription());
		e.setDepartment(m.getDepartment());
		e.setSanctionNumber(m.getSanctionNumber());
		e.setSanctionDate(m.getSanctionDate());
		e.setActive(m.getActive());
		e.setCreatedBy(m.getCreatedBy());
		e.setCreatedDate(m.getCreatedDate());
		e.setLastModifiedBy(m.getLastModifiedBy());
		e.setLastModifiedDate(m.getLastModifiedDate());

		e.setSupplier(validateAndGetSupplier(m.getSupplierId()));
		e.setFund(validateAndGetFund(m.getFundId()));
		e.setScheme(validateAndGetScheme(m.getSchemeId()));
		e.setSubScheme(validateAndGetSubScheme(m.getSubSchemeId()));

		return e;
	}

	public PurchaseOrderModel entityToModel(PurchaseOrder e) {
		return PurchaseOrderModel.builder().id(e.getId()).orderNumber(e.getOrderNumber()).name(e.getName())
				.orderDate(e.getOrderDate()).supplierId(e.getSupplier() != null ? e.getSupplier().getId() : null)
				.orderValue(e.getOrderValue()).advancePayable(e.getAdvancePayable()).description(e.getDescription())
				.fundId(e.getFund() != null ? e.getFund().getId() : null).department(e.getDepartment())
				.schemeId(e.getScheme() != null ? e.getScheme().getId() : null)
				.subSchemeId(e.getSubScheme() != null ? e.getSubScheme().getId() : null)
				.sanctionNumber(e.getSanctionNumber()).sanctionDate(e.getSanctionDate()).active(e.getActive())
				.createdBy(e.getCreatedBy()).createdDate(e.getCreatedDate()).lastModifiedBy(e.getLastModifiedBy())
				.lastModifiedDate(e.getLastModifiedDate()).build();
	}

	public void validateCreate(PurchaseOrderModel m) {
		Map<String, String> errors = new HashMap<>();

		if (isOrderNumberExists(m.getOrderNumber()))
			errors.put(MasterConstants.ORDER_NOT_UNIQUE, MasterConstants.ORDER_IS_ALREADY_EXISTS_MSG);

		if (isNameExists(m.getName()))
			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);

		if (!errors.isEmpty())
			throw new MasterServiceException(errors);

		validateAndGetSupplier(m.getSupplierId());
		validateAndGetFund(m.getFundId());
		validateAndGetScheme(m.getSchemeId());
		validateAndGetSubScheme(m.getSubSchemeId());
	}

	public void validateUpdate(PurchaseOrderModel m, Set<String> updatedFields) {
		Map<String, String> errors = new HashMap<>();

		PurchaseOrder existing = repo.findById(m.getId())
				.orElseThrow(() -> new MasterServiceException(Map.of("id", "Purchase Order with given ID not found")));

		if (updatedFields.contains("orderNumber") && !existing.getOrderNumber().equalsIgnoreCase(m.getOrderNumber())
				&& isOrderNumberExistsExcludeId(m.getOrderNumber(), m.getId())) {
			errors.put(MasterConstants.ORDER_NOT_UNIQUE, MasterConstants.ORDER_IS_ALREADY_EXISTS_MSG);
		}

		if (updatedFields.contains("name") && !existing.getName().equalsIgnoreCase(m.getName())
				&& isNameExistsExcludeId(m.getName(), m.getId())) {
			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		}

		if (!errors.isEmpty())
			throw new MasterServiceException(errors);

		if (updatedFields.contains("supplierId"))
			validateAndGetSupplier(m.getSupplierId());

		if (updatedFields.contains("fundId"))
			validateAndGetFund(m.getFundId());

		if (updatedFields.contains("schemeId"))
			validateAndGetScheme(m.getSchemeId());

		if (updatedFields.contains("subSchemeId"))
			validateAndGetSubScheme(m.getSubSchemeId());
	}

	// -------------------------------
	// Reusable helper methods below
	// -------------------------------

	private Supplier validateAndGetSupplier(Long id) {
		if (id == null)
			return null;
		return supplierRepo.findById(id).orElseThrow(() -> {
			Map<String, String> error = new HashMap<>();
			error.put(MasterConstants.INVALID_SUPPLIER, MasterConstants.INVALID_SUPPLIER_ASSOCIATED_MSG);
			return new MasterServiceException(error);
		});
	}

	private Fund validateAndGetFund(Long id) {
		if (id == null)
			return null;
		return fundRepo.findById(id).orElseThrow(() -> {
			Map<String, String> error = new HashMap<>();
			error.put(MasterConstants.INVALID_FUND, MasterConstants.INVALID_FUND_ASSOCIATED_MSG);
			return new MasterServiceException(error);
		});
	}

	private Scheme validateAndGetScheme(Long id) {
		if (id == null)
			return null;
		return schemeRepo.findById(id).orElseThrow(() -> {
			Map<String, String> error = new HashMap<>();
			error.put(MasterConstants.INVALID_SCHEMEID, MasterConstants.INVALID_SCHEMEID_ASSOCIATED_MSG);
			return new MasterServiceException(error);
		});
	}

	private SubScheme validateAndGetSubScheme(Long id) {
		if (id == null)
			return null;
		return subSchemeRepo.findById(id).orElseThrow(() -> {
			Map<String, String> error = new HashMap<>();
			error.put(MasterConstants.INVALID_SUBSCHEMEID, MasterConstants.INVALID_SUBSCHEMEID_ASSOCIATED_MSG);
			return new MasterServiceException(error);
		});
	}

	private boolean isOrderNumberExists(String orderNumber) {
		if (orderNumber == null)
			return false;
		Specification<PurchaseOrder> spec = SpecificationHelper.equal("orderNumber", orderNumber.trim());
		return repo.count(spec) > 0;
	}

	private boolean isOrderNumberExistsExcludeId(String orderNumber, Long id) {
		if (orderNumber == null || id == null)
			return false;
		Specification<PurchaseOrder> spec = SpecificationHelper
				.<PurchaseOrder, String>equal("orderNumber", orderNumber.trim())
				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));
		return repo.count(spec) > 0;
	}

	private boolean isNameExists(String name) {
		if (name == null)
			return false;
		Specification<PurchaseOrder> spec = SpecificationHelper.likeIgnoreCase("name", name.trim());
		return repo.count(spec) > 0;
	}

	private boolean isNameExistsExcludeId(String name, Long id) {
		if (name == null || id == null)
			return false;

		Specification<PurchaseOrder> spec = SpecificationHelper.<PurchaseOrder, String>equal("name", name.trim())
				.and((root, query, cb) -> cb.notEqual(root.get("id"), id));

		return repo.count(spec) > 0;
	}

}
