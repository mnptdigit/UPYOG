package org.egov.finance.master.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrderModel {
	private Long id;
	@NotNull
	private String orderNumber;
	@NotNull
	private String name;
	@NotNull
	private Date orderDate;
	@NotNull
	private Long contractorId;
	@NotNull
	@DecimalMin("0.01")
	private BigDecimal orderValue;
	private BigDecimal advancePayable;
	private String description;
	@NotNull
	private Long fundId;
	private String department;
	private Long schemeId;
	private Long subSchemeId;
	private String sanctionNumber;
	private Date sanctionDate;
	@NotNull
	private Boolean active;
	private Long createdBy;
	private Long lastModifiedBy;
	private Date createdDate;
	private Date lastModifiedDate;
	
}
