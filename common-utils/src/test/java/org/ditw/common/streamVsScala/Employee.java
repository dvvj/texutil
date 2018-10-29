package org.ditw.common.streamVsScala;

import java.util.*;

class Employee {
    final int empNo;
    final String firstName;
    final String lastName;

    Employee(
        int empNo,
        String firstName,
        String lastName
    ) {
        this.empNo = empNo;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("(%d) %s %s", empNo, firstName, lastName);
    }

    final static Comparator<Employee> byEmpNo = new Comparator<Employee>() {
        @Override
        public int compare(Employee o1, Employee o2) {
            return o1.empNo - o2.empNo;
        }
    };

    static void trace(Collection<Employee> employees) {
        if (employees.isEmpty())
            return;

        StringBuilder b = new StringBuilder();
        Iterator<Employee> it = employees.iterator();

        b.append(it.next());
        while (it.hasNext()) {
            b.append("\n");
            b.append(it.next());
        }
        System.out.println(b);
    }


    static List<Employee> createTestList() {
        List<Employee> res = new ArrayList<>();

        res.add(new Employee(123, "Jack", "Johnson"));
        res.add(new Employee(124, "jackie", "Johnson"));
        res.add(new Employee(345, "Cindy", "Bower"));
        res.add(new Employee(567, "Perry", "Node"));
        res.add(new Employee(467, "Pam", "Krauss"));
        res.add(new Employee(435, "Fred", "Shak"));
        res.add(new Employee(678, "Ann", "Lee"));

        return res;
    }
}
