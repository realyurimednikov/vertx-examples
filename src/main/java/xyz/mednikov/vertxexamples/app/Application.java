package xyz.mednikov.vertxexamples.app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import xyz.mednikov.vertxexamples.web.EmployeeController;

public class Application extends AbstractVerticle {

    private final EmployeeController employeeController;

    public Application(EmployeeController employeeController) {
        this.employeeController = employeeController;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/*").handler(BodyHandler.create());

        employeeController.setRoutes(router);

        int port = config().getInteger("port", 8080);

        server.requestHandler(router);
        server.listen(port)
                .onFailure(err -> startPromise.fail(err))
                .onSuccess(result -> {
                    System.out.println("The server started on the port " + port);
                    startPromise.complete();
                });
    }

    public static void main(String[] args) {
        // todo implement main()
        // todo implement config retriever
        // todo implement di
    }
}
