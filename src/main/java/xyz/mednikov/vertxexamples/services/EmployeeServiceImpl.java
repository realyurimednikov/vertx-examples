package xyz.mednikov.vertxexamples.services;

import io.vertx.core.Future;
import xyz.mednikov.vertxexamples.models.Employee;
import xyz.mednikov.vertxexamples.models.EmployeesList;
import xyz.mednikov.vertxexamples.repositories.EmployeesRepository;

import java.util.Optional;

public record EmployeeServiceImpl(EmployeesRepository repository) implements EmployeeService{


    @Override
    public Future<Employee> createEmployee(Employee employee) {
        return repository().createEmployee(employee);
    }

    @Override
    public Future<Employee> updateEmployee(Employee employee) {
        return repository().updateEmployee(employee);
    }

    @Override
    public Future<Optional<Employee>> findEmployeeById(String id) {
        return repository().findEmployeeById(id);
    }

    @Override
    public Future<Void> removeEmployee(String id) {
        return repository().removeEmployee(id);
    }

    @Override
    public Future<EmployeesList> findAllEmployees() {
        return repository().findAllEmployees();
    }
}
