package org.egov.finance.master.controller;

import java.util.List;

import org.egov.finance.master.model.PurchaseOrderModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.PurchaseOrderRequest;
import org.egov.finance.master.model.response.PurchaseOrderResponse;
import org.egov.finance.master.service.PurchaseOrderService;
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
@RequestMapping("/purchaseorder")
public class PurchaseOrderController {

	@Autowired
	private PurchaseOrderService service;

	@Autowired
	private ResponseInfoFactory resFactory;

	@PostMapping("/_create")
	public ResponseEntity<PurchaseOrderResponse> create(@Valid @RequestBody PurchaseOrderRequest req) {
		PurchaseOrderModel m = service.save(req);
		ResponseInfo res = resFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				PurchaseOrderResponse.builder().responseInfo(res).purchaseOrders(List.of(m)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<PurchaseOrderResponse> update(@Valid @RequestBody PurchaseOrderRequest req) {
		PurchaseOrderModel m = service.update(req);
		ResponseInfo res = resFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				PurchaseOrderResponse.builder().responseInfo(res).purchaseOrders(List.of(m)).build(), HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<PurchaseOrderResponse> search(@RequestBody PurchaseOrderRequest req) {
		List<PurchaseOrderModel> list = service.search(req.getPurchaseOrder());
		ResponseInfo res = resFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(PurchaseOrderResponse.builder().responseInfo(res).purchaseOrders(list).build(),
				HttpStatus.OK);
	}
}
