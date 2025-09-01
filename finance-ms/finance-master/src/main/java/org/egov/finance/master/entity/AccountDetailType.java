package org.egov.finance.master.entity;

import org.egov.finance.master.customannotation.SafeHtml;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accountdetailtype")
@SequenceGenerator(name = AccountDetailType.SEQ, sequenceName = AccountDetailType.SEQ, allocationSize = 1)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetailType extends AuditDetailswithVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ = "SEQ_AccountDetailType";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@SafeHtml
	private String name;

	@SafeHtml
	private String description;

	@SafeHtml
	private String tablename;

	@Column(name = "columnname")
	private String columnname;

	@Column(name = "attributename")
	private String attributename;

	@Column(name = "nbroflevels")
	private int nbroflevels;

	@Column(name = "full_qualified_name")
	@SafeHtml
	private String fullyQualifiedName;

	@Column(name = "isactive")
	private Boolean active;

	public AccountDetailType(Long id) {
		this.id = id;
	}
}
