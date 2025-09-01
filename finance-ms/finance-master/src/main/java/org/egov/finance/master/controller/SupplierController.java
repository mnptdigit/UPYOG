package org.egov.finance.master.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.SupplierModel;
import org.egov.finance.master.model.request.SupplierRequest;
import org.egov.finance.master.model.response.SupplierResponse;
import org.egov.finance.master.service.SupplierService;
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
@RequestMapping("/supplier")
public class SupplierController {

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
		SupplierModel model = supplierService.save(request);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(
				SupplierResponse.builder().responseInfo(resInfo).suppliers(Arrays.asList(model)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<SupplierResponse> update(@Valid @RequestBody SupplierRequest request) {
		SupplierModel model = supplierService.update(request);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(
				SupplierResponse.builder().responseInfo(resInfo).suppliers(Arrays.asList(model)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<SupplierResponse> search(@RequestBody SupplierRequest request) {
		List<SupplierModel> results = supplierService.search(request.getSupplier());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(SupplierResponse.builder().responseInfo(resInfo).suppliers(results).build(),
				HttpStatus.OK);
	}
}
