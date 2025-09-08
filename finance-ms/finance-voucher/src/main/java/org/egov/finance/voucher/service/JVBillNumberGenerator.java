package org.egov.finance.voucher.service;

import org.egov.finance.voucher.entity.EgBillregister;
import org.springframework.stereotype.Service;

@Service
public interface JVBillNumberGenerator {
    public String getNextNumber(EgBillregister br);

}
