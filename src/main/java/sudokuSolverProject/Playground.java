package sudokuSolverProject;
/*
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
*/

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

public class Playground {
    public static void main(String[] args)
    {
        Stack<int[]> clauses = new Stack<>();
        File direc = new File("sudokuInputs");
        File[] files = direc.listFiles();
        assert files != null;
        File test = files[1];
        for(File file : files){
            try
            {

                Scanner scanner = new Scanner(test);
                int gridLength = scanner.nextInt();
                int gridSize = gridLength * scanner.nextInt();
                //advance the scanner to avoid issues
                scanner.nextLine();

                // helper method calls to generate the clauses
                clueClauses(clauses, scanner, gridSize);
                atLeastOne(clauses, gridSize);
                rowAndColHaveVal(clauses, gridSize);
                subGroupClauses(clauses, gridLength);

                final int MAXVAR = gridSize*gridSize*gridSize;
                final int NBCLAUSES = clauses.size();

                ISolver solver = SolverFactory.newDefault();

                // prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT solving
                solver.newVar(MAXVAR);
                solver.setExpectedNumberOfClauses(NBCLAUSES);
                while(!clauses.isEmpty())
                {
                    int [] clause = clauses.pop();
                    solver.addClause(new VecInt(clause)); // adapt Array to IVecInt
                }

                // we are done. Working now on the IProblem interface
                IProblem problem = solver;
                if (problem.isSatisfiable())
                {
                    printSolution(problem.model(), gridSize);
                }
                else
                {
                    System.out.println("there was not solution to the problem");
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("No file listed");
            } catch (ContradictionException e)
            {
                System.out.println("Unsatisfiable (trivial)!");
            }
            catch (TimeoutException e) {
                System.out.println("Timeout, sorry!");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

        }
    }

    public static void clueClauses(Stack<int[]> clauses, Scanner scanner, int gridSize){
        int row = 1;
        int col = 1;

        //generate the clue clauses
        while(scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            String[] clues = line.split("[ ]+");
            for(String clue : clues)
            {
                if(!clue.equals("0"))
                {
                    int[] clueNum = { encodeVariable(Integer.parseInt(clue), row, col, gridSize) };
                    clauses.push(clueNum);
                }
                col++;
            }
            row++;
        }
    }
    /**
     * Create clauses to ensure there is a value in every cell
     *
     * @param   clauses     The overarching clauses collection to be added to
     * @param   gridSize    The size of the whole board
     */
    private static void atLeastOne(Stack<int[]> clauses, int gridSize)
    {

        ArrayList<Integer> clause = new ArrayList<>();
        // in each row
        for(int row = 1; row <= gridSize; row++)
        {
            // at each column
            for(int col = 1; col <= gridSize; col++)
            {
                // for every value
                for(int val = 1; val <= gridSize; val++)
                {
                    // make a clause of one variable
                    clause.add(encodeVariable(val, row, col, gridSize));
                }
            }
            // add clause to the stack of clauses
            clauses.push(clauseFromList(clause));
        }
    }

    /**
     * Create clauses to ensure each row has the specified value and each column also
     * has the specified value.
     *
     * @param   clauses    The overarching clauses collection to be added to
     * @param   gridSize   The size of the whole board
     */
    private static void rowAndColHaveVal(Stack<int[]> clauses, int gridSize){
        ArrayList<Integer> clause = new ArrayList<>();
        // for every value
        for(int val = 1; val <= gridSize; val++)
        {
            // ensure you start with a clean clause
            clause.clear();
            // in each row
            for(int row = 1; row <= gridSize; row++)
            {
                // in every col of that row
                for(int col = 1; col <= gridSize; col++)
                {
                    // add the respective variable to the clause
                    clause.add(encodeVariable(val, row, col, gridSize));
                }
            }
            // add the clause to the stack
            clauses.push(clauseFromList(clause));
            clause.clear();

            // for each col
            for(int col = 1; col <= gridSize; col++)
            {
                // in every row in that col
                for(int row = 1; row <= gridSize; row++)
                {
                    // add the respective variable to the clause
                    clause.add(encodeVariable(val, row, col, gridSize));
                }
            }
            // add the clause to the stack
            clauses.push(clauseFromList(clause));
        }
    }

    /**
     *  Create clauses that check if each subgroup contains each value at least once in its nxn grid
     *
     * @param   clauses     The overarching clauses collection to be added to
     * @param   gridLength  The size of the subgroups
     */
    private static void subGroupClauses(Stack<int[]> clauses, int gridLength)
    {
        ArrayList<Integer> clause = new ArrayList<>();
        int max = gridLength*gridLength;
        for(int val = 1; val <= gridLength*gridLength; val++)
        {
            for(int overallCol = 1; overallCol <= gridLength; overallCol++)
            {
              for(int subRow = 1; subRow <= max; subRow++)
              {
                  int maxCol = max - gridLength*(gridLength - overallCol);
                  for(int subCol = (gridLength*(overallCol - 1) + 1); subCol <= maxCol; subCol++)
                  {
                      clause.add(encodeVariable(val, subRow, subCol, max));
                  }
                  if(subRow % gridLength == 0)
                  {
                      clauses.push(clauseFromList(clause));
                      clause.clear();
                  }
              }
            }
        }
        clauses.push(clauseFromList(clause));
    }
    private static int encodeVariable(int value, int row, int col, int gridSize)
    {
        return (row - 1)*gridSize*gridSize + (col - 1) * gridSize + (value - 1) + 1;
    }
    private static int decodeVariable(int var, int row, int col, int gridSize)
    {
        return -(gridSize*gridSize)*row - (gridSize*col) + (gridSize*gridSize) + gridSize + var;
    }
    private static int[] clauseFromList(ArrayList<Integer> list)
    {
        int[] result = new int[list.size()];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = list.get(i);
        }
        return result;
    }

    private static void printSolution(int[] solution, int gridSize)
    {
        int row = 1, col = 1;
        for(int variable : solution)
        {
            if(variable >= 0)
            {
                if(col != 9)
                {
                    System.out.println(decodeVariable(variable, row, col, gridSize) + " ");
                    col++;
                }
                else
                {
                    row += 1;
                    col = 1;
                    System.out.println("\n" + decodeVariable(variable, row, col, gridSize) + " ");
                }
            }
        }
    }


}


