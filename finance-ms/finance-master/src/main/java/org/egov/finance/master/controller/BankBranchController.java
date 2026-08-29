package org.egov.finance.master.controller;

import java.util.Collections;
import java.util.List;

import org.egov.finance.master.model.BankbranchModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.BankbranchRequest;
import org.egov.finance.master.model.response.BankbranchResponse;
import org.egov.finance.master.service.BankBranchService;
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
@RequestMapping("/bankbranch")
public class BankBranchController {

	@Autowired
	private BankBranchService bankBranchService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<BankbranchResponse> create(@Valid @RequestBody BankbranchRequest bankBranchRequest) {
		BankbranchModel bankBranch = bankBranchService.save(bankBranchRequest);
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(bankBranchRequest.getRequestInfo(), true);

		return new ResponseEntity<>(BankbranchResponse.builder().responseInfo(responseInfo)
				.bankbranches(Collections.singletonList(bankBranch)).build(), HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<BankbranchResponse> update(@Valid @RequestBody BankbranchRequest bankBranchRequest) {
		BankbranchModel bankBranch = bankBranchService.update(bankBranchRequest);
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(bankBranchRequest.getRequestInfo(), true);

		return new ResponseEntity<>(BankbranchResponse.builder().responseInfo(responseInfo)
				.bankbranches(Collections.singletonList(bankBranch)).build(), HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<BankbranchResponse> search(@Valid @RequestBody BankbranchRequest searchRequest) {
		List<BankbranchModel> branches = bankBranchService.search(searchRequest.getBankbranch());
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(searchRequest.getRequestInfo(), true);

		return new ResponseEntity<>(
				BankbranchResponse.builder().responseInfo(responseInfo).bankbranches(branches).build(), HttpStatus.OK);
	}
}