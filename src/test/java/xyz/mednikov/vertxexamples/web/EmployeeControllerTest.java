package xyz.mednikov.vertxexamples.web;

import com.github.javafaker.Faker;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.mednikov.vertxexamples.models.Employee;
import xyz.mednikov.vertxexamples.services.EmployeeService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@ExtendWith(VertxExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService service;

    @InjectMocks
    private EmployeeController controller;

    Employee createMockEmployee () {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String position = faker.job().position();
        String phone = faker.phoneNumber().phoneNumber();
        Employee employee = new Employee(null, firstName, lastName, position, phone);
        return employee;
    }

    @BeforeEach
    void setup (Vertx vertx, VertxTestContext context){
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/api/*").handler(BodyHandler.create());
        controller.setRoutes(router);
        server.requestHandler(router);
        server.listen(8000).onSuccess(result -> context.completeNow())
                .onFailure(err -> context.failNow(err));
    }

    @Test
    void createEmployeeTest(Vertx vertx, VertxTestContext context){
        WebClient client = WebClient.create(vertx);
        Employee mock = createMockEmployee();
        JsonObject body = JsonObject.mapFrom(mock);
        Future<Employee> future = Future.succeededFuture(mock);
        Mockito.when(service.createEmployee(Mockito.any())).thenReturn(future);
        context.verify(() -> client.postAbs("http://localhost:8000/api/employees")
                .as(BodyCodec.jsonObject())
                .sendJsonObject(body)
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    int status = result.statusCode();
                    Assertions.assertEquals(201, status);
                    context.completeNow();
                })
        );
    }

    @Test
    void updateEmployeeTest(Vertx vertx, VertxTestContext context){
        WebClient client = WebClient.create(vertx);
        Employee mock = createMockEmployee();
        JsonObject body = JsonObject.mapFrom(mock);
        Future<Employee> future = Future.succeededFuture(mock);
        Mockito.when(service.updateEmployee(Mockito.any())).thenReturn(future);
        context.verify(() -> client.putAbs("http://localhost:8000/api/employees")
                .as(BodyCodec.jsonObject())
                .sendJsonObject(body)
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    int status = result.statusCode();
                    Assertions.assertEquals(200, status);
                    context.completeNow();
                })
        );
    }

    @Test
    void findEmployeeByIdExistsTest(Vertx vertx, VertxTestContext context){
        WebClient client = WebClient.create(vertx);
        Employee mock = createMockEmployee();
        Future<Optional<Employee>> future = Future.succeededFuture(Optional.of(mock));
        Mockito.when(service.findEmployeeById("my-id")).thenReturn(future);

        context.verify(() -> client.getAbs("http://localhost:8000/api/employees/my-id")
                .as(BodyCodec.jsonObject())
                .send()
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    int status = result.statusCode();
                    Assertions.assertEquals(200, status);
                    context.completeNow();
                })
        );
    }

    @Test
    void findEmployeeByIdDoesNotExistTest(Vertx vertx, VertxTestContext context){
        WebClient client = WebClient.create(vertx);
        Future<Optional<Employee>> future = Future.succeededFuture(Optional.empty());
        Mockito.when(service.findEmployeeById("my-id")).thenReturn(future);

        context.verify(() -> client.getAbs("http://localhost:8000/api/employees/my-id")
                .as(BodyCodec.jsonObject())
                .send()
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    int status = result.statusCode();
                    Assertions.assertEquals(404, status);
                    context.completeNow();
                })
        );
    }

    @Test
    void removeEmployeeTest(Vertx vertx, VertxTestContext context){
        WebClient client = WebClient.create(vertx);
        Future<Void> future = Future.succeededFuture();
        Mockito.when(service.removeEmployee("my-id")).thenReturn(future);

        context.verify(() -> client.deleteAbs("http://localhost:8000/api/employees/my-id")
                .as(BodyCodec.jsonObject())
                .send()
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    int status = result.statusCode();
                    Assertions.assertEquals(204, status);
                    context.completeNow();
                })
        );
    }
}
