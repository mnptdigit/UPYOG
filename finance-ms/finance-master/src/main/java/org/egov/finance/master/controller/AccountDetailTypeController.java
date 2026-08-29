package org.egov.finance.master.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.master.model.AccountDetailTypeModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.AccountDetailTypeRequest;
import org.egov.finance.master.model.response.AccountDetailTypeResponse;
import org.egov.finance.master.service.AccountDetailTypeService;
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
@RequestMapping("/accountdetailtype")
public class AccountDetailTypeController {

	@Autowired
	private AccountDetailTypeService accountDetailTypeService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<AccountDetailTypeResponse> create(@Valid @RequestBody AccountDetailTypeRequest request) {
		AccountDetailTypeModel model = accountDetailTypeService.save(request);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(AccountDetailTypeResponse.builder().responseInfo(resInfo)
				.accountDetailTypes(Arrays.asList(model)).build(), HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<AccountDetailTypeResponse> update(@Valid @RequestBody AccountDetailTypeRequest request) {
		AccountDetailTypeModel model = accountDetailTypeService.update(request);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(AccountDetailTypeResponse.builder().responseInfo(resInfo)
				.accountDetailTypes(Arrays.asList(model)).build(), HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<AccountDetailTypeResponse> search(@RequestBody AccountDetailTypeRequest request) {
		List<AccountDetailTypeModel> list = accountDetailTypeService.search(request.getAccountDetailType());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		return new ResponseEntity<>(
				AccountDetailTypeResponse.builder().responseInfo(resInfo).accountDetailTypes(list).build(),
				HttpStatus.OK);
	}
}
