package org.egov.finance.master.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "accountentitymaster")
@SequenceGenerator(name = AccountEntity.SEQ, sequenceName = AccountEntity.SEQ, allocationSize = 1)
@Data
public class AccountEntity {

    public static final String SEQ = "seq_accountentitymaster";

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Length(max = 350)
    private String name;

    @NotNull
    @Length(max = 25)
    private String code;

    @Length(max = 250)
    private String narration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detailtypeid")
    private AccountDetailType accountDetailType;

    private Boolean isactive;

    private Long createdBy;

    private Date createdDate;

    private Long lastModifiedBy;

    private Date lastModifiedDate;
}
