package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateCooperativeCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CooperativeTests {

    private Cooperative newCooperative() {
        return new Cooperative(new RegisterCooperativeCommand(
                "20612345678", "Cooperativa Agraria Valle del Mantaro", "Junín", 3L,
                null, null, null, null));
    }

    @Test
    void shouldRegisterWithoutInstitutionalData() {
        var cooperative = newCooperative();

        assertNull(cooperative.getInstitutionalEmail());
        assertTrue(cooperative.getMembers().isEmpty());
    }

    @Test
    void shouldUpdateInstitutionalData() {
        var cooperative = newCooperative();

        cooperative.updateInstitutionalData(new UpdateCooperativeCommand(
                1L, "20612345678", "Cooperativa Agraria Valle del Mantaro", "Junín - Valle del Mantaro",
                "Contacto@ValleDelMantaro.pe", "Cristian Santana", "Av. Ferrocarril 123, Huancayo", "+51 64 555 200"));

        assertEquals("contacto@valledelmantaro.pe", cooperative.getInstitutionalEmail());
        assertEquals("+51 64 555 200", cooperative.getSwitchboardPhone());
    }

    @Test
    void shouldAddActiveMemberAndRejectRepeatedDni() {
        var cooperative = newCooperative();
        var command = new AddCooperativeMemberCommand(1L, "45678912", "Guillermo", "Quispe", "Sicaya", null, 1L);

        var member = cooperative.addMember(command);

        assertTrue(member.isActive());
        assertEquals("Guillermo Quispe", member.getFullName());
        assertTrue(cooperative.hasActiveMemberWithUserId(1L));
        assertThrows(IllegalArgumentException.class, () -> cooperative.addMember(command));
    }
}
