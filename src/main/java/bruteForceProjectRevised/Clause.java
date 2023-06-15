package bruteForceProjectRevised;

import java.util.ArrayList;

/**
 * A place to store and evaluate the clauses
 *
 * @author Matthew Tobino
 */
public class Clause {
    ArrayList<Integer> variables;

    /**
     * Constructor for a clause object, initializes the array list to the correct size;
     *
     * @param numOfVariables    how many variables a clause could have;
     */
    public Clause(int numOfVariables)
    {
        variables = new ArrayList<>(numOfVariables);
    }

    /**
     * Add the valid variable to the list
     *
     * @param variable  the variable being added to the clause
     */
    public void addVariable(int variable){
        variables.add(variable);
    }

    /**
     * Evaluate the clause to be either true or false depending on the test values provided
     *
     * @param testValues the test values being evaluated
     * @return true, iff the clause can be solved with the given test values
     */
    public boolean evaluate(boolean[] testValues) {
        boolean result;

        //if it is an empty clause
        if (variables.isEmpty())
            return true;

        for (Integer number : variables) {
            // to ensure the variable is flipped and index does not go out of bounds
            if (number < 0) {
                result = !testValues[(number * -1) - 1];
            } else {
                result = testValues[number - 1];
            }
            //if there is a positive in the clause, the rest will be true
            if (result)
                return true;
        }
        // if there was no positive to stop the loop, the solution failed for this clause
        return false;
    }

    /**
     * Return a string format of the variable ArrayList to see it in debugging mode
     *
     * @return a string format of the variable array
     */
    public String toString() {
        return variables.toString();
    }
}