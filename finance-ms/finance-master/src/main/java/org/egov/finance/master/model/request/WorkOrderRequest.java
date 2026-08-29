package org.egov.finance.master.model.request;

import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.model.WorkOrderModel;

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
public class WorkOrderRequest {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@Valid
	@JsonProperty("WorkOrder")
	private WorkOrderModel workOrder;
}
