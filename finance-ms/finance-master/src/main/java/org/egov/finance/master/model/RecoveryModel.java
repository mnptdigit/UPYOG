package org.egov.finance.master.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryModel {

	private Long id;

	@NotNull
	private Long glcodeId;

	@NotNull
	private String type;

	private Boolean isActive;

	private BigDecimal rate;

	@NotNull
	private String remitted;

	private String description;

	private Long partyTypeId;

	private Long bankId;

	private BigDecimal capLimit;

	@NotNull
	private String recoveryName;

	private String calculationType;

	private String ifscCode;

	private String accountNumber;

	@NotNull
	private Character recoveryMode;

	private Character remittanceMode;

	private Long createdBy;

	private Date createdDate;

	private Long lastModifiedBy;

	private Date lastModifiedDate;
}
