/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.master.util;

public class MasterConstants {

	private MasterConstants() {
		super();
	}

	public static final String REQUEST_INFO = "RequestInfo";
	public static final String REQUEST_TENANT_SPLIT_REGEX = "\\.";
	public static final String ORG_EGOV_FINANCE = "org.egov.finance.*";
	public static final String EGOV_PERSISTENCE_UNIT = "EgovPersistenceUnit";
	public static final String TRANSACTION_MANAGER = "transactionManager";
	public static final String ENTITY_MANAGER_FACTORY = "entityManagerFactory";
	public static final String NAME_IS_ALREADY_EXISTS_MSG = "Name is already exists";
	public static final String ORDER_IS_ALREADY_EXISTS_MSG = "Order is already exists";

	public static final String NAME_FUND_ALREADY_EXISTS_MSG = "Scheme with same name and fund already exists";

	public static final String CODE_IS_ALREADY_EXISTS_MSG = "Code is already exists";
	public static final String NAME_NOT_UNIQUE = "NAME_NOT_UNIQUE";
	public static final String ORDER_NOT_UNIQUE = "ORDER_NOT_UNIQUE";
	public static final String DUPLICATE_SCHEME = "DUPLICATE_SCHEME";

	public static final String CODE_NOT_UNIQUE = "CODE_NOT_UNIQUE";
	public static final String ID_CANNOT_BE_PASSED_IN_CREATION_MSG = "Id cannot be passed in creation";
	public static final String INVALID_ID_PASSED = "INVALID_ID_PASSED";
	public static final String INVALID_ID_PASSED_MESSAGE = "Please pass correct id in case of update";
	public static final String INVALID_PARENT_ID = "INVALID_PARENT_ID";
	public static final String INVALID_PARENT_ID_MSG = "Please Provide a valid parent Parent Id";
	public static final String INVALID_SCHEME_ID = "INVALID_SCHEME_ID";
	public static final String INVALID_SCHEME_ID_MSG = "Please Provide a valid scheme Id";
	public static final String INVALID_TEXT_CONTAINS_HTML_TAGS_MSG = "Invalid Text, contains HTML Tags";
	public static final String FUND_SEARCH_REDIS_KEY_GENERATOR = "fundSearchKeyGenerator";
	public static final String SCHEME_SEARCH_REDIS_KEY_GENERATOR = "schemeSearchKeyGenerator";
	public static final String FUND_SEARCH_REDIS_CACHE_NAME = "fundSearchCache";
	public static final String SCHEME_SEARCH_REDIS_CACHE_NAME = "schemeSearchCache";
	public static final String FUND_SEARCH_REDIS_CACHE_VERSION_KEY = "fundSearchCacheVersion::";

	public static final String SCHEME_SEARCH_REDIS_CACHE_VERSION_KEY = "schemeSearchCacheVersion::";

	public static final String REDIS_SEARCH_VERSION_TAG = "::version=";

	public static final String REDIS_SEARCH_TENANT_TAG = "::tenant=";
	public static final String REDIS_START_VERSION_V0 = "v0";
	public static final String CODE_NAME_NOT_UNIQUE = "CODE_NAME_NOT_UNIQUE";
	public static final String CODE_NAME_NOT_UNIQUE_MSG = "Code Or Name Provided already exist ";

	public static final String INVALID_PARAMETERS = "INVALID_PARAMETERS";
	public static final String INVALID_PARAMETERS_MSG = "Invlaid Parameters Passed";
	public static final String EXCEPTION_FROM_MASTER_SERVICE_MSG = "Exception From master Service---- {}";
	public static final String INVALID_NAME = "INVALID_NAME";
	public static final String INVALID_NAME_MSG = "Please Provide a valid name";
	public static final String INVALID_CODE = "INVALID_CODE";
	public static final String INVALID_CODE_MSG = "Please Provide a valid code";

	public static final String INVALID_FUND = "INVALID_FUND";
	public static final String INVALID_FUND_ASSOCIATED_MSG = "Invalid fund associated with scheme.";

	public static final String FUNCTION_SEARCH_REDIS_KEY_GENERATOR = "functionSearchKeyGenerator";
	public static final String FUNCTION_SEARCH_REDIS_CACHE_VERSION_KEY = "fuctionSearchCacheVersion::";
	public static final String FUNCTION_SEARCH_REDIS_CACHE_NAME = "functionSearchCache";
	public static final String SUBSCHEME_SEARCH_REDIS_KEY_GENERATOR = "subschemeSearchKeyGenerator";
	public static final String SUBSCHEME_SEARCH_REDIS_CACHE_NAME = "subschemeSearchCache";
	public static final String SUBSCHEME_SEARCH_REDIS_CACHE_VERSION_KEY = "subschemeSearchCacheVersion::";
	public static final String CODE_SCHEMEID_NOT_UNIQUE = "CODE_SCHEMEID_NOT_UNIQUE";
	public static final String CODE_SCHEMEID_NOT_UNIQUE_MESSAGE = "Code and SchemeID already exists";

	public static final String BANK_SEARCH_REDIS_CACHE_NAME = "bankSearchCache";
	public static final String BANK_SEARCH_REDIS_KEY_GENERATOR = "bankSearchKeyGenerator";
	public static final String BANK_SEARCH_REDIS_CACHE_VERSION_KEY = "bankSearchCacheVersion::";

	public static final String BANK_BRANCH_SEARCH_REDIS_CACHE_NAME = "bankBranchSearchCache";
	public static final String BANK_BRANCH_SEARCH_REDIS_KEY_GENERATOR = "bankBranchSearchKeyGenerator";
	public static final String BANK_BRANCH_SEARCH_REDIS_CACHE_VERSION_KEY = "bankBranchSearchCacheVersion::";

	public static final String BANK_ACCOUNT_SEARCH_REDIS_CACHE_NAME = "bankAccountSearchCache";
	public static final String BANK_ACCOUNT_SEARCH_REDIS_KEY_GENERATOR = "bankAccountSearchKeyGenerator";
	public static final String BANK_ACCOUNT_SEARCH_REDIS_CACHE_VERSION_KEY = "bankAccountSearchCacheVersion::";

	public static final String ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_CACHE_NAME = "accountDetailTypeSearchCache";
	public static final String ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_KEY_GENERATOR = "accountDetailTypeSearchKeyGenerator";
	public static final String ACCOUNT_DETAIL_TYPE_SEARCH_REDIS_CACHE_VERSION_KEY = "accountDetailTypeSearchCacheVersion::";

	public static final String SUPPLIER_SEARCH_REDIS_CACHE_NAME = "supplierSearchCache";
	public static final String SUPPLIER_SEARCH_REDIS_KEY_GENERATOR = "supplierSearchKeyGenerator";
	public static final String SUPPLIER_SEARCH_REDIS_CACHE_VERSION_KEY = "supplierSearchCacheVersion::";

	public static final String CONTRACTOR_SEARCH_REDIS_CACHE_NAME = "contractorSearchCache";
	public static final String CONTRACTOR_SEARCH_REDIS_KEY_GENERATOR = "contractorSearchKeyGenerator";
	public static final String CONTRACTOR_SEARCH_REDIS_CACHE_VERSION_KEY = "contractorSearchCacheVersion::";

	public static final String WORK_ORDER_SEARCH_REDIS_CACHE_NAME = "workOrderSearchCache";
	public static final String WORK_ORDER_SEARCH_REDIS_KEY_GENERATOR = "workOrderSearchKeyGenerator";
	public static final String WORK_ORDER_SEARCH_REDIS_CACHE_VERSION_KEY = "workOrderSearchCacheVersion::";

	public static final String PURCHASE_ORDER_SEARCH_REDIS_CACHE_NAME = "purchaseOrderSearchCache";
	public static final String PURCHASE_ORDER_SEARCH_REDIS_KEY_GENERATOR = "purchaseOrderSearchKeyGenerator";
	public static final String PURCHASE_ORDER_SEARCH_REDIS_CACHE_VERSION_KEY = "purchaseOrderSearchCacheVersion::";

	public static final String ACCOUNT_ENTITY_SEARCH_REDIS_KEY_GENERATOR = "accountEntitySearchKeyGenerator";
	public static final String ACCOUNT_ENTITY_SEARCH_REDIS_CACHE_NAME = "accountEntitySearchCache";
	public static final String ACCOUNT_ENTITY_SEARCH_REDIS_CACHE_VERSION_KEY = "accountEntitySearchCacheVersion::";
	public static final String ACCOUNTNUMBER_NOT_UNIQUE = "accountnumber NOT UNIQUE";
	public static final String ACCOUNT_IS_ALREADY_EXISTS_MSG = "Account number already exists for the given branch.";
	public static final String BRANCHCODE_NOT_UNIQUE = "branchcode NOT UNIQUE";
	public static final String BRANCHCODE_IS_ALREADY_EXISTS_MSG = "Branch code already exists for the given bank.";
	public static final String INVALID_DETAILTYPEID = "detailTypeId";
	public static final String INVALID_DETAILTYPEID_ASSOCIATED_MSG = "Invalid Detail Type";
	public static final String INVALID_BANKBRANCHID = "bankbranchId";
	public static final String INVALID_BANKBRANCHID_ASSOCIATED_MSG = "Bank branch not found";
	public static final String INVALID_GLCODEID = "glcodeId";
	public static final String INVALID_GLCODEID_ASSOCIATED_MSG = "GL Code already exist ";
	public static final String INVALID_CHEQUEFORMATID = "chequeformatId";
	public static final String INVALID_CHEQUEFORMATID_ASSOCIATED_MSG = "Cheque Format not found";
	public static final String INVALID_BANKID = "bankId";
	public static final String INVALID_BANKID_ASSOCIATED_MSG = "Bank not found for ID: ";
	public static final String INVALID_STATUSID = "statusId";
	public static final String INVALID_STATUSID_ASSOCIATED_MSG = "Status not found";
	public static final String INVALID_SCHEMEID = "schemeId";
	public static final String INVALID_SCHEMEID_ASSOCIATED_MSG = "Invalid scheme";
	public static final String INVALID_SUBSCHEMEID = "subSchemeId";
	public static final String INVALID_SUBSCHEMEID_ASSOCIATED_MSG = "Invalid subScheme";

	public static final String INVALID_SUPPLIER = "supplierId";
	public static final String INVALID_SUPPLIER_ASSOCIATED_MSG = "Invalid supplier";

	public static final String INVALID_CONTRACTOR = "contractorId";
	public static final String INVALID_CONTRACTOR_ASSOCIATED_MSG = "Invalid contractor";
	public static final String REGISTRATION_NUMBER_NOT_UNIQUE = "registrationNumber";
	public static final String REGISTRATION_NUMBER_IS_ALREADY_EXISTS_MSG = "Registration number already exists";

	public static final String COA_SEARCH_REDIS_CACHE_NAME = "chartofAccountSearchCache";
	public static final String COA_SEARCH_REDIS_KEY_GENERATOR = "chartOfAccountSearchKeyGenerator";
	public static final String COA_SEARCH_REDIS_CACHE_VERSION_KEY = "chartOfAccountSearchCacheVersion::";

	public static final String COA_DETAIL_SEARCH_REDIS_CACHE_NAME = "chartofAccountDetailSearchCache";
	public static final String COA_DETAIL_SEARCH_REDIS_KEY_GENERATOR = "chartOfAccountDetailSearchKeyGenerator";
	public static final String COA_DETAIL_SEARCH_REDIS_CACHE_VERSION_KEY = "chartOfAccountDetailSearchCacheVersion::";

	public static final String RECOVERY_SEARCH_REDIS_CACHE_NAME = "recoverySearchCache";
	public static final String RECOVERY_SEARCH_REDIS_KEY_GENERATOR = "recoverySearchKeyGenerator";
	public static final String RECOVERY_SEARCH_REDIS_CACHE_VERSION_KEY = "recoverySearchCacheVersion::";
	
	public static final String TIN_NUMBER_NOT_UNIQUE = "Tin Number_NOT_UNIQUE";
	public static final String TIN_NUMBER_IS_ALREADY_EXISTS_MSG = "Tin number already exists for the given bank.";

}
