package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.WorkOrderModel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkOrderResponse {
	private ResponseInfo responseInfo;
	private List<WorkOrderModel> workOrders;
}
