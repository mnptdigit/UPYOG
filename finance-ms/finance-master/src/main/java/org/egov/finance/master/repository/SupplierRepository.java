package org.egov.finance.master.repository;

import org.egov.finance.master.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long>, JpaSpecificationExecutor<Supplier> {

	boolean existsByCodeAndRegistrationNumberAndIdNot(String code, String registrationNumber, Long id);

	boolean existsByCodeAndRegistrationNumber(String code, String registrationNumber);

	boolean existsByRegistrationNumber(String registrationNumber);

	boolean existsByCodeAndIdNot(String code, Long id);

	boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id);

	boolean existsByCode(String code);
}
