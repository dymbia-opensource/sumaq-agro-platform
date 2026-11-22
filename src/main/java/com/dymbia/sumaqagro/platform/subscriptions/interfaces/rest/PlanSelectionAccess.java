package com.dymbia.sumaqagro.platform.subscriptions.interfaces.rest;

import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;

/**
 * Each plan belongs to one role: Semilla to farmers, Cooperativa Pro to directors and
 * Asesor Técnico to agronomists. Shared by the demo checkout and the Mercado Pago checkout.
 */
final class PlanSelectionAccess {

    private PlanSelectionAccess() {
    }

    static boolean canSelectPlan(UserDetailsImpl principal, PlanCode planCode) {
        if (principal == null) {
            return false;
        }
        var authority = switch (planCode) {
            case SEED -> "ROLE_FARMER";
            case COOPERATIVE_PRO -> "ROLE_COOPERATIVE_DIRECTOR";
            case TECHNICAL_ADVISOR -> "ROLE_AGRONOMIST";
        };
        return principal.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }
}
