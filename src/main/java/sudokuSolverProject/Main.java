package sudokuSolverProject;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Driver for Sudoku Project, will read in a sudoku file, write a cnf, and print out the solution for
 * that sudoku board
 *
 * @author Matthew Tobino
 */
public class Main {
    public static void main(String[] args)
    {
        long programStart = System.currentTimeMillis();
        File sudokuCNFs = new File("sudokuCNFs");
        boolean makeDir = sudokuCNFs.mkdir();
        File sudokuInputs = new File("sudokuInputs");
        File[] files = sudokuInputs.listFiles();
        assert files != null;
        //File file = files[1];
        for(int i = 0; i < files.length; i++)
        {
            long sudokuBoardStart = System.currentTimeMillis();
            File cnfFile = new File(files[i].getName() + "CNFInputs.txt");
            try
            {

                System.out.println("Testing: " + files[i].getName());
                PrintWriter pw = new PrintWriter(new BufferedWriter( new FileWriter(cnfFile)));
                Scanner scanner = new Scanner(files[i]);
                int gridLength = scanner.nextInt();
                int gridSize = gridLength * scanner.nextInt();
                int clauseCounter;
                //advance the scanner to avoid issues
                scanner.nextLine();

                // Make Clause Generator class to generate all the clauses
                ClauseGenerator clauses = new ClauseGenerator(scanner, pw, gridSize);
                clauseCounter = clauses.generateClauses();

                pw.close();

                File puzzleCNF = new File(sudokuCNFs,files[i].getName() + ".cnf");
                PrintWriter cnfWriter = new PrintWriter(new BufferedWriter( new FileWriter(puzzleCNF)));
                BufferedReader br = new BufferedReader(new FileReader(cnfFile));
                cnfWriter.println("p cnf " + (gridSize*gridSize*gridSize) + " " + clauseCounter);
                String line = br.readLine();

                while(line != null)
                {
                    cnfWriter.println(line);
                    line = br.readLine();
                }
                cnfWriter.close();

                boolean deleted = cnfFile.delete();
                ISolver solver = SolverFactory.newDefault();
                solver.setTimeout(3600); // 1 hour timeout
                Reader reader = new DimacsReader(solver);
                IProblem problem = reader.parseInstance(new FileInputStream(puzzleCNF));
                if (problem.isSatisfiable()) {
                    int[] solution = problem.model();
                    printSolution(solution, gridSize);
                    long sudokuBoardEnd = System.currentTimeMillis();
                    System.out.println("\nTime taken to complete: " + (sudokuBoardEnd - sudokuBoardStart));
                } else {
                    System.out.println("Unsatisfiable !");

                    long sudokuBoardEnd = System.currentTimeMillis();
                    System.out.println("\nTime taken to complete: " + (sudokuBoardEnd - sudokuBoardStart));
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("No file listed");
            }
            catch (IOException e)
            {
                System.out.println("Classic IO");
            } catch (ContradictionException e)
            {
                System.out.println("Unsatisfiable (trivial)!");
            }
            catch (ParseFormatException e)
            {
                System.out.println("Format is wrong");
            }
            catch (TimeoutException e) {
                System.out.println("Timeout, sorry!");
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        long programEnd = System.currentTimeMillis();
        System.out.println("\nTime taken to run to completion: " + (programEnd - programStart) );

    }

    /**
     * The encoder but if it was set to solve for value instead of the variable
     *
     * @param var       the variable
     * @param row       the row the variable was located in
     * @param col       the col the variable was located in
     * @param gridSize  the size of the board
     * @return          The value of the variable
     */
    private static int decodeVariable(int var, int row, int col, int gridSize)
    {
        return -(gridSize*gridSize)*row - (gridSize*col) + (gridSize*gridSize) + gridSize + var;
    }
    /**
     * Prints the solution of the board based on the given solution and size
     * of the board
     *
     * @param solution  The solution provided by the SAT solver
     * @param gridSize  The size of the board
     */
    private static void printSolution(int[] solution, int gridSize)
    {
        //System.out.println(Arrays.toString(solution));
        int row = 1, col = 1;
        for(int variable : solution)
        {
            if(variable > 0)
            {
                if(col <= gridSize)
                {
                    System.out.print( decodeVariable(variable, row, col, gridSize) + " ");
                }
                else
                {
                    row += 1;
                    col = 1;
                    System.out.print("\n" + decodeVariable(variable, row, col, gridSize) + " ");
                }
                col++;
            }
        }
    }
}


