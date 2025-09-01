package org.egov.finance.master.repository;

import org.egov.finance.master.entity.Recovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryRepository extends JpaRepository<Recovery, Long>, JpaSpecificationExecutor<Recovery> {

	boolean existsByTypeIgnoreCase(String type);

	boolean existsByChartofaccounts_Id(Long glcodeId);

	boolean existsByChartofaccounts_IdAndIdNot(Long glcodeId, Long id);

}
