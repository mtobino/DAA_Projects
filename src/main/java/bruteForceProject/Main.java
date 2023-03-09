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
        /*File[] files = new File[10];
        files[0] = new File("s5.cnf");
        files[1] = new File("s15.cnf");
        files[2] = new File("s20.cnf");
        files[3] = new File("s28.cnf");
        files[4] = new File("u15.cnf");
        files[5] = new File("u20.cnf");
        files[6] = new File("u27.cnf");
        files[7] = new File("u29.cnf");
        files[8] = new File("u30.cnf");
        files[9] = new File("u32.cnf");*/

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
        // assume there is no solution at first
        boolean result = false;
        // run until you find a solution
        while(!result)
        {
            result = testClauses(testValues, clauses);
            // check if the all true case has been tested
            if(!isAllTrue(testValues))
            {
                // if it has not, get the next set of test values
                generateNextVars(testValues);
            }
            else
            {
                // otherwise there is no solution.
                return false;
            }
        }
        return true;
    }

    /**
     * Using binary addition, produce a new test values set with the given test values.
     *
     * @param testValues    the current test value array
     */
    private static void generateNextVars(Boolean[] testValues)
    {
        int zeroPos = 0;
        // find the right most zero and store it into zero pos
        for(int i = 0; i < testValues.length; i++)
        {
            if(!testValues[i])
                zeroPos = i;
        }
        // set the right most zero to true
        testValues[zeroPos] = true;

        //turn every value after the right most zero to false
        for(int j = zeroPos + 1; j < testValues.length; j++ )
        {
            testValues[j] = false;
        }
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

    /**
     * Test if the array is all true values
     *
     * @param testValues    The test values being assessed
     * @return              true, iff the array is all true values
     */
    private static boolean isAllTrue(Boolean[] testValues)
    {
        boolean allTrue = true;
        for(int i = 0; i < testValues.length && allTrue; i++)
        {
            allTrue = testValues[i];
        }
        return allTrue;
    }
}
