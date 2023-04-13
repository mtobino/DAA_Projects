package sudokuSolverProject;

/**
 * Variable class to encode a variable to fit a nxn sudoku board
 *
 * @author Matthew Tobino
 */
public class Variable {
    private final int row;
    private final int col;
    private final int value;
    private final int gridSize;

    /**
     * Constructor for a Variable
     * @param row       The given row the value is in
     * @param col       The given column the value is in
     * @param value     The given value
     * @param gridSize  The given size of the board
     */
    public Variable(int row, int col, int value, int gridSize)
    {
        this.row = row;
        this.col = col;
        this.value = value;
        this.gridSize = gridSize;
    }
    /**
     * Encode the variable to be within the n*n*n delimiter of max number of variables
     * in each sudoku problem. Uses a base n encoding process. row, col, and val are all subtracted
     * by one due to the loop setup in the main driver.
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
