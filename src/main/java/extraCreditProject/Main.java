package extraCreditProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static List<Student> studentWorkers = new LinkedList<Student>();
    static List<Teacher> teacherList = new LinkedList<Teacher>();
    static HashMap<String, Employee> employees = new HashMap<String, Employee>();
    public static void main(String[] args)
    {
        int input;
        try {
            File file = new File("Staff.txt");
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] elements = line.split(" ");
                if(elements[0].startsWith("841"))
                {
                    employees.put(elements[0], new Teacher(elements[0],elements[1] + " " + elements[2]));
                }
                else{
                    employees.put(elements[0], new Student(elements[0],elements[1] + " " + elements[2]));
                }
            }
            scanner.close();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        employees.forEach( (id, employee) ->{
            if(id.startsWith("841")) {
                teacherList.add((Teacher) employee);
            }
            else{
                studentWorkers.add((Student) employee);
            }
        });

        try{
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter("StaffRoster.txt")));
            pw.println("Student Workers");
            for (Student studentWorker : studentWorkers)
            {
                pw.println(studentWorker);
            }
            pw.println("\nTeaching Staff");
            for(Teacher teacher: teacherList)
            {
                pw.println(teacher);
            }
            pw.close();

        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}