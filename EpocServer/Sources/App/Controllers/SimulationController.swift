import Vapor
import Fluent

struct SimulationController: RouteCollection {
    func boot(routes: RoutesBuilder) throws {
        let simulations = routes.grouped("api", "simulations")
        simulations.get(use: index)
        simulations.get(":simulationId", "statistics", use: statistics)
        simulations.post(use: create)
        simulations.put(":simulationId", use: update)

        let companies = routes.grouped("api", "companies")
        companies.get(":companyId", "step", use: getCurrentStep)
        companies.post(":companyId", "finish", use: finishMove)

        let orders = routes.grouped("api", "orders")
        orders.post("credit-line", "increase", use: increaseCreditLine)
        orders.post("credit-line", "decrease", use: decreaseCreditLine)
        orders.post("factory", use: buildFactory)
        orders.post("storage", use: buildStorage)
        orders.post("raw-material", use: buyRawMaterial)
        orders.post("enter-market", use: enterMarket)
        orders.post("quality", use: increaseQuality)
        orders.post("productivity", use: increaseProductivity)
        orders.post("marketing", use: runMarketingCampaign)
        orders.post("sales-price", use: setIntendedSalesAndPrice)
    }

    // MARK: - Simulation CRUD

    @Sendable
    func index(req: Request) async throws -> [SimulationDTO] {
        let service = SimulationService(db: req.db)
        return try await service.getSimulationsForOwner(ownerId: 0) // TODO: use authenticated user
    }

    @Sendable
    func statistics(req: Request) async throws -> SimulationStatisticsDTO {
        guard let simulationId = req.parameters.get("simulationId", as: Int64.self) else {
            throw Abort(.badRequest)
        }
        let service = SimulationService(db: req.db)
        return try await service.getSimulationStatistics(simulationId: simulationId)
    }

    @Sendable
    func create(req: Request) async throws -> SimulationDTO {
        let dto = try req.content.decode(SimulationDTO.self)
        let simulation = SimulationModel()
        simulation.name = dto.name ?? "<no name>"
        simulation.startMonth = dto.startMonth ?? "2020-01"
        simulation.nrOfMonths = dto.nrOfMonths ?? 12
        simulation.isStarted = false
        simulation.isFinished = false
        simulation.interestRate = 0.05
        simulation.buildingMaintenanceAmount = 10000
        simulation.buildingMaintenanceCurrency = "CHF"
        simulation.depreciationRate = 0.15
        simulation.headquarterAmount = 1500000
        simulation.headquarterCurrency = "CHF"
        simulation.productionCostAmount = 30
        simulation.productionCostCurrency = "CHF"
        try await simulation.save(on: req.db)
        return SimulationDTO(id: simulation.id, name: simulation.name, startMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths)
    }

    @Sendable
    func update(req: Request) async throws -> SimulationDTO {
        guard let simulationId = req.parameters.get("simulationId", as: Int64.self) else {
            throw Abort(.badRequest)
        }
        let dto = try req.content.decode(SimulationDTO.self)
        guard let simulation = try await SimulationModel.find(simulationId, on: req.db) else {
            throw Abort(.notFound)
        }
        guard !simulation.isStarted else {
            throw Abort(.conflict, reason: "Cannot update a started simulation")
        }
        if let name = dto.name { simulation.name = name }
        if let startMonth = dto.startMonth { simulation.startMonth = startMonth }
        if let nrOfMonths = dto.nrOfMonths { simulation.nrOfMonths = nrOfMonths }
        try await simulation.save(on: req.db)
        return SimulationDTO(id: simulation.id, name: simulation.name, startMonth: simulation.startMonth, nrOfMonths: simulation.nrOfMonths)
    }

    // MARK: - Company Step

    @Sendable
    func getCurrentStep(req: Request) async throws -> CompanySimulationStepDTO {
        guard let companyId = req.parameters.get("companyId", as: Int64.self) else {
            throw Abort(.badRequest)
        }
        let service = SimulationService(db: req.db)
        guard let step = try await service.getCurrentCompanySimulationStep(companyId: companyId) else {
            throw Abort(.notFound, reason: "No active simulation step")
        }
        return step
    }

    @Sendable
    func finishMove(req: Request) async throws -> HTTPStatus {
        guard let companyId = req.parameters.get("companyId", as: Int64.self) else {
            throw Abort(.badRequest)
        }
        // Find the open company simulation step
        guard let step = try await CompanySimulationStepModel.query(on: req.db)
            .filter(\.$company.$id == companyId)
            .filter(\.$isOpen == true)
            .first() else {
            throw Abort(.notFound, reason: "No open step for company")
        }
        let service = SimulationService(db: req.db)
        try await service.finishMoveFor(companySimulationStepId: step.id!)
        return .ok
    }

    // MARK: - Orders

    @Sendable
    func increaseCreditLine(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(AdjustCreditLineDTO.self)
        try AdjustCreditLineDTO.validate(content: req)
        let service = SimulationService(db: req.db)
        try await service.increaseCreditLine(dto)
        return .created
    }

    @Sendable
    func decreaseCreditLine(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(AdjustCreditLineDTO.self)
        try AdjustCreditLineDTO.validate(content: req)
        let service = SimulationService(db: req.db)
        try await service.decreaseCreditLine(dto)
        return .created
    }

    @Sendable
    func buildFactory(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(BuildFactoryDTO.self)
        try BuildFactoryDTO.validate(content: req)
        let service = SimulationService(db: req.db)
        try await service.buildFactory(dto)
        return .created
    }

    @Sendable
    func buildStorage(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(BuildStorageDTO.self)
        try BuildStorageDTO.validate(content: req)
        let service = SimulationService(db: req.db)
        try await service.buildStorage(dto)
        return .created
    }

    @Sendable
    func buyRawMaterial(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(BuyRawMaterialDTO.self)
        try BuyRawMaterialDTO.validate(content: req)
        let service = SimulationService(db: req.db)
        try await service.buyRawMaterial(dto)
        return .created
    }

    @Sendable
    func enterMarket(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(EnterMarketDTO.self)
        try EnterMarketDTO.validate(content: req)
        let service = SimulationService(db: req.db)
        try await service.enterMarket(dto)
        return .created
    }

    @Sendable
    func increaseQuality(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(IncreaseQualityDTO.self)
        let service = SimulationService(db: req.db)
        try await service.increaseQuality(dto)
        return .created
    }

    @Sendable
    func increaseProductivity(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(IncreaseProductivityDTO.self)
        let service = SimulationService(db: req.db)
        try await service.increaseProductivity(dto)
        return .created
    }

    @Sendable
    func runMarketingCampaign(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(RunMarketingCampaignDTO.self)
        let service = SimulationService(db: req.db)
        try await service.runMarketingCampaign(dto)
        return .created
    }

    @Sendable
    func setIntendedSalesAndPrice(req: Request) async throws -> HTTPStatus {
        let dto = try req.content.decode(IntendedSalesAndPriceDTO.self)
        let service = SimulationService(db: req.db)
        try await service.setIntendedSalesAndPrice(dto)
        return .created
    }
}
