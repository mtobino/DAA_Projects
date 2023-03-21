package sudokuSolverProject;

public class Variable {
    private final int row;
    private final int col;
    private final int value;

    public Variable(int row, int col, int value)
    {
        this.row = row;
        this.col = col;
        this.value = value;
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

    public String toString()
    {
        return "Row: " + row + " Col: " + col + " Value: " + value;
    }
}
