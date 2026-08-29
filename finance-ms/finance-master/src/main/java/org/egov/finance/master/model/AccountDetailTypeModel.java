package org.egov.finance.master.model;

import org.egov.finance.master.customannotation.SafeHtml;

import lombok.Data;

@Data
public class AccountDetailTypeModel {
	
	private Long id;

    @SafeHtml
    private String name;

    @SafeHtml
    private String description;

    @SafeHtml
    private String tablename;
    
	private String columnname;
    
	private String attributename;
    
	private int nbroflevels;

    @SafeHtml
    private String fullyQualifiedName;

    private Boolean active;

    private Long createdBy;
    private java.util.Date createdDate;
    private Long lastModifiedBy;
    private java.util.Date lastModifiedDate;

}
