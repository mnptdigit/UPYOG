package org.egov.finance.master.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "egf_purchaseorder")
@SequenceGenerator(name = PurchaseOrder.SEQ_PURCHASEORDER, sequenceName = PurchaseOrder.SEQ_PURCHASEORDER, allocationSize = 1)
@Data
public class PurchaseOrder extends AuditDetailswithVersion {

	public static final String SEQ_PURCHASEORDER = "seq_egf_purchaseorder";

	@Id
	@GeneratedValue(generator = SEQ_PURCHASEORDER, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "ordernumber", length = 100, nullable = false, unique = true)
	private String orderNumber;

	@Column(name = "orderdate", nullable = false)
	private Date orderDate;

	@ManyToOne
	@JoinColumn(name = "supplier")
	private Supplier supplier;

	@Column(name = "ordervalue", nullable = false)
	private BigDecimal orderValue;

	@Column(name = "advancepayable")
	private BigDecimal advancePayable;

	@Column(name = "description", length = 250)
	private String description;

	@ManyToOne
	@JoinColumn(name = "fund")
	private Fund fund;

	@Column(name = "department", length = 100)
	private String department;

	@ManyToOne
	@JoinColumn(name = "scheme")
	private Scheme scheme;

	@ManyToOne
	@JoinColumn(name = "subscheme")
	private SubScheme subScheme;

	@Column(name = "sanctionnumber", length = 100)
	private String sanctionNumber;

	@Column(name = "sanctiondate")
	private Date sanctionDate;

	@Column(name = "active", nullable = false)
	private Boolean active;

	@Transient
	private String departmentName;

	@Transient
	private Boolean editAllFields;

}
