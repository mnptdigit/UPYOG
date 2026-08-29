package org.egov.finance.master.controller;

import java.util.Collections;
import java.util.List;

import org.egov.finance.master.model.BankModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.BankRequest;
import org.egov.finance.master.model.response.BankResponse;
import org.egov.finance.master.service.BankService;
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
@RequestMapping("/bank")
public class BankController {

	@Autowired
	private BankService bankService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<BankResponse> create(@Valid @RequestBody BankRequest bankRequest) {
		BankModel bankModel = bankService.save(bankRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(bankRequest.getRequestInfo(),
				true);
		return new ResponseEntity<>(
				BankResponse.builder().responseInfo(resInfo).banks(Collections.singletonList(bankModel)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<BankResponse> update(@Valid @RequestBody BankRequest bankRequest) {
		BankModel bankModel = bankService.update(bankRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(bankRequest.getRequestInfo(),
				true);
		return new ResponseEntity<>(
				BankResponse.builder().responseInfo(resInfo).banks(Collections.singletonList(bankModel)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<BankResponse> search(@Valid @RequestBody BankRequest searchRequest) {
		List<BankModel> banks = bankService.search(searchRequest.getBank());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(searchRequest.getRequestInfo(),
				true);
		return new ResponseEntity<>(BankResponse.builder().responseInfo(resInfo).banks(banks).build(), HttpStatus.OK);
	}
}
