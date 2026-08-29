package org.egov.finance.master.model.request;

import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.model.SupplierModel;

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
public class SupplierRequest {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@Valid
	@JsonProperty("Supplier")
	private SupplierModel supplier;
}
