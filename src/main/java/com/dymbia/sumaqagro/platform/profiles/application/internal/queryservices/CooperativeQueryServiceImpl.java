package com.dymbia.sumaqagro.platform.profiles.application.internal.queryservices;

import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.*;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CooperativeQueryServiceImpl implements CooperativeQueryService {

    private final CooperativeRepository cooperativeRepository;

    public CooperativeQueryServiceImpl(CooperativeRepository cooperativeRepository) {
        this.cooperativeRepository = cooperativeRepository;
    }

    // inherited javadoc
    @Override
    public Optional<Cooperative> handle(GetCooperativeByIdQuery query) {
        return cooperativeRepository.findById(query.cooperativeId());
    }

    // inherited javadoc
    @Override
    public List<Cooperative> handle(GetCooperativesByManagerUserIdQuery query) {
        return cooperativeRepository.findAllByManagerUserId(query.managerUserId());
    }

    // inherited javadoc
    @Override
    public List<Cooperative> handle(GetAllCooperativesQuery query) {
        return cooperativeRepository.findAll();
    }

    // inherited javadoc
    @Override
    public List<CooperativeMember> handle(GetCooperativeMembersByCooperativeIdQuery query) {
        return cooperativeRepository.findById(query.cooperativeId())
                .map(Cooperative::getMembers)
                .orElse(List.of());
    }

    // inherited javadoc
    @Override
    public List<CooperativeMember> handle(GetCooperativeMembersByUserIdQuery query) {
        return cooperativeRepository.findAllByMemberUserId(query.userId()).stream()
                .flatMap(cooperative -> cooperative.getMembers().stream())
                .filter(member -> query.userId().equals(member.getUserId()))
                .toList();
    }
}
