package org.egov.finance.master.repository;

import org.egov.finance.master.entity.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContractorRepository extends JpaRepository<Contractor, Long>, JpaSpecificationExecutor<Contractor> {

	boolean existsByCodeIgnoreCase(String code);

	boolean existsByTinNumberIgnoreCase(String tinNumber);

	boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

	boolean existsByTinNumberIgnoreCaseAndIdNot(String tinNumber, Long id);

}
