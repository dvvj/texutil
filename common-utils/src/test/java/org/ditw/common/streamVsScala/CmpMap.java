package org.ditw.common.streamVsScala;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CmpMap {
    public static void main(String[] args) {
        List<Employee> employees = Employee.createTestList();

        System.out.println("----- Create Map: employee number -> employee");
        Map<Integer, Employee> empNo2Emp = employees.stream()
            .collect(
                Collectors.toMap(emp -> emp.empNo, Function.identity())
            );
        System.out.println(empNo2Emp.size());

        System.out.println("----- Filtering Map: first name != 'Ann'");
        Map<Integer, Employee> empNo2EmpWOAnn = empNo2Emp.entrySet().stream()
            .filter(p -> !p.getValue().firstName.equalsIgnoreCase("Ann"))
            .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        System.out.println(empNo2EmpWOAnn.size());


    }
}
