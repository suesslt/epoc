import Vapor
import Fluent
import FluentPostgresDriver

func configure(_ app: Application) async throws {
    let hostname = Environment.get("DATABASE_HOST") ?? "localhost"
    let port = Environment.get("DATABASE_PORT").flatMap(Int.init) ?? SQLPostgresConfiguration.ianaPortNumber
    let username = Environment.get("DATABASE_USERNAME") ?? "epoc"
    let password = Environment.get("DATABASE_PASSWORD") ?? "epoc"
    let database = Environment.get("DATABASE_NAME") ?? "epoc"
    let config = SQLPostgresConfiguration(
        hostname: hostname,
        port: port,
        username: username,
        password: password,
        database: database,
        tls: .disable
    )
    app.databases.use(.postgres(configuration: config), as: .psql)
    // Migrations
    app.migrations.add(CreateInitialSchema())
    app.migrations.add(SeedMarketData())

    try routes(app)
}
