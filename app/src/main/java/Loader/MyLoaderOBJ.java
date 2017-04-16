package Loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.rajawali3d.Object3D;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Etc1Texture;
import org.rajawali3d.materials.textures.NormalMapTexture;
import org.rajawali3d.materials.textures.SpecularMapTexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.materials.textures.TextureManager;
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.util.RajLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

public class MyLoaderOBJ extends MyAMeshLoader {
    protected final String VERTEX;
    protected final String FACE;
    protected final String TEXCOORD;
    protected final String NORMAL;
    protected final String OBJECT;
    protected final String GROUP;
    protected final String MATERIAL_LIB;
    protected final String USE_MATERIAL;
    protected final String NEW_MATERIAL;
    protected final String DIFFUSE_COLOR;
    protected final String DIFFUSE_TEX_MAP;

    public MyLoaderOBJ.ObjIndexData currObjIndexData;
    public ArrayList objIndices;
    public ArrayList<Float> verticesAList;
    public ArrayList texCoords;
    public ArrayList normalsAList;
    public MyLoaderOBJ.MaterialLib matLib;
    public String currentMaterialName;
    public HashMap groups;

    public float[] vertices;
    public float[] textureCoords;
    public float[] normalsList;
    public float[] colors;
    public int[] indeces;

    private static Field mParent;

    public MyLoaderOBJ(Renderer renderer, String fileOnSDCard) {
        super(renderer, fileOnSDCard);
        this.VERTEX = "v";
        this.FACE = "f";
        this.TEXCOORD = "vt";
        this.NORMAL = "vn";
        this.OBJECT = "o";
        this.GROUP = "g";
        this.MATERIAL_LIB = "mtllib";
        this.USE_MATERIAL = "usemtl";
        this.NEW_MATERIAL = "newmtl";
        this.DIFFUSE_COLOR = "Kd";
        this.DIFFUSE_TEX_MAP = "map_Kd";
    }

    public MyLoaderOBJ(Renderer renderer, int resourceId) {
        this(renderer.getContext().getResources(), renderer.getTextureManager(), resourceId);
    }

    public MyLoaderOBJ(Resources resources, TextureManager textureManager, int resourceId) {
        super(resources, textureManager, resourceId);
        this.VERTEX = "v";
        this.FACE = "f";
        this.TEXCOORD = "vt";
        this.NORMAL = "vn";
        this.OBJECT = "o";
        this.GROUP = "g";
        this.MATERIAL_LIB = "mtllib";
        this.USE_MATERIAL = "usemtl";
        this.NEW_MATERIAL = "newmtl";
        this.DIFFUSE_COLOR = "Kd";
        this.DIFFUSE_TEX_MAP = "map_Kd";

        currObjIndexData = new MyLoaderOBJ.ObjIndexData(new Object3D(generateObjectName()));
        objIndices = new ArrayList();
        verticesAList = new ArrayList();
        texCoords = new ArrayList();
        normalsAList = new ArrayList();
        matLib = new MyLoaderOBJ.MaterialLib();
        currentMaterialName = null;
        groups = new HashMap();
    }

    public MyLoaderOBJ(Renderer renderer, File file) {
        super(renderer, file);
        this.VERTEX = "v";
        this.FACE = "f";
        this.TEXCOORD = "vt";
        this.NORMAL = "vn";
        this.OBJECT = "o";
        this.GROUP = "g";
        this.MATERIAL_LIB = "mtllib";
        this.USE_MATERIAL = "usemtl";
        this.NEW_MATERIAL = "newmtl";
        this.DIFFUSE_COLOR = "Kd";
        this.DIFFUSE_TEX_MAP = "map_Kd";
    }

    public MyLoaderOBJ parse() throws ParsingException {
        super.parse();
        BufferedReader buffer = null;
        if(this.mFile == null) {
            InputStream line = this.mResources.openRawResource(this.mResourceId);
            buffer = new BufferedReader(new InputStreamReader(line));
        } else {
            try {
                buffer = new BufferedReader(new FileReader(this.mFile));
            } catch (FileNotFoundException var31) {
                RajLog.e("[" + this.getClass().getCanonicalName() + "] Could not find file.");
                var31.printStackTrace();
            }
        }


        boolean currentObjHasFaces = false;
        Object3D currentGroup = this.mRootObject;
        this.mRootObject.setName("default");

        int countNumObjects;
        int i1;
        int tme;
        try {
            String lines;
            while((lines = buffer.readLine()) != null) {
                if(lines.length() != 0 && lines.charAt(0) != 35) {
                    StringTokenizer numObjects = new StringTokenizer(lines, " ");
                    countNumObjects = numObjects.countTokens();
                    if(countNumObjects != 0) {
                        String group = numObjects.nextToken();
                        if(group.equals("v")) {
                            verticesAList.add(Float.valueOf(Float.parseFloat(numObjects.nextToken())));
                            verticesAList.add(Float.valueOf(Float.parseFloat(numObjects.nextToken())));
                            verticesAList.add(Float.valueOf(Float.parseFloat(numObjects.nextToken())));
                        } else if(group.equals("f")) {
                            currentObjHasFaces = true;
                            boolean var39 = countNumObjects == 5;
                            int[] var40 = new int[4];
                            int[] var42 = new int[4];
                            int[] var44 = new int[4];
                            boolean var46 = lines.indexOf("//") > -1;
                            if(var46) {
                                lines = lines.replace("//", "/");
                            }

                            numObjects = new StringTokenizer(lines);
                            numObjects.nextToken();
                            StringTokenizer aIndices = new StringTokenizer(numObjects.nextToken(), "/");
                            tme = aIndices.countTokens();
                            boolean ni = tme >= 2 && !var46;
                            boolean e = tme == 3 || tme == 2 && var46;

                            for(int indices = 1; indices < countNumObjects; ++indices) {
                                if(indices > 1) {
                                    aIndices = new StringTokenizer(numObjects.nextToken(), "/");
                                }

                                int idx = Integer.parseInt(aIndices.nextToken());
                                if(idx < 0) {
                                    idx += verticesAList.size() / 3;
                                } else {
                                    --idx;
                                }

                                if(!var39) {
                                    currObjIndexData.vertexIndices.add(Integer.valueOf(idx));
                                } else {
                                    var40[indices - 1] = idx;
                                }

                                if(ni) {
                                    idx = Integer.parseInt(aIndices.nextToken());
                                    if(idx < 0) {
                                        idx += texCoords.size() / 2;
                                    } else {
                                        --idx;
                                    }

                                    if(!var39) {
                                        currObjIndexData.texCoordIndices.add(Integer.valueOf(idx));
                                    } else {
                                        var42[indices - 1] = idx;
                                    }
                                }

                                if(e) {
                                    idx = Integer.parseInt(aIndices.nextToken());
                                    if(idx < 0) {
                                        idx += normalsAList.size() / 3;
                                    } else {
                                        --idx;
                                    }

                                    if(!var39) {
                                        currObjIndexData.normalIndices.add(Integer.valueOf(idx));
                                    } else {
                                        var44[indices - 1] = idx;
                                    }
                                }
                            }

                            if(var39) {
                                int[] var50 = new int[]{0, 1, 2, 0, 2, 3};

                                for(int i2 = 0; i2 < 6; ++i2) {
                                    int index = var50[i2];
                                    currObjIndexData.vertexIndices.add(Integer.valueOf(var40[index]));
                                    currObjIndexData.texCoordIndices.add(Integer.valueOf(var42[index]));
                                    currObjIndexData.normalIndices.add(Integer.valueOf(var44[index]));
                                }
                            }
                        } else if(group.equals("vt")) {
                            texCoords.add(Float.valueOf(Float.parseFloat(numObjects.nextToken())));
                            texCoords.add(Float.valueOf(1.0F - Float.parseFloat(numObjects.nextToken())));
                        } else if(group.equals("vn")) {
                            normalsAList.add(Float.valueOf(Float.parseFloat(numObjects.nextToken())));
                            normalsAList.add(Float.valueOf(Float.parseFloat(numObjects.nextToken())));
                            normalsAList.add(Float.valueOf(Float.parseFloat(numObjects.nextToken())));
                        } else if(!group.equals("g")) {
                            String var38;
                            if(group.equals("o")) {
                                var38 = numObjects.hasMoreTokens()?numObjects.nextToken():generateObjectName();
                                if(currentObjHasFaces) {
                                    objIndices.add(currObjIndexData);
                                    currObjIndexData = new MyLoaderOBJ.ObjIndexData(new Object3D(currObjIndexData.targetObj.getName()));
                                    currObjIndexData.materialName = currentMaterialName;
                                    addChildSetParent(currentGroup, currObjIndexData.targetObj);
                                    RajLog.i("Parsing object: " + currObjIndexData.targetObj.getName());
                                    currentObjHasFaces = false;
                                }

                                currObjIndexData.targetObj.setName(var38);
                            } else if(group.equals("mtllib")) {
                                if(numObjects.hasMoreTokens()) {
                                    var38 = numObjects.nextToken().replace(".", "_");
                                    RajLog.d("Found Material Lib: " + var38);
                                    if(this.mFile != null) {
                                        matLib.parse(var38, (String)null, (String)null);
                                    } else {
                                        matLib.parse(var38, this.mResources.getResourceTypeName(this.mResourceId), this.mResources.getResourcePackageName(this.mResourceId));
                                    }
                                }
                            } else if(group.equals("usemtl")) {
                                currentMaterialName = numObjects.nextToken();
                                if(currentObjHasFaces) {
                                    objIndices.add(currObjIndexData);
                                    currObjIndexData = new MyLoaderOBJ.ObjIndexData(new Object3D(generateObjectName()));
                                    RajLog.i("Parsing object: " + currObjIndexData.targetObj.getName());
                                    addChildSetParent(currentGroup, currObjIndexData.targetObj);
                                    currentObjHasFaces = false;
                                }

                                currObjIndexData.materialName = currentMaterialName;
                            }
                        } else {
                            i1 = numObjects.countTokens();
                            Object3D aVertices = null;

                            for(int aTexCoords = 0; aTexCoords < i1; ++aTexCoords) {
                                String aNormals = numObjects.nextToken();
                                if(!groups.containsKey(aNormals)) {
                                    groups.put(aNormals, new Object3D(aNormals));
                                }

                                Object3D aColors = (Object3D)groups.get(aNormals);
                                if(aVertices != null) {
                                    addChildSetParent(aColors, aVertices);
                                } else {
                                    currentGroup = aColors;
                                }

                                aVertices = aColors;
                            }

                            RajLog.i("Parsing group: " + currentGroup.getName());
                            if(currentObjHasFaces) {
                                objIndices.add(currObjIndexData);
                                currObjIndexData = new MyLoaderOBJ.ObjIndexData(new Object3D(generateObjectName()));
                                RajLog.i("Parsing object: " + currObjIndexData.targetObj.getName());
                                currObjIndexData.materialName = currentMaterialName;
                                currentObjHasFaces = false;
                            }

                            addChildSetParent(currentGroup, currObjIndexData.targetObj);
                        }
                    }
                }
            }

            buffer.close();
            if(currentObjHasFaces) {
                RajLog.i("Parsing object: " + currObjIndexData.targetObj.getName());
                objIndices.add(currObjIndexData);
            }
        } catch (IOException var32) {
            throw new ParsingException(var32);
        }

        loadObj(true);

        return this;
    }

    public MyLoaderOBJ loadObj(boolean mat) throws ParsingException {
        int var34 = objIndices.size();

        int i;
        int i1;
        int tme;
        for(i = 0; i < var34; ++i) {
            MyLoaderOBJ.ObjIndexData var36 = (MyLoaderOBJ.ObjIndexData)objIndices.get(i);
            vertices = new float[var36.vertexIndices.size() * 3];
            textureCoords = new float[var36.texCoordIndices.size() * 2];
            normalsList = new float[var36.normalIndices.size() * 3];
            colors = new float[var36.colorIndices.size() * 4];
            indeces = new int[var36.vertexIndices.size()];

            int var49;
            for(i1 = 0; i1 < var36.vertexIndices.size(); ++i1) {
                tme = ((Integer)var36.vertexIndices.get(i1)).intValue() * 3;
                var49 = i1 * 3;

                try {
                    vertices[var49] = ((Float) verticesAList.get(tme)).floatValue();
                    vertices[var49 + 1] = ((Float) verticesAList.get(tme + 1)).floatValue();
                    vertices[var49 + 2] = ((Float) verticesAList.get(tme + 2)).floatValue();
                    indeces[i1] = i1;
                } catch (ArrayIndexOutOfBoundsException var30) {
                    RajLog.d("Obj array index out of bounds: " + var49 + ", " + tme);
                }
            }

            if(texCoords != null && texCoords.size() > 0) {
                for(i1 = 0; i1 < var36.texCoordIndices.size(); ++i1) {
                    tme = ((Integer)var36.texCoordIndices.get(i1)).intValue() * 2;
                    var49 = i1 * 2;
                    textureCoords[var49] = ((Float)texCoords.get(tme)).floatValue();
                    textureCoords[var49 + 1] = ((Float)texCoords.get(tme + 1)).floatValue();
                }
            }

            for(i1 = 0; i1 < var36.colorIndices.size(); ++i1) {
                tme = ((Integer)var36.colorIndices.get(i1)).intValue() * 4;
                var49 = i1 * 4;
                textureCoords[var49] = ((Float)texCoords.get(tme)).floatValue();
                textureCoords[var49 + 1] = ((Float)texCoords.get(tme + 1)).floatValue();
                textureCoords[var49 + 2] = ((Float)texCoords.get(tme + 2)).floatValue();
                textureCoords[var49 + 3] = ((Float)texCoords.get(tme + 3)).floatValue();
            }

            for(i1 = 0; i1 < var36.normalIndices.size(); ++i1) {
                tme = ((Integer)var36.normalIndices.get(i1)).intValue() * 3;
                var49 = i1 * 3;
                if(this.normalsAList.size() == 0) {
                    RajLog.e("[" + this.getClass().getName() + "] There are no normalsAList specified for this model. Please re-export with normalsAList.");
                    throw new ParsingException("[" + this.getClass().getName() + "] There are no normalsAList specified for this model. Please re-export with normalsAList.");
                }

                normalsList[var49] = ((Float) this.normalsAList.get(tme)).floatValue();
                normalsList[var49 + 1] = ((Float) this.normalsAList.get(tme + 1)).floatValue();
                normalsList[var49 + 2] = ((Float) this.normalsAList.get(tme + 2)).floatValue();
            }

            var36.targetObj.setData(vertices, normalsList, textureCoords, colors, indeces, false);

            if(mat) {
                try {
                    matLib.setMaterial(var36.targetObj, var36.materialName);
                } catch (ATexture.TextureException var29) {
                    throw new ParsingException(var29);
                }

                if (var36.targetObj.getParent() == null) {
                    addChildSetParent(this.mRootObject, var36.targetObj);
                }
            }
        }

        Iterator var35 = groups.values().iterator();

        while(var35.hasNext()) {
            Object3D var37 = (Object3D)var35.next();
            if(var37.getParent() == null) {
                addChildSetParent(this.mRootObject, var37);
            }
        }

        if(this.mRootObject.getNumChildren() == 1 && !this.mRootObject.getChildAt(0).isContainer()) {
            this.mRootObject = this.mRootObject.getChildAt(0);
        }

        for(i = 0; i < this.mRootObject.getNumChildren(); ++i) {
            this.mergeGroupsAsObjects(this.mRootObject.getChildAt(i));
        }

        return this;
    }

    private void mergeGroupsAsObjects(Object3D object) {
        if(object.isContainer() && object.getNumChildren() == 1 && object.getChildAt(0).getName().startsWith("Object")) {
            Object3D i = object.getChildAt(0);
            object.removeChild(i);
            i.setName(object.getName());
            addChildSetParent(object.getParent(), i);
            object.getParent().removeChild(object);
            object = i;
        }

        for(int var3 = 0; var3 < object.getNumChildren(); ++var3) {
            this.mergeGroupsAsObjects(object.getChildAt(var3));
        }

    }

    private static String generateObjectName() {
        return "Object" + (int)(Math.random() * 10000.0D);
    }

    private void buildObjectGraph(Object3D parent, StringBuffer sb, String prefix) {
        sb.append(prefix).append("-->").append((parent.isContainer()?"GROUP ":"") + parent.getName()).append('\n');

        for(int i = 0; i < parent.getNumChildren(); ++i) {
            this.buildObjectGraph(parent.getChildAt(i), sb, prefix + "\t");
        }

    }

    private static void addChildSetParent(Object3D parent, Object3D object) {
        try {
            parent.addChild(object);
            mParent.set(object, parent);
        } catch (Exception var3) {
            RajLog.e("Reflection error Object3D.mParent");
        }

    }

    public String toString() {
        if(this.mRootObject == null) {
            return "Object not parsed";
        } else {
            StringBuffer sb = new StringBuffer();
            this.buildObjectGraph(this.mRootObject, sb, "");
            return sb.toString();
        }
    }

    static {
        try {
            mParent = Object3D.class.getDeclaredField("mParent");
            mParent.setAccessible(true);
        } catch (NoSuchFieldException var1) {
            RajLog.e("Reflection error Object3D.mParent");
        }

    }

    protected class MaterialLib {
        private final String MATERIAL_NAME = "newmtl";
        private final String AMBIENT_COLOR = "Ka";
        private final String DIFFUSE_COLOR = "Kd";
        private final String SPECULAR_COLOR = "Ks";
        private final String SPECULAR_COEFFICIENT = "Ns";
        private final String ALPHA_1 = "d";
        private final String ALPHA_2 = "Tr";
        private final String AMBIENT_TEXTURE = "map_Ka";
        private final String DIFFUSE_TEXTURE = "map_Kd";
        private final String SPECULAR_COLOR_TEXTURE = "map_Ks";
        private final String SPECULAR_HIGHLIGHT_TEXTURE = "map_Ns";
        private final String ALPHA_TEXTURE_1 = "map_d";
        private final String ALPHA_TEXTURE_2 = "map_Tr";
        private final String BUMP_TEXTURE = "map_Bump";
        private Stack<MaterialDef> mMaterials = new Stack();
        private String mResourcePackage;

        public MaterialLib() {
        }

        public void parse(String materialLibPath, String resourceType, String resourcePackage) {
            BufferedReader buffer = null;
            if(MyLoaderOBJ.this.mFile == null) {
                this.mResourcePackage = resourcePackage;
                int line = MyLoaderOBJ.this.mResources.getIdentifier(materialLibPath, resourceType, resourcePackage);

                try {
                    InputStream matDef = MyLoaderOBJ.this.mResources.openRawResource(line);
                    buffer = new BufferedReader(new InputStreamReader(matDef));
                } catch (Exception var11) {
                    RajLog.e("[" + this.getClass().getCanonicalName() + "] Could not find material library file (.mtl).");
                    return;
                }
            } else {
                try {
                    File line1 = new File(MyLoaderOBJ.this.mFile.getParent() + File.separatorChar + materialLibPath);
                    buffer = new BufferedReader(new FileReader(line1));
                } catch (Exception var10) {
                    RajLog.e("[" + this.getClass().getCanonicalName() + "] Could not find file.");
                    var10.printStackTrace();
                    return;
                }
            }

            MaterialDef matDef1 = null;

            try {
                String line2;
                while((line2 = buffer.readLine()) != null) {
                    if(line2.length() != 0 && line2.charAt(0) != 35) {
                        StringTokenizer e = new StringTokenizer(line2, " ");
                        int numTokens = e.countTokens();
                        if(numTokens != 0) {
                            String type = e.nextToken();
                            type = type.replaceAll("\\t", "");
                            type = type.replaceAll(" ", "");
                            if(type.equals("newmtl")) {
                                if(matDef1 != null) {
                                    this.mMaterials.add(matDef1);
                                }

//                                matDef1 = new MaterialDef(MyLoaderOBJ.this);
                                matDef1 = new MaterialDef();
                                matDef1.name = e.hasMoreTokens()?e.nextToken():"";
                                RajLog.d("Parsing material: " + matDef1.name);
                            } else if(type.equals("Kd")) {
                                matDef1.diffuseColor = this.getColorFromParts(e);
                            } else if(type.equals("Ka")) {
                                matDef1.ambientColor = this.getColorFromParts(e);
                            } else if(type.equals("Ks")) {
                                matDef1.specularColor = this.getColorFromParts(e);
                            } else if(type.equals("Ns")) {
                                matDef1.specularCoefficient = Float.parseFloat(e.nextToken());
                            } else if(!type.equals("d") && !type.equals("Tr")) {
                                if(type.equals("map_Ka")) {
                                    matDef1.ambientTexture = e.nextToken();
                                } else if(type.equals("map_Kd")) {
                                    matDef1.diffuseTexture = e.nextToken();
                                } else if(type.equals("map_Ks")) {
                                    matDef1.specularColorTexture = e.nextToken();
                                } else if(type.equals("map_Ns")) {
                                    matDef1.specularHighlightTexture = e.nextToken();
                                } else if(!type.equals("map_d") && !type.equals("map_Tr")) {
                                    if(type.equals("map_Bump")) {
                                        matDef1.bumpTexture = e.nextToken();
                                    }
                                } else {
                                    matDef1.alphaTexture = e.nextToken();
                                }
                            } else {
                                matDef1.alpha = Float.parseFloat(e.nextToken());
                            }
                        }
                    }
                }

                if(matDef1 != null) {
                    this.mMaterials.add(matDef1);
                }

                buffer.close();
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

        public void setMaterial(Object3D object, String materialName) throws ATexture.TextureException {
            if(materialName == null) {
                RajLog.i(object.getName() + " has no material definition.");
            } else {
                MaterialDef matDef = null;

                for(int hasTexture = 0; hasTexture < this.mMaterials.size(); ++hasTexture) {
                    if(((MaterialDef)this.mMaterials.get(hasTexture)).name.equals(materialName)) {
                        matDef = (MaterialDef)this.mMaterials.get(hasTexture);
                        break;
                    }
                }

                boolean var22 = matDef != null && matDef.diffuseTexture != null;
                boolean hasBump = matDef != null && matDef.bumpTexture != null;
                boolean hasSpecularTexture = matDef != null && matDef.specularColorTexture != null;
                boolean hasSpecular = matDef != null && matDef.specularColor > -16777216 && matDef.specularCoefficient > 0.0F;
                Material mat = new Material();
                mat.enableLighting(true);
                mat.setDiffuseMethod(new DiffuseMethod.Lambert());
                int filePath;
                if(matDef != null) {
                    filePath = (int)(matDef.alpha * 255.0F);
                    mat.setColor(filePath << 24 & -16777216 | matDef.diffuseColor & 16777215);
                } else {
                    mat.setColor((int)(Math.random() * 1.6777215E7D));
                }

                if(hasSpecular || hasSpecularTexture) {
                    SpecularMethod.Phong var23 = new SpecularMethod.Phong();
                    var23.setSpecularColor(matDef.specularColor);
                    var23.setShininess(matDef.specularCoefficient);
                }

                String var24;
                if(var22) {
                    if(MyLoaderOBJ.this.mFile == null) {
                        var24 = MyLoaderOBJ.this.getFileNameWithoutExtension(matDef.diffuseTexture);
                        int fis = MyLoaderOBJ.this.mResources.getIdentifier(var24, "drawable", this.mResourcePackage);
                        int e = MyLoaderOBJ.this.mResources.getIdentifier(var24, "raw", this.mResourcePackage);
                        if(e != 0) {
                            mat.addTexture(new Texture(object.getName() + var24, new Etc1Texture(object.getName() + e, e, fis != 0? BitmapFactory.decodeResource(MyLoaderOBJ.this.mResources, fis):null)));
                        } else if(fis != 0) {
                            mat.addTexture(new Texture(object.getName() + var24, fis));
                        }
                    } else {
                        var24 = MyLoaderOBJ.this.mFile.getParent() + File.separatorChar + MyLoaderOBJ.this.getOnlyFileName(matDef.diffuseTexture);
                        if(var24.endsWith(".pkm")) {
                            FileInputStream var25 = null;

                            try {
                                var25 = new FileInputStream(var24);
                                mat.addTexture(new Texture(MyLoaderOBJ.this.getOnlyFileName(matDef.diffuseTexture), new Etc1Texture(MyLoaderOBJ.this.getOnlyFileName(matDef.diffuseTexture) + "etc1", var25, (Bitmap)null)));
                            } catch (FileNotFoundException var20) {
                                RajLog.e("File decode error");
                            } finally {
                                try {
                                    var25.close();
                                } catch (IOException var19) {
                                    ;
                                }

                            }
                        } else {
                            mat.addTexture(new Texture(MyLoaderOBJ.this.getOnlyFileName(matDef.diffuseTexture), BitmapFactory.decodeFile(var24)));
                        }
                    }

                    mat.setColorInfluence(0.0F);
                }

                if(hasBump) {
                    if(MyLoaderOBJ.this.mFile == null) {
                        filePath = MyLoaderOBJ.this.mResources.getIdentifier(MyLoaderOBJ.this.getFileNameWithoutExtension(matDef.bumpTexture), "drawable", this.mResourcePackage);
                        mat.addTexture(new NormalMapTexture(object.getName() + filePath, filePath));
                    } else {
                        var24 = MyLoaderOBJ.this.mFile.getParent() + File.separatorChar + MyLoaderOBJ.this.getOnlyFileName(matDef.bumpTexture);
                        mat.addTexture(new NormalMapTexture(MyLoaderOBJ.this.getOnlyFileName(matDef.bumpTexture), BitmapFactory.decodeFile(var24)));
                    }
                }

                if(hasSpecularTexture) {
                    if(MyLoaderOBJ.this.mFile == null) {
                        filePath = MyLoaderOBJ.this.mResources.getIdentifier(MyLoaderOBJ.this.getFileNameWithoutExtension(matDef.specularColorTexture), "drawable", this.mResourcePackage);
                        mat.addTexture(new SpecularMapTexture(object.getName() + filePath, filePath));
                    } else {
                        var24 = MyLoaderOBJ.this.mFile.getParent() + File.separatorChar + MyLoaderOBJ.this.getOnlyFileName(matDef.specularColorTexture);
                        mat.addTexture(new SpecularMapTexture(MyLoaderOBJ.this.getOnlyFileName(matDef.specularColorTexture), BitmapFactory.decodeFile(var24)));
                    }
                }

                object.setMaterial(mat);
                if(matDef != null && matDef.alpha < 1.0F) {
                    object.setTransparent(true);
                }

            }
        }

        private int getColorFromParts(StringTokenizer parts) {
            int r = (int)(Float.parseFloat(parts.nextToken()) * 255.0F);
            int g = (int)(Float.parseFloat(parts.nextToken()) * 255.0F);
            int b = (int)(Float.parseFloat(parts.nextToken()) * 255.0F);
            return Color.rgb(r, g, b);
        }
    }

    public class ObjIndexData {
        public Object3D targetObj;
        public ArrayList<Integer> vertexIndices;
        public ArrayList<Integer> texCoordIndices;
        public ArrayList<Integer> colorIndices;
        public ArrayList<Integer> normalIndices;
        public String materialName;

        public ObjIndexData(Object3D targetObj) {
            this.targetObj = targetObj;
            this.vertexIndices = new ArrayList();
            this.texCoordIndices = new ArrayList();
            this.colorIndices = new ArrayList();
            this.normalIndices = new ArrayList();
        }
    }
}
