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

    public int getCol() {
        return col;
    }
    public int getRow(){
        return row;
    }

    public int getValue(){
        return value;
    }

    public int encodeVariable()
    {
        return (row - 1)*gridSize*gridSize + (col - 1) * gridSize + (value - 1) + 1;
    }

    public String toString()
    {
        return "Row: " + row + " Col: " + col + " Value: " + value;
    }
}
