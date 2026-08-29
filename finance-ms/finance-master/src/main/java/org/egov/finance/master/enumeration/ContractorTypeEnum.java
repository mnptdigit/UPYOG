package org.egov.finance.master.enumeration;

public enum ContractorTypeEnum {
    
    FIRM("Firm"), INDIVIDUALS("Individuals");

    private String value;

    ContractorTypeEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
