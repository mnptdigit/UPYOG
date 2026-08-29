package org.egov.finance.master.entity;

import org.egov.finance.master.customannotation.SafeHtml;
import org.egov.finance.master.enumeration.ContractorTypeEnum;
import org.egov.finance.master.validation.Unique;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.AbstractAuditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EGF_CONTRACTOR")
@Unique(id = "id", tableName = "EGF_CONTRACTOR", columnName = { "code", "tinNumber" }, fields = { "code",
		"tinNumber" }, enableDfltMsg = true)
@SequenceGenerator(name = Contractor.SEQ_EGF_CONTRACTOR, sequenceName = Contractor.SEQ_EGF_CONTRACTOR, allocationSize = 1)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contractor extends AuditDetailswithVersion {
	public static final String SEQ_EGF_CONTRACTOR = "SEQ_EGF_CONTRACTOR";

	@Id
	@GeneratedValue(generator = SEQ_EGF_CONTRACTOR, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@SafeHtml
	@Length(max = 50)
	@Column(updatable = false, nullable = false)
	private String code;

	@NotNull
	@SafeHtml
	@Length(max = 100)
	private String name;

	@NotNull
	@SafeHtml
	@Length(max = 250)
	private String correspondenceAddress;

	@SafeHtml
	@Length(max = 250)
	private String paymentAddress;

	@NotNull
	@SafeHtml
	@Length(max = 100)
	private String contactPerson;

	@SafeHtml
	@Length(max = 100)
	private String email;

	@SafeHtml
	@Length(max = 1024)
	private String narration;

	@SafeHtml
	@Length(max = 10)
	@Column(updatable = false)
	private String panNumber;

	@NotNull
	@SafeHtml
	@Length(min = 15, max = 15)
	@Column(updatable = false)
	private String tinNumber;

	@ManyToOne
	@JoinColumn(name = "bank")
	private Bank bank;

	@SafeHtml
	@Length(min = 11, max = 11)
	private String ifscCode;

	@SafeHtml
	@Length(max = 22)
	private String bankAccount;

	@NotNull
	@SafeHtml
	@Length(max = 10)
	private String mobileNumber;

	@SafeHtml
	@Length(max = 21)
	private String registrationNumber;

	@SafeHtml
	@Length(max = 24)
	private String epfNumber;

	@SafeHtml
	@Length(max = 21)
	private String esiNumber;

	@NotNull
	@SafeHtml
	@Length(max = 250)
	private String gstRegisteredState;

	@Enumerated(EnumType.STRING)
	@Column(name = "contractortype")
	private ContractorTypeEnum contractorType;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "status")
	private EgwStatus status;
}
