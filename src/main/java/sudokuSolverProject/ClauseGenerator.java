package sudokuSolverProject;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class that generates the necessary clauses and writes them to a file.
 *
 * @author Matthew Tobino
 */
public class ClauseGenerator
{
    Scanner scanner;
    PrintWriter pw;
    int gridSize;
    int gridLength;
    public ClauseGenerator(Scanner scanner, PrintWriter pw, int gridSize)
    {
        this.scanner = scanner;
        this.pw = pw;
        this.gridSize = gridSize;
        gridLength = (int) Math.sqrt(gridSize);
    }

    /**
     * Generate all the clauses and write them to a file.
     */
    public void generateClauses()
    {
        // comments indicate the formula for how many clauses each method will produce for a nxn board.
        clueClauses(scanner, gridSize, pw);
        // x -> depends on the given sudoku file
        atMostOnePerRow(gridSize, pw);
        // n choose 2 * n^2
        atMostOnePerCol(gridSize, pw);
        // n choose 2 * n^2
        atMostOnePerSubGroup(gridLength, pw);
        // n choose 2 * n^2
        atLeastOnePerCell(gridSize, pw);
        // n^2
        atLeastOnePerRowAndCol(gridSize, pw);
        // 2n^2
        atLeastOnePerSubGroup(gridLength, pw);
        // n^2
    }

    /**
     * Calculate how many clauses are generated before clue clauses are calculated
     *
     * @return  The total number of clauses before the clue clauses are added.
     */
    private int howManyClauses()
    {
        return 3*combination(gridSize)*(gridSize*gridSize) + 4*(gridSize*gridSize);
    }

    /**
     * Generate clue clauses based on the given file and write the clauses to a new file
     *
     * @param scanner   The given file being searched through
     * @param gridSize  The overall size of the board
     * @param pw        The file writer
     */
    private void clueClauses(Scanner scanner, int gridSize, PrintWriter pw)
    {
        int row = 1;
        int col = 1;
        int clauseCounter = 0;
        //Store clue clauses in a long string to be concatenated with the cnf file header
        StringBuilder clauses = new StringBuilder();
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
                    clauses.append("\n").append(clueNum).append(" 0");
                    clauseCounter++;
                }
                col++;
            }
            row++;
            col = 1;
        }
        int totalClauses = howManyClauses() + clauseCounter;
        String cnfFile = "p cnf " + (gridSize * gridSize * gridSize) + " " + totalClauses + clauses;
        pw.println(cnfFile);
    }

    /**
     * Create clauses to ensure there is a value in every cell and write the clause to a file
     *
     * @param   gridSize    The size of the whole board
     * @param   pw          The file writer
     */
    private void atLeastOnePerCell(int gridSize, PrintWriter pw)
    {
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
                // print the variables on one line to make a clause
                for(Variable variable : clause)
                {
                    pw.print(variable.encodeVariable() + " ");
                }
                pw.print("0\n");
                clause.clear();
            }

        }
    }

    /**
     * Create clauses to ensure each row has the specified value and each column also
     * has the specified value.
     *
     * @param   pw         The file writer
     * @param   gridSize   The size of the whole board
     */
    private void atLeastOnePerRowAndCol(int gridSize, PrintWriter pw) {
        ArrayList<Variable> clause = new ArrayList<>();
        // for every value
        for (int val = 1; val <= gridSize; val++) {

            // in each row
            for (int row = 1; row <= gridSize; row++) {
                // in every col of that row
                for (int col = 1; col <= gridSize; col++) {
                    // add the respective variable to the clause
                    clause.add(new Variable(row, col, val, gridSize));
                }
                // print the variables on one line to make a clause
                for (Variable variable : clause) {
                    pw.print(variable.encodeVariable() + " ");
                }
                pw.print("0\n");
                clause.clear();
            }

            // for each col
            for (int col = 1; col <= gridSize; col++) {
                // in every row in that col
                for (int row = 1; row <= gridSize; row++) {
                    // add the respective variable to the clause
                    clause.add(new Variable(row, col, val, gridSize));
                }
                // print the variables on one line to make a clause
                for (Variable variable : clause) {
                    pw.print(variable.encodeVariable() + " ");
                }
                pw.print("0\n");
                clause.clear();
            }

        }
    }

    /**
     *  Create clauses that check if each subgroup contains each value at least once in its nxn grid
     *  and write that clause to the cnf file
     *
     * @param   pw          The file writer
     * @param   gridLength  The size of the subgroups
     */
    private void atLeastOnePerSubGroup(int gridLength, PrintWriter pw)
    {
        ArrayList<Variable> clause = new ArrayList<>();
        int gridSize = gridLength*gridLength;
        for(int val = 1; val <= gridLength*gridLength; val++)
        {
            for(int overallCol = 1; overallCol <= gridLength; overallCol++)
            {
                for(int subRow = 1; subRow <= gridSize; subRow++)
                {
                    int maxCol = gridSize - gridLength*(gridLength - overallCol);
                    for(int subCol = (gridLength*(overallCol - 1) + 1); subCol <= maxCol; subCol++)
                    {
                        clause.add(new Variable(subRow, subCol, val, gridSize));
                    }
                    if(subRow % gridLength == 0)
                    {
                        // print the variables on one line to make a clause
                        for(Variable variable : clause)
                        {
                            pw.print(variable.encodeVariable() + " ");
                        }
                        pw.print("0\n");
                        clause.clear();
                    }
                }
            }
        }
    }

    /**
     * Creates clauses that guarantee that for every col, and in each row, there is
     * only one instance of a value that will satisfy the board. It will then write
     * that clause to the file
     *
     * @param gridSize  The size of the board
     * @param pw        The file writer
     */
    private void atMostOnePerCol(int gridSize, PrintWriter pw){
        // in every col
        for(int col = 1; col <= gridSize; col++){
            // in each row of that col
            for(int row = 1; row <= gridSize; row++){
                // for every value of the col
                for(int val = 1; val <= gridSize; val++){
                    // add the next values to the clause
                    for(int nextCol = col + 1; nextCol <= gridSize; nextCol++)
                    {
                        Variable var1 = new Variable(row, col, val, gridSize);
                        Variable var2 = new Variable(row, nextCol, val, gridSize);
                        pw.print( (-var1.encodeVariable()) + " " + (-var2.encodeVariable()) );
                        pw.print(" 0\n");
                    }
                    // build a not clause that will only be true if correct value is in
                    // the correct position
                }
            }
        }
    }

    /**
     * Creates clauses that guarantee that in every row, and for each column, there is
     * only one value that will satisfy the board and writes that clause to a file
     *
     * @param pw        The file writer
     * @param gridSize  The size of the board
     */
    private void atMostOnePerRow(int gridSize, PrintWriter pw){
        // in every row
        for(int row = 1; row <= gridSize; row++){
            // in each col of that row
            for(int col = 1; col <= gridSize; col++){
                // for every value of the row
                for(int val = 1; val <= gridSize; val++)
                {
                    // for every value after that next value
                    for(int nextRow = row + 1; nextRow <= gridSize; nextRow++)
                    {
                        Variable var1 = new Variable(row, col, val, gridSize);
                        Variable var2 = new Variable(nextRow, col, val, gridSize);
                        pw.print( (-var1.encodeVariable()) + " " + (-var2.encodeVariable()) );
                        pw.print(" 0\n");
                    }
                    // build a not clause that will only be true if correct value is in
                    // the correct position
                }
            }
        }
    }

    /**
     * Creates clauses that guarantee there is only one instance of a value in a given
     * subgroup, and writes the clause to a file
     *
     * @param gridLength    The overall size of the subgroup
     * @param pw            The file writer
     */
    private void atMostOnePerSubGroup(int gridLength, PrintWriter pw)
    {
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
                        }
                        // build a not clause that will only be true if correct value is in
                        // the correct position
                    }
                }
            }
        }
    }

    /**
     * Return the combination of a value choose 2
     * ( n(n-1) ) / 2
     *
     * @param   value   the value being chosen from
     * @return          the result of the combination
     */
    private int combination(int value)
    {
        return (value*(value-1))/ 2;
    }
}
