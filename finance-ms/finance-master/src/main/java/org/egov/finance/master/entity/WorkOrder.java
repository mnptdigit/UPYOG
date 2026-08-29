package org.egov.finance.master.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "egf_workorder", uniqueConstraints = {
		@UniqueConstraint(columnNames = "orderNumber", name = "unq_workorder") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_egf_workorder")
	@SequenceGenerator(name = "seq_egf_workorder", sequenceName = "seq_egf_workorder", allocationSize = 1)
	private Long id;

	@NotBlank
	@Length(max = 100)
	private String name;

	@NotBlank
	@Length(max = 100)
	private String orderNumber;

	@NotNull
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date orderDate;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contractor", nullable = false)
	private Contractor contractor;

	@NotNull
	@DecimalMin(value = "0.01", message = "Order value must be positive")
	private BigDecimal orderValue;

	@DecimalMin(value = "0.00", inclusive = true)
	private BigDecimal advancePayable;

	@Length(max = 250)
	private String description;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fund", nullable = false)
	private Fund fund;

	@Length(max = 100)
	private String department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scheme")
	private Scheme scheme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subScheme")
	private SubScheme subScheme;

	@Length(max = 100)
	private String sanctionNumber;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date sanctionDate;

	@NotNull
	private Boolean active;

	@NotNull
	private Long createdBy;

	private Long lastModifiedBy;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate;

	private Long version;

	// Optional fields, not persisted
	@Transient
	private String departmentName;

	@Transient
	private Boolean editAllFields;
}
