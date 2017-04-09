package com.example.jonat.rajawaliwithfragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 2017. 04. 02..
 */

public class Influence {
    private String type;
    private float weight;
    private List<Integer> indeces;
    private int fp; //index
    private String affects;
    private String name;
    private List<Float> weights;

    public Influence() {
        indeces = new ArrayList<>();
        weights = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public List<Integer> getIndeces() {
        return indeces;
    }

    public void setIndeces(List<Integer> indeces) {
        this.indeces = indeces;
    }

    public int getFp() {
        return fp;
    }

    public void setFp(int fp) {
        this.fp = fp;
    }

    public String getAffects() {
        return affects;
    }

    public void setAffects(String affects) {
        this.affects = affects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Float> getWeights() {
        return weights;
    }

    public void setWeights(List<Float> weights) {
        this.weights = weights;
    }
}
