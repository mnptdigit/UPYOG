package org.egov.finance.master.repository;

import org.egov.finance.master.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountEntityRepository
		extends JpaRepository<AccountEntity, Long>, JpaSpecificationExecutor<AccountEntity> {

	boolean existsByCodeIgnoreCase(String code);

	boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

	boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

	boolean existsByNameIgnoreCase(String name);

	boolean existsByCodeIgnoreCaseAndNameIgnoreCaseAndIdNot(String code, String name, Long id);

	boolean existsByCodeIgnoreCaseAndNameIgnoreCase(String code, String name);
}
