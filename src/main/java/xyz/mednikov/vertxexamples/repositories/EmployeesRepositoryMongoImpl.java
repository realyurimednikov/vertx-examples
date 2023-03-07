package xyz.mednikov.vertxexamples.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import xyz.mednikov.vertxexamples.models.Employee;
import xyz.mednikov.vertxexamples.models.EmployeesList;

import java.util.Optional;
import java.util.stream.Collectors;

public record EmployeesRepositoryMongoImpl(MongoClient client) implements EmployeesRepository {

    private final static String COLLECTION_NAME = "employees";

    @Override
    public Future<Employee> createEmployee(Employee employee) {
        JsonObject document = new JsonObject();
        document.put("firstName", employee.firstName());
        document.put("lastName", employee.lastName());
        document.put("position", employee.position());
        document.put("phone", employee.phone());
        return client().save(COLLECTION_NAME, document).map(id -> {
            String firstName = employee.firstName();
            String lastName = employee.lastName();
            String position = employee.position();
            String phone = employee.phone();
            Employee result = new Employee(id, firstName, lastName, position, phone);
            return result;
        });
    }

    @Override
    public Future<Employee> updateEmployee(Employee employee) {
        JsonObject query = new JsonObject();
        JsonObject update = new JsonObject();
        JsonObject operation = new JsonObject();

        query.put("_id", employee.id());

        update.put("firstName", employee.firstName());
        update.put("lastName", employee.lastName());
        update.put("position", employee.position());
        update.put("phone", employee.phone());

        operation.put("$set", update);

//        return client().findOneAndUpdate(COLLECTION_NAME, query, operation).map(document -> {
//            String documentId = document.getString("_id");
//            String firstName = document.getString("firstName");
//            String lastName = document.getString("lastName");
//            String phone = document.getString("phone");
//            String position = document.getString("position");
//            Employee result = new Employee(documentId, firstName, lastName, position, phone);
//            return result;
//        });

        return client().updateCollection(COLLECTION_NAME, query, operation).flatMap(result -> findEmployeeById(employee.id())).map(result -> result.get());
    }

    @Override
    public Future<Optional<Employee>> findEmployeeById(String id) {
        JsonObject query = new JsonObject();
        query.put("_id", id);
        return client().findOne(COLLECTION_NAME, query, null)
                .map(Optional::ofNullable)
                .map(result -> result.map(document -> {
                    String documentId = document.getString("_id");
                    String firstName = document.getString("firstName");
                    String lastName = document.getString("lastName");
                    String phone = document.getString("phone");
                    String position = document.getString("position");
                    Employee employee = new Employee(documentId, firstName, lastName, position, phone);
                    return employee;
                }));
    }

    @Override
    public Future<Void> removeEmployee(String id) {
        JsonObject query = new JsonObject();
        query.put("_id", id);
        return client().removeDocument(COLLECTION_NAME, query).flatMap(result -> {
            if (result.getRemovedCount() == 1) {
                return Future.succeededFuture();
            } else {
                return Future.failedFuture(new RuntimeException());
            }
        });
    }

    @Override
    public Future<EmployeesList> findAllEmployees() {
        return client().find(COLLECTION_NAME, new JsonObject())
                .map(list -> list.stream().map(document -> {
                    String documentId = document.getString("_id");
                    String firstName = document.getString("firstName");
                    String lastName = document.getString("lastName");
                    String phone = document.getString("phone");
                    String position = document.getString("position");
                    Employee employee = new Employee(documentId, firstName, lastName, position, phone);
                    return employee;
                }).collect(Collectors.toList()))
                .map(result -> new EmployeesList(result));
    }
}
