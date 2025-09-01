package org.egov.finance.master.controller;

import java.util.List;

import org.egov.finance.master.model.ChartOfAccountDetailModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.CChartOfAccountDetailRequest;
import org.egov.finance.master.model.response.CChartOfAccountDetailResponse;
import org.egov.finance.master.service.ChartOfAccountDetailService;
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
@RequestMapping("/chartofaccountdetail")
public class ChartOfAccountDetailController {

	@Autowired
	private ChartOfAccountDetailService service;
	@Autowired
	private ResponseInfoFactory responseFactory;

	@PostMapping("/_create")
	public ResponseEntity<CChartOfAccountDetailResponse> create(@Valid @RequestBody CChartOfAccountDetailRequest req) {
		ChartOfAccountDetailModel m = service.save(req);
		ResponseInfo res = responseFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				CChartOfAccountDetailResponse.builder().responseInfo(res).chartOfAccountDetails(List.of(m)).build(),
				HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<CChartOfAccountDetailResponse> update(@Valid @RequestBody CChartOfAccountDetailRequest req) {
		ChartOfAccountDetailModel m = service.update(req);
		ResponseInfo res = responseFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				CChartOfAccountDetailResponse.builder().responseInfo(res).chartOfAccountDetails(List.of(m)).build(),
				HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<CChartOfAccountDetailResponse> search(@RequestBody CChartOfAccountDetailRequest req) {
		List<ChartOfAccountDetailModel> list = service.search(req.getChartOfAccountDetail());
		ResponseInfo res = responseFactory.createResponseInfoFromRequestInfo(req.getRequestInfo(), true);
		return new ResponseEntity<>(
				CChartOfAccountDetailResponse.builder().responseInfo(res).chartOfAccountDetails(list).build(),
				HttpStatus.OK);
	}
}
