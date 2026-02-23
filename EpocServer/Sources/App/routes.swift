import Vapor

func routes(_ app: Application) throws {
    app.get { req async in
        "EPOC Simulation Server"
    }
    app.get("health") { req async in
        "OK"
    }
}
