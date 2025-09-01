package org.egov.finance.master.controller;

import java.util.List;

import org.egov.finance.master.model.ContractorModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.ContractorRequest;
import org.egov.finance.master.model.response.ContractorResponse;
import org.egov.finance.master.service.ContractorService;
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
@RequestMapping("/contractor")
public class ContractorController {

	@Autowired
	private ContractorService contractorService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_create")
	public ResponseEntity<ContractorResponse> create(@Valid @RequestBody ContractorRequest req) {
		ContractorModel model = contractorService.save(req);
		ResponseInfo ri = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(ContractorResponse.builder().responseInfo(ri).contractors(List.of(model)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<ContractorResponse> update(@Valid @RequestBody ContractorRequest req) {
		ContractorModel model = contractorService.update(req);
		ResponseInfo ri = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(ContractorResponse.builder().responseInfo(ri).contractors(List.of(model)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<ContractorResponse> search(@RequestBody ContractorRequest req) {
		List<ContractorModel> res = contractorService.search(req.getContractor());
		ResponseInfo ri = responseInfoFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(ContractorResponse.builder().responseInfo(ri).contractors(res).build(),
				HttpStatus.OK);
	}
}
