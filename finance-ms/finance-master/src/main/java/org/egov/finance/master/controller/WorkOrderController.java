package org.egov.finance.master.controller;

import java.util.List;

import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.WorkOrderModel;
import org.egov.finance.master.model.request.WorkOrderRequest;
import org.egov.finance.master.model.response.WorkOrderResponse;
import org.egov.finance.master.service.WorkOrderService;
import org.egov.finance.master.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/workorder")
public class WorkOrderController {

	@Autowired
	private WorkOrderService service;
	@Autowired
	private ResponseInfoFactory resFactory;

	@PostMapping("/_create")
	public ResponseEntity<WorkOrderResponse> create(@Valid @RequestBody WorkOrderRequest req) {
		WorkOrderModel m = service.save(req);
		ResponseInfo res = resFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(WorkOrderResponse.builder().responseInfo(res).workOrders(List.of(m)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<WorkOrderResponse> update(@Valid @RequestBody WorkOrderRequest req) {
		WorkOrderModel m = service.update(req);
		ResponseInfo res = resFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(WorkOrderResponse.builder().responseInfo(res).workOrders(List.of(m)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<WorkOrderResponse> search(@RequestBody WorkOrderRequest req) {
		List<WorkOrderModel> list = service.search(req.getWorkOrder());
		ResponseInfo res = resFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(WorkOrderResponse.builder().responseInfo(res).workOrders(list).build(),
				HttpStatus.OK);
	}
}
