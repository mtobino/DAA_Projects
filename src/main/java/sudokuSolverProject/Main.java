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

public class Main {
    public static void main(String[] args)
    {
        ISolver solver = SolverFactory.newDefault();
        solver.setTimeout(3600); // 1 hour timeout
        File[] files = new File("bruteForceInputs").listFiles();
        for(File file: files)
        {
            Reader reader = new DimacsReader(solver);
            PrintWriter out = new PrintWriter(System.out,true);
            // CNF filename is given on the command line

            try {
                IProblem problem = reader.parseInstance(new FileInputStream(file));
                if (problem.isSatisfiable())
                {
                    int[] solution = problem.model();
                    StringBuilder result = new StringBuilder("[" + solution[0]);
                    for(int i = 1; i < solution.length; i++ )
                    {
                        result.append(", ").append(solution[i]);
                    }
                    System.out.println(result + "]");
                    System.out.println("Satisfiable !");
                    reader.decode(problem.model(),out);
                } else
                {
                    System.out.println("Unsatisfiable !");
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("No file listed");
            }
            catch (ParseFormatException e)
            {
                System.out.println("Format is wrong");
            }
            catch (IOException e)
            {
                System.out.println("Classic IO");
            }
            catch (ContradictionException e)
            {
                System.out.println("Unsatisfiable (trivial)!");
            }
            catch (TimeoutException e)
            {
                System.out.println("Timeout, sorry!");
            }
        }

    }
}
