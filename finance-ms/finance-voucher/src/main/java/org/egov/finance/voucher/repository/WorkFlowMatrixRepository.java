package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.workflow.entity.WorkFlowMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkFlowMatrixRepository
		extends JpaRepository<WorkFlowMatrix, Long>, JpaSpecificationExecutor<WorkFlowMatrix> {
}