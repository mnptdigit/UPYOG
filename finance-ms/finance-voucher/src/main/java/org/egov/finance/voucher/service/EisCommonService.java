package org.egov.finance.voucher.service;

import java.util.Arrays;

import org.egov.finance.voucher.workflow.entity.WorkFlowMatrix;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EisCommonService {

	/**
	 * Validates whether the position is valid as per the workflow matrix.
	 * 
	 * @param workFlowMatrix - Matrix for the current state
	 * @param desginations   - Approver position
	 * @return
	 */
	public boolean isValidAppover(WorkFlowMatrix workFlowMatrix, String designation) {
		if (workFlowMatrix.getCurrentDesignation() != null) {
			return Arrays.asList(workFlowMatrix.getCurrentDesignation().toLowerCase().split(","))
					.contains(designation.toLowerCase());
		}
		return false;
	}

}
