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

    private final int actionsCount = 5; //Number of states

    private final int reward = 100; //Useless for now
    private final int penalty = -10;

    private int[] actions; //REMOVE List of actions
    private int[] R; //REMOVE List of rewards (has to be replaced with criteria evaluation)
    private double[] Q; //List of Q values

    //Initialize arrays R Q and Actions
    public QLearning() {
        R = new int[actionsCount]; //REMOVE
        Q = new double[actionsCount]; //REMOVE initialise at the beginning {0.7, 0.8, 0.9}
        actions =  new int[actionsCount]; //REMOVE

        int a = 1;
        for (int i=0; i<actions.length; i++) { //REMOVE
            actions[i]=a;
            a++;
        }

        for (int k = 0; k < actionsCount; k++) { //REMOVE
            R[k] = 5*(k+1);
        }
        initializeQ(); //REMOVE
        printR(R); //REMOVE
        printActions(actions); //REMOVE
    }

    //Set Q values to R values
    void initializeQ() //REMOVE
    {
        System.out.println("Q Values ");
        for (int i = 0; i < actionsCount; i++){
            Q[i] = (double)(R[i]-1);
            System.out.println(Q[i]);
        }
    }

    // Used for debugging
    void printR(int[] matrix) { //REMOVE
        System.out.println("Rewards: ");
        for (int i = 0; i < actionsCount; i++) {
            System.out.println(matrix[i]);
        }
    }

    // Used for debugging
    void printActions(int[] matrix) { //REMOVE
        System.out.println("Actions: ");
        for (int i = 0; i < actionsCount; i++) {
            System.out.println(matrix[i]);
        }
    }

    // Used for debugging
    void printQ() {
        System.out.println("Q matrix");
        for (int i = 0; i < Q.length; i++) {
            System.out.printf("%6.2f ", (Q[i]));
            System.out.println();
        }
    }

    //Exploration or exploitation
    public void take_decision() { //Return String or int(action) and Tell the User with Toast
        Random rand = new Random();
        double y;
        for (int i = 0; i < 10; i++) { // Train cycle 10 times

            y = rand.nextDouble();
            if (y>epsilon) {
                calculateQ("Exploitation");
            }
            else {
                calculateQ("Exploration");
            }
            nbtrials++;
            epsilon= 1 - 1/(1+Math.exp((-nbtrials+7)/2));
        }
    }

    public void calculateQ(String exp) { //Replace with Update_Q at the end
        Random rand = new Random();      // Take parts of this code and put them in take_decision
        System.out.println(exp);
        double y = rand.nextDouble();
        int index;
        if (exp == "Exploitation") {
            index = maxQ(Q);
            System.out.printf("We chose action %d \n",actions[index]);
        }
        else {
            index = rand.nextInt(actions.length);
            System.out.printf("We chose action %d ",actions[index]);

            // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
            double q = Q[index];
            int r = R[index];
            System.out.printf("and it has a reward %d ",r);

            double value = (1-alpha)*q + alpha * r;
            Q[index] = value;
            System.out.printf("the new Q value is %f \n",Q[index]);
        }
    }

    // Find index of maximum Qvalue
    int maxQ(double[] matrix) { //keep and call in take_decision
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
        Q[act]=(1-alpha)*Q[act] + alpha * (throughp*w[0] +batt*w[1] +jitt*w[2] +packloss*w[3] +lat*w[4]);
        //Maybe act-1 not act
    }

    public int[] getactions() {
        return actions;
    } //REMOVE
}
