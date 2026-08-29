package org.egov.finance.master.repository;

import org.egov.finance.master.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

	boolean existsByOrderNumber(String orderNumber);

	boolean existsByNameIgnoreCase(String name);

	boolean existsByOrderNumberAndIdNot(String orderNumber, Long id);

	boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

}
