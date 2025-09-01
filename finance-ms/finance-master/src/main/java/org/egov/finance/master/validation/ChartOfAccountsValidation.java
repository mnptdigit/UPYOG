package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.egov.finance.master.entity.CChartOfAccounts;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.ChartOfAccountsModel;
import org.egov.finance.master.repository.ChartOfAccountsRepository;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChartOfAccountsValidation {

	@Autowired
	private ChartOfAccountsRepository repo;

	public CChartOfAccounts modelToEntity(ChartOfAccountsModel m) {
		CChartOfAccounts e = new CChartOfAccounts();
		BeanUtils.copyProperties(m, e);
		return e;
	}

	public ChartOfAccountsModel entityToModel(CChartOfAccounts e) {
		ChartOfAccountsModel m = new ChartOfAccountsModel();
		BeanUtils.copyProperties(e, m);
		return m;
	}

	public void validateCreate(ChartOfAccountsModel m) {
		Map<String, String> errors = new HashMap<>();
		if (repo.existsByGlcodeIgnoreCase(m.getGlcode())) {
			// errors.put("glcode", "GL Code already exists");
			errors.put(MasterConstants.INVALID_GLCODEID, MasterConstants.INVALID_GLCODEID_ASSOCIATED_MSG);
		}

		if (repo.existsByNameIgnoreCase(m.getName())) {
			// errors.put("name", "Name already exists");
			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		}

		if (repo.existsByGlcodeIgnoreCaseAndNameIgnoreCase(m.getGlcode(), m.getName())) {
			// errors.put("code+name", "Code and Name combination already exists");
			errors.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);

		}
		if (!errors.isEmpty())
			throw new MasterServiceException(errors);
	}

	public void validateUpdate(ChartOfAccountsModel m, Set<String> updatedFields) {
		Map<String, String> errors = new HashMap<>();

		if (updatedFields.contains("glcode") && repo.existsByGlcodeIgnoreCaseAndIdNot(m.getGlcode(), m.getId())) {
			// errors.put("glcode", "GL Code already exists for another record");
			errors.put(MasterConstants.INVALID_GLCODEID, MasterConstants.INVALID_GLCODEID_ASSOCIATED_MSG);
		}

		if (updatedFields.contains("name") && repo.existsByNameIgnoreCaseAndIdNot(m.getName(), m.getId())) {
			// errors.put("name", "Name already exists for another entity");
			errors.put(MasterConstants.NAME_NOT_UNIQUE, MasterConstants.NAME_IS_ALREADY_EXISTS_MSG);
		}

		if ((updatedFields.contains("glcode") || updatedFields.contains("name"))
				&& repo.existsByGlcodeIgnoreCaseAndNameIgnoreCaseAndIdNot(m.getGlcode(), m.getName(), m.getId())) {
			// errors.put("code+name", "Code and Name combination already exists for another
			// entity");
			errors.put(MasterConstants.CODE_NAME_NOT_UNIQUE, MasterConstants.CODE_NAME_NOT_UNIQUE_MSG);
		}
		if (!errors.isEmpty())
			throw new MasterServiceException(errors);
	}
}
