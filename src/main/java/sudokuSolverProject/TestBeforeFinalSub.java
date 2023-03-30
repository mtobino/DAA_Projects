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

public class TestBeforeFinalSub {
    public static void main(String[] args)
    {
        int fileIndex = 4;
        File tempDir = new File("sudokuCNFs");
        boolean makeDir = tempDir.mkdir();
        //Stack<int[]> clauses = new Stack<>();
        File direc = new File("sudokuInputs");
        File[] files = direc.listFiles();
        assert files != null;
        File file = files[4];
        File cnfFile = new File(file.getName() + "CNFInputs.txt");
            try
            {

                long startTime = System.currentTimeMillis();
                System.out.println("Testing: " + file.getName());
                PrintWriter pw = new PrintWriter(new BufferedWriter( new FileWriter(cnfFile)));
                Scanner scanner = new Scanner(file);
                int gridLength = scanner.nextInt();
                int gridSize = gridLength * scanner.nextInt();
                int clauseCounter = 0;
                //advance the scanner to avoid issues
                scanner.nextLine();

                // Make Clause Generator class to generate all the clauses
                ClauseGenerator clauses = new ClauseGenerator(scanner, pw, gridSize);
                clauseCounter = clauses.generateClauses();

                pw.close();

                File puzzleCNF = new File(tempDir,file.getName() + ".cnf");
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

                //Files.delete(cnfFile.toPath());
                boolean deleted = cnfFile.delete();
                ISolver solver = SolverFactory.newDefault();
                solver.setTimeout(3600); // 1 hour timeout
                File[] puzzleCNFFiles = new File("sudokuCNFs").listFiles();
                assert puzzleCNFFiles != null;
                File puzCNF = puzzleCNFFiles[0];
                System.out.println("testing: " + file.getName());
                Reader reader = new DimacsReader(solver);
                PrintWriter out = new PrintWriter(System.out, true);
                IProblem problem = reader.parseInstance(new FileInputStream(puzCNF));
                if (problem.isSatisfiable()) {
                    int[] solution = problem.model();
                    printSolution(solution, gridSize);
                    System.out.println("Satisfiable !");
                    reader.decode(problem.model(), out);
                } else {
                    System.out.println("Unsatisfiable !");
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

    private static int decodeVariable(int var, int row, int col, int gridSize)
    {
        return -(gridSize*gridSize)*row - (gridSize*col) + (gridSize*gridSize) + gridSize + var;
    }
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


