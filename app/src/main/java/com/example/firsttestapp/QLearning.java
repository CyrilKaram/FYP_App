package com.example.firsttestapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class QLearning {

    private final double alpha = 0.1; // Learning rate
    private double epsilon = 1; // Exploration rate
    private double nbtrials = 0;
    Double[] Q_values={0.7,0.8,0.9};
    private final int actionsCount = 3; //Number of states


    //Initialize arrays R Q and Actions
    public QLearning() {

    }


    // Used for debugging
    void printQ() {
        System.out.println("Q matrix");
        for (int i = 0; i < Q_values.length; i++) {
            System.out.printf("%6.2f ", (Q_values[i]));
            System.out.println();
        }
    }

    //Exploration or exploitation
    public int take_decision() { //Return String or int(action) and Tell the User with Toast
        Random rand = new Random();
        double y;
        y = rand.nextDouble();
        nbtrials++;
        epsilon= 1 - 1/(1+Math.exp((-nbtrials+7)/2));
        if (y>epsilon) {
            return calculateQ("Exploitation");
        }
        else {
            return calculateQ("Exploration");
        }
    }

    public int calculateQ(String exp) { //Replace with Update_Q at the end
        Random rand = new Random();      // Take parts of this code and put them in take_decision
        System.out.println(exp);
        double y = rand.nextDouble();
        int index;
        if (exp == "Exploitation") {
            index = maxQ(Q_values);
            System.out.printf("We chose action %d \n",index+1);
        }
        else {
            index = rand.nextInt(3); //3 Actions
            System.out.printf("We chose action %d ",index+1);
        }
        return index;
    }

    // Find index of maximum Qvalue
    int maxQ(Double[] matrix) { //keep and call in take_decision
        double maxValue = -10;
        int maxvalue_index = 0;
        for (int s=0; s<matrix.length; s++) {
            double value = matrix[s];

            if (value > maxValue)
                maxValue = value;
            maxvalue_index = s;
        }
        return maxvalue_index;
    }

    public void update_Q(int act, double[] w, double throughp, double batt, double jitt, double packloss, double lat){
        //Normalisation
        throughp=throughp/30; //30Mbps

        if (jitt!=0){
            jitt=6/jitt;
        } else {
            jitt=1;
        }

        if (packloss!=0){
            packloss=0.1/packloss; //0.1%
        } else{
            packloss=1;
        }

        if(lat!=0){
            lat=25/lat; //25ms
        } else {
            lat=1;
        }

        //UPDATE Q = (1-alpha) * Q +alpha * reward
        Q_values[act]=(1-alpha)*Q_values[act] + alpha * (throughp*w[0] +batt*w[1] +jitt*w[2] +packloss*w[3] +lat*w[4]);
        //Maybe act-1 not act
    }

}
