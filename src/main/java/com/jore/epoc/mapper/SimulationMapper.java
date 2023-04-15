package com.jore.epoc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.jore.epoc.bo.Simulation;
import com.jore.epoc.dto.SimulationDto;

@Mapper
public interface SimulationMapper {
    SimulationMapper INSTANCE = Mappers.getMapper(SimulationMapper.class);

    Simulation simulationDtoToSimulation(SimulationDto simulationDto);

    SimulationDto simulationToSimulationDto(Simulation simulation);
}
