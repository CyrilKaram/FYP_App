package com.example.firsttestapp;


public class State {
    private QLearning learner;
    private int time_state;
    private int location_state;
    private int scenario_state;
    private int ID;

    public State (int ID, QLearning learner, int time_state, int location_state, int scenario_state) {
        this.learner = learner;
        this.time_state = time_state;
        this.location_state = location_state;
        this.scenario_state = scenario_state;
        this.ID=ID;
    }

    public QLearning getlearner() {
        return learner;
    }

    public int gettime() {
        return time_state;
    }

    public int getlocation() {
        return location_state;
    }

    public int getscenario() {
        return scenario_state;
    }

    public int getID() {
        return ID;
    }
}

