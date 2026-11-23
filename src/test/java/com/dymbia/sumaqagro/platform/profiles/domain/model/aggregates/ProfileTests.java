package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateProfileCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTests {

    private Profile newProfile() {
        return new Profile(new CreateProfileCommand(9L, "Yamil", "Tejada"));
    }

    private UpdateProfileCommand update(String dni) {
        return new UpdateProfileCommand(1L, "Yamil", "Tejada", "+51 987 654 321", null, null, dni);
    }

    @Test
    void shouldStartWithoutDni() {
        assertNull(newProfile().getDni());
    }

    @Test
    void shouldAddKeepAndRemoveTheDni() {
        var profile = newProfile();

        profile.update(update(" 45678912 "));
        assertEquals("45678912", profile.getDni());

        profile.update(update(null));
        assertEquals("45678912", profile.getDni(), "A DNI that is not sent keeps its value");

        profile.update(update(""));
        assertNull(profile.getDni(), "A blank DNI removes it");
    }

    @Test
    void shouldRejectADniWithoutEightDigits() {
        var profile = newProfile();

        assertThrows(IllegalArgumentException.class, () -> profile.update(update("1234")));
    }
}
