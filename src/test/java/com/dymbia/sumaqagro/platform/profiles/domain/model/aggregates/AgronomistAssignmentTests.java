package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgronomistAssignmentTests {

    private AgronomistAssignment newAssignment() {
        return new AgronomistAssignment(new AssignAgronomistToPlotCommand(1L, 4L, 10L), 7L);
    }

    @Test
    void shouldCreateActiveAssignmentWithProducer() {
        var assignment = newAssignment();

        assertTrue(assignment.isActive());
        assertEquals(7L, assignment.getProducerUserId());
        assertTrue(assignment.getHistory().isEmpty());
    }

    @Test
    void shouldMoveCurrentAgronomistToHistoryWhenReassigning() {
        var assignment = newAssignment();

        assignment.reassignTo(5L);

        assertEquals(5L, assignment.getAgronomistUserId());
        assertEquals(1, assignment.getHistory().size());
        assertEquals(4L, assignment.getHistory().getFirst().agronomistUserId());
    }

    @Test
    void shouldNotChangeAnythingWhenReassigningToSameAgronomist() {
        var assignment = newAssignment();

        assignment.reassignTo(4L);

        assertTrue(assignment.getHistory().isEmpty());
    }

    @Test
    void shouldRequireCooperative() {
        assertThrows(IllegalArgumentException.class, () -> new AssignAgronomistToPlotCommand(null, 4L, 10L));
    }
}
