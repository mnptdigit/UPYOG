package org.egov.finance.voucher.service;

import java.util.List;

import org.egov.finance.voucher.entity.AppConfig;
import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.model.request.AppConfigSearchRequest;
import org.egov.finance.voucher.repository.AppConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AppConfigService {
	 @Autowired
	    private AppConfigRepository appConfigRepository;

	    public AppConfig getAppConfigByModuleNameAndKeyName(String moduleName, String keyName) {
	        return appConfigRepository.findByModuleNameAndKeyName(moduleName, keyName);
	    }

	    public AppConfig getAppConfigByKeyName(String keyName) {
	        return appConfigRepository.findByKeyName(keyName);
	    }

	    public List<AppConfig> getAllAppConfigByModuleName(String moduleName) {
	        return appConfigRepository.findByModuleName(moduleName);
	    }

//	    public Page<AppConfig> getAllAppConfig(AppConfigSearchRequest searchRequest) {
//	        Pageable pageable = new PageRequest(searchRequest.pageNumber(), searchRequest.pageSize(),
//	                searchRequest.orderDir(), searchRequest.orderBy());
//	        return isBlank(searchRequest.getModuleName()) ? appConfigRepository.findAll(pageable) :
//	                appConfigRepository.findByModuleName(searchRequest.getModuleName(), pageable);
//	    }

	    @Transactional
	    public void createAppConfig(AppConfig appConfig) {
	        appConfigRepository.save(appConfig);
	    }

	    @Transactional
	    public void updateAppConfig(AppConfig appConfig) {
	        appConfig.getConfValues().removeIf(AppConfigValues::isMarkedForRemoval);
	        appConfigRepository.save(appConfig);
	    }

}
