package org.egov.finance.voucher.service;

import java.io.Serializable;

import org.egov.finance.voucher.entity.EgBillregister;
import org.egov.finance.voucher.entity.FinancialYear;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.repository.FinancialYearRepository;
import org.egov.finance.voucher.util.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JVBillNumberGeneratorImpl implements JVBillNumberGenerator {

	@Autowired
	private FinancialYearRepository financialYearDAO;

	@Autowired
	private GenericSequenceNumberGenerator genericSequenceNumberGenerator;

	/**
	 * 
	 * Format DepartmentCode/MN/seqnumber/financialyear but sequence is running
	 * number for a year
	 * 
	 * @throws Exception
	 * 
	 */
	public String getNextNumber(EgBillregister br) {
		String jvBillNumber = "";

		String sequenceName = "";

		final FinancialYear financialYear = financialYearDAO.getFinancialYearByDate(br.getBilldate());
		if (financialYear == null)
			// throw new ApplicationRuntimeException("Financial Year is not defined for the
			// voucher date");
			throw new ValidationException(FinancialConstants.EMPTY_STRING,
					"Financial Year is not defined for the voucher date");
		sequenceName = "seq_jv_billnumber_" + financialYear.getFinYearRange();
		Serializable nextSequence = genericSequenceNumberGenerator.getNextSequence(sequenceName);
		jvBillNumber = String.format("%s/%s/%04d/%s", br.getEgBillregistermis().getDepartmentcode(), "MN", nextSequence,
				financialYear.getFinYearRange());
		return jvBillNumber;
	}
}
