package org.egov.finance.voucher.model.request;

import java.util.List;

import org.egov.finance.voucher.entity.Voucher;
import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.WorkflowBean;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VoucherRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("vouchers")
	private List<Voucher> vouchers;

	private WorkflowBean workflowBean;

	private String cutOffDate;

}
