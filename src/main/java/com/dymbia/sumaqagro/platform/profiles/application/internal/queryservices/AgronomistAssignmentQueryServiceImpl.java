package com.dymbia.sumaqagro.platform.profiles.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.AgronomistAssignmentQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.*;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.AgronomistAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AgronomistAssignmentQueryServiceImpl implements AgronomistAssignmentQueryService {

    private final AgronomistAssignmentRepository agronomistAssignmentRepository;

    public AgronomistAssignmentQueryServiceImpl(AgronomistAssignmentRepository agronomistAssignmentRepository) {
        this.agronomistAssignmentRepository = agronomistAssignmentRepository;
    }

    // inherited javadoc
    @Override
    public Optional<AgronomistAssignment> handle(GetAgronomistAssignmentByIdQuery query) {
        return agronomistAssignmentRepository.findById(query.assignmentId());
    }

    // inherited javadoc
    @Override
    public Optional<AgronomistAssignment> handle(GetAgronomistAssignedToPlotQuery query) {
        return agronomistAssignmentRepository.findActiveByPlotId(query.plotId());
    }

    // inherited javadoc
    @Override
    public List<AgronomistAssignment> handle(GetAllAgronomistAssignmentsQuery query) {
        return agronomistAssignmentRepository.findAll();
    }

    // inherited javadoc
    @Override
    public List<AgronomistAssignment> handle(GetAgronomistAssignmentsByCooperativeQuery query) {
        return agronomistAssignmentRepository.findAllByCooperativeId(query.cooperativeId());
    }

    // inherited javadoc
    @Override
    public List<AgronomistAssignment> handle(GetAgronomistAssignmentsByProducerUserIdQuery query) {
        return agronomistAssignmentRepository.findAllByProducerUserId(query.producerUserId());
    }

    // inherited javadoc
    @Override
    public List<AgronomistAssignment> handle(GetAgronomistAssignmentsByAgronomistUserIdQuery query) {
        return agronomistAssignmentRepository.findAllByAgronomistUserId(query.agronomistUserId());
    }

    // inherited javadoc
    @Override
    public List<AgronomistAssignment> handle(GetAgronomistAssignmentsByPlotIdQuery query) {
        return agronomistAssignmentRepository.findAllByPlotId(query.plotId());
    }
}
