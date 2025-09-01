package org.egov.finance.master.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Date;

import org.egov.finance.master.enumeration.SupplierTypeEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierModel {

	private Long id;

	@NotNull
	private String code;

	@NotNull
	private String name;

	@NotNull
	private String correspondenceAddress;

	private String paymentAddress;
	private String contactPerson;
	private String email;
	private String narration;
	private String panNumber;
	private String tinNumber;

	private Long bankId;
	private String ifscCode;
	private String bankAccount;

	@NotNull
	private String mobileNumber;

	private String registrationNumber;

	@NotNull
	private Long statusId;

	private String epfNumber;
	private String esiNumber;
	private String gstRegisteredState;

	private SupplierTypeEnum supplierType;

	private String tenantId;

	private Long createdBy;
	private Long lastModifiedBy;
	private Date createdDate;
	private Date lastModifiedDate;
}
