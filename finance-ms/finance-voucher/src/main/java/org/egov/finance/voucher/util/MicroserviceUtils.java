package org.egov.finance.voucher.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.egov.finance.voucher.entity.Designation;
import org.egov.finance.voucher.entity.Voucher;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.model.ApplicationConfigManager;
import org.egov.finance.voucher.model.EmployeeInfo;
import org.egov.finance.voucher.model.EmployeeSearchCriteria;
import org.egov.finance.voucher.model.MasterDetail;
import org.egov.finance.voucher.model.MdmsCriteria;
import org.egov.finance.voucher.model.MdmsCriteriaReq;
import org.egov.finance.voucher.model.ModuleDetail;
import org.egov.finance.voucher.model.RequestInfo;
import org.egov.finance.voucher.model.request.RequestInfoWrapper;
import org.egov.finance.voucher.model.response.EmployeeInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MicroserviceUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicroserviceUtils.class);

	private static final String CLIENT_ID = "client.id";
	private static final int DEFAULT_PAGE_SIZE = 100;

	@Autowired
	private Environment environment;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${egov.hrms.service.endpoint}")
	private String hmrsurl;

	@Value("${egov.services.user.approvers.url}")
	private String approverSrvcUrl;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ApplicationConfigManager appConfigManager;

	/*---- SI user details-----*/
	@Value("${token.authorization.key}")
	private String tokenAuthorizationKey;

	@Value("${si.microservice.user}")
	private String siUser;

	@Value("${si.microservice.password}")
	private String siPassword;

	@Value("${si.microservice.usertype}")
	private String siUserType;

	@Value("${si.microservice.scope}")
	private String siScope;

	@Value("${si.microservice.granttype}")
	private String siGrantType;

	@Value("${egov.services.user.token.url}")
	private String tokenGenUrl;

	@Value("${egov.services.master.mdms.search.url}")
	private String mdmsSearchUrl;

	public String getTenentId() {
		environment.getProperty(CLIENT_ID);
		String userTenantId = ApplicationThreadLocals.getUserTenantId();
		String tenantId = ApplicationThreadLocals.getTenantID();
		// if (isNotBlank(clientId)) {
		// final StringBuilder stringBuilder = new StringBuilder();
		// stringBuilder.append(clientId).append('.').append(tenantId);
		// tenantId = stringBuilder.toString();
		// }

		// If tenantId already includes ".", assume it's in full format and return it
		// directly
		if (StringUtils.isNotBlank(tenantId) && tenantId.contains(".")) {
			return tenantId;
		}

		// If userTenantId is blank, default to "mn"
		if (StringUtils.isBlank(userTenantId)) {
			userTenantId = "mn";
		}

		// If tenantId is not blank, combine with userTenantId
		if (StringUtils.isNotBlank(tenantId)) {
			return userTenantId + "." + tenantId;
		}

		// Fallback: only userTenantId is available
		return userTenantId;
	}

	public String generateAdminToken(String tenantId) {
		final RestTemplate restTemplate = createRestTemplate();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		header.add("Authorization", this.tokenAuthorizationKey);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", this.siUser);
		map.add("scope", this.siScope);
		map.add("password", this.siPassword);
		map.add("grant_type", this.siGrantType);
		map.add("tenantId", tenantId);
		map.add("userType", this.siUserType);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, header);
		try {
			StringBuilder url = new StringBuilder(appConfigManager.getEgovUserSerHost()).append(tokenGenUrl);
			LOGGER.info("call:" + url);
			Object response = restTemplate.postForObject(url.toString(), request, Object.class);
			if (response != null)
				return String.valueOf(((HashMap) response).get("access_token"));
		} catch (RestClientException e) {
			LOGGER.info("Eror while getting admin authtoken", e);
			return null;
		}
		return null;
	}

	public RestTemplate createRestTemplate() {

		return restTemplate;
	}

	public EmployeeInfo getEmployeeByPositionId(Long approverPositionId) {
		EmployeeSearchCriteria criteria = EmployeeSearchCriteria.builder()
				.positions(Collections.singletonList(approverPositionId)).build();

		List<EmployeeInfo> list = getEmployeeBySearchCriteria(criteria);
		return list.isEmpty() ? null : list.get(0);
	}

	public List<EmployeeInfo> getEmployeeBySearchCriteria(EmployeeSearchCriteria criteria) {
		final RestTemplate restTemplate = createRestTemplate();
		StringBuilder url = new StringBuilder(hmrsurl).append(approverSrvcUrl).append("?tenantId=")
				.append(getTenentId());
		this.prepareEmplyeeSearchQueryString(criteria, url);
		RequestInfo requestInfo = new RequestInfo();
		RequestInfoWrapper reqWrapper = new RequestInfoWrapper();
		requestInfo.setAuthToken(ApplicationThreadLocals.getUserToken());
		requestInfo.setTs(new Date().toInstant().toEpochMilli());
		reqWrapper.setRequestInfo(requestInfo);
		EmployeeInfoResponse empResponse = restTemplate.postForObject(url.toString(), reqWrapper,
				EmployeeInfoResponse.class);
		return empResponse.getEmployees();
	}

	private void prepareEmplyeeSearchQueryString(EmployeeSearchCriteria criteria, StringBuilder url) {
		if (criteria.getAsOnDate() != null && criteria.getAsOnDate() != 0) {
			url.append("&asOnDate=").append(criteria.getAsOnDate());
		}
		if (CollectionUtils.isNotEmpty(criteria.getCodes())) {
			url.append("&codes=").append(StringUtils.join(criteria.getCodes(), ","));
		}
		if (CollectionUtils.isNotEmpty(criteria.getNames())) {
			url.append("&names=").append(StringUtils.join(criteria.getNames(), ","));
		}
		if (CollectionUtils.isNotEmpty(criteria.getDepartments())) {
			url.append("&departments=").append(StringUtils.join(criteria.getDepartments(), ","));
		}
		if (CollectionUtils.isNotEmpty(criteria.getDesignations())) {
			url.append("&designations=").append(StringUtils.join(criteria.getDesignations(), ","));
		}
		if (CollectionUtils.isNotEmpty(criteria.getRoles())) {
			url.append("&roles=").append(StringUtils.join(criteria.getRoles(), ","));
		}
		if (CollectionUtils.isNotEmpty(criteria.getIds())) {
			url.append("&ids=").append(StringUtils.join(criteria.getIds(), ","));
		}
		if (CollectionUtils.isNotEmpty(criteria.getEmployeestatuses())) {
			url.append("&employeestatuses=").append(StringUtils.join(criteria.getEmployeestatuses(), ","));
		}
		if (CollectionUtils.isNotEmpty(criteria.getEmployeetypes())) {
			url.append("&employeetypes=").append(StringUtils.join(criteria.getEmployeetypes(), ","));
		}
		if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(criteria.getPositions())) {
			url.append("&positions=").append(StringUtils.join(criteria.getPositions(), ","));
		}
		if (StringUtils.isNotBlank(criteria.getPhone())) {
			url.append("&phone=").append(criteria.getPhone());
		}
		if (criteria.getLimit() != null && criteria.getLimit() != 0) {
			url.append("&limit=").append(criteria.getLimit());
		}

	}

	public List<Designation> getDesignation(String code) {
		List<ModuleDetail> moduleDetailsList = new ArrayList<>();
		try {
			prepareModuleDetails(moduleDetailsList, "common-masters", "Designation", "code", code, String.class);

			Map<String, Object> mdmsResponse = mapper.convertValue(getMdmsData(moduleDetailsList, true, null, null),
					new TypeReference<Map<String, Object>>() {
					});

			if (mdmsResponse != null) {
				return mapper.convertValue(JsonPath.read(mdmsResponse, "$.MdmsRes.common-masters.Designation"),
						new TypeReference<List<Designation>>() {
						});
			}
		} catch (ApplicationRuntimeException e) {
			LOGGER.error("ERROR occurred while fetching designation from MDMS for code: {}", code, e);
		}
		return Collections.emptyList();
	}

	public Object getMdmsData(List<ModuleDetail> moduleDetails, boolean isStateLevel, String tenantId, String token) {
		String mdmsUrl = appConfigManager.getEgovMdmsSerHost() + this.mdmsSearchUrl;
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setAuthToken(ApplicationThreadLocals.getUserToken());
		MdmsCriteria mdmscriteria = new MdmsCriteria();
		if (tenantId == null) {
			if (isStateLevel) {
				mdmscriteria.setTenantId(getTenentId().split(Pattern.quote("."))[0]);
			} else {
				mdmscriteria.setTenantId(getTenentId());
			}
		} else {
			mdmscriteria.setTenantId(tenantId);
		}
		mdmscriteria.setModuleDetails(moduleDetails);
		MdmsCriteriaReq mdmsrequest = new MdmsCriteriaReq();
		mdmsrequest.setRequestInfo(requestInfo);
		mdmsrequest.setMdmsCriteria(mdmscriteria);
		return restTemplate.postForObject(mdmsUrl, mdmsrequest, Map.class);
	}

	private void prepareModuleDetails(List<ModuleDetail> moduleDetailsList, String moduleName, String masterName,
			String filterKey, String filterValue, Class<?> filterType) {

		for (ListIterator<ModuleDetail> it = moduleDetailsList.listIterator(); it.hasNext();) {
			ModuleDetail existing = it.next();
			if (existing.getModuleName().equals(moduleName)) {
				prepareMasterDetails(existing.getMasterDetails(), masterName, filterKey, filterValue, filterType);
				it.set(existing); // replace
				return;
			}
		}

		List<MasterDetail> masterDetails = new ArrayList<>();
		prepareMasterDetails(masterDetails, masterName, filterKey, filterValue, filterType);
		moduleDetailsList.add(new ModuleDetail(moduleName, masterDetails));
	}

	private void prepareMasterDetails(List<MasterDetail> masterDetailList, String masterName, String filterKey,
			String filterValue, Class<?> filterType) {

		String filter = null;
		StringBuilder filterBuilder = new StringBuilder();

		for (ListIterator<MasterDetail> it = masterDetailList.listIterator(); it.hasNext();) {
			MasterDetail md = it.next();
			if (md.getName().equals(masterName)) {
				String oldFilter = md.getFilter();
				if (StringUtils.isNotBlank(filterKey) && StringUtils.isNotBlank(filterValue)) {
					if (StringUtils.isNotBlank(oldFilter)) {
						if (oldFilter.contains(filterKey)) {
							// merge into existing
							String newFilter = oldFilter.substring(0, oldFilter.length() - 3);
							filterBuilder.append(newFilter).append(",").append(getSingleQuote(filterType))
									.append(filterValue).append(getSingleQuote(filterType)).append("])]");
						} else {
							String newFilter = oldFilter.substring(0, oldFilter.length() - 2);
							filterBuilder.append(newFilter).append(" && @.").append(filterKey).append(" in [")
									.append(getSingleQuote(filterType)).append(filterValue)
									.append(getSingleQuote(filterType)).append("])]");
						}
					} else {
						filterBuilder.append("[?(@.").append(filterKey).append(" in [")
								.append(getSingleQuote(filterType)).append(filterValue)
								.append(getSingleQuote(filterType)).append("])]");
					}

					it.remove(); // remove old
					filter = filterBuilder.toString();
					masterDetailList.add(new MasterDetail(masterName, filter));
				}
				return;
			}
		}

// Add new if not present
		if (StringUtils.isNotBlank(filterKey) && StringUtils.isNotBlank(filterValue)) {
			filterBuilder.append("[?(@.").append(filterKey).append(" in [").append(getSingleQuote(filterType))
					.append(filterValue).append(getSingleQuote(filterType)).append("])]");
			filter = filterBuilder.toString();
		}

		masterDetailList.add(new MasterDetail(masterName, filter));
	}

	private String getSingleQuote(Class<?> clazz) {
		return (clazz == String.class) ? "'" : "";
	}

	public List<EmployeeInfo> getEmployee(Long empId, Date toDay, String departmentId, String designationId) {
		EmployeeSearchCriteria creiteria = this.prepareEmployeeSearchQueryBuilder(empId, toDay, departmentId,
				designationId);
		return this.getEmployeeBySearchCriteria(creiteria);
	}

	private EmployeeSearchCriteria prepareEmployeeSearchQueryBuilder(Long empId, Date toDay, String departmentId,
			String designationId) {
		EmployeeSearchCriteria criteria = new EmployeeSearchCriteria().builder().build();
		if (empId != null && empId != 0) {
			criteria.setIds(Collections.singletonList(empId));
		}
		if (toDay != null) {
			criteria.setAsOnDate(new Date().toInstant().toEpochMilli());
		}
		if (departmentId != null && !departmentId.isEmpty()) {
			criteria.setDepartments(Collections.singletonList(departmentId));
		}
		if (designationId != null && !designationId.isEmpty()) {
			criteria.setDesignations(Collections.singletonList(designationId));
		}
		return criteria;
	}

	public EmployeeInfo getEmployeeById(Long empId) {
		List<EmployeeInfo> list = getEmployeeBySearchCriteria(
				new EmployeeSearchCriteria().builder().ids(Collections.singletonList(empId)).build());
		return list.isEmpty() ? null : list.get(0);
	}

}
