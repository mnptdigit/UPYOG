package org.egov.finance.master.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntityModel {

    private Long id;
    private String name;
    private String code;
    private String narration;
    private Long detailTypeId;
    private Boolean isactive;
    private Long createdBy;
    private Date createdDate;
    private Long lastModifiedBy;
    private Date lastModifiedDate;
}

