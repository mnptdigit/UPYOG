package org.egov.finance.master.validation;

import java.util.HashMap;
import java.util.Map;

import org.egov.finance.master.entity.CChartOfAccountDetail;
import org.egov.finance.master.exception.MasterServiceException;
import org.egov.finance.master.model.ChartOfAccountDetailModel;
import org.egov.finance.master.repository.AccountDetailTypeRepository;
import org.egov.finance.master.repository.ChartOfAccountDetailRepository;
import org.egov.finance.master.repository.ChartOfAccountsRepository;
import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChartOfAccountDetailValidation {
	@Autowired
	private ChartOfAccountDetailRepository repo;
	@Autowired
	private ChartOfAccountsRepository coaRepo;
	@Autowired
	private AccountDetailTypeRepository dtRepo;

	public CChartOfAccountDetail modelToEntity(ChartOfAccountDetailModel m) {
		CChartOfAccountDetail e = new CChartOfAccountDetail();
		BeanUtils.copyProperties(m, e);
		e.setGlCodeId(coaRepo.findById(m.getGlcodeId()).orElseThrow(() -> {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(MasterConstants.INVALID_GLCODEID, MasterConstants.INVALID_GLCODEID_ASSOCIATED_MSG);
			return new MasterServiceException(errorMap);
		}));

		e.setDetailTypeId(dtRepo.findById(m.getDetailTypeId()).orElseThrow(() -> {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put(MasterConstants.INVALID_DETAILTYPEID, MasterConstants.INVALID_DETAILTYPEID_ASSOCIATED_MSG);
			return new MasterServiceException(errorMap);
		}));

		return e;
	}

	public ChartOfAccountDetailModel entityToModel(CChartOfAccountDetail e) {
		return ChartOfAccountDetailModel.builder().id(e.getId()).glcodeId(e.getGlCodeId().getId())
				.detailTypeId(e.getDetailTypeId().getId()).createdBy(e.getCreatedBy()).createdDate(e.getCreatedDate())
				.lastModifiedBy(e.getLastModifiedBy()).lastModifiedDate(e.getLastModifiedDate()).build();
	}

	public void validateCreate(ChartOfAccountDetailModel m) {
		if (repo.existsByGlCodeIdAndDetailTypeId(m.getGlcodeId(), m.getDetailTypeId())) {
			throw new MasterServiceException(Map.of("combination", "GL Code + Detail Type already exists"));
		}
	}

	public void validateUpdate(ChartOfAccountDetailModel m) {
		if (repo.existsByGlCodeIdAndDetailTypeIdAndIdNot(m.getGlcodeId(), m.getDetailTypeId(), m.getId())) {
			throw new MasterServiceException(Map.of("combination", "Another record has same GL Code + Detail Type"));
		}
	}
}
