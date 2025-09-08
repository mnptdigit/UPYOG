package org.egov.finance.voucher.repository;

import static org.egov.finance.voucher.entity.AppConfig.FETCH_WITH_VALUES;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

import java.util.List;
import java.util.Optional;

import org.egov.finance.voucher.entity.AppConfig;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {

	@EntityGraph(value = FETCH_WITH_VALUES, type = FETCH)
	AppConfig findByModuleNameAndKeyName(String moduleName, String keyName);

	Optional<AppConfig> findById(Long id);

	@EntityGraph(value = FETCH_WITH_VALUES, type = FETCH)
	AppConfig findByKeyName(final String keyName);

	List<AppConfig> findByModuleName(String moduleName);

	@EntityGraph(value = FETCH_WITH_VALUES, type = FETCH)
	org.springframework.data.domain.Page<AppConfig> findByModuleName(String moduleName, Pageable pageable);
}
