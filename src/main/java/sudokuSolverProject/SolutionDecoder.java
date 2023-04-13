package sudokuSolverProject;

public class SolutionDecoder {
    private final int[] solution;

    private final int gridSize;

    public SolutionDecoder(int[] solution, int gridSize)
    {
        this.solution = solution;
        this.gridSize = gridSize;
    }

    /**
     * Decode the given variable based on the size of the board
     *
     * @param var       the variable
     * @return          The value of the variable
     */
    private int decodeVariable(int var)
    {
        int value;
        return ( value = var % gridSize) == 0 ? gridSize : value;
    }
    /**
     * Prints the solution of the board based on the given solution and size
     * of the board
     */
    public void printSolution()
    {
        int col = 1;
        for(int variable : solution)
        {
            if(variable > 0)
            {
                if(col <= gridSize)
                {
                    System.out.print( decodeVariable(variable) + " ");
                }
                else
                {
                    col = 1;
                    System.out.print("\n" + decodeVariable(variable) + " ");
                }
                col++;
            }
        }
    }
}
