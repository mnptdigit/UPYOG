package org.egov.finance.master.model.response;

import java.util.List;

import org.egov.finance.master.model.RecoveryModel;
import org.egov.finance.master.model.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecoveryResponse {
    private ResponseInfo responseInfo;
    private List<RecoveryModel> recoveries;
}

