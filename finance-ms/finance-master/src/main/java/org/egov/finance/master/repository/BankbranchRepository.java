package org.egov.finance.master.repository;

import java.util.Optional;

import org.egov.finance.master.entity.Bankbranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankbranchRepository extends JpaRepository<Bankbranch, Long>, JpaSpecificationExecutor<Bankbranch> {

	Optional<Bankbranch> findByBranchMICR(String branchMICR);

	boolean existsByBranchcodeAndBank_Id(String branchCode, Long bankId);

	boolean existsByBranchcodeAndBank_IdAndIdNot(String branchCode, Long bankId, Long id);
}