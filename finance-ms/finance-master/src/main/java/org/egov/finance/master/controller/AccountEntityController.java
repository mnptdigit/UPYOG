package org.egov.finance.master.controller;

import java.util.List;

import org.egov.finance.master.model.AccountEntityModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.AccountEntityRequest;
import org.egov.finance.master.model.response.AccountEntityResponse;
import org.egov.finance.master.service.AccountEntityService;
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
@RequestMapping("/accountentity")
public class AccountEntityController {

	@Autowired
	private AccountEntityService service;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<AccountEntityResponse> create(@Valid @RequestBody AccountEntityRequest req) {
		AccountEntityModel model = service.save(req);
		ResponseInfo res = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				AccountEntityResponse.builder().responseInfo(res).accountEntities(List.of(model)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<AccountEntityResponse> update(@Valid @RequestBody AccountEntityRequest req) {
		AccountEntityModel model = service.update(req);
		ResponseInfo res = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				AccountEntityResponse.builder().responseInfo(res).accountEntities(List.of(model)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<AccountEntityResponse> search(@RequestBody AccountEntityRequest req) {
		List<AccountEntityModel> list = service.search(req.getAccountEntity());
		ResponseInfo res = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(AccountEntityResponse.builder().responseInfo(res).accountEntities(list).build(),
				HttpStatus.OK);
	}
}
