package com.example.jonat.rajawaliwithfragment;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.ArcballCamera;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import FapUtils.FapUtil;
import Loader.MyLoaderOBJ;

/**
 * Created by jonat on 2017. 03. 07..
 */
public class MainRenderer extends Renderer {

    private PointLight pointLight1;
    private PointLight pointLight2;
    private Sphere sphere1;
    private Sphere sphere2;
    private Object3D mObjectGroup;

    private FapUtil fapUtil;
    private Model model;
    private long ido = 0;

//    private Task task;

    MyLoaderOBJ objParser;

    private double[] light1Position = new double[]{-10, 0, 15};
    private double[] light2Position = new double[]{10, 0, 15};

    private int iter = 0;

    private float[] renderedVertices;
    private float[] renderedVertices2;
    private Map<Integer, Float> changedVertices;
    private ArrayList<Float> tmpVertices;

    private Map<Integer, ArrayList<Integer>> indexek;

    public MainRenderer(Context context) {
        super(context);
        model = new Model(context);
        fapUtil = new FapUtil(context);
        indexek = new TreeMap<>();
    }

    @Override
    protected void initScene() {
        pointLight1 = new PointLight();
        pointLight1.setColor(1.0f, 1.0f, 1.0f);
        pointLight1.setPower(5);
        pointLight1.setPosition(light1Position[0], light1Position[1], light1Position[2]);
        getCurrentScene().addLight(pointLight1);

        pointLight2 = new PointLight();
        pointLight2.setColor(1.0f, 1.0f, 1.0f);
        pointLight2.setPower(5);
        pointLight2.setPosition(light2Position[0], light2Position[1], light2Position[2]);
        getCurrentScene().addLight(pointLight2);

        Material material = new Material();
        material.enableLighting(true);
        material.setDiffuseMethod(new DiffuseMethod.Lambert());
        float[] a = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        material.setColor(a);

        sphere1 = new Sphere(1f, 24, 24);
        sphere1.setPosition(light1Position[0], light1Position[1], light1Position[2]);
        sphere1.setMaterial(material);
        getCurrentScene().addChild(sphere1);

        sphere2 = new Sphere(1f, 24, 24);
        sphere2.setPosition(light2Position[0], light2Position[1], light2Position[2]);
        sphere2.setMaterial(material);
        getCurrentScene().addChild(sphere2);

        objParser = new MyLoaderOBJ(mContext.getResources(), mTextureManager, R.raw.kondor_zoltan_with_mouth_1_obj);
        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.5);
            getCurrentScene().addChild(mObjectGroup);
        } catch (ParsingException e) {
            e.printStackTrace();
        }

        ArcballCamera arcball = new ArcballCamera(mContext, ((Activity) mContext).findViewById(R.id.drawer_layout));
        arcball.setTarget(mObjectGroup); //your 3D Object
        arcball.setPosition(0, 0, 20); //optional

        getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(), arcball);

        renderedVertices = new float[objParser.vertices.length];
        renderedVertices2 = new float[objParser.vertices.length];
        for (int i = 0; i < objParser.vertices.length; i++) {
            renderedVertices[i] = objParser.vertices[i];
            renderedVertices2[i] = objParser.vertices[i];
        }

        for (int i = 0; i < objParser.currObjIndexData.vertexIndices.size(); i++) {
            if (indexek.containsKey(objParser.currObjIndexData.vertexIndices.get(i))){
                indexek.get(objParser.currObjIndexData.vertexIndices.get(i)).add(i);
            }else {
                ArrayList temp = new ArrayList();
                temp.add(i);
                indexek.put(objParser.currObjIndexData.vertexIndices.get(i), temp);
            }
        }

        preCalculateDistance();
    }

    @Override
    public void onOffsetsChanged(float v, float v1, float v2, float v3, int i, int i1) {
    }

    @Override
    public void onTouchEvent(MotionEvent motionEvent) {
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);
        update();
    }

    public void writeToFile(String s) {
        File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "Mary");
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        try {
            File f = new File(cacheDir, "log.txt");
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(s);
            writer.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean egyenlo(float[] f1, float[] f2){
        boolean t = true;

        if (f1.length != f2.length){
            return false;
        }else {
            for (int i = 0; i < f1.length; i++) {
                if (f1[i] != f2[i]){
                    System.out.println();
                    return false;
                }
            }
        }
        return true;
    }

    public void update() {
        if (fapUtil.getnFaps() != 0) {

            if (iter < fapUtil.getnFaps()) {
                changedVertices = new HashMap<>();
                int index = 0;
                for (int i = 0; i < 68; i++) {
                    if (fapUtil.getMask().get(iter).get(i) == 1) {
                        if (fapUtil.getFdps().containsKey(i)) {
                            for (int j = 0; j < fapUtil.getFdps().get(i).size(); j++) {
                                for (int k = 0; k < fapUtil.getFdps().get(i).get(j).getIndeces().size(); k++) {
                                    int bindex = fapUtil.getFdps().get(i).get(j).getIndeces().get(k);
                                    float fapuMap = (float) fapUtil.getFapuMap().get(i);
                                    float delta = (fapUtil.getFaps().get(iter).get(index)) *
                                            (fapUtil.getFdps().get(i).get(j).getWeights().get(k) * fapuMap);

                                    if (delta != 0) {
                                        ArrayList<Integer> arrayList = indexek.get(bindex);
                                        for (int i1 : arrayList){
                                            if (!changedVertices.containsKey(i1*3))
                                                changedVertices.put(i1*3, renderedVertices[i1*3]);
                                            if (!changedVertices.containsKey(i1*3+1))
                                                changedVertices.put(i1*3+1, renderedVertices[i1*3+1]);
                                            if (!changedVertices.containsKey(i1*3+2))
                                                changedVertices.put(i1*3+2, renderedVertices[i1*3+2]);

                                            renderedVertices[i1*3] += delta * fapUtil.getFapAxis().get(i)[0];
                                            renderedVertices[i1*3+1] += delta * fapUtil.getFapAxis().get(i)[1];
                                            renderedVertices[i1*3+2] += delta * fapUtil.getFapAxis().get(i)[2];
                                        }
                                    }
                                }
                            }
                        }
                        index++;
                    }
                }

                iter++;
                mObjectGroup.getGeometry().setData(renderedVertices, objParser.normalsList, objParser.textureCoords, objParser.colors, objParser.indeces, false);
                mObjectGroup.reload();

                for (int a : changedVertices.keySet()) {
                    renderedVertices[a] = changedVertices.get(a);
                }
            }
            if (iter == fapUtil.getnFaps()) {
                fapUtil.setnFaps(0);
            }
        }
    }

    public void play() {
        fapUtil.loadFaps();
        iter = 0;
    }

    public void preCalculateDistance() {
        for (int i = 0; i < 68; i++) {
            if (fapUtil.getFdps().get(i) != null) {
                List<Influence> tmp = fapUtil.getFdps().get(i);
                for (Influence influence : tmp) {
                    calculateDistance(influence);
                }
            }
        }
    }

    public void calculateDistance(Influence influence) {
        int index2 = influence.getFp();
        float maxDist = 0;
        float val;


        if (influence.getType().equals("RaisedCosInfluenceWaveX")) {
            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                // x
                val = calculateDistanceXYZ(model.getVertices().get(index1 * 3),
                        model.getVertices().get(index2 * 3));
                if (val > maxDist)
                    maxDist = val;
            }

            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                val = calculateDistanceXYZ(model.getVertices().get(index1 * 3),
                        model.getVertices().get(index2 * 3));
                influence.getWeights().add((float) ((1 + Math.cos(Math.PI * val / maxDist)) * influence.getWeight()));
            }
        } else if (influence.getType().equals("RaisedCosInfluenceWaveY")) {
            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                // y
                val = calculateDistanceXYZ(model.getVertices().get(index1 * 3 + 1),
                        model.getVertices().get(index2 * 3 + 1));
                if (val > maxDist)
                    maxDist = val;
            }

            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                val = calculateDistanceXYZ(model.getVertices().get(index1 * 3 + 1),
                        model.getVertices().get(index2 * 3 + 1));
                influence.getWeights().add((float) ((1 + Math.cos(Math.PI * val / maxDist)) * influence.getWeight()));
            }
        } else if (influence.getType().equals("RaisedCosInfluenceWaveZ")) {
            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                // z
                val = calculateDistanceXYZ(model.getVertices().get(index1 * 3 + 2),
                        model.getVertices().get(index2 * 3 + 2));
                if (val > maxDist)
                    maxDist = val;
            }

            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                val = calculateDistanceXYZ(model.getVertices().get(index1 * 3 + 2),
                        model.getVertices().get(index2 * 3 + 2));
                influence.getWeights().add((float) ((1 + Math.cos(Math.PI * val / maxDist)) * influence.getWeight()));
            }
        } else if (influence.getType().equals("RaisedCosInfluenceSph")) {
            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                val = calculateDistanceSph(model.getVertices().get(index1 * 3), model.getVertices().get(index1 * 3 + 1), model.getVertices().get(index1 * 3 + 2),
                        model.getVertices().get(index2 * 3), model.getVertices().get(index2 * 3 + 1), model.getVertices().get(index2 * 3 + 2));
                if (val > maxDist)
                    maxDist = val;
            }

            for (int i = 0; i < influence.getIndeces().size(); i++) {
                int index1 = influence.getIndeces().get(i);
                val = calculateDistanceSph(model.getVertices().get(index1 * 3), model.getVertices().get(index1 * 3 + 1), model.getVertices().get(index1 * 3 + 2),
                        model.getVertices().get(index2 * 3), model.getVertices().get(index2 * 3 + 1), model.getVertices().get(index2 * 3 + 2));
                influence.getWeights().add((float) ((1 + Math.cos(Math.PI * val / maxDist)) * influence.getWeight()));
            }
        }
    }

    public float calculateDistanceXYZ(float first, float second) {
        float d = second - first;
        return Math.abs(d);
    }


    public float calculateDistanceSph(float firstX, float firstY, float firstZ, float secondX, float secondY, float secondZ) {
        float d1 = secondX - firstX;
        float d2 = secondY - firstY;
        float d3 = secondZ - firstZ;
        return (float) Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
    }
}
