package bruteForceProject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Main Driver for project 1, iterates through all the files provided to find a test case that will
 * satisfy the solution.
 *
 * @author Matthew Tobino
 */
public class Main
{
    public static void main(String[] args)
    {
        long programStart = System.currentTimeMillis();
        ArrayList<Clause> clauses = new ArrayList<>();
        Boolean[] testValues = new Boolean[0];
        Scanner scanner;
        File[] files = new File("bruteForceInputs").listFiles();
        assert files != null;
        for(File file : files)
        {
            try
            {
                scanner = new Scanner(file);
                int numOfVariables = 0;
                int numOfClauses;
                while(scanner.hasNextLine())
                {
                    String line = scanner.nextLine();
                    if(line.startsWith("c"))
                        continue; // skip comment lines

                    if(line.startsWith("p"))
                    {
                        String numberString = line.substring(6).trim();
                        String[] numbers = numberString.split("[ ]+");
                        // using a regex of 1 or more spaces, split the string into two elements
                        numOfClauses = Integer.parseInt(numbers[1]);
                        numOfVariables = Integer.parseInt(numbers[0]);
                        testValues = new Boolean[numOfVariables];
                        clauses = new ArrayList<>(numOfClauses);
                        continue;
                        // grab the important info and restart loop for the clauses
                    }
                    String numbers = tillNextZero(line, scanner);
                    clauses.add(new Clause(numOfVariables, numbers.trim()));
                    //ensure this is no uneven leading or trailing spacing for a clause.
                }
                scanner.close();
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }

            long startTime = System.currentTimeMillis(); // start a timer
            System.out.println("Testing: " + file.getName() + " Started at " + startTime);
            System.out.println("\tSatisfiable: " + isSatisfiable(testValues, clauses));
            long endTime = System.currentTimeMillis() - startTime; // end the timer
            System.out.println("\tTesting took " + endTime + " milliseconds"); //print out how long in ms it took
        }
        long endOfProgram = System.currentTimeMillis() - programStart;
        System.out.println("\nProgram took " + endOfProgram + " milliseconds to complete");
    }
    /**
     * Continuously creates a string of numbers until the line contains a zero in which case it will return
     * the string of numbers
     * @param numberLine    the scanner's next line
     * @param scanner       The current scanner
     * @return              the numbers needed.
     */
    private static String tillNextZero(String numberLine, Scanner scanner)
    {
        if(numberLine.endsWith("0"))
            return numberLine;

        return tillNextZero(numberLine + scanner.nextLine(), scanner);
        // should not go out of bounds as the format of the file demands
        // that it ends with a zero
    }

    /**
     * Run through all possible combinations and evaluate is the cnf file is satisfiable.
     *
     * @param clauses       the clauses being tested
     * @param testValues    the array of test values to be tested
     * @return              true, iff the CNF file was solvable
     */
    private static boolean isSatisfiable(Boolean[] testValues, ArrayList<Clause> clauses)
    {
        // start with all variables being false
        Arrays.fill(testValues, false);
        boolean result = false;
        boolean done = false;
        //generateNextVars(testValues);

        // run until you find a solution
        while (!done && !(result = testClauses(testValues, clauses)))
            done = generateNextVars(testValues);
         // while(!done);

        return result;
    }

    /**
     * Using binary addition, produce a new test values set with the given test values.
     *
     * @param testValues    the current test value array
     * @return              true, iff the all values in the testValues array are true
     */
    private static boolean generateNextVars(Boolean[] testValues)
    {
        boolean allTrue = true;
        int zeroPos = 0;
        // find the right most zero and store it into zero pos
        for(int i = 0; i < testValues.length; i++)
        {
//           if(allTrue)
//           {
//               allTrue = testValues[i];
//           }
            allTrue &= testValues[i];
            if(!testValues[i])
                zeroPos = i;
        }
        // set the right most zero to true
        testValues[zeroPos] = true;
        if(allTrue)
        {
            return true;
        }

        //turn every value after the right most zero to false
        for(int j = zeroPos + 1; j < testValues.length; j++ )
        {
            testValues[j] = false;
        }
        return false;
    }
    /**
     * Test each clause with the given test values and see if they all evaluate to true
     *
     * @param testValues    the given test values
     * @param clauses       The given list of clauses to be tested
     * @return              true, iff every clause evaluated to true with the given test values.
     */
    private static boolean testClauses(Boolean[] testValues, ArrayList<Clause> clauses)
    {
        boolean solvable = true;
        for(int i = 0; i < clauses.size() && solvable; i++)
        {
            solvable = clauses.get(i).evaluate(testValues);
        }
        return solvable;
    }
}
