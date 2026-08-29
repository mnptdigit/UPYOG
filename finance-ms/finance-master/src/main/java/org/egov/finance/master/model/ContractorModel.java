package org.egov.finance.master.model;

import java.util.Date;

import org.egov.finance.master.enumeration.ContractorTypeEnum;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractorModel {
	private Long id;
	@NotNull
	private String code;
	@NotNull
	private String name;
	@NotNull
	private String correspondenceAddress;
	private String paymentAddress;
	@NotNull
	private String contactPerson;
	private String email;
	private String narration;
	private String panNumber;
	@NotNull
	private String tinNumber;
	private Long bankId;
	private String ifscCode;
	private String bankAccount;
	@NotNull
	private String mobileNumber;
	private String registrationNumber;
	private String epfNumber;
	private String esiNumber;
	private String gstRegisteredState;
	private ContractorTypeEnum contractorType;
	@NotNull
	private Long statusId;
	
	private Long createdBy;
	private Long lastModifiedBy;
	private Date createdDate;
	private Date lastModifiedDate;
}
