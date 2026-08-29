/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.master.util;

import java.util.ArrayList;
import java.util.List;

import org.egov.finance.master.model.AccountDetailTypeModel;
import org.egov.finance.master.model.AccountEntityModel;
import org.egov.finance.master.model.BankModel;
import org.egov.finance.master.model.BankaccountModel;
import org.egov.finance.master.model.BankbranchModel;
import org.egov.finance.master.model.ChartOfAccountDetailModel;
import org.egov.finance.master.model.ChartOfAccountsModel;
import org.egov.finance.master.model.ContractorModel;
import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.model.PurchaseOrderModel;
import org.egov.finance.master.model.RecoveryModel;
import org.egov.finance.master.model.SchemeModel;

import org.egov.finance.master.model.SubSchemeModel;
import org.egov.finance.master.model.SupplierModel;
import org.egov.finance.master.model.WorkOrderModel;
import org.egov.finance.master.service.CacheEvictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

	private CacheEvictionService cacheEvictionService;

	@Autowired
	public CacheConfig(CacheEvictionService cacheEvictionService) {
		this.cacheEvictionService = cacheEvictionService;
	}

	@Bean(MasterConstants.FUND_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator fundSearchKeyGenerator() {

		return (target, method, params) -> {
			FundModel criteria = (FundModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.FUND_SEARCH_REDIS_CACHE_VERSION_KEY);
			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "identifier", criteria.getIdentifier());
			addIfNotNull(parts, "llevel", criteria.getLlevel());
			addIfNotNull(parts, "parentId", criteria.getParentId());
			addIfNotNull(parts, "isnotleaf", criteria.getIsnotleaf());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.SCHEME_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator schemeSearchKeyGenerator() {
		return (target, method, params) -> {
			SchemeModel criteria = (SchemeModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.SCHEME_SEARCH_REDIS_CACHE_VERSION_KEY);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "fundId", criteria.getFundId());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "validfrom",
					criteria.getValidfrom() != null ? criteria.getValidfrom().getTime() : null);
			addIfNotNull(parts, "validto", criteria.getValidto() != null ? criteria.getValidto().getTime() : null);
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.SUBSCHEME_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator subschemeSearchKeyGenerator() {
		return (target, method, params) -> {
			SubSchemeModel criteria = (SubSchemeModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.SUBSCHEME_SEARCH_REDIS_CACHE_VERSION_KEY);
			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "schemeid", criteria.getScheme());
			addIfNotNull(parts, "validfrom", criteria.getValidfrom());
			addIfNotNull(parts, "validto", criteria.getValidto());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};

	}

	@Bean(MasterConstants.FUNCTION_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator functionSearchKeyGenerator() {
		return (target, method, params) -> {
			FunctionModel criteria = (FunctionModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY);
			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "llevel", criteria.getLlevel());
			addIfNotNull(parts, "parentId", criteria.getParentId());
			addIfNotNull(parts, "isNotLeaf", criteria.getIsNotLeaf());
			addIfNotNull(parts, "isaAtive", criteria.getIsActive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.BANK_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator bankSearchKeyGenerator() {
		return (target, method, params) -> {
			BankModel criteria = (BankModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.BANK_SEARCH_REDIS_CACHE_VERSION_KEY);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "type", criteria.getType());
			addIfNotNull(parts, "description", criteria.getNarration()); // narration is used as description here
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.BANK_BRANCH_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator bankBranchSearchKeyGenerator() {
		return (target, method, params) -> {
			BankbranchModel criteria = (BankbranchModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.BANK_BRANCH_SEARCH_REDIS_KEY_GENERATOR);
			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "bankId", criteria.getBankId());
			addIfNotNull(parts, "branchcode", criteria.getBranchcode());
			addIfNotNull(parts, "branchname", criteria.getBranchname());
			addIfNotNull(parts, "address1", criteria.getBranchaddress1());
			addIfNotNull(parts, "address2", criteria.getBranchaddress2());
			addIfNotNull(parts, "city", criteria.getBranchcity());
			addIfNotNull(parts, "state", criteria.getBranchstate());
			addIfNotNull(parts, "pincode", criteria.getBranchpin());
			addIfNotNull(parts, "phone", criteria.getBranchphone());
			addIfNotNull(parts, "fax", criteria.getBranchfax());
			addIfNotNull(parts, "contactperson", criteria.getContactperson());
			addIfNotNull(parts, "micr", criteria.getBranchMICR());
			addIfNotNull(parts, "narration", criteria.getNarration());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator bankAccountSearchKeyGenerator() {
		return (target, method, params) -> {
			BankaccountModel criteria = (BankaccountModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.BANK_ACCOUNT_SEARCH_REDIS_KEY_GENERATOR);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "branchId", criteria.getBankbranchId());
			addIfNotNull(parts, "accountnumber", criteria.getAccountnumber());
			addIfNotNull(parts, "accounttype", criteria.getAccounttype());
			addIfNotNull(parts, "narration", criteria.getNarration());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "glcodeid", criteria.getGlcodeId());
			addIfNotNull(parts, "fundid", criteria.getFundId());
			addIfNotNull(parts, "type", criteria.getType());
			addIfNotNull(parts, "payto", criteria.getPayTo());
			addIfNotNull(parts, "chequeformatid", criteria.getChequeformatId());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator accountDetailTypeSearchKeyGenerator() {
		return (target, method, params) -> {
			AccountDetailTypeModel criteria = (AccountDetailTypeModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_KEY_GENERATOR);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "description", criteria.getDescription());
			addIfNotNull(parts, "tableName", criteria.getTablename());
			addIfNotNull(parts, "columname", criteria.getColumnname());
			addIfNotNull(parts, "attributename", criteria.getAttributename());
			addIfNotNull(parts, "nbroflevels", criteria.getNbroflevels());
			addIfNotNull(parts, "fullyQualifiedName", criteria.getFullyQualifiedName());
			addIfNotNull(parts, "active", criteria.getActive());

			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.SUPPLIER_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator supplierSearchKeyGenerator() {
		return (target, method, params) -> {
			SupplierModel criteria = (SupplierModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.SUPPLIER_SEARCH_REDIS_KEY_GENERATOR);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "correspondenceAddress", criteria.getCorrespondenceAddress());
			addIfNotNull(parts, "paymentAddress", criteria.getPaymentAddress());
			addIfNotNull(parts, "contactPerson", criteria.getContactPerson());
			addIfNotNull(parts, "email", criteria.getEmail());
			addIfNotNull(parts, "narration", criteria.getNarration());
			addIfNotNull(parts, "panNumber", criteria.getPanNumber());
			addIfNotNull(parts, "tinNumber", criteria.getTinNumber());
			addIfNotNull(parts, "bankId", criteria.getBankId());
			addIfNotNull(parts, "ifscCode", criteria.getIfscCode());
			addIfNotNull(parts, "bankAccount", criteria.getBankAccount());
			addIfNotNull(parts, "mobileNumber", criteria.getMobileNumber());
			addIfNotNull(parts, "registrationNumber", criteria.getRegistrationNumber());
			addIfNotNull(parts, "statusId", criteria.getStatusId());
			addIfNotNull(parts, "epfNumber", criteria.getEpfNumber());
			addIfNotNull(parts, "esiNumber", criteria.getEsiNumber());
			addIfNotNull(parts, "gstRegisteredState", criteria.getGstRegisteredState());
			addIfNotNull(parts, "supplierType", criteria.getSupplierType());

			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.CONTRACTOR_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator contractorSearchKeyGenerator() {
		return (target, method, params) -> {
			ContractorModel criteria = (ContractorModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.CONTRACTOR_SEARCH_REDIS_KEY_GENERATOR);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "correspondenceAddress", criteria.getCorrespondenceAddress());
			addIfNotNull(parts, "paymentAddress", criteria.getPaymentAddress());
			addIfNotNull(parts, "contactPerson", criteria.getContactPerson());
			addIfNotNull(parts, "email", criteria.getEmail());
			addIfNotNull(parts, "narration", criteria.getNarration());
			addIfNotNull(parts, "panNumber", criteria.getPanNumber());
			addIfNotNull(parts, "tinNumber", criteria.getTinNumber());
			addIfNotNull(parts, "bankId", criteria.getBankId());
			addIfNotNull(parts, "ifscCode", criteria.getIfscCode());
			addIfNotNull(parts, "bankAccount", criteria.getBankAccount());
			addIfNotNull(parts, "mobileNumber", criteria.getMobileNumber());
			addIfNotNull(parts, "registrationNumber", criteria.getRegistrationNumber());
			addIfNotNull(parts, "epfNumber", criteria.getEpfNumber());
			addIfNotNull(parts, "esiNumber", criteria.getEsiNumber());
			addIfNotNull(parts, "gstRegisteredState", criteria.getGstRegisteredState());
			addIfNotNull(parts, "contractorType", criteria.getContractorType());
			addIfNotNull(parts, "statusId", criteria.getStatusId());

			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.WORK_ORDER_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator workOrderSearchKeyGenerator() {
		return (target, method, params) -> {
			WorkOrderModel criteria = (WorkOrderModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.WORK_ORDER_SEARCH_REDIS_KEY_GENERATOR);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "orderNumber", criteria.getOrderNumber());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "contractorId", criteria.getContractorId());
			addIfNotNull(parts, "fundId", criteria.getFundId());
			addIfNotNull(parts, "schemeId", criteria.getSchemeId());
			addIfNotNull(parts, "subSchemeId", criteria.getSubSchemeId());
			addIfNotNull(parts, "department", criteria.getDepartment());
			addIfNotNull(parts, "active", criteria.getActive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator purchaseOrderSearchKeyGenerator() {
		return (target, method, params) -> {
			PurchaseOrderModel criteria = (PurchaseOrderModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.PURCHASE_ORDER_SEARCH_REDIS_KEY_GENERATOR);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "orderNumber", criteria.getOrderNumber());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "supplierId", criteria.getSupplierId());
			addIfNotNull(parts, "fundId", criteria.getFundId());
			addIfNotNull(parts, "schemeId", criteria.getSchemeId());
			addIfNotNull(parts, "subSchemeId", criteria.getSubSchemeId());
			addIfNotNull(parts, "department", criteria.getDepartment());
			addIfNotNull(parts, "active", criteria.getActive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());
			addIfNotNull(parts, "createdDate",
					criteria.getCreatedDate() != null ? criteria.getCreatedDate().getTime() : null);
			addIfNotNull(parts, "lastModifiedDate",
					criteria.getLastModifiedDate() != null ? criteria.getLastModifiedDate().getTime() : null);
			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator accountEntitySearchKeyGenerator() {
		return (target, method, params) -> {
			AccountEntityModel criteria = (AccountEntityModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.ACCOUNT_ENTITY_SEARCH_REDIS_KEY_GENERATOR);

			addIfNotNull(parts, "tenant", tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "code", criteria.getCode());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "detailTypeId", criteria.getDetailTypeId());
			addIfNotNull(parts, "isactive", criteria.getIsactive());
			addIfNotNull(parts, "createdBy", criteria.getCreatedBy());
			addIfNotNull(parts, "lastModifiedBy", criteria.getLastModifiedBy());

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.COA_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator chartOfAccountSearchKeyGenerator() {
		return (target, method, params) -> {
			ChartOfAccountsModel criteria = (ChartOfAccountsModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.COA_SEARCH_REDIS_KEY_GENERATOR);

			parts.add("tenant=" + tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "glcode", criteria.getGlcode());
			addIfNotNull(parts, "name", criteria.getName());
			addIfNotNull(parts, "type", criteria.getType());
			addIfNotNull(parts, "purposeId", criteria.getPurposeId());
			addIfNotNull(parts, "isActiveForPosting", criteria.getIsActiveForPosting());

			return String.join("::", parts);
		};
	}

	@Bean(MasterConstants.COA_DETAIL_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator chartOfAccountDetailSearchKeyGenerator() {
		return (target, method, params) -> {
			ChartOfAccountDetailModel criteria = (ChartOfAccountDetailModel) params[0];
			List<String> parts = new ArrayList<>();
			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(tenantId,
					MasterConstants.COA_DETAIL_SEARCH_REDIS_KEY_GENERATOR);

			parts.add("tenant=" + tenantId);
			parts.add("version=" + version);
			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "glcodeId", criteria.getGlcodeId());
			addIfNotNull(parts, "detailTypeId", criteria.getDetailTypeId());

			return String.join("::", parts);
		};
	}
	
	@Bean(MasterConstants.RECOVERY_SEARCH_REDIS_KEY_GENERATOR)
	public KeyGenerator recoverySearchKeyGenerator(CacheEvictionService cacheEvictionService) {
		return (target, method, params) -> {
			RecoveryModel criteria = (RecoveryModel) params[0];
			List<String> parts = new ArrayList<>();

			String tenantId = ApplicationThreadLocals.getTenantID();
			String version = cacheEvictionService.getVersionForTenant(
					tenantId, MasterConstants.RECOVERY_SEARCH_REDIS_CACHE_VERSION_KEY);

			parts.add("tenant=" + tenantId);
			parts.add("version=" + version);

			addIfNotNull(parts, "id", criteria.getId());
			addIfNotNull(parts, "glcodeId", criteria.getGlcodeId());
			addIfNotNull(parts, "type", criteria.getType());
			addIfNotNull(parts, "isActive", criteria.getIsActive());
			addIfNotNull(parts, "bankId", criteria.getBankId());
			addIfNotNull(parts, "partyTypeId", criteria.getPartyTypeId());

			return String.join("::", parts);
		};
	}


	private void addIfNotNull(List<String> parts, String key, Object value) {
		if (value != null) {
			parts.add(key + "=" + value);
		}
	}

}
