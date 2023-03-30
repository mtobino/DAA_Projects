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
    private static void findEmptyClause(Stack<int[]> clauses) {
        int index = 0;
        while(!clauses.empty())
        {
            if(clauses.pop().length == 0)
            {
                System.out.println("Empty clause at " + index);
            }
            index++;
        }
    }
    private static int[] clauseFromList(ArrayList<Variable> list)
    {
        int[] result = new int[list.size()];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = list.get(i).encodeVariable();
        }
        return result;
    }


    public static int clueClauses(Stack<int[]> clauses, Scanner scanner, int gridSize, PrintWriter pw){
        int row = 1;
        int col = 1;
        int clauseCounter = 0;
        //generate the clue clauses
        while(scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            String[] clues = line.split("[ ]+");
            for(String clue : clues)
            {
                if(!clue.equals("0"))
                {
                    int clueNum = new Variable(row, col, Integer.parseInt(clue), gridSize).encodeVariable() ;
                    //clauses.push(clueNum);
                    pw.println(clueNum + " 0");
                    clauseCounter++;
                }
                col++;
            }
            row++;
            col = 1;
        }
        return clauseCounter;
    }
    /**
     * Create clauses to ensure there is a value in every cell
     *
     * @param   clauses     The overarching clauses collection to be added to
     * @param   gridSize    The size of the whole board
     */
    private static int atLeastOnePerCell(Stack<int[]> clauses, int gridSize, PrintWriter pw)
    {
        int clauseCounter = 0;
        ArrayList<Variable> clause = new ArrayList<>();
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
                    clause.add(new Variable(row, col, val, gridSize));
                }
                // add clause to the stack of clauses
                for(Variable variable : clause)
                {
                    pw.print(variable.encodeVariable() + " ");
                }
                pw.print("0\n");
                clauseCounter++;
                //clauses.push(clauseFromList(clause));
                clause.clear();
            }

        }
        return clauseCounter;
    }

    /**
     * Create clauses to ensure each row has the specified value and each column also
     * has the specified value.
     *
     * @param   clauses    The overarching clauses collection to be added to
     * @param   gridSize   The size of the whole board
     */
    private static int atLeastOnePerRowAndCol(Stack<int[]> clauses, int gridSize, PrintWriter pw){
        int clauseCounter = 0;
        ArrayList<Variable> clause = new ArrayList<>();
        // for every value
        for(int val = 1; val <= gridSize; val++)
        {
            // in each row
            for(int row = 1; row <= gridSize; row++)
            {
                // in every col of that row
                for(int col = 1; col <= gridSize; col++)
                {
                    // add the respective variable to the clause
                    clause.add(new Variable(row, col, val, gridSize));
                }
                // add the clause to the stack
                for(Variable variable : clause)
                {
                    pw.print(variable.encodeVariable() + " ");
                }
                pw.print("0\n");
                clauseCounter++;
                clause.clear();
            }

            // for each col
            for(int col = 1; col <= gridSize; col++)
            {
                // in every row in that col
                for(int row = 1; row <= gridSize; row++)
                {
                    // add the respective variable to the clause
                    clause.add(new Variable(row, col, val, gridSize));
                }
                // add the clause to the stack
                for(Variable variable : clause)
                {
                    pw.print(variable.encodeVariable() + " ");
                }
                pw.print("0\n");
                clauseCounter++;
                clause.clear();
            }

        }
        return clauseCounter;
    }

    /**
     *  Create clauses that check if each subgroup contains each value at least once in its nxn grid
     *
     * @param   clauses     The overarching clauses collection to be added to
     * @param   gridLength  The size of the subgroups
     */
    private static int atLeastOnePerSubGroup(Stack<int[]> clauses, int gridLength, PrintWriter pw)
    {
        int clauseCounter = 0;
        ArrayList<Variable> clause = new ArrayList<>();
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
                        clause.add(new Variable(subRow, subCol, val, max));
                    }
                    if(subRow % gridLength == 0)
                    {
                        //pw.println();
                        for(Variable variable : clause)
                        {
                            pw.print(variable.encodeVariable() + " ");
                        }
                        pw.print("0\n");
                        clauseCounter++;
                        clause.clear();
                    }
                }
            }
        }
        return clauseCounter;
    }


    private static int atMostOnePerCell(Stack<int[]> clauses, int gridSize, PrintWriter pw)
    {
        int clauseCounter = 0;
        //ArrayList<Variable> clause = new ArrayList<>();
        // for every value
        for(int val = 1; val <= gridSize; val++)
        {
            //in each row
            for(int row = 1; row <= gridSize; row++){
                // for every col of that row
                for(int col = 1; col <= gridSize; col++){
                    // make sure that the no value repeats in that cell
                   /* for(int nextVal = val + 1; nextVal <= gridSize; nextVal++)
                    {
                        int [] clause = { -(new Variable(row, col, val, gridSize ).encodeVariable()),
                                -(new Variable(row, col, nextVal, gridSize ).encodeVariable())  };
                       //pw.println();
                        for(Integer variable : clause)
                        {
                            pw.print(variable + " ");
                        }
                        pw.print("0\n");
                        clauseCounter++;
                        //clauses.push(clause);
                    }*/
                    for(int nextVal = val + 1; nextVal <= gridSize; nextVal++)
                    {
                        Variable var1 = new Variable(row, col, val, gridSize);
                        Variable var2 = new Variable(row, col, nextVal, gridSize);
                        pw.print( (-var1.encodeVariable()) + " " + (-var2.encodeVariable()) );
                        pw.print(" 0\n");
                        clauseCounter++;
                    }
                }
            }
        }
        return clauseCounter;
    }
    /**
     * Creates clauses that guarantee that for every col, and for each row, there is
     * only one value that will satisfy the board.
     *
     * @param clauses   The overarching clauses collection to be added to
     * @param gridSize  The size of the board
     */
    private static int atMostOnePerCol(Stack<int[]> clauses, int gridSize, PrintWriter pw){
        int clauseCounter = 0;
        // in every col
        for(int col = 1; col <= gridSize; col++){
            // in each row of that col
            for(int row = 1; row <= gridSize; row++){
                // for every value of the col
                for(int val = 1; val <= gridSize; val++){
                    // add the next values to the clause

                    /*for(int nextVal = val + 1; nextVal <= gridSize; nextVal++)
                    {
                        int[] clause = { -(new Variable(row, col, val, gridSize).encodeVariable()),
                                -(new Variable(row, col, nextVal, gridSize).encodeVariable())};
                       // pw.println();
                        for(Integer variable : clause)
                        {
                            pw.print(variable + " ");
                        }
                        pw.print("0\n");
                        clauseCounter++;
                    }*/

                    for(int nextVal = val + 1; nextVal <= gridSize; nextVal++)
                    {
                        Variable var1 = new Variable(row, col, val, gridSize);
                        Variable var2 = new Variable(row, col, nextVal, gridSize);
                        pw.print( (-var1.encodeVariable()) + " " + (-var2.encodeVariable()) );
                        pw.print(" 0\n");
                        clauseCounter++;
                    }
                    // build a not clause that will only be true if correct value is in
                    // the correct position

                }
            }
        }
        return clauseCounter;
    }

    /**
     * Creates clauses that guarantee that in every row, and for each column, there is
     * only one value that will satisfy the board.
     *
     * @param clauses   The overarching clauses collection to be added to
     * @param gridSize  The size of the board
     */
    private static int atMostOnePerRow(Stack<int[]> clauses, int gridSize, PrintWriter pw){
        int clauseCounter = 0;
        // in every row
        for(int row = 1; row <= gridSize; row++){
            // in each col of that row
            for(int col = 1; col <= gridSize; col++){
                // for every value of the row
                for(int val = 1; val <= gridSize; val++){
                    /*
                    for(int nextVal = val + 1; nextVal <= gridSize; nextVal++)
                    {
                        int[] clause = { -(new Variable(row, col, val, gridSize).encodeVariable()),
                                -(new Variable(row, col, nextVal, gridSize).encodeVariable())};
                        //pw.println();
                        for(Integer variable : clause)
                        {
                            pw.print(variable + " ");
                        }
                        pw.print("0\n");
                        clauseCounter++;
                    }
                    */
                    for(int nextVal = val + 1; nextVal <= gridSize; nextVal++)
                    {
                        Variable var1 = new Variable(row, col, val, gridSize);
                        Variable var2 = new Variable(row, col, nextVal, gridSize);
                        pw.print( (-var1.encodeVariable()) + " " + (-var2.encodeVariable()) );
                        pw.print(" 0\n");
                        clauseCounter++;
                    }
                }
            }
        }
        return clauseCounter;
    }

    private static int atMostOnePerSubGroup(Stack<int[]> clauses, int gridLength, PrintWriter pw)
    {
        int clauseCounter = 0;
        //int[] clause = new int[2];
        ArrayList<Variable> clause = new ArrayList<>();
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
                        //clause.add(new Variable(subRow, subCol, val, max));
                        for(int nextVal = val + 1; nextVal <= max; nextVal++)
                        {
                            Variable var1 = new Variable(subRow, subCol, val, max);
                            Variable var2 = new Variable(subRow, subCol, nextVal, max);
                            pw.print( (-var1.encodeVariable()) + " " + (-var2.encodeVariable()) );
                            pw.print(" 0\n");
                            clauseCounter++;
                        }
                    }
                    /*if(subRow % gridLength == 0)
                    {
                        //pw.println();
                        for(Variable variable : clause)
                        {
                            pw.print( (variable.encodeVariable() * -1)  + " ");
                        }
                        pw.print("0\n");
                        clauseCounter++;
                        clause.clear();
                    }*/
                }
            }
        }
        return clauseCounter;
    }
    private static int[] clauseFromListWithNegatives(List<Variable> variables)
    {
        int[] result = new int[variables.size()];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = -variables.get(i).encodeVariable();
        }
        return result;
    }


}


