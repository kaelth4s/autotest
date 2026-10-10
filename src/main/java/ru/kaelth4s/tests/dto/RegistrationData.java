package ru.kaelth4s.tests.dto;

import ru.kaelth4s.framework.dto.BaseTableData;
import java.util.Objects;

import static ru.kaelth4s.framework.util.ParserUtil.parseInt;

public class RegistrationData extends BaseTableData {
    private String firstName;
    private String lastName;
    private String email;
    private int age;
    private int salary;
    private String department;

    public RegistrationData() {}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public int getSalary() {
        return salary;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    protected void fillFields(int rowIndex) {
        firstName = getCellText("First Name");
        lastName = getCellText("Last Name");
        email = getCellText("Email");
        age = parseInt(getCellText("Age"));
        salary = parseInt(getCellText("Salary"));
        department = getCellText("Department");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationData that = (RegistrationData) o;
        return age == that.age &&
                salary == that.salary &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(email, that.email) &&
                Objects.equals(department, that.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, age, salary, department);
    }
}