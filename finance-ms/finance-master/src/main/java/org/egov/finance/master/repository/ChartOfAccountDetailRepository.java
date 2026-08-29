package org.egov.finance.master.repository;

import org.egov.finance.master.entity.CChartOfAccountDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartOfAccountDetailRepository
		extends JpaRepository<CChartOfAccountDetail, Long>, JpaSpecificationExecutor<CChartOfAccountDetail> {

//	boolean existsByGlCodeIdAndDetailTypeId(Long glCodeId, Long detailTypeId);
//
//	boolean existsByGlCodeIdAndDetailTypeIdAndIdNot(Long glCodeId, Long detailTypeId, Long id);
//	
//	boolean existsByGlCodeIdAndDetailTypeId(CChartOfAccounts glCodeId, AccountDetailType detailTypeId);
//
//	boolean existsByGlCodeIdAndDetailTypeIdAndIdNot(CChartOfAccounts glCodeId, AccountDetailType detailTypeId, Long id);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CChartOfAccountDetail c WHERE c.glCodeId.id = :glCodeId AND c.detailTypeId.id = :detailTypeId")
	boolean existsByGlCodeIdAndDetailTypeId(@Param("glCodeId") Long glCodeId, @Param("detailTypeId") Long detailTypeId);

	@Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CChartOfAccountDetail c WHERE c.glCodeId.id = :glCodeId AND c.detailTypeId.id = :detailTypeId AND c.id <> :id")
	boolean existsByGlCodeIdAndDetailTypeIdAndIdNot(@Param("glCodeId") Long glCodeId,
			@Param("detailTypeId") Long detailTypeId, @Param("id") Long id);

}
