package xyz.mednikov.vertxexamples.repositories;

import io.vertx.core.Future;
import xyz.mednikov.vertxexamples.models.Employee;
import xyz.mednikov.vertxexamples.models.EmployeesList;

import java.util.Optional;

public interface EmployeesRepository {

    Future<Employee> createEmployee (Employee employee);

    Future<Employee> updateEmployee (Employee employee);

    Future<Optional<Employee>> findEmployeeById (String id);

    Future<Void> removeEmployee (String id);

    Future<EmployeesList> findAllEmployees ();
}
