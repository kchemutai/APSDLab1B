package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1021L, "Daniel", "Agar", LocalDate.of(2018,1,17),945.50, new PensionPlan("EX1089", LocalDate.of(2023, 1, 17), 100.00)));
        employees.add(new Employee(1022L, "Benard", "Show", LocalDate.of(2019,5,3),197750.00, null));
        employees.add(new Employee(1023L, "Carly", "Agar", LocalDate.of(2014,5,16),842000.75, new PensionPlan("SM2307", LocalDate.of(2019,11,4), 1555.50)));
        employees.add(new Employee(1024L, "Wesley", "Schneider", LocalDate.of(2019,5,2),74500.00, null));

        //sort the employees
        List<Employee> sortedEmployees = employees.stream().sorted(Comparator.comparing(Employee::getLastName))
                .sorted(Comparator.comparing((Employee::getYearlySalary)).reversed())
                .collect(Collectors.toList());

        System.out.println("Sorted employees:");
        printEmployeesInJson(sortedEmployees);
        System.out.println();

        List<Employee> monthlyUpcomingEnrollees= getMonthlyUpcomingEnrollees(sortedEmployees);

        System.out.println("Monthly Upcoming Enrollees report:");
        printEmployeesInJson(monthlyUpcomingEnrollees);
    }

    private static List<Employee> getMonthlyUpcomingEnrollees(List<Employee> employees) {
        LocalDate now = LocalDate.now();
        LocalDate nextMonthStart = now.plusMonths(1).withDayOfMonth(1);
        LocalDate nextMonthEnd = nextMonthStart.withDayOfMonth(nextMonthStart.lengthOfMonth());

        return employees.stream()
                .filter(e -> e.getPensionPlan() == null)
                .filter(e -> ChronoUnit.YEARS.between(e.getEmploymentDate(), nextMonthEnd) >= 5)
                .collect(Collectors.toList());
    }

    private static  void printEmployeesInJson(List<Employee> employees){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            // Convert list of employees to JSON string
            String jsonString = objectMapper.writeValueAsString(employees);
            // Print the JSON string
            System.out.println(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
