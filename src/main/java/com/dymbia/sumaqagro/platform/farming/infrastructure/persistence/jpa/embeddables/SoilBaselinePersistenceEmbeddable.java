package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.embeddables;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilTexture;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA embeddable for the soil analysis of a field plot.
 * <p>
 * It keeps the {@code SoilBaseline} value object in the columns of the {@code field_plots} table.
 * The four columns stay empty until the analysis is recorded, so Hibernate reads the embeddable as null.
 * </p>
 *
 * @since 1.0
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class SoilBaselinePersistenceEmbeddable {

    @Column(name = "soil_ph", precision = 4, scale = 2)
    private BigDecimal ph;

    @Enumerated(EnumType.STRING)
    @Column(name = "soil_texture", length = 50)
    private SoilTexture texture;

    @Column(name = "soil_organic_matter_percentage", precision = 5, scale = 2)
    private BigDecimal organicMatterPercentage;

    @Column(name = "soil_recorded_at")
    private LocalDate recordedAt;

    public SoilBaselinePersistenceEmbeddable(BigDecimal ph, SoilTexture texture, BigDecimal organicMatterPercentage,
                                             LocalDate recordedAt) {
        this.ph = ph;
        this.texture = texture;
        this.organicMatterPercentage = organicMatterPercentage;
        this.recordedAt = recordedAt;
    }
}
