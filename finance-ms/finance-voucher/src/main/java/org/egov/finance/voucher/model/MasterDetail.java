/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.voucher.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MasterDetail implements Serializable {
	private String name;
	private String filter;

	public MasterDetail(String name, String filter) {
		this.name = name;
		this.filter = filter;
	}

	public MasterDetail() {
	}
}
