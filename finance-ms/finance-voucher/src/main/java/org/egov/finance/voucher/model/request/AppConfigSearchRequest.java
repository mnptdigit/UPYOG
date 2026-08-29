package org.egov.finance.voucher.model.request;

import org.egov.finance.voucher.customannotation.SafeHtml;

public class AppConfigSearchRequest extends DataTableSearchRequest {
	@SafeHtml
    private String moduleName;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }

}
