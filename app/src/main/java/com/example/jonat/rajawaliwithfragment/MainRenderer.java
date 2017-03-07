package com.example.jonat.rajawaliwithfragment;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import org.rajawali3d.Object3D;
import org.rajawali3d.cameras.ArcballCamera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderAWD;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.Renderer;

/**
 * Created by jonat on 2017. 03. 07..
 */
public class MainRenderer extends Renderer {

    private PointLight pointLight1;
    private PointLight pointLight2;
    private Sphere sphere1;
    private Sphere sphere2;
    private Object3D mObjectGroup;

    private double[] light1Position = new double[]{-10, 0, 15};
    private double[] light2Position = new double[]{10, 0, 15};
    public MainRenderer(Context context) {
        super(context);
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

        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.kondor_zoltan_with_mouth_1_obj);
        try {
            objParser.parse();
            mObjectGroup = objParser.getParsedObject();
            mObjectGroup.setScale(0.25);
            mObjectGroup.setMaterial(material);
            getCurrentScene().addChild(mObjectGroup);
        } catch (ParsingException e) {
            e.printStackTrace();
        }

        ArcballCamera arcball = new ArcballCamera(mContext, ((Activity) mContext).findViewById(R.id.drawer_layout));
        arcball.setTarget(mObjectGroup); //your 3D Object
        arcball.setPosition(0, 0, 16); //optional

        getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(), arcball);    }

    @Override
    public void onOffsetsChanged(float v, float v1, float v2, float v3, int i, int i1) {

    }

    @Override
    public void onTouchEvent(MotionEvent motionEvent) {

    }

}
