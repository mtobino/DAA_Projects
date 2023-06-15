package bruteForceProjectRevised;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CNFReader {
    int numOfClauses;
    int numOfVariables;
    List<Clause> clauses;
    Scanner scanner;

    /**
     * Constructor will take in the file and perform the necessary actions to get the clauses
     * and any other information needed.
     *
     * @param cnfFile                   The CNF file being read
     * @throws FileNotFoundException    If the file was not found.
     */
    CNFReader(File cnfFile) throws FileNotFoundException
    {
        scanner = new Scanner(cnfFile);
        skipCommentLines();
        getPLine();
        generateClauses();
        scanner.close();
    }

    /**
     * Stores the clauses from the file into the list
     */
    private void generateClauses()
    {
        Clause clause = new Clause(numOfVariables);
        while(scanner.hasNextInt())
        {
            int variable = scanner.nextInt();
            if(variable != 0)
            {
                clause.addVariable(variable);
            }
            else
            {
                clauses.add(clause);
                clause = new Clause(numOfVariables);
            }
        }
    }

    /**
     * Gets the information from the p line of the CNF file and stores the information in its
     * respective places
     */
    private void getPLine()
    {
        String line = scanner.nextLine();
        String numberString = line.substring(6).trim();
        String[] numbers = numberString.split("[ ]+");
        // using a regex of 1 or more spaces, split the string into two elements
        numOfClauses = Integer.parseInt(numbers[1]);
        numOfVariables = Integer.parseInt(numbers[0]);
        clauses = new ArrayList<>(numOfClauses);
    }

    /**
     * Skips all the comment lines in the CNF file.
     */
    private void skipCommentLines()
    {
        while(scanner.hasNext("c"))
        {
            scanner.nextLine();
        }
    }

}
