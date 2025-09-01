package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.WorkOrder;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.WorkOrderModel;
import org.egov.finance.master.repository.ContractorRepository;
import org.egov.finance.master.repository.FundRepository;
import org.egov.finance.master.repository.SchemeRepository;
import org.egov.finance.master.repository.SubSchemeRepository;
import org.egov.finance.master.repository.WorkOrderRepository;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderValidation {

	@Autowired
	private WorkOrderRepository repo;
	@Autowired
	private ContractorRepository contractorRepo;
	@Autowired
	private FundRepository fundRepo;
	@Autowired
	private SchemeRepository schemeRepo;
	@Autowired
	private SubSchemeRepository subSchemeRepo;

	public WorkOrder modelToEntity(WorkOrderModel m) {
		WorkOrder e = new WorkOrder();
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

		// Associations
		if (m.getContractorId() != null)
			e.setContractor(contractorRepo.findById(m.getContractorId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_CONTRACTOR, MasterConstants.INVALID_CONTRACTOR_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			}));

		if (m.getFundId() != null)
			e.setFund(fundRepo.findById(m.getFundId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_FUND, MasterConstants.INVALID_FUND_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			}));

		if (m.getSchemeId() != null)
			e.setScheme(schemeRepo.findById(m.getSchemeId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_SCHEME_ID, MasterConstants.INVALID_SCHEME_ID_MSG);
				return new MasterServiceException(errorMap);
			}));

		if (m.getSubSchemeId() != null)
			e.setSubScheme(subSchemeRepo.findById(m.getSubSchemeId()).orElseThrow(() -> {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put(MasterConstants.INVALID_SUBSCHEMEID, MasterConstants.INVALID_SUBSCHEMEID_ASSOCIATED_MSG);
				return new MasterServiceException(errorMap);
			}));

		return e;
	}

	public WorkOrderModel entityToModel(WorkOrder e) {
		return WorkOrderModel.builder().id(e.getId()).orderNumber(e.getOrderNumber()).name(e.getName())
				.orderDate(e.getOrderDate()).contractorId(e.getContractor() != null ? e.getContractor().getId() : null)
				.orderValue(e.getOrderValue()).advancePayable(e.getAdvancePayable()).description(e.getDescription())
				.fundId(e.getFund() != null ? e.getFund().getId() : null).department(e.getDepartment())
				.schemeId(e.getScheme() != null ? e.getScheme().getId() : null)
				.subSchemeId(e.getSubScheme() != null ? e.getSubScheme().getId() : null)
				.sanctionNumber(e.getSanctionNumber()).sanctionDate(e.getSanctionDate()).active(e.getActive())
				.createdBy(e.getCreatedBy()).createdDate(e.getCreatedDate()).lastModifiedBy(e.getLastModifiedBy())
				.lastModifiedDate(e.getLastModifiedDate()).build();
	}

	public void validateCreate(WorkOrderModel m) {
		Map<String, String> errors = new HashMap<>();

		if (repo.existsByOrderNumber(m.getOrderNumber()))
			// errors.put("orderNumber", "Order number already exists");
			errors.put(MasterConstants.ORDER_NOT_UNIQUE, MasterConstants.ORDER_IS_ALREADY_EXISTS_MSG);

		if (repo.existsByNameIgnoreCase(m.getName()))
			// errors.put("name", "Work Order name already exists");
			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);

		if (!errors.isEmpty())
			throw new MasterServiceException(errors);
	}

	public void validateUpdate(WorkOrderModel m, Set<String> updated) {
		Map<String, String> errors = new HashMap<>();

		if (updated.contains("orderNumber")) {
			if (repo.existsByOrderNumberAndIdNot(m.getOrderNumber(), m.getId()))
				errors.put(MasterConstants.ORDER_NOT_UNIQUE, MasterConstants.ORDER_IS_ALREADY_EXISTS_MSG);

		}

		if (updated.contains("name")) {
			if (repo.existsByNameIgnoreCaseAndIdNot(m.getName(), m.getId()))
				errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);

		}

		if (!errors.isEmpty())
			throw new MasterServiceException(errors);
	}
}