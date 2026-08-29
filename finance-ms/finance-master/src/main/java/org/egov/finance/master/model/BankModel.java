package org.egov.finance.master.model;

import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BankModel {

	private Long id;

	@SafeHtml
	@Length(max = 50)
	private String code; // No @NotNull

	@SafeHtml
	@Length(max = 100)
	private String name;

	@SafeHtml
	@Length(max = 250)
	private String narration;

	private Boolean isactive; // No @NotNull

	@SafeHtml
	@Length(max = 50)
	private String type;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
