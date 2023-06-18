package com.jore.epoc.mapper;

import java.util.ArrayList;
import java.util.List;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Simulation;
import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.SimulationDto;

import jakarta.validation.Valid;

public interface SimulationMapper {
    SimulationMapper INSTANCE = new SimulationMapper() {
        @Override
        public void simulationDtoToSimulation(Simulation simulation, @Valid SimulationDto simulationDto) {
            updateSimulation(simulationDto, simulation);
        }

        @Override
        public Simulation simulationDtoToSimulation(SimulationDto simulationDto) {
            Simulation result = new Simulation();
            result.setId(simulationDto.getId());
            result.setIsFinished(simulationDto.isFinished());
            result.setIsStarted(simulationDto.isStarted());
            updateSimulation(simulationDto, result);
            return result;
        }

        @Override
        public SimulationDto simulationToSimulationDto(Simulation simulation) {
            List<CompanyDto> companyDtos = new ArrayList<>();
            List<Company> companies = simulation.getCompanies();
            for (Company company : companies) {
                companyDtos.add(CompanyMapper.INSTANCE.companyToCompanyDto(company));
            }
            return SimulationDto.builder().id(simulation.getId()).isFinished(simulation.isFinished()).name(simulation.getName()).isStarted(simulation.isStarted()).nrOfMonths(simulation.getNrOfMonths()).startMonth(simulation.getStartMonth()).companies(CompanyMapper.INSTANCE.companyToCompanyDto(companies)).baseCurrency(simulation.getSettings().getBaseCurrency()).costToBuildFactory(simulation.getSettings().getFactoryConstructionCost()).simulationType(simulation.getSettings().getSimulationType()).build();
        }

        private void updateSimulation(SimulationDto simulationDto, Simulation result) {
            result.setName(simulationDto.getName());
            result.setNrOfMonths(simulationDto.getNrOfMonths());
            result.setStartMonth(simulationDto.getStartMonth());
        }
    };

    void simulationDtoToSimulation(Simulation simulation, @Valid SimulationDto simulationDto);

    Simulation simulationDtoToSimulation(SimulationDto simulationDto);

    SimulationDto simulationToSimulationDto(Simulation simulation);
}
