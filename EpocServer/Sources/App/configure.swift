import Vapor
import Fluent
import FluentPostgresDriver

func configure(_ app: Application) async throws {
    app.databases.use(
        .postgres(configuration: .init(
            hostname: Environment.get("DATABASE_HOST") ?? "localhost",
            port: Environment.get("DATABASE_PORT").flatMap(Int.init) ?? SQLPostgresConfiguration.ianaPortNumber,
            username: Environment.get("DATABASE_USERNAME") ?? "epoc",
            password: Environment.get("DATABASE_PASSWORD") ?? "epoc",
            database: Environment.get("DATABASE_NAME") ?? "epoc",
            tls: .disable
        )),
        as: .psql
    )
    // Migrations
    app.migrations.add(CreateInitialSchema())
    app.migrations.add(SeedMarketData())

    try routes(app)
}
