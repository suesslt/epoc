package com.jore.epoc.mapper;

import com.jore.epoc.bo.Simulation;
import com.jore.epoc.dto.SimulationDto;

public interface SimulationMapper {
    SimulationMapper INSTANCE = new SimulationMapper() {
        @Override
        public Simulation simulationDtoToSimulation(SimulationDto simulationDto) {
            Simulation result = new Simulation();
            result.setId(simulationDto.getId());
            result.setIsFinished(simulationDto.isFinished());
            result.setName(simulationDto.getName());
            result.setIsStarted(simulationDto.isStarted());
            result.setNrOfSteps(simulationDto.getNrOfSteps());
            result.setStartMonth(simulationDto.getStartMonth());
            return result;
        }

        @Override
        public SimulationDto simulationToSimulationDto(Simulation simulation) {
            return SimulationDto.builder().id(simulation.getId()).isFinished(simulation.isFinished()).name(simulation.getName()).isStarted(simulation.isStarted()).nrOfSteps(simulation.getNrOfSteps()).startMonth(simulation.getStartMonth()).build();
        }
    };

    Simulation simulationDtoToSimulation(SimulationDto simulationDto);

    SimulationDto simulationToSimulationDto(Simulation simulation);
}
