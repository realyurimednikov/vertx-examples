package xyz.mednikov.vertxexamples.services;

import com.github.javafaker.Faker;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.mednikov.vertxexamples.models.Employee;
import xyz.mednikov.vertxexamples.models.EmployeesList;
import xyz.mednikov.vertxexamples.repositories.EmployeesRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeesRepository repository;

    @InjectMocks
    private EmployeeServiceImpl service;

    Employee createMockEmployee () {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String position = faker.job().position();
        String phone = faker.phoneNumber().phoneNumber();
        Employee employee = new Employee(null, firstName, lastName, position, phone);
        return employee;
    }

    @Test
    void createEmployeeTest(Vertx vertx, VertxTestContext context){
        Employee mock = createMockEmployee();

        Future<Employee> future = Future.succeededFuture(mock);

        Mockito.when(repository.createEmployee(Mockito.any())).thenReturn(future);

        context.verify(() -> service.createEmployee(mock)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    Assertions.assertEquals(mock.firstName(), result.firstName());
                    Assertions.assertEquals(mock.lastName(), result.lastName());
                    Assertions.assertEquals(mock.phone(), result.phone());
                    Assertions.assertEquals(mock.position(), result.position());
                    context.completeNow();
                })
        );
    }

    @Test
    void updateEmployeeTest(Vertx vertx, VertxTestContext context){
        Employee mock = createMockEmployee();

        Future<Employee> future = Future.succeededFuture(mock);

        Mockito.when(repository.updateEmployee(Mockito.any())).thenReturn(future);

        context.verify(() -> service.updateEmployee(mock)
                .onFailure(context::failNow)
                .onSuccess(result -> {
                    Assertions.assertEquals(mock.firstName(), result.firstName());
                    Assertions.assertEquals(mock.lastName(), result.lastName());
                    Assertions.assertEquals(mock.phone(), result.phone());
                    Assertions.assertEquals(mock.position(), result.position());
                    context.completeNow();
                })
        );
    }

    @Test
    void findEmployeeByIdExistsTest(Vertx vertx, VertxTestContext context){
        Employee mock = createMockEmployee();
        String id = "id";

        Future<Optional<Employee>> future = Future.succeededFuture(Optional.of(mock));

        Mockito.when(repository.findEmployeeById(id)).thenReturn(future);

        context.verify(() -> service.findEmployeeById(id)
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    Assertions.assertTrue(result.isPresent());
                    context.completeNow();
                })
        );
    }


    @Test
    void findEmployeeByIdDoesNotExistTest(Vertx vertx, VertxTestContext context){
        String id = "id";

        Future<Optional<Employee>> future = Future.succeededFuture(Optional.empty());

        Mockito.when(repository.findEmployeeById(id)).thenReturn(future);

        context.verify(() -> service.findEmployeeById(id)
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    Assertions.assertTrue(result.isEmpty());
                    context.completeNow();
                })
        );
    }

    @Test
    void findAllEmployeesTest(Vertx vertx, VertxTestContext context){
        List<Employee> employees = List.of(createMockEmployee(), createMockEmployee(), createMockEmployee());
        EmployeesList employeesList = new EmployeesList(employees);

        Future<EmployeesList> future = Future.succeededFuture(employeesList);

        Mockito.when(repository.findAllEmployees()).thenReturn(future);

        context.verify(() -> service.findAllEmployees()
                .onFailure(err -> context.failNow(err))
                .onSuccess(result -> {
                    int size = result.employees().size();
                    Assertions.assertEquals(3, size);
                    context.completeNow();
                })
        );
    }
}
