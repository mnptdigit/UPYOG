package org.egov.finance.master.controller;

import java.util.List;

import org.egov.finance.master.model.RecoveryModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.RecoveryRequest;
import org.egov.finance.master.model.response.RecoveryResponse;
import org.egov.finance.master.service.RecoveryService;
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
@RequestMapping("/recovery")
public class RecoveryController {
	@Autowired
	private RecoveryService service;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<RecoveryResponse> create(@Valid @RequestBody RecoveryRequest req) {
		RecoveryModel model = service.save(req);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(RecoveryResponse.builder().responseInfo(resInfo).recoveries(List.of(model)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<RecoveryResponse> update(@Valid @RequestBody RecoveryRequest req) {
		RecoveryModel model = service.update(req);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(RecoveryResponse.builder().responseInfo(resInfo).recoveries(List.of(model)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<RecoveryResponse> search(@RequestBody RecoveryRequest req) {
		List<RecoveryModel> models = service.search(req.getRecovery());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(RecoveryResponse.builder().responseInfo(resInfo).recoveries(models).build(),
				HttpStatus.OK);
	}
}
