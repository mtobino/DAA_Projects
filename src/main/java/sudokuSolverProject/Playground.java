package sudokuSolverProject;
/*
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
*/
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Playground {
    public static void main(String[] args) {
        Stack<int[]> clauses = new Stack<>();
       //ArrayList<int[]> clauses = new ArrayList<>();
        try
        {
           File direc = new File("sudokuInputs");
           File[] files = direc.listFiles();
           File test = files[0];
           Scanner scanner = new Scanner(test);
           int gridLength = scanner.nextInt();
           int gridSize = gridLength * gridLength;
           scanner.nextLine();
           int row = 1;
           int col = 1;

           //generate the clue clauses
           while(scanner.hasNextLine())
           {
               String line = scanner.nextLine();
               String[] clues = line.split(" ");
               for(String clue : clues)
               {
                   if(!clue.equals("0"))
                   {
                       int[] clueNum = { encodeVariable(Integer.parseInt(clue), row, col, gridSize) };
                       clauses.push(clueNum);
                   }
                   col++;
               }
               row++;
           }

           // ensure each cell has at least one value
           row = 1;
           col = 1;
           int val = 1;
           ArrayList<Integer> nums;
           // go through every row
           while(row < gridSize + 1)
           {
               nums = new ArrayList<>(gridSize);
               // go through each column
               while(col < gridSize + 1)
               {
                   // make sure each variable has a place in each cell
                   while (val < gridSize + 1){
                       nums.add(encodeVariable(val, row, col, gridSize));
                       val++;
                   }
                   col++;
               }
               // add the created clause to the list of clauses and reset val and col to 1 for next loop
               clauses.push(clauseFromList(nums));
               col = 1;
               val = 1;
               row++;
           }

           // ensure each cell has at least one value
           row = 1;
           col = 1;
           val = 1;
            while(row < gridSize + 1)
            {
                while(col < gridSize + 1)
                {
                    int[] clause = new int[2];
                    while( val < gridSize + 1)
                    {
                        int nextVal = val + 1;
                        while(nextVal < gridSize + 1)
                        {
                            clause[0] = -encodeVariable(val, row, col, gridSize);
                            clause[1] = -encodeVariable(nextVal, row, col, gridSize);
                            clauses.push(clause);
                            nextVal++;
                        }
                        val++;
                    }
                    val = 1;
                    col++;
                }
                col = 1;
                val++;
                row++;
            }

            row = 1;
            col = 1;
            val = 1;
            //Ensure each row and col have one copy of the val
            while( val < gridSize + 1)
            {
                // each row has the value
                while(row < gridSize + 1)
                {
                    nums = new ArrayList<>(gridSize);
                    while(col < gridSize + 1)
                    {
                        nums.add(encodeVariable(val, row, col, gridSize));
                        col++;
                    }
                    clauses.push(clauseFromList(nums));
                    row++;
                }
                // each col has the value
                while(col < gridSize + 1)
                {
                    nums = new ArrayList<>(gridSize);
                    while(row < gridSize + 1)
                    {
                        nums.add(encodeVariable(val, row, col, gridSize));
                        row++;
                    }
                    clauses.push(clauseFromList(nums));
                    col++;
                }
                val++;
            }

            // Each nxn grid is complete with all variables included
            int subRow = 1;
            int subCol = 1;
            while( val < gridLength + 1)
            {
                while (subRow < gridLength + 1)
                {
                    while (subCol < gridLength + 1)
                    {
                        String you = "bozo";
                    }
                }
                val++;
            }


       }
       catch (Exception e)
       {
           System.out.println(e.getMessage());
       }


    }

    private static int encodeVariable(int value, int row, int col, int gridSize)
    {
        return (row - 1)*gridSize*gridSize + (col - 1) * gridSize + (value - 1) + 1;
    }
    private static int decodeVariable(int var, int row, int col, int gridSize)
    {
        return -(gridSize*gridSize)*row - (gridSize*col) + (gridSize*gridSize) + gridSize + var;
    }
    private static int[] clauseFromList(ArrayList<Integer> list){
        int[] result = new int[list.size()];
        for(int i = 0; i < result.length; i++)
        {
            result[i] = list.get(i);
        }
        return result;
    }

}
