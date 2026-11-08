package com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects;

/**
 * Lifecycle states of a field inspection.
 * Transition rules are enforced by the inspection's domain behavior.
 */
public enum FieldInspectionStatus {

    SCHEDULED,
    COMPLETED
}