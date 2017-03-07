package com.example.jonat.rajawaliwithfragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.rajawali3d.lights.PointLight;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.IDisplay;
import org.rajawali3d.view.ISurface;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements IDisplay{

    public static final String TAG = "Fragment";

    protected FrameLayout frameLayout;
    protected ISurface iSurface;
    protected ISurfaceRenderer iSurfaceRenderer;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        frameLayout = (FrameLayout) inflater.inflate(getLayoutID(), container, false);

        frameLayout.findViewById(R.id.relative_layout_loader_container);
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER);

        TextView label = new TextView(getActivity());
        label.setText("Hello Maci");
        label.setTextSize(20);
        label.setTextColor(Color.rgb(255, 255, 255));
        label.setGravity(Gravity.CENTER);
        label.setHeight(100);
        ll.addView(label);

        ImageView image = new ImageView(getActivity());
        image.setImageResource(R.drawable.rajawali_outline);
        ll.addView(image);

        frameLayout.addView(ll);

        iSurface = (ISurface) frameLayout.findViewById(R.id.rajwali_surface);
        iSurfaceRenderer = createRenderer();
        iSurface.setSurfaceRenderer(iSurfaceRenderer);
        return frameLayout;
    }

    @Override
    public ISurfaceRenderer createRenderer() {
        return new MainRenderer(getActivity());
    }

    /*Fragmenthez tartozó xml*/
    @Override
    public int getLayoutID() {
        return R.layout.fragment_main;
    }
}
