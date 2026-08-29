/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.voucher.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuleDetail {
	private String moduleName;
	private List<MasterDetail> masterDetails;

	public ModuleDetail(String moduleName, List<MasterDetail> masterDetails) {
		this.moduleName = moduleName;
		this.masterDetails = masterDetails;
	}

	public ModuleDetail() {
	}

}
