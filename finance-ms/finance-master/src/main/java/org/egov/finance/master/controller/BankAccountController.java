package org.egov.finance.master.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.master.model.BankaccountModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.BankaccountRequest;
import org.egov.finance.master.model.response.BankaccountResponse;
import org.egov.finance.master.service.BankAccountService;
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

@RequestMapping("/bankaccount")
public class BankAccountController {

	@Autowired
	private BankAccountService bankAccountService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<BankaccountResponse> createBankAccount(@Valid @RequestBody BankaccountRequest request) {
		BankaccountModel model = bankAccountService.save(request);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(
				BankaccountResponse.builder().responseInfo(resInfo).bankaccounts(Arrays.asList(model)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<BankaccountResponse> updateBankAccount(@Valid @RequestBody BankaccountRequest request) {
		BankaccountModel model = bankAccountService.update(request);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(
				BankaccountResponse.builder().responseInfo(resInfo).bankaccounts(Arrays.asList(model)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<BankaccountResponse> searchBankAccounts(@RequestBody BankaccountRequest request) {
		List<BankaccountModel> accounts = bankAccountService.search(request.getBankaccount());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(BankaccountResponse.builder().responseInfo(resInfo).bankaccounts(accounts).build(),
				HttpStatus.OK);
	}
}
