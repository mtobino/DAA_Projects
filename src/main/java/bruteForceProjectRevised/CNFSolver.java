package bruteForceProjectRevised;

import java.util.List;

public class CNFSolver
{
    List<Clause> clauses;
    boolean[] testValues;

    public CNFSolver(List<Clause> clauses, int numOfVariables)
    {
        this.clauses = clauses;
        testValues = new boolean[numOfVariables];
    }

    /**
     * Run through all possible combinations and evaluate is the cnf file is satisfiable.
     *
     * @return              true, iff the CNF file was solvable
     */
    public boolean isSatisfiable()
    {
        // start with all variables being false
        boolean result = false;
        boolean done = false;

        // run until you find a solution
        while (!done && !(result = testClauses()))
            done = generateNextVars();

        return result;
    }

    /**
     * Using binary addition, produce a new test values set with the given test values.
     *
     * @return              true, iff the all values in the testValues array are true
     */
    private boolean generateNextVars()
    {
        boolean allTrue = true;
        int zeroPos = 0;
        // find the right most zero and store it into zero pos
        for(int i = 0; i < testValues.length; i++)
        {
            allTrue &= testValues[i];
            if(!testValues[i])
                zeroPos = i;
        }
        // set the right most zero to true
        testValues[zeroPos] = true;
        if(allTrue)
        {
            return true;
        }

        //turn every value after the right most zero to false
        for(int j = zeroPos + 1; j < testValues.length; j++ )
        {
            testValues[j] = false;
        }
        return false;
    }
    /**
     * Test each clause with the given test values and see if they all evaluate to true
     *
     * @return              true, iff every clause evaluated to true with the given test values.
     */
    private boolean testClauses()
    {
        boolean solvable = true;
        for(int i = 0; i < clauses.size() && solvable; i++)
        {
            solvable = clauses.get(i).evaluate(testValues);
        }
        return solvable;
    }
}
