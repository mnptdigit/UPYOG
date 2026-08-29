package org.egov.finance.voucher.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@Setter
@Getter
@NoArgsConstructor
public class ApplicationConfigManager {

	@Value("${egov.user.service.endpoint:}")
	private String egovUserSerHost;

	@Value("${egov.default.services.endpoint}")
	private String egovSerHost;

	@Value("${egov.mdms.service.endpoint:}")
	private String egovMdmsSerHost;

	public String getEgovUserSerHost() {
		return StringUtils.isNotBlank(egovUserSerHost) ? egovUserSerHost : egovSerHost;
	}

	public String getEgovMdmsSerHost() {
		return StringUtils.isNotBlank(egovMdmsSerHost) ? egovMdmsSerHost : egovSerHost;
	}

}
