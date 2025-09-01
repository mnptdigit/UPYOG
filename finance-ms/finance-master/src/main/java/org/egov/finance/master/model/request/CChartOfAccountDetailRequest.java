package org.egov.finance.master.model.request;

import org.egov.finance.master.model.ChartOfAccountDetailModel;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CChartOfAccountDetailRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@Valid
	@JsonProperty("ChartOfAccountDetail")
	private ChartOfAccountDetailModel chartOfAccountDetail;

}
