package project1;

import java.util.Arrays;
//import java.util.Scanner;
//import java.util.ArrayList;

public class Tester {
    public static void main(String[] args)
    {
        test2();
    }

    private static void test1(){
        String line = "p cnf 20  91";
        String numbers = line.substring(6);
        String[] nums = numbers.split("' '+");
        String list = "[" + nums[0];
        for(int i = 1 ; i < nums.length; i++){
            list += "," + nums[i];
        }
        System.out.println(list + "]");
    }

    private static void test2(){
        Boolean[] test = new Boolean[10];
        Arrays.fill(test, false);
        long count = 1;
        while(true)
        {
            printArray(test);
            if(!isAllTrue(test))
            {
                generateNextVars(test);
            }
            else
            {
                break;
            }
            count++;
        }
        System.out.println(count);
    }

    private static void printArray(Boolean[] test)
    {
        String result = "[" + test[0];
        for(int i = 1; i < test.length; i++)
        {
            result += ", " + test[i];
        }
        System.out.println(result + "]");
    }
    private static void generateNextVars(Boolean[] testValues) {
        int zeroPos = 0;
        // find the right most zero and store it into zero pos
        for (int i = 0; i < testValues.length; i++) {
            if (!testValues[i])
                zeroPos = i;
        }
        // set the right most zero to true
        testValues[zeroPos] = true;

        //turn every value after the right most zero to false
        for (int j = zeroPos + 1; j < testValues.length; j++) {
            testValues[j] = false;
        }
        //return testValues;

    }
    private static boolean isAllTrue(Boolean[] testValues)
    {
        boolean allTrue = true;
        for(int i = 0; i < testValues.length && allTrue; i++)
        {
            allTrue = testValues[i];
        }
        return allTrue;

    }
}
