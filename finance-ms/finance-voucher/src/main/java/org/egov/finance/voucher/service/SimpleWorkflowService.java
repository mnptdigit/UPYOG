package org.egov.finance.voucher.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.egov.finance.voucher.repository.WorkFlowMatrixRepository;
import org.egov.finance.voucher.workflow.entity.StateAware;
import org.egov.finance.voucher.workflow.entity.WorkFlowMatrix;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@Qualifier("workflowService")
@RequiredArgsConstructor
public class SimpleWorkflowService<T extends StateAware> implements WorkflowService<T> {

    private static final String ANY = "ANY";
    private final WorkFlowMatrixRepository workFlowMatrixRepository;

    @Override
    public WorkFlowMatrix getWfMatrix(String type, String department, BigDecimal amountRule,
                                      String additionalRule, String currentState, String pendingActions) {

        WorkFlowMatrix matrix = findWorkflowMatrix(type, department, amountRule, additionalRule, currentState, pendingActions, null);

        // Try with department = "ANY" if not found
        if (matrix == null) {
            matrix = findWorkflowMatrix(type, ANY, amountRule, additionalRule, currentState, pendingActions, null);
        }

        return matrix;
    }

    private WorkFlowMatrix findWorkflowMatrix(String type, String department, BigDecimal amountRule,
                                              String additionalRule, String currentState, String pendingActions, String designation) {

        Specification<WorkFlowMatrix> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("objectType"), type));

            if (department != null)
                predicates.add(cb.equal(root.get("department"), department));

            if (additionalRule != null)
                predicates.add(cb.equal(root.get("additionalRule"), additionalRule));

            if (currentState != null && !currentState.isBlank())
                predicates.add(cb.equal(root.get("currentState"), currentState));
            else
                predicates.add(cb.equal(root.get("currentState"), "NEW"));

            if (pendingActions != null && !pendingActions.isBlank())
                predicates.add(cb.like(root.get("pendingActions"), "%" + pendingActions + "%"));

            if (designation != null && !designation.isBlank())
                predicates.add(cb.like(root.get("currentDesignation"), "%" + designation + "%"));

            if (amountRule != null && BigDecimal.ZERO.compareTo(amountRule) != 0) {
                Predicate amountBetween = cb.and(
                        cb.lessThanOrEqualTo(root.get("fromQty"), amountRule),
                        cb.greaterThanOrEqualTo(root.get("toQty"), amountRule)
                );

                Predicate amountOpenEnded = cb.and(
                        cb.lessThanOrEqualTo(root.get("fromQty"), amountRule),
                        cb.isNull(root.get("toQty"))
                );

                predicates.add(cb.or(amountBetween, amountOpenEnded));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<WorkFlowMatrix> result = workFlowMatrixRepository.findAll(spec);

        if (result.isEmpty())
            return null;

        return result.stream()
                .filter(r -> r.getToDate() == null)
                .findFirst()
                .orElse(result.get(0));
    }
}