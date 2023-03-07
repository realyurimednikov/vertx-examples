package xyz.mednikov.vertxexamples.repositories;

import com.github.javafaker.Faker;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.mednikov.vertxexamples.models.Employee;

@ExtendWith(VertxExtension.class)
@Testcontainers
class EmployeesRepositoryMongoImplTest {

    private EmployeesRepositoryMongoImpl repository;

    @Container
    MongoDBContainer container = new MongoDBContainer("mongo:focal");

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
        String connectionString = "mongodb://localhost:" + container.getFirstMappedPort();
        JsonObject config = new JsonObject();
        config.put("connection_string", connectionString);

        MongoClient client = MongoClient.create(vertx, config);
        repository = new EmployeesRepositoryMongoImpl(client);

        context.completeNow();

    }

    @Test
    void createEmployeeTest(Vertx vertx, VertxTestContext context){
        Employee mock = createMockEmployee();
        context.verify(() -> repository.createEmployee(mock)
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    Assertions.assertNotNull(result.id());
                    context.completeNow();
                })
        );
    }

    @Test
    void findEmployeeByIdExistsTest(Vertx vertx, VertxTestContext context){
        Employee mock = createMockEmployee();
        context.verify(() -> repository.createEmployee(mock)
                .map(employee -> {
                    String id = employee.id();
                    Assertions.assertNotNull(id);
                    return id;
                })
                .flatMap(id -> repository.findEmployeeById(id))
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    Assertions.assertTrue(result.isPresent());
                    context.completeNow();
                })
        );
    }

    @Test
    void findEmployeeByIdDoesNotExistTest(Vertx vertx, VertxTestContext context){
        context.verify(() -> repository.findEmployeeById("some-fake-id")
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    Assertions.assertTrue(result.isEmpty());
                    context.completeNow();
                })
        );
    }

    @Test
    void updateEmployeeTest(Vertx vertx, VertxTestContext context){
        Employee mock = createMockEmployee();
        context.verify(() -> repository.createEmployee(mock)
                .map(result -> {
                    String id = result.id();
                    Assertions.assertNotNull(id);
                    Employee update = new Employee(id, "Yuri", "Mednikov", mock.position(), mock.phone());
                    return update;
                })
                .flatMap(result -> repository.updateEmployee(result))
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    Assertions.assertEquals("Yuri", result.firstName());
                    Assertions.assertEquals("Mednikov", result.lastName());
                    context.completeNow();
                })
        );
    }

    @Test
    void removeEmployeeTest(Vertx vertx, VertxTestContext context){
        Employee mock = createMockEmployee();
        context.verify(() -> repository.createEmployee(mock)
                .map(result -> {
                    String id = result.id();
                    Assertions.assertNotNull(id);
                    return id;
                })
                .flatMap(result -> repository.removeEmployee(result))
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> context.completeNow())
        );
    }

    @Test
    void findAllEmployeesTest(Vertx vertx, VertxTestContext context){
        Employee mock1 = createMockEmployee();
        Employee mock2 = createMockEmployee();
        Employee mock3 = createMockEmployee();
        context.verify(() -> CompositeFuture.all(
                repository.createEmployee(mock1),
                repository.createEmployee(mock2),
                repository.createEmployee(mock3)
        ).flatMap(result -> repository.findAllEmployees())
                        .onFailure(err -> context.failNow(err))
                        .onSuccess(result -> {
                            Assertions.assertEquals(3, result.employees().size());
                            context.completeNow();
                        })
        );
    }

}
