package org.egov.finance.master.repository;

import java.util.Optional;

import org.egov.finance.master.entity.CChartOfAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountsRepository
		extends JpaRepository<CChartOfAccounts, Long>, JpaSpecificationExecutor<CChartOfAccounts> {

	Optional<CChartOfAccounts> findByGlcode(String glcode);

    boolean existsByGlcodeIgnoreCase(String glcode);

    boolean existsByGlcodeIgnoreCaseAndIdNot(String glcode, Long id);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByGlcodeIgnoreCaseAndNameIgnoreCase(String glcode, String name); 

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    boolean existsByGlcodeIgnoreCaseAndNameIgnoreCaseAndIdNot(String glcode, String name, Long id);
}
