import Fluent

struct CreateInitialSchema: AsyncMigration {
    func prepare(on database: Database) async throws {
        // Settings
        try await database.schema("epoc_settings")
            .field("id", .int64, .identifier(auto: true))
            .field("is_template", .bool, .required)
            .create()

        try await database.schema("epoc_setting")
            .field("id", .int64, .identifier(auto: true))
            .field("settings_id", .int64, .required, .references("epoc_settings", "id", onDelete: .cascade))
            .field("setting_key", .string, .required)
            .field("setting_format", .string)
            .field("value_text", .string, .required)
            .field("description", .string)
            .create()

        // Markets
        try await database.schema("markets")
            .field("id", .int64, .identifier(auto: true))
            .field("name", .string, .required)
            .field("gdp_amount", .double)
            .field("gdp_currency", .string)
            .field("gdp_ppp_amount", .double)
            .field("gdp_ppp_currency", .string)
            .field("gdp_growth", .double)
            .field("unemployment", .double)
            .field("life_expectancy", .double)
            .field("labor_force", .int)
            .field("cost_to_enter_market_amount", .double)
            .field("cost_to_enter_market_currency", .string)
            .field("distribution_cost_amount", .double)
            .field("distribution_cost_currency", .string)
            .field("age_to14_male", .int, .required)
            .field("age_to14_female", .int, .required)
            .field("age_to24_male", .int, .required)
            .field("age_to24_female", .int, .required)
            .field("age_to54_male", .int, .required)
            .field("age_to54_female", .int, .required)
            .field("age_to64_male", .int, .required)
            .field("age_to64_female", .int, .required)
            .field("age65older_male", .int, .required)
            .field("age65older_female", .int, .required)
            .create()

        // Simulations
        try await database.schema("simulations")
            .field("id", .int64, .identifier(auto: true))
            .field("name", .string, .required)
            .field("start_month", .string, .required)
            .field("nr_of_months", .int, .required)
            .field("is_started", .bool, .required)
            .field("is_finished", .bool, .required)
            .field("interest_rate", .double, .required)
            .field("maintenance_amount", .double, .required)
            .field("maintenance_currency", .string, .required)
            .field("depreciation_rate", .double, .required)
            .field("headquarter_amount", .double, .required)
            .field("headquarter_currency", .string, .required)
            .field("production_cost_amount", .double, .required)
            .field("production_cost_currency", .string, .required)
            .field("settings_id", .int64, .references("epoc_settings", "id"))
            .create()

        // Market Simulations
        try await database.schema("market_simulations")
            .field("id", .int64, .identifier(auto: true))
            .field("market_id", .int64, .required, .references("markets", "id"))
            .field("simulation_id", .int64, .required, .references("simulations", "id", onDelete: .cascade))
            .field("start_month", .string, .required)
            .field("higher_price_amount", .double, .required)
            .field("higher_price_currency", .string, .required)
            .field("higher_percent", .double, .required)
            .field("lower_price_amount", .double, .required)
            .field("lower_price_currency", .string, .required)
            .field("lower_percent", .double, .required)
            .field("product_lifecycle_duration", .int, .required)
            .create()

        // Financial Accounting
        try await database.schema("financial_accountings")
            .field("id", .int64, .identifier(auto: true))
            .field("base_currency", .string, .required)
            .create()

        // Companies
        try await database.schema("companies")
            .field("id", .int64, .identifier(auto: true))
            .field("name", .string, .required)
            .field("simulation_id", .int64, .required, .references("simulations", "id", onDelete: .cascade))
            .field("quality_factor", .double, .required)
            .field("marketing_factor", .double, .required)
            .field("productivity_factor", .double, .required)
            .field("accounting_id", .int64, .references("financial_accountings", "id"))
            .create()

        // Accounts
        try await database.schema("accounts")
            .field("id", .int64, .identifier(auto: true))
            .field("accounting_id", .int64, .required, .references("financial_accountings", "id", onDelete: .cascade))
            .field("account_type", .string, .required)
            .field("number", .string, .required)
            .field("name", .string, .required)
            .field("start_balance", .double, .required)
            .create()

        // Journal Entries
        try await database.schema("journal_entries")
            .field("id", .int64, .identifier(auto: true))
            .field("accounting_id", .int64, .required, .references("financial_accountings", "id", onDelete: .cascade))
            .field("booking_text", .string, .required)
            .field("booking_date", .date, .required)
            .field("value_date", .date, .required)
            .create()

        // Bookings
        try await database.schema("bookings")
            .field("id", .int64, .identifier(auto: true))
            .field("journal_entry_id", .int64, .required, .references("journal_entries", "id", onDelete: .cascade))
            .field("debit_account_id", .int64, .required, .references("accounts", "id"))
            .field("credit_account_id", .int64, .required, .references("accounts", "id"))
            .field("amount", .double, .required)
            .create()

        // Factories
        try await database.schema("factories")
            .field("id", .int64, .identifier(auto: true))
            .field("company_id", .int64, .required, .references("companies", "id", onDelete: .cascade))
            .field("production_lines", .int, .required)
            .field("production_start_month", .string, .required)
            .field("daily_capacity_per_production_line", .int, .required)
            .field("labour_cost_amount", .double, .required)
            .field("labour_cost_currency", .string, .required)
            .create()

        // Storages
        try await database.schema("storages")
            .field("id", .int64, .identifier(auto: true))
            .field("company_id", .int64, .required, .references("companies", "id", onDelete: .cascade))
            .field("capacity", .int, .required)
            .field("storage_start_month", .string, .required)
            .field("stored_products", .int, .required)
            .field("stored_raw_materials", .int, .required)
            .field("inventory_management_cost_amount", .double, .required)
            .field("inventory_management_cost_currency", .string, .required)
            .create()

        // Distribution in Markets
        try await database.schema("distribution_in_markets")
            .field("id", .int64, .identifier(auto: true))
            .field("company_id", .int64, .required, .references("companies", "id", onDelete: .cascade))
            .field("market_simulation_id", .int64, .required, .references("market_simulations", "id", onDelete: .cascade))
            .field("offered_price_amount", .double)
            .field("offered_price_currency", .string)
            .field("intented_product_sale", .int)
            .create()

        // Simulation Steps
        try await database.schema("simulation_steps")
            .field("id", .int64, .identifier(auto: true))
            .field("simulation_id", .int64, .required, .references("simulations", "id", onDelete: .cascade))
            .field("simulation_month", .string, .required)
            .field("is_open", .bool, .required)
            .create()

        // Company Simulation Steps
        try await database.schema("company_simulation_steps")
            .field("id", .int64, .identifier(auto: true))
            .field("simulation_step_id", .int64, .required, .references("simulation_steps", "id", onDelete: .cascade))
            .field("company_id", .int64, .required, .references("companies", "id", onDelete: .cascade))
            .field("is_open", .bool, .required)
            .create()

        // Distribution Steps
        try await database.schema("distribution_steps")
            .field("id", .int64, .identifier(auto: true))
            .field("distribution_in_market_id", .int64, .required, .references("distribution_in_markets", "id", onDelete: .cascade))
            .field("company_simulation_step_id", .int64, .required, .references("company_simulation_steps", "id", onDelete: .cascade))
            .field("sold_products", .int, .required)
            .field("intented_product_sale", .int, .required)
            .field("offered_price_amount", .double)
            .field("offered_price_currency", .string)
            .field("market_potential_for_product", .int, .required)
            .create()

        // Simulation Orders
        try await database.schema("simulation_orders")
            .field("id", .int64, .identifier(auto: true))
            .field("company_id", .int64, .required, .references("companies", "id", onDelete: .cascade))
            .field("order_type", .string, .required)
            .field("execution_month", .string, .required)
            .field("is_executed", .bool, .required)
            .field("amount_value", .double)
            .field("amount_currency", .string)
            .field("int_param1", .int)
            .field("int_param2", .int)
            .field("double_param1", .double)
            .field("string_param1", .string)
            .field("money_param1_amount", .double)
            .field("money_param1_currency", .string)
            .field("money_param2_amount", .double)
            .field("money_param2_currency", .string)
            .field("market_simulation_id", .int64)
            .field("market_id", .int64)
            .create()

        // Messages
        try await database.schema("messages")
            .field("id", .int64, .identifier(auto: true))
            .field("company_id", .int64, .required, .references("companies", "id", onDelete: .cascade))
            .field("relevant_month", .string, .required)
            .field("message", .string, .required)
            .field("level", .string, .required)
            .create()
    }

    func revert(on database: Database) async throws {
        let tables = [
            "messages", "simulation_orders", "distribution_steps",
            "company_simulation_steps", "simulation_steps",
            "distribution_in_markets", "storages", "factories",
            "bookings", "journal_entries", "accounts", "financial_accountings",
            "companies", "market_simulations", "simulations",
            "markets", "epoc_setting", "epoc_settings"
        ]
        for table in tables {
            try await database.schema(table).delete()
        }
    }
}
