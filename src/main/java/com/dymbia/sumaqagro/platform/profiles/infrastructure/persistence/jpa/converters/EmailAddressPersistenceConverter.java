package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.converters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.EmailAddress;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores the {@link EmailAddress} value object as a plain text column.
 *
 * @since 1.0
 */
@Converter(autoApply = false)
public class EmailAddressPersistenceConverter implements AttributeConverter<EmailAddress, String> {

    @Override
    public String convertToDatabaseColumn(EmailAddress attribute) {
        return attribute == null ? null : attribute.address();
    }

    @Override
    public EmailAddress convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isBlank() ? null : new EmailAddress(dbData);
    }
}
