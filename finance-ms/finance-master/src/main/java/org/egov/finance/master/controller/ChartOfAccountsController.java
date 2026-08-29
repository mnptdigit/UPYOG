package org.egov.finance.master.controller;

import java.util.List;

import org.egov.finance.master.model.ChartOfAccountsModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.ChartOfAccountsRequest;
import org.egov.finance.master.model.response.ChartOfAccountsResponse;
import org.egov.finance.master.service.ChartOfAccountsService;
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
@RequestMapping("/chartofaccounts")
public class ChartOfAccountsController {

	@Autowired
	private ChartOfAccountsService service;
	@Autowired
	private ResponseInfoFactory responseFactory;

	@PostMapping("/_create")
	public ResponseEntity<ChartOfAccountsResponse> create(@Valid @RequestBody ChartOfAccountsRequest req) {
		ChartOfAccountsModel m = service.save(req);
		ResponseInfo res = responseFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				ChartOfAccountsResponse.builder().responseInfo(res).chartOfAccounts(List.of(m)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<ChartOfAccountsResponse> update(@Valid @RequestBody ChartOfAccountsRequest req) {
		ChartOfAccountsModel m = service.update(req);
		ResponseInfo res = responseFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				ChartOfAccountsResponse.builder().responseInfo(res).chartOfAccounts(List.of(m)).build(), HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<ChartOfAccountsResponse> search(@RequestBody ChartOfAccountsRequest req) {
		List<ChartOfAccountsModel> list = service.search(req.getChartOfAccount());
		ResponseInfo res = responseFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(ChartOfAccountsResponse.builder().responseInfo(res).chartOfAccounts(list).build(),
				HttpStatus.OK);
	}
}
