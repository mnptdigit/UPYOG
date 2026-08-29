package org.egov.finance.master.model.request;

import org.egov.finance.master.model.PurchaseOrderModel;
import org.egov.finance.master.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	@Valid
	@JsonProperty("PurchaseOrder")
	private PurchaseOrderModel purchaseOrder;
}
