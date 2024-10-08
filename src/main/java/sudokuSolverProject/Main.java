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

        for (File file : files) {
            long sudokuBoardStart = System.currentTimeMillis();
            File cnfFile = new File(sudokuCNFs,file.getName() + ".cnf");
            try {

                System.out.println("Testing: " + file.getName());
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(cnfFile)));
                Scanner scanner = new Scanner(file);
                int gridLength = scanner.nextInt();
                int gridSize = gridLength * scanner.nextInt();
                //advance the scanner to avoid issues
                scanner.nextLine();

                // Make Clause Generator class to generate all the clauses and print them to their respective files
                ClauseGenerator clauses = new ClauseGenerator(scanner, pw, gridSize);
                clauses.generateClauses();

                pw.close();
                scanner.close();

                // Begin solving process
                ISolver solver = SolverFactory.newDefault();
                solver.setTimeout(3600); // 1 hour timeout
                Reader reader = new DimacsReader(solver);
                IProblem problem = reader.parseInstance(new FileInputStream(cnfFile));

                if (problem.isSatisfiable()) {
                    SolutionDecoder decoder = new SolutionDecoder(problem.model(), gridSize);
                    decoder.printSolution();
                    long sudokuBoardEnd = System.currentTimeMillis();
                    System.out.println("\nTime taken to complete: " + (sudokuBoardEnd - sudokuBoardStart));
                } else {
                    System.out.println("Unsatisfiable !");
                    long sudokuBoardEnd = System.currentTimeMillis();
                    System.out.println("\nTime taken to complete: " + (sudokuBoardEnd - sudokuBoardStart));
                }
            } catch (FileNotFoundException e) {
                System.out.println("No file listed");
            } catch (IOException e) {
                System.out.println("Classic IO");
            } catch (ContradictionException e) {
                System.out.println("Unsatisfiable (trivial)!");
                long sudokuBoardEnd = System.currentTimeMillis();
                System.out.println("Time taken to complete: " + (sudokuBoardEnd - sudokuBoardStart));
            } catch (ParseFormatException e) {
                System.out.println("Format is wrong");
            } catch (TimeoutException e) {
                System.out.println("Timeout, sorry!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        long programEnd = System.currentTimeMillis();
        System.out.println("\nTime taken to run to completion: " + (programEnd - programStart) );

    }
}


