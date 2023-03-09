package bruteForceProject;

import java.util.ArrayList;
//import java.util.Iterator;

/**
 * A place to store and evaluate the clauses
 *
 * @author Matthew Tobino
 */
public class Clause {
    ArrayList<Integer> variables;
    public Clause(int numOfVariables, String numbers)
    {
        variables = new ArrayList<>(numOfVariables) ;
        buildArray(numbers);
    }

    /**
     * Add every number in the number line to the Array List except for the
     * terminating zero.
     *
     * @param numbers   The string of numbers used to build a clause
     */
    private void buildArray(String numbers)
    {
        String[] soleNumbers = numbers.split("[ ]+");
        // ensure that numbers are properly spaced out
        for (int i = 0; i < soleNumbers.length - 1; i++)
        {
            variables.add(Integer.parseInt(soleNumbers[i]));
        }
    }

    /**
     * Evaluate the clause to be either true or false depending on the test values provided
     *
     * @param   testValues  the test values being evaluated
     * @return              true, iff the clause can be solved with the given test values
     */
    public boolean evaluate(Boolean[] testValues)
    {
        boolean result;

        //if it is an empty clause
        if(variables.isEmpty())
            return true;

        for (Integer number : variables)
        {
            // to ensure the variable is flipped and index does not go out of bounds
            if (number < 0)
            {
                result = !testValues[(number * -1) - 1];
            }
            else
            {
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
     * @return  a string format of the variable array
     */
    public String toString()
    {
        return variables.toString();
    }
}
