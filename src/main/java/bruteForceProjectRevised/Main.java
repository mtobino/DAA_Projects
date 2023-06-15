package bruteForceProjectRevised;

import java.io.File;
import java.util.Date;

public class Main {
    public static void main(String[] args)
    {
        long programStart = System.currentTimeMillis();
        File[] files = new File("bruteForceInputs").listFiles();
        assert files != null;
        for(File file : files)
        {
            try {
                System.out.println("Parsing " + file.getName() + "...");
                CNFReader reader = new CNFReader(file);


                CNFSolver solver = new CNFSolver(reader.clauses, reader.numOfVariables);
                long startTime = System.currentTimeMillis(); // start a timer
                System.out.println("Testing: " + file.getName() + " Started at " + new Date(startTime));
                System.out.println("\tSatisfiable: " + solver.isSatisfiable());
                long endTime = System.currentTimeMillis() - startTime; // end the timer

                if(endTime > 60000)
                {
                    System.out.println("\tTesting took about " + (endTime/60000) + " minutes");
                }
                else if(endTime > 1000)
                {
                    System.out.println("\tTesting took about " + (endTime/1000) + " seconds");
                }
                else
                {
                    System.out.println("\tTesting took " + endTime + " milliseconds"); //print out how long in ms it took
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        long endOfProgram = System.currentTimeMillis() - programStart;
        System.out.println("\nProgram took " + endOfProgram + " milliseconds to complete");
        System.out.println("\nProgram took about " + (endOfProgram/60000) + " minutes to complete");
    }

}
