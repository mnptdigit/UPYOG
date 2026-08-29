package org.egov.finance.voucher.model.request;

import org.egov.finance.voucher.model.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestInfoWrapper {

	@JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void setRequestInfo(final RequestInfo requestInfo) {
        this.requestInfo = requestInfo;
    }

}
