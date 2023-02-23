package com.company;

public class Main {

    public static void main(String[] args) {

        // Initializing all the arrays
        double [] delay = new double[10];
        double [] throughput = new double[10];
        double [] load = new double[10];
        double [] loss = new double[10];

        int l=0;

        for(double i=0.1;i<=1;i+=0.1){

            Simulation a = new Simulation(8,5,i);
            a.run(1000000);

            delay[l]=a.delay();
            throughput[l]= a.throughput();
            loss[l] = a.loss();
            load[l] = i;
            l++;
        }


        /* DELAY */
        System.out.println("Delay");
        for (double i:delay)
            System.out.print(i + " , ");

        System.out.println();

        System.out.println("Throughput");
        /* THROUGHPUT */
        for (double i : throughput)
            System.out.print(i + " , ");

        System.out.println();

        System.out.println("Load");
        /* LOAD */
        for (double i : load)
            System.out.print(i + " , ");

        System.out.println();

        System.out.println("Loss");
        /* LOSS */
        for (double i : loss)
            System.out.print(i + " , ");


    }
}