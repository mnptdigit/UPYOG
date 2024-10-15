package org.egov.pt.service;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Appeal;
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.Document;
import org.egov.pt.models.Institution;
import org.egov.pt.models.Locality;
import org.egov.pt.models.Notice;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.TypeOfRoad;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.NoticeType;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.user.User;
import org.egov.pt.util.CommonUtils;
import org.egov.pt.util.NoticeUtils;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.web.contracts.AppealRequest;
import org.egov.pt.web.contracts.NoticeRequest;
import org.egov.pt.web.contracts.PropertyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@Service
public class EnrichmentService {



	@Autowired
	private PropertyUtil propertyutil;

	@Autowired
	private BoundaryService boundaryService;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private UserService userService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NoticeUtils noticeutil;





	/**
	 * Assigns UUIDs to all id fields and also assigns acknowledgement-number and assessment-number generated from id-gen
	 * @param request  PropertyRequest received for property creation
	 */
	public void enrichCreateRequest(PropertyRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		Property property = request.getProperty();

		property.setAccountId(requestInfo.getUserInfo().getUuid());
		enrichUuidsForPropertyCreate(requestInfo, property);
		setIdgenIds(request);
		enrichBoundary(property, requestInfo);
		enrichRoadType(property, requestInfo);
	}


	public void enrichAppealCreateRequest(AppealRequest request) {

		Appeal appeal = request.getAppeal();
		RequestInfo requestInfo = request.getRequestInfo();
		enrichUuidsForAppealCreate(requestInfo, appeal);
		setIdgenIdsForAppeal(request);
	}


	public void enrichCreateNoticeRequest(NoticeRequest noticeRequest)
	{
		setIdgenIds(noticeRequest);
		if(!noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_FOR_HEARING) && !noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_ENTER_PREMISE) && !noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_FILE_RETURN) && !noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_FOR_PENALTY))
			setCommentIds(noticeRequest.getNotice());
		noticeRequest.getNotice().setAuditDetails(noticeutil.getAuditDetails(noticeRequest.getRequestInfo().getUserInfo().getUuid(), true));
		if(!noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_FOR_HEARING) && !noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_ENTER_PREMISE) && !noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_FILE_RETURN) && !noticeRequest.getNotice().getNoticeType().equals(NoticeType.NOTICE_FOR_PENALTY))
			noticeRequest.getNotice().getNoticeComment().stream().forEach(audt->audt.setAuditDetails(noticeutil.getAuditDetails(noticeRequest.getRequestInfo().getUserInfo().getUuid(), true)));
	}

	private void setCommentIds(Notice noticerequest) {

		noticerequest.getNoticeComment().stream().filter(x-> StringUtils.isEmpty(x.getUuid())).forEach(x-> x.setUuid(UUID.randomUUID().toString()));
	}

	private void setIdgenIds(NoticeRequest noticerequest) {
		// TODO Auto-generated method stub
		String tanetid=noticerequest.getNotice().getTenantId();
		String noticeId = noticeutil.getIdList(noticerequest.getRequestInfo(), tanetid, config.getNoticeidname(), config.getNoticeformat(), 1).get(0);
		noticerequest.getNotice().setNoticeNumber(noticeId);
		noticerequest.getNotice().setNoticeuuid(UUID.randomUUID().toString());
	}


	public void enrichAppealForUpdateRequest(AppealRequest request) {


		enrichUuidsForAppealUpdate(request);

	}

	private void enrichUuidsForPropertyCreate(RequestInfo requestInfo, Property property) {

		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		property.setId(UUID.randomUUID().toString());

		if (!CollectionUtils.isEmpty(property.getDocuments()))
			property.getDocuments().forEach(doc -> {
				doc.setId(UUID.randomUUID().toString());
				doc.setStatus(Status.ACTIVE);
			});

		property.getAddress().setTenantId(property.getTenantId());
		property.getAddress().setId(UUID.randomUUID().toString());

		if (!ObjectUtils.isEmpty(property.getInstitution()))
			property.getInstitution().setId(UUID.randomUUID().toString());

		property.setAuditDetails(propertyAuditDetails);

		if (!CollectionUtils.isEmpty(property.getUnits()))
			property.getUnits().forEach(unit -> {

				unit.setId(UUID.randomUUID().toString());
				unit.setActive(true);
			});

		property.getOwners().forEach(owner -> {

			owner.setOwnerInfoUuid(UUID.randomUUID().toString());
			if (!CollectionUtils.isEmpty(owner.getDocuments()))
				owner.getDocuments().forEach(doc -> {
					if(null!=doc.getDocumentType() && !doc.getDocumentType().isEmpty()) {
						doc.setId(UUID.randomUUID().toString());
						doc.setStatus(Status.ACTIVE);
					}
					
				});

			owner.setStatus(Status.ACTIVE);
		});
	}



	private void enrichUuidsForAppealCreate(RequestInfo requestInfo, Appeal appeal) {

		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		appeal.setId(UUID.randomUUID().toString());
		appeal.setAuditDetails(propertyAuditDetails);

		if (!CollectionUtils.isEmpty(appeal.getDocuments()))
			appeal.getDocuments().forEach(doc -> {
				doc.setId(UUID.randomUUID().toString());
				doc.setStatus(Status.ACTIVE);
			});
		appeal.getDocuments().stream().forEach(audit->audit.setAuditDetails(propertyAuditDetails));
	}


	private void enrichUuidsForAppealUpdate(AppealRequest request) {

		RequestInfo requestInfo = request.getRequestInfo();
		Appeal appeal = request.getAppeal();
		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		if(!CollectionUtils.isEmpty(appeal.getDocuments()))
		{
			List<Document> doc=request.getAppeal().getDocuments();
			doc=doc.stream().filter(d->d.getId()==null || StringUtils.isEmpty(d.getId())).collect(Collectors.toList());
			request.getAppeal().setDocuments(doc);
		}
		if (!CollectionUtils.isEmpty(appeal.getDocuments()))
			appeal.getDocuments().forEach(doc -> {
				if(null==doc.getId()) {
					doc.setId(UUID.randomUUID().toString());
					doc.setStatus(Status.ACTIVE);
				}	
			});

		appeal.setAuditDetails(propertyAuditDetails);
	}

	/**
	 * Assigns UUID for new fields that are added and sets propertyDetail and address id from propertyId
	 * 
	 * @param request  PropertyRequest received for property update
	 * @param propertyFromDb Properties returned from DB
	 */
	public void enrichUpdateRequest(PropertyRequest request,Property propertyFromDb) {

		Property property = request.getProperty();
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetailsForUpdate = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		propertyFromDb.setAuditDetails(auditDetailsForUpdate);


		Boolean isWfEnabled = config.getIsWorkflowEnabled();
		Boolean iswfStarting = propertyFromDb.getStatus().equals(Status.ACTIVE);

		if (!isWfEnabled) {

			property.setStatus(Status.ACTIVE);
			property.getAddress().setId(propertyFromDb.getAddress().getId());

		} else if (isWfEnabled && iswfStarting) {

			enrichPropertyForNewWf(requestInfo, property, false);
		}

		if (!CollectionUtils.isEmpty(property.getDocuments()))
			property.getDocuments().forEach(doc -> {

				if (doc.getId() == null) {
					doc.setId(UUID.randomUUID().toString());
					doc.setStatus(Status.ACTIVE);
				}
			});

		if (!CollectionUtils.isEmpty(property.getUnits()))
			property.getUnits().forEach(unit -> {

				if (unit.getId() == null) {
					unit.setId(UUID.randomUUID().toString());
					unit.setActive(true);
				}
			});

		Institution institute = property.getInstitution();
		if (!ObjectUtils.isEmpty(institute) && null == institute.getId())
			property.getInstitution().setId(UUID.randomUUID().toString());

		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		property.setAuditDetails(auditDetails);
		property.setAccountId(propertyFromDb.getAccountId());

		property.setAdditionalDetails(
				propertyutil.jsonMerge(propertyFromDb.getAdditionalDetails(), property.getAdditionalDetails()));
	}


	public void enrichUpdateRequestForAmalgamation(PropertyRequest request,Property propertyFromDb) {

		Property property = request.getProperty();
		RequestInfo requestInfo = request.getRequestInfo();
		AuditDetails auditDetailsForUpdate = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		propertyFromDb.setAuditDetails(auditDetailsForUpdate);


		Boolean isWfEnabled = config.getIsWorkflowEnabled();
		Boolean iswfStarting = propertyFromDb.getStatus().equals(Status.ACTIVE);

		if (!isWfEnabled) {

			property.setStatus(Status.ACTIVE);
			property.getAddress().setId(propertyFromDb.getAddress().getId());

			@SuppressWarnings("unchecked")
			Map<String, Object> additionalDetails = mapper.convertValue(propertyFromDb.getAdditionalDetails(), Map.class);
			additionalDetails.put(PTConstants.CREATED_FROM_PROPERTY, propertyFromDb.getPropertyId());
			additionalDetails.put(PTConstants.AMALGAMATED_PROPERTY, property.getAmalgamatedProperty());
			JsonNode node=mapper.convertValue(additionalDetails, JsonNode.class);
			property.setAdditionalDetails(node);

		} else if (isWfEnabled && iswfStarting) {

			enrichPropertyForNewWf(requestInfo, property, false);

			@SuppressWarnings("unchecked")
			Map<String, Object> additionalDetails = mapper.convertValue(propertyFromDb.getAdditionalDetails(), Map.class);
			additionalDetails.put(PTConstants.CREATED_FROM_PROPERTY, propertyFromDb.getPropertyId());
			additionalDetails.put(PTConstants.AMALGAMATED_PROPERTY, property.getAmalgamatedProperty());
			JsonNode node=mapper.convertValue(additionalDetails, JsonNode.class);
			property.setAdditionalDetails(node);

		}

		if (!CollectionUtils.isEmpty(property.getDocuments()))
			property.getDocuments().forEach(doc -> {

				if (doc.getId() == null) {
					doc.setId(UUID.randomUUID().toString());
					doc.setStatus(Status.ACTIVE);
				}
			});

		if (!CollectionUtils.isEmpty(property.getUnits()))
			property.getUnits().forEach(unit -> {

				if (unit.getId() == null) {
					unit.setId(UUID.randomUUID().toString());
					unit.setActive(true);
				}
			});

		Institution institute = property.getInstitution();
		if (!ObjectUtils.isEmpty(institute) && null == institute.getId())
			property.getInstitution().setId(UUID.randomUUID().toString());

		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		property.setAuditDetails(auditDetails);
		property.setAccountId(propertyFromDb.getAccountId());

		//Setting The Property ID

		/*
		 * @SuppressWarnings("unchecked") Map<String, Object> additionalDetails =
		 * mapper.convertValue(propertyFromDb.getAdditionalDetails(), Map.class);
		 * additionalDetails.put("amalgamtedProperty",
		 * request.getProperty().getAmalgamatedProperty()); JsonNode
		 * node=mapper.convertValue(additionalDetails, JsonNode.class);
		 * propertyFromDb.setAdditionalDetails(node);
		 */

		property.setAdditionalDetails(
				propertyutil.jsonMerge(propertyFromDb.getAdditionalDetails(), property.getAdditionalDetails()));
	}



	/**
	 * Sets the acknowledgement and assessment Numbers for given PropertyRequest
	 * 
	 * @param request PropertyRequest which is to be created
	 */
	private void setIdgenIds(PropertyRequest request) {

		Property property = request.getProperty();
		String tenantId = property.getTenantId();
		RequestInfo requestInfo = request.getRequestInfo();

		if (!config.getIsWorkflowEnabled()) {

			property.setStatus(Status.ACTIVE);
		}


		String pId = propertyutil.getIdList(requestInfo, tenantId, config.getPropertyIdGenName(), config.getPropertyIdGenFormat(), 1).get(0);
		String ackNo = propertyutil.getIdList(requestInfo, tenantId, config.getAckIdGenName(), config.getAckIdGenFormat(), 1).get(0);
		if(!config.isDefaultPropertyId()) {

			String hyphe = "-";
			StringBuffer sb =  new StringBuffer();
			StringBuffer finalSb = new StringBuffer();

			List<String> masterNames = new ArrayList<>(
					Arrays.asList("tenants"));

			Map<String, List<String>> codes = propertyutil.getAttributeValues(config.getStateLevelTenantId(), "tenant", masterNames,
					"[?(@.city.districtTenantCode== '"+request.getProperty().getTenantId()+"')].city.code", "$.MdmsRes.tenant", request.getRequestInfo());

			List<String> masterNamesOwn = new ArrayList<>(
					Arrays.asList("OwnerShipCategory"));

			Map<String, List<String>> codesOwn = propertyutil.getAttributeValues(config.getStateLevelTenantId(), "PropertyTax", masterNamesOwn,
					"[?(@.code=='"+request.getProperty().getOwnershipCategory()+"')].OwnerShipCategoryCode", "$.MdmsRes.PropertyTax", request.getRequestInfo());
			String Districtcode=districtCodemap(tenantId,property);
			String cityCode = codes.get("tenants").get(0);
			//String ownerShipCode = codesOwn.get("OwnerShipCategory").get(0);
			sb.append(Districtcode);
			sb.append(cityCode);
			//sb.append(request.getProperty().getAddress().getLocality().getCode());
			//sb.append(ownerShipCode);
			String code=request.getProperty().getAddress().getLocality().getCode();
			if(!request.getProperty().getTenantId().equalsIgnoreCase("mn.imphal"))
				code=code.replace("WD", "");
			String lekaicode=code.substring(code.length()-3);
			String wardcode=code.replace(lekaicode, "");
			if(wardcode.length()==1)
				wardcode="0"+wardcode;
			String ownership=request.getProperty().getOwnershipCategory();
			ownership=ownership.substring(ownership.length()-10);
			if(ownership.equalsIgnoreCase("GOVERNMENT"))
				ownership="0";
			else
				ownership="1";
			sb.append(wardcode).append(lekaicode).append(ownership);
			String[] serialnumber=pId.split("-");
			String propid="MN"+serialnumber[serialnumber.length-1];
			sb.append(propid);
			//String[] propId = pId.split("PT");

			//finalSb.append(propId[0]).append("PT-").append(sb).append(propId[1]);
			//sb.append(propId[1]).append(hyphe);

			//pId = finalSb.toString();
			pId=sb.toString();
			System.out.println(pId);



		}
		property.setPropertyId(pId);
		property.setAcknowldgementNumber(ackNo);
	}


	private String districtCodemap(String tenantId, Property property) {
		// TODO Auto-generated method stub
		if(tenantId.equalsIgnoreCase("mn.nambol"))
		{
			if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Nambol Mongjing Leikai"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Phoijing Awang Leikai"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Utlou Heibi Makhong"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Khajri Maning Leikai-ward 1"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Khajri Maning Leikai-ward 2"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Khajri Mamang Leikai"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Phoijing Chingning"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Thangtek"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Hidakshungba"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Tera makhong-ward 18"))
				return "2";
			else if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Tera makhong-ward 3"))
				return "2";
			else
				return "3";
		}
		else if(tenantId.equalsIgnoreCase("mn.samurou"))
		{
			if(property.getAddress().getLocality().getArea().equalsIgnoreCase("Lilong Haoreibi Makha Leikai"))
				return "6";
			else
				return "2";
		}
		else if(tenantId.equalsIgnoreCase("mn.imphal"))
		{
			return "7";
		}
		else
		{
			Map<String, String> distCity=new HashMap<String, String>();
			distCity.put("mn.thoubal", "Thoubal");
			distCity.put("mn.lilongthoubal", "Thoubal");
			distCity.put("mn.kakching", "Kakching");
			distCity.put("mn.mayangimphal", "Imphal West");
			distCity.put("mn.bishnupur", "Bishnupur");
			distCity.put("mn.kakchingkhunou", "Kakching");
			distCity.put("mn.lilongimphal", "Imphal West");
			distCity.put("mn.moirang", "Bishnupur");
			distCity.put("mn.ningthoukhong", "Bishnupur");
			distCity.put("mn.yairipok", "Thoubal");
			distCity.put("mn.kumbi", "Bishnupur");
			distCity.put("mn.wangoi", "Imphal West");
			distCity.put("mn.andro", "Imphal East");
			distCity.put("mn.kwakta", "Bishnupur");
			distCity.put("mn.lamsang", "Imphal West");
			distCity.put("mn.wangjinglamding", "Thoubal");
			distCity.put("mn.shikhongsekmai", "Thoubal");
			distCity.put("mn.jiribam", "Jiribam");
			distCity.put("mn.oinam", "Bishnupur");
			distCity.put("mn.sugnu", "Kakching");
			distCity.put("mn.sekmai", "Imphal West");
			distCity.put("mn.lamlai", "Imphal East");
			distCity.put("mn.heirok", "Thoubal");
			distCity.put("mn.thongkhongluxmi", "Imphal West");
			distCity.put("mn.lilongimphalwest", "Imphal West");

			String district=distCity.get(tenantId);

			if(district.equalsIgnoreCase("Imphal East"))
				return "1";
			else if(district.equalsIgnoreCase("Imphal West"))
				return "2";
			else if(district.equalsIgnoreCase("Bishnupur"))
				return "3";
			else if(district.equalsIgnoreCase("Jiribam"))
				return "4";
			else if(district.equalsIgnoreCase("Kakching"))
				return "5";
			else if(district.equalsIgnoreCase("Thoubal"))
				return "6";
		}

		return null;
	}


	/**
	 * Returns PropertyCriteria with ids populated using propertyids from properties
	 * @param properties properties whose propertyids are to added to propertyCriteria for search
	 * @return propertyCriteria to search on basis of propertyids
	 */
	public PropertyCriteria getPropertyCriteriaFromPropertyIds(List<Property> properties) {

		PropertyCriteria criteria = new PropertyCriteria();
		Set<String> propertyids = new HashSet<>();
		properties.forEach(property -> propertyids.add(property.getPropertyId()));
		criteria.setPropertyIds(propertyids);
		criteria.setTenantId(properties.get(0).getTenantId());
		return criteria;
	}

	/**
	 *  Enriches the locality object
	 * @param property The property object received for create or update
	 */
	public void enrichBoundary(Property property, RequestInfo requestInfo){

		boundaryService.getAreaType(property, requestInfo, PTConstants.BOUNDARY_HEIRARCHY_CODE);
	}

	public void enrichRoadType(Property property, RequestInfo requestInfo){
		String tenantId = property.getTenantId();
		List<String> masterNames = new ArrayList<>(Arrays.asList(PTConstants.MDMS_PT_ROADTYPE));
		String filter = "$.*.[?(@.code=="+"'"+property.getAddress().getTypeOfRoad().getCode()+"')]";
		Map<String,List<String>> mdmsRet = propertyutil.getAttributeValues(tenantId, PTConstants.MDMS_PT_MOD_NAME, 
				masterNames, filter, PTConstants.JSONPATH_CODES, requestInfo);
		ObjectMapper mapper = new ObjectMapper();
		DocumentContext context = JsonPath.parse(mdmsRet.get(PTConstants.MDMS_PT_ROADTYPE));
		ArrayList roadType = context.read("$.*");
		TypeOfRoad rt = mapper.convertValue(roadType.get(0), TypeOfRoad.class);
		property.getAddress().setTypeOfRoad(rt);
	}

	/**
	 * 
	 * Enrichment method for mutation request
	 * 
	 * @param request
	 */
	public void enrichMutationRequest(PropertyRequest request, Property propertyFromSearch) {

		RequestInfo requestInfo = request.getRequestInfo();
		Property property = request.getProperty();
		Boolean isWfEnabled = config.getIsMutationWorkflowEnabled();
		Boolean iswfStarting = propertyFromSearch.getStatus().equals(Status.ACTIVE);
		AuditDetails auditDetailsForUpdate = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		propertyFromSearch.setAuditDetails(auditDetailsForUpdate);

		if (!isWfEnabled) {

			property.setStatus(Status.ACTIVE);

		} else if (isWfEnabled && iswfStarting) {

			enrichPropertyForNewWf(requestInfo, property, true);
		}

		property.getOwners().forEach(owner -> {

			if (owner.getOwnerInfoUuid() == null) {

				owner.setOwnerInfoUuid(UUID.randomUUID().toString());
				owner.setStatus(Status.ACTIVE);
			}

			if (!CollectionUtils.isEmpty(owner.getDocuments()))
				owner.getDocuments().forEach(doc -> {
					if (doc.getId() == null && null!=doc.getDocumentType() && !doc.getDocumentType().isEmpty() ) {
						doc.setId(UUID.randomUUID().toString());
						doc.setStatus(Status.ACTIVE);
					}
				});
		});
		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		property.setAuditDetails(auditDetails);
	}


	public void enrichBiFurcationRequest(PropertyRequest request, Property propertyFromSearch) {

		RequestInfo requestInfo = request.getRequestInfo();
		Property property = request.getProperty();
		Boolean isWfEnabled = config.getIsMutationWorkflowEnabled();
		Boolean iswfStarting = propertyFromSearch.getStatus().equals(Status.ACTIVE);
		AuditDetails auditDetailsForUpdate = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		propertyFromSearch.setAuditDetails(auditDetailsForUpdate);

		if (!isWfEnabled) {

			property.setStatus(Status.ACTIVE);

		} else if (isWfEnabled && iswfStarting) {

			enrichPropertyForBifurcation(requestInfo, property);
		}

		property.getOwners().forEach(owner -> {

			if (owner.getOwnerInfoUuid() == null) {

				owner.setOwnerInfoUuid(UUID.randomUUID().toString());
				owner.setStatus(Status.ACTIVE);
			}

			if (!CollectionUtils.isEmpty(owner.getDocuments()))
				owner.getDocuments().forEach(doc -> {
					if (doc.getId() == null) {
						doc.setId(UUID.randomUUID().toString());
						doc.setStatus(Status.ACTIVE);
					}
				});
		});
		AuditDetails auditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid().toString(), true);
		property.setAuditDetails(auditDetails);
	}

	/**
	 * enrich property as new entry for workflow validation
	 * 
	 * @param requestInfo
	 * @param property
	 */
	private void enrichPropertyForNewWf(RequestInfo requestInfo, Property property, Boolean isMutation) {

		String ackNo;
		String pId ="";
		if (isMutation) {
			ackNo = propertyutil.getIdList(requestInfo, property.getTenantId(), config.getMutationIdGenName(), config.getMutationIdGenFormat(), 1).get(0);
		} else
			ackNo = propertyutil.getIdList(requestInfo, property.getTenantId(), config.getAckIdGenName(), config.getAckIdGenFormat(), 1).get(0);

		if(property.getCreationReason().equals(CreationReason.UPDATE))
			pId = property.getPropertyId();
		else {
			pId=getPorpertyIdForAmBiMt(requestInfo,property);
		}
		property.setPropertyId(pId);
		property.setId(UUID.randomUUID().toString());
		property.setAcknowldgementNumber(ackNo);

		enrichUuidsForNewUpdate(requestInfo, property);
	}
	
	//Creating new Property for Mutation, Amalgamation, Bifurcation
	public String getPorpertyIdForAmBiMt(RequestInfo request, Property property) {


		String hyphe = "-";
		StringBuffer sb =  new StringBuffer();
		String pId = propertyutil.getIdList(request, property.getTenantId(), config.getPropertyIdGenName(), config.getPropertyIdGenFormat(), 1).get(0);
		List<String> masterNames = new ArrayList<>(
				Arrays.asList("tenants"));
		Map<String, List<String>> codes = propertyutil.getAttributeValues(config.getStateLevelTenantId(), "tenant", masterNames,
				"[?(@.city.districtTenantCode== '"+property.getTenantId()+"')].city.code", "$.MdmsRes.tenant", request);

		String Districtcode=districtCodemap(property.getTenantId(),property);
		String cityCode = codes.get("tenants").get(0);
		sb.append(Districtcode);
		sb.append(cityCode);
		String code=property.getAddress().getLocality().getCode();
		if(!property.getTenantId().equalsIgnoreCase("mn.imphal"))
			code=code.replace("WD", "");
		String lekaicode=code.substring(code.length()-3);
		String wardcode=code.replace(lekaicode, "");
		if(wardcode.length()==1)
			wardcode="0"+wardcode;
		String ownership=property.getOwnershipCategory();
		ownership=ownership.substring(ownership.length()-10);
		if(ownership.equalsIgnoreCase("GOVERNMENT"))
			ownership="0";
		else
			ownership="1";
		sb.append(wardcode).append(lekaicode).append(ownership);
		String[] serialnumber=pId.split("-");
		String propid="MN"+serialnumber[serialnumber.length-1];
		sb.append(propid);
		pId=sb.toString();
		System.out.println(pId);

		return pId;

	
	}

	private void enrichPropertyForBifurcation(RequestInfo requestInfo, Property property) {

		String ackNo;
		ackNo = propertyutil.getIdList(requestInfo, property.getTenantId(), config.getBifurcationIdGenName(), config.getBifurcationIdGenFormat(), 1).get(0);
		property.setId(UUID.randomUUID().toString());
		property.setAcknowldgementNumber(ackNo);

		enrichUuidsForNewUpdate(requestInfo, property);
	}

	private void enrichUuidsForNewUpdate(RequestInfo requestInfo, Property property) {

		AuditDetails propertyAuditDetails = propertyutil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		property.setId(UUID.randomUUID().toString());

		if (!CollectionUtils.isEmpty(property.getDocuments()))
			property.getDocuments().forEach(doc -> {
				doc.setId(UUID.randomUUID().toString());
				if (null == doc.getStatus())
					doc.setStatus(Status.ACTIVE);
			});

		property.getAddress().setTenantId(property.getTenantId());
		property.getAddress().setId(UUID.randomUUID().toString());

		if (!ObjectUtils.isEmpty(property.getInstitution()))
			property.getInstitution().setId(UUID.randomUUID().toString());

		property.setAuditDetails(propertyAuditDetails);

		if (!CollectionUtils.isEmpty(property.getUnits()))
			property.getUnits().forEach(unit -> {

				unit.setId(UUID.randomUUID().toString());
				unit.setActive(unit.getActive());
			});

		property.getOwners().forEach(owner -> {

			owner.setOwnerInfoUuid(UUID.randomUUID().toString());
			if (!CollectionUtils.isEmpty(owner.getDocuments()))
				owner.getDocuments().forEach(doc -> {
					doc.setId(UUID.randomUUID().toString());
					if (null == doc.getStatus())
						doc.setStatus(Status.ACTIVE);
				});
			if (null == owner.getStatus())
				owner.setStatus(Status.ACTIVE);
		});
	}

	/**
	 * In case of SENDBACKTOCITIZEN enrich the assignee with the owners and creator of property
	 * @param property to be enriched
	 */
	public void enrichAssignes(Property property){

		if(config.getIsWorkflowEnabled() && property.getWorkflow().getAction().equalsIgnoreCase(PTConstants.CITIZEN_SENDBACK_ACTION)){

			List<OwnerInfo> assignes = new LinkedList<>();

			// Adding owners to assignes list
			property.getOwners().forEach(ownerInfo -> {
				assignes.add(ownerInfo);
			});

			// Adding creator of application
			if(property.getAccountId()!=null)
				assignes.add(OwnerInfo.builder().uuid(property.getAccountId()).build());

			Set<OwnerInfo> registeredUsers = userService.getUUidFromUserName(property);

			if(!CollectionUtils.isEmpty(registeredUsers))
				assignes.addAll(registeredUsers);

			property.getWorkflow().setAssignes(assignes);
		}
	}


	private void setIdgenIdsForAppeal(AppealRequest request) {

		Appeal appeal = request.getAppeal();
		String tenantId = appeal.getTenantId();
		RequestInfo requestInfo = request.getRequestInfo();

		String pId = propertyutil.getIdList(requestInfo, tenantId, config.getAppealidname(), config.getAppealidformat(), 1).get(0);
		String ackNo = propertyutil.getIdList(requestInfo, tenantId, config.getAckIdGenName(), config.getAckIdGenFormat(), 1).get(0);
		appeal.setAppealId(pId);
		appeal.setAcknowldgementNumber(ackNo);
	}


}
