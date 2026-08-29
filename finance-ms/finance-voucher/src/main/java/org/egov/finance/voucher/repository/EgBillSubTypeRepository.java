package org.egov.finance.voucher.repository;

import java.util.List;
import java.util.Optional;

import org.egov.finance.voucher.entity.EgBillSubType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EgBillSubTypeRepository extends JpaRepository<EgBillSubType, Long> {

    List<EgBillSubType> findByExpenditureType(String expenditureType);

    Optional<EgBillSubType> findByExpenditureTypeAndName(String expenditureType, String name);
}
