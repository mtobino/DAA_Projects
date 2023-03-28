package sudokuSolverProject;

public class Variable {
    private final int row;
    private final int col;
    private final int value;
    private final int gridSize;

    public Variable(int row, int col, int value, int gridSize)
    {
        this.row = row;
        this.col = col;
        this.value = value;
        this.gridSize = gridSize;
    }
    /**
     * Encode the variable to be within the n*n*n delimiter of max number of variables
     * in each sudoku problem
     *
     * Thought Process:
     * Each board is made up of n*n number of cells and each cell has n possible answers
     * By multiplying the col by n and the row by n*n and summing both of those results
     * with the value, you will get a number with the n*n*n range that will accurately
     * depict what row, col, and value the variable is
     *
     * The minus 1 on each field is due to the loops in the driver starting at 1 instead
     * of the normal 0
     *
     * @return  The encoded variable
     */
    public int encodeVariable()
    {
        return (row - 1)*gridSize*gridSize + (col - 1) * gridSize + (value - 1) + 1;
    }

    /**
     * Returns a string that notes the variable's position and value and
     * makes it easier to read the variables in the debugger
     *
     * @return  a string that notes the variables position and value
     */
    public String toString()
    {
        return "Row: " + row + " Col: " + col + " Value: " + value;
    }
}
