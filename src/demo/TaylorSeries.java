package demo;

import java.util.Arrays;
import java.util.Scanner;

public class TaylorSeries {
    public static void main(String args[]) {

        String line = "x.i9j11k2d1\" index=\"603\" value=\"0\"/>";
        String[] numbers = line.split("\\D+");

        for (String number : numbers) {
            if (!number.isEmpty()) {
                System.out.println(number);
            }
        }
        //System.out.println(Arrays.toString(numbers));

//        Scanner input = new Scanner(System.in);
//
//        System.out.println("Enter x:");
//        double x = input.nextDouble();
//
//        double result = calcExp(x);
//        System.out.println("calcExp(x) = " + result);
//        System.out.println("       e^x = " + Math.pow(Math.E, x));
    }

    static double calcExp(double x) {
        double eps = 0.0000000000000000001;
        double elem = 1.0;
        double sum = 0.0;
        boolean negative = false;
        int i = 1;
        sum = 0.0;

        if (x < 0) {
            negative = true;
            x = -x;
        }

        do {
            sum += elem;
            elem *= x / i;
            i++;
            if (sum > Double.MAX_VALUE) {
                System.out.println("Too Large");
                break;
            }
        }
        while (elem >= eps);

        return negative ? 1.0 / sum : sum;
    }
}
