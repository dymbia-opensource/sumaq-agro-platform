package com.dymbia.sumaqagro.platform.certification.domain.model.valueobjects;

/**
 * Commercial quality result of a graded harvest batch.
 */
public enum QualityCategory {
    PREMIUM_GOLD,
    STANDARD,
    B_GRADE_REQUIRES_SORTING,
    SPECIALTY_COFFEE,
    COMMERCIAL_COFFEE;

    /**
     * @return {@code true} when the category applies to potato
     */
    public boolean isPotatoCategory() {
        return this == PREMIUM_GOLD || this == STANDARD || this == B_GRADE_REQUIRES_SORTING;
    }

    /**
     * @return {@code true} when the category applies to coffee
     */
    public boolean isCoffeeCategory() {
        return this == SPECIALTY_COFFEE || this == COMMERCIAL_COFFEE;
    }
}
