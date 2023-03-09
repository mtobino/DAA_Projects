package extraCreditProject;

public class Teacher extends Employee{

    public Teacher(String id, String name)
    {
        super(id, name);
    }
    public String toString()
    {
        return "Teacher ID: " + id + ", Name: " + name;
    }
}
