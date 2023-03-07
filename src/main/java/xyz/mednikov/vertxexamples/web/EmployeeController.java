package xyz.mednikov.vertxexamples.web;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import xyz.mednikov.vertxexamples.models.Employee;
import xyz.mednikov.vertxexamples.services.EmployeeService;

public record EmployeeController (EmployeeService service){

    public void setRoutes (Router router){
        router.post("/api/employees").handler(this::createEmployee);
        router.put("/api/employees").handler(this::updateEmployee);
        router.delete("/api/employees/:id").handler(this::removeEmployee);
        router.get("/api/employees/:id").handler(this::findEmployeeById);
        router.get("/api/employees").handler(this::findAllEmployees);
    }

    void createEmployee (RoutingContext context) {
        JsonObject body = context.getBodyAsJson();
        System.out.println(body);

        // todo validation

        String firstName = body.getString("firstName");
        String lastName = body.getString("lastName");
        String position = body.getString("position");
        String phone = body.getString("phone");

        Employee employee = new Employee(null, firstName, lastName, position, phone);

        service().createEmployee(employee)
                .onFailure(err -> context.fail(err))
                .onSuccess(result -> {
                    JsonObject payload = new JsonObject();
                    payload.put("id", result.id());
                    payload.put("firstName", result.firstName());
                    payload.put("lastName", result.lastName());
                    payload.put("position", result.position());
                    payload.put("phone", result.phone());
                    context.response().setStatusCode(201)
                            .end(payload.encode());
                });

    }

    void updateEmployee (RoutingContext context) {
        JsonObject body = context.getBodyAsJson();

        // todo validation

        String id = body.getString("id");
        String firstName = body.getString("firstName");
        String lastName = body.getString("lastName");
        String position = body.getString("position");
        String phone = body.getString("phone");

        Employee employee = new Employee(id, firstName, lastName, position, phone);

        service().updateEmployee(employee)
                .onFailure(err -> context.fail(err))
                .onSuccess(result -> {
                    JsonObject payload = new JsonObject();
                    payload.put("id", result.id());
                    payload.put("firstName", result.firstName());
                    payload.put("lastName", result.lastName());
                    payload.put("position", result.position());
                    payload.put("phone", result.phone());
                    context.response().setStatusCode(200)
                            .end(payload.encode());
                });
    }

    void removeEmployee (RoutingContext context) {
        String id = context.pathParam("id");
        service().removeEmployee(id)
                .onFailure(err -> context.fail(err))
                .onSuccess(result -> context.response().setStatusCode(204).end());
    }

    void findEmployeeById (RoutingContext context) {
        String id = context.pathParam("id");
        service().findEmployeeById(id)
                .onFailure(err -> context.fail(err))
                .onSuccess(result -> {
                    if (result.isPresent()) {
                        Employee employee = result.get();
                        JsonObject payload = new JsonObject();
                        payload.put("id", employee.id());
                        payload.put("firstName", employee.firstName());
                        payload.put("lastName", employee.lastName());
                        payload.put("position", employee.position());
                        payload.put("phone", employee.phone());
                        context.response().setStatusCode(200).end(payload.encode());
                    } else {
                        context.response().setStatusCode(404).end();
                    }
                });
    }

    void findAllEmployees (RoutingContext context) {
        service().findAllEmployees()
                .onFailure(err -> context.fail(err))
                .onSuccess(list -> {
                    JsonArray employees = new JsonArray();
                    for (Employee e : list.employees()) {
                        JsonObject payload = new JsonObject();
                        payload.put("id", e.id());
                        payload.put("firstName", e.firstName());
                        payload.put("lastName", e.lastName());
                        payload.put("position", e.position());
                        payload.put("phone", e.phone());
                        employees.add(payload);
                    }
                    JsonObject body = new JsonObject();
                    body.put("employees", employees);
                    context.response().setStatusCode(200).end(body.encode());
                });
    }
}
