package org.egov.finance.master.repository;

import org.egov.finance.master.entity.EgPartytype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgPartytypeRepository extends JpaRepository<EgPartytype, Long>, JpaSpecificationExecutor<EgPartytype> {

}
