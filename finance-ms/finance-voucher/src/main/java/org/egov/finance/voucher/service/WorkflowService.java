package org.egov.finance.voucher.service;

import java.math.BigDecimal;

import org.egov.finance.voucher.workflow.entity.StateAware;
import org.egov.finance.voucher.workflow.entity.WorkFlowMatrix;

public interface WorkflowService<T extends StateAware> {

	WorkFlowMatrix getWfMatrix(String type, String department, BigDecimal amountRule, String additionalRule,
			String currentState, String pendingAction);

}
