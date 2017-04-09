package Loader;

import android.content.res.Resources;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.ALoader;
import org.rajawali3d.loader.IMeshLoader;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.renderer.Renderer;

import java.io.File;

/**
 * Created by jonat on 2017. 04. 09..
 */

public abstract class MyAMeshLoader extends ALoader implements IMeshLoader {
    protected TextureManager mTextureManager;
    protected Object3D mRootObject;

    public MyAMeshLoader(File file) {
        super(file);
        this.mRootObject = new Object3D();
    }

    public MyAMeshLoader(String fileOnSDCard) {
        super(fileOnSDCard);
        this.mRootObject = new Object3D();
    }

    public MyAMeshLoader(Renderer renderer, String fileOnSDCard) {
        super(renderer, fileOnSDCard);
        this.mRootObject = new Object3D();
    }

    public MyAMeshLoader(Resources resources, TextureManager textureManager, int resourceId) {
        super(resources, resourceId);
        this.mTextureManager = textureManager;
        this.mRootObject = new Object3D();
    }

    public MyAMeshLoader(Renderer renderer, File file) {
        super(renderer, file);
        this.mRootObject = new Object3D();
    }

    public MyAMeshLoader parse() throws ParsingException {
        super.parse();
        return this;
    }

    public Object3D getParsedObject() {
        return this.mRootObject;
    }

    protected class MaterialDef {
        public String name;
        public int ambientColor;
        public int diffuseColor;
        public int specularColor;
        public float specularCoefficient;
        public float alpha = 1.0F;
        public String ambientTexture;
        public String diffuseTexture;
        public String specularColorTexture;
        public String specularHighlightTexture;
        public String alphaTexture;
        public String bumpTexture;

        protected MaterialDef() {
        }
    }
}

