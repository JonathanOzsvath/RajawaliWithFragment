package com.example.jonat.rajawaliwithfragment;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by jonat on 2017. 04. 07..
 */

public class Model {

    private ArrayList<Float> vertices;
    private ArrayList<Float> normals;
    private Context context;

    public Model(Context context) {
        this.context = context;
        vertices = new ArrayList<>();
        normals = new ArrayList<>();

        readInVertices();
    }

    public void readInVertices(){
        InputStream inputStream = context.getResources().openRawResource(R.raw.kondor_zoltan_with_mouth_1_obj);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try {
            while ((line = reader.readLine()) != null){
                if (line.startsWith("v ")) {
                    String[] tmp = line.split(" ");
                    vertices.add(Float.parseFloat(tmp[1]));
                    vertices.add(Float.parseFloat(tmp[2]));
                    vertices.add(Float.parseFloat(tmp[3]));
                } else if(line.startsWith("vn ")){
                    String[] tmp = line.split(" ");
                    normals.add(Float.parseFloat(tmp[1]));
                    normals.add(Float.parseFloat(tmp[2]));
                    normals.add(Float.parseFloat(tmp[3]));
                } else {
                    continue;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Float> getVertices() {
        return vertices;
    }

    public void setVertices(ArrayList<Float> vertices) {
        this.vertices = vertices;
    }
}
