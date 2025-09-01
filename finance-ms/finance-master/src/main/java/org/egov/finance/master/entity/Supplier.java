package org.egov.finance.master.entity;

import org.egov.finance.master.enumeration.SupplierTypeEnum;
import org.hibernate.validator.constraints.Length;

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

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity

@Table(name = "EGF_SUPPLIER")
@SequenceGenerator(name = Supplier.SEQ_EGF_SUPPLIER, sequenceName = Supplier.SEQ_EGF_SUPPLIER, allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier extends AuditDetailswithVersion {

	private static final long serialVersionUID = 2507334170114202599L;

	public static final String SEQ_EGF_SUPPLIER = "SEQ_EGF_SUPPLIER";

	@Id
	@GeneratedValue(generator = SEQ_EGF_SUPPLIER, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@Length(max = 50)
	@Column(updatable = false, nullable = false)
	private String code;

	@NotNull
	@Length(max = 100)
	private String name;

	@NotNull
	@Length(max = 250)
	private String correspondenceAddress;

	@Length(max = 250)
	private String paymentAddress;

	@NotNull
	@Length(max = 100)
	private String contactPerson;

	@Length(max = 100)
	private String email;

	@Length(max = 1024)
	private String narration;

	@Length(max = 10)
	@Column(updatable = false)
	private String panNumber;

	@Length(min = 15, max = 15)
	@Column(updatable = false)
	private String tinNumber;

	@ManyToOne
	@JoinColumn(name = "bank")
	private Bank bank;

	@Length(min = 11, max = 11)
	private String ifscCode;

	@Length(max = 22)
	private String bankAccount;

	@NotNull
	@Length(max = 10)
	private String mobileNumber;

	@Length(max = 21)
	private String registrationNumber;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "status")
	private EgwStatus status;

	@Length(max = 24)
	private String epfNumber;

	@Length(max = 21)
	private String esiNumber;

	@Length(max = 250)
	private String gstRegisteredState;

	@Enumerated(EnumType.STRING)
	@Column(name = "suppliertype")
	private SupplierTypeEnum supplierType;
}
