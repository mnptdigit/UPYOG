package org.egov.finance.voucher.repository;

import java.util.Optional;

import org.egov.finance.voucher.entity.EgwStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgwStatusRepository extends JpaRepository<EgwStatus, Long>, JpaSpecificationExecutor<EgwStatus> {
	Optional<EgwStatus> findByModuleTypeIgnoreCaseAndDescriptionIgnoreCase(String moduleType, String description);

}
