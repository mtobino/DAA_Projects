package extraCreditProject;

public class Student extends Employee{
    public Student(String id, String name)
    {
        super(id, name);
    }
    public String toString()
    {
        return "Student ID: " + id + ", Name: " + name;
    }
}
