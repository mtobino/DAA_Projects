package extraCreditProject;

public abstract class Employee{
    String id;
    String name;

    public Employee(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public abstract String toString();

}
