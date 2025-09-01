package org.egov.finance.master.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseOrderModel {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String orderNumber;
    @NotNull
    private Date orderDate;
    @NotNull
    private Long supplierId;
    @NotNull
    private BigDecimal orderValue;
    
    private BigDecimal advancePayable;
    private String description;
    @NotNull
    private Long fundId;
    
    private String department;
    private Long schemeId;
    private Long subSchemeId;
    private String sanctionNumber;
    @NotNull
    private Date sanctionDate;
    private Boolean active;
    private Long createdBy;
	private Long lastModifiedBy;
	private Date createdDate;
	private Date lastModifiedDate;
}

