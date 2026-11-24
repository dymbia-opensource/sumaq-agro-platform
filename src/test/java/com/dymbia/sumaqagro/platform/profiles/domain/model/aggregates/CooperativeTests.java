package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Dni;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.InvitationStatus;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Ruc;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

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
        var command = new AddCooperativeMemberCommand(1L, 3L, "45678912", "Guillermo", "Quispe", "Sicaya", null, 1L, null);

        var member = cooperative.addMember(command);

        assertTrue(member.isActive());
        assertEquals("Guillermo Quispe", member.getFullName());
        assertTrue(cooperative.hasActiveMemberWithUserId(1L));
        assertThrows(IllegalArgumentException.class, () -> cooperative.addMember(command));
    }

    /** A saved cooperative whose member 11 was registered without an account. */
    private Cooperative cooperativeWithMemberWithoutAccount() {
        var member = CooperativeMember.reconstitute(11L, 1L, new Dni("45678912"),
                new PersonName("Guillermo", "Quispe"), "Sicaya", LocalDate.of(2024, 3, 15), true, null);
        return Cooperative.reconstitute(1L, new Ruc("20612345678"), "Cooperativa Agraria Valle del Mantaro", "Junín",
                3L, null, null, null, null, List.of(member), List.of());
    }

    @Test
    void shouldRejectBothAccountEmailAndUserId() {
        assertThrows(IllegalArgumentException.class, () -> new AddCooperativeMemberCommand(
                1L, 3L, "45678912", "Guillermo", "Quispe", "Sicaya", null, 1L, "guillermo@gmail.com"));
    }

    @Test
    void shouldLinkAMemberRegisteredWithoutAccount() {
        var cooperative = cooperativeWithMemberWithoutAccount();

        var member = cooperative.linkMemberAccount(11L, 9L);

        assertEquals(9L, member.getUserId());
        assertTrue(cooperative.hasActiveMemberWithUserId(9L));
        assertThrows(IllegalStateException.class, () -> cooperative.linkMemberAccount(11L, 10L));
    }

    @Test
    void shouldNotLinkAnUnknownMemberOrAnAccountAlreadyInTheCooperative() {
        var cooperative = cooperativeWithMemberWithoutAccount();
        cooperative.addMember(new AddCooperativeMemberCommand(
                1L, 3L, "45678913", "Rosa", "Quispe", "Sicaya", null, 9L, null));

        assertThrows(NoSuchElementException.class, () -> cooperative.linkMemberAccount(99L, 10L));
        assertThrows(IllegalArgumentException.class, () -> cooperative.linkMemberAccount(11L, 9L));
    }

    @Test
    void shouldKeepTheInvitationPendingUntilTheAgronomistSignsUp() {
        var cooperative = newCooperative();

        var invitation = cooperative.inviteAgronomist(new InviteAgronomistCommand(1L, 3L, "Lucia@Agro.pe"), null);

        assertEquals(InvitationStatus.PENDING, invitation.getStatus());
        assertTrue(cooperative.getTechnicalTeam().isEmpty());
        assertTrue(cooperative.acceptInvitationsOf("lucia@agro.pe", 7L));
        assertEquals(7L, invitation.getAgronomistUserId());
        assertEquals(1, cooperative.getTechnicalTeam().size());
        assertFalse(cooperative.acceptInvitationsOf("lucia@agro.pe", 7L));
    }

    @Test
    void shouldAcceptAtOnceTheInvitationOfAnExistingAgronomist() {
        var cooperative = newCooperative();

        var invitation = cooperative.inviteAgronomist(new InviteAgronomistCommand(1L, 3L, "lucia@agro.pe"), 7L);

        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        assertEquals(7L, invitation.getAgronomistUserId());
    }

    @Test
    void shouldNotInviteTheSameEmailTwice() {
        var cooperative = newCooperative();
        cooperative.inviteAgronomist(new InviteAgronomistCommand(1L, 3L, "lucia@agro.pe"), null);

        assertThrows(IllegalArgumentException.class, () ->
                cooperative.inviteAgronomist(new InviteAgronomistCommand(1L, 3L, "LUCIA@agro.pe"), null));
    }

    @Test
    void onlyAcceptedAgronomistsBelongToTheTechnicalTeam() {
        var cooperative = newCooperative();
        cooperative.inviteAgronomist(new InviteAgronomistCommand(1L, 3L, "lucia@agro.pe"), 7L);
        cooperative.inviteAgronomist(new InviteAgronomistCommand(1L, 3L, "pendiente@agro.pe"), null);

        assertTrue(cooperative.hasAgronomistInTeam(7L));
        assertFalse(cooperative.hasAgronomistInTeam(8L));
        assertFalse(cooperative.hasAgronomistInTeam(null));
    }
}
