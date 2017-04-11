package FapUtils;

import android.content.Context;
import android.provider.Settings;

import com.example.jonat.rajawaliwithfragment.Influence;
import com.example.jonat.rajawaliwithfragment.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by jonat on 2017. 03. 27..
 */

public class FapUtil {

    private Context context;

    public enum Fapus {ENS0, ES0, IRISD0, MNS0, MW0, AU, NA}

    private ArrayList<float[]> fapAxis;
    private List<Float> fapuMap;
    private Map<Integer, List<Influence>> fdps;
    private String version;
    private String stupidname;
    private int fps;
    private int nFaps;

    List<List<Integer>> mask;
    List<List<Integer>> faps;

    public Map<String, Float> FAPU;

    public FapUtil(Context context) {
        this.context = context;

        fapAxis = new ArrayList<>();
        fapuMap = new ArrayList<>();
        fdps = new TreeMap<>();

        mask = new ArrayList<>();
        faps = new ArrayList<>();

        FAPU = new HashMap<>();

        loadFdp();

        init();
    }

    public void loadFdp() {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.fdp_yoda);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nodeList = doc.getElementsByTagName("fapu");
            Node node = nodeList.item(0);
            loadFAPU(node);

            NodeList fdpElements = doc.getElementsByTagName("fdp");

            for (int i = 0; i < fdpElements.getLength(); i++) {
                NodeList childNodes = fdpElements.item(i).getChildNodes();

                List<Integer> indeces = new ArrayList<>();

                String[] splitChildNodes;
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node item = childNodes.item(j);
                    if (item.getNodeName().toString().equals("indices")) {
                        splitChildNodes = item.getFirstChild().getNodeValue().split(" ");

                        for (String s : splitChildNodes) {
                            indeces.add(Integer.parseInt(s));
                        }
                    } else if (item.getNodeName().toString().equals("influence")) {
                        Node index = item.getAttributes().getNamedItem("fap");
//                        int k;
                        if (fdps.get(Integer.parseInt(index.getNodeValue())) == null) {
                            List<Influence> influenceList = new ArrayList<>();
                            fdps.put(Integer.parseInt(index.getNodeValue()), influenceList);
//                            k = 0;
                        }

                        Influence influence = new Influence();
                        influence.setType(item.getAttributes().getNamedItem("type").getNodeValue());
                        influence.setWeight(Float.parseFloat(item.getAttributes().getNamedItem("weight").getNodeValue()));
                        influence.setIndeces(indeces);
                        influence.setFp(Integer.parseInt(fdpElements.item(i).getAttributes().getNamedItem("index").getNodeValue()));
                        influence.setAffects(fdpElements.item(i).getAttributes().getNamedItem("affects").getNodeValue());
                        influence.setName(fdpElements.item(i).getAttributes().getNamedItem("name").getNodeValue());

                        fdps.get(Integer.parseInt(index.getNodeValue())).add(influence);
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        fapAxis.add(new float[]{0f, 0f, 0f});     //1. viseme
        fapAxis.add(new float[]{0f, 0f, 0f});     //2. emotion
        fapAxis.add(new float[]{0f, -1f, 0f});    //3. Open_jaw
        fapAxis.add(new float[]{0f, -1f, 0f});    //4. Lower_t_midlip
        fapAxis.add(new float[]{0f, 1f, 0f});     //5.Raise_b_midlip
        fapAxis.add(new float[]{1f, 0f, 0f});     //6.Stretch_l_cornerlip
        fapAxis.add(new float[]{-1f, 0f, 0f});    //7.Stretch_r_cornerlip
        fapAxis.add(new float[]{0f, -1f, 0f});    //8.Lower_t_lip_lm
        fapAxis.add(new float[]{0f, -1f, 0f});    //9.Lower_t_lip_rm
        fapAxis.add(new float[]{0f, 1f, 0f});     //10.Raise_b_lip_lm
        fapAxis.add(new float[]{0f, 1f, 0f});     //11.Raise_b_lip_rm
        fapAxis.add(new float[]{0f, 1f, 0f});     //12.Raise_l_cornerlip
        fapAxis.add(new float[]{0f, 1f, 0f});     //13.Raise_r_cornerlip
        fapAxis.add(new float[]{0f, 0f, -1f});    //14.Thrust_jaw
        fapAxis.add(new float[]{-1f, 0f, 0f});    //15.Shift_jaw
        fapAxis.add(new float[]{0f, 0f, -1f});    //16.Push_b_lip
        fapAxis.add(new float[]{0f, 0f, -1f});    //17.Push_t_lip
        fapAxis.add(new float[]{0f, 1f, 0f});     //18.Depress_chin
        fapAxis.add(new float[]{0f, -1f, -0.5f});   //19.Close_t_l_eyelid
        fapAxis.add(new float[]{0f, -1f, -0.5f});   //20.Close_t_r_eyelid
        fapAxis.add(new float[]{0f, 1f, 0f});     //21.Close_b_l_eyelid
        fapAxis.add(new float[]{0f, 1f, 0f});     //22.Close_b_r_eyelid
        fapAxis.add(new float[]{1f, 0f, 0f});     //23.Yaw_l_eyeball
        fapAxis.add(new float[]{1f, 0f, 0f});     //24.Yaw_r_eyeball
        fapAxis.add(new float[]{0f, -1f, 0f});    //25.Pitch_l_eyeball
        fapAxis.add(new float[]{0f, -1f, 0f});    //26.Pitch_r_eyeball
        fapAxis.add(new float[]{0f, 0f, -1f});    //27.Thrust_l_eyeball
        fapAxis.add(new float[]{0f, 0f, -1f});    //28.Thrust_r_eyeball
        fapAxis.add(new float[]{1f, 1f, -1f});    //29.Dilate_l_pupil(grow)
        fapAxis.add(new float[]{1f, 1f, -1f});    //30.Dilate_r_pupil(grow)
        fapAxis.add(new float[]{0f, 1f, 0f});     //31.Raise_l_i_eyebrow
        fapAxis.add(new float[]{0f, 1f, 0f});     //32.Raise_r_i_eyebrow
        fapAxis.add(new float[]{0f, 1f, 0f});     //33.Raise_l_m_eyebrow
        fapAxis.add(new float[]{0f, 1f, 0f});     //34.Raise_r_m_eyebrow
        fapAxis.add(new float[]{0f, 1f, 0f});     //35.Raise_l_o_eyebrow
        fapAxis.add(new float[]{0f, 1f, 0f});     //36.Raise_r_o_eyebrow
        fapAxis.add(new float[]{-1f, 0f, 0f});    //37.Squeeze_l_eyebrow
        fapAxis.add(new float[]{1f, 0f, 0f});     //38.Squeeze_r_eyebrow
        fapAxis.add(new float[]{1f, 0f, 0f});     //39.Puff_l_cheek
        fapAxis.add(new float[]{-1f, 0f, 0f});    //40.Puff_r_cheek
        fapAxis.add(new float[]{0f, 1f, 0f});     //41.Lift_l_cheek
        fapAxis.add(new float[]{0f, 1f, 0f});     //42.Lift_r_cheek
        fapAxis.add(new float[]{-1f, 0f, 0f});    //43.Shift_tongue_tip
        fapAxis.add(new float[]{0f, 1f, 0f});     //44.Raise_tongue_tip
        fapAxis.add(new float[]{0f, 0f, -1f});    //45.Thrust_tongue_tip
        fapAxis.add(new float[]{0f, 1f, 0f});     //46.Raise_tongue
        fapAxis.add(new float[]{0f, 1f, -1f});    //47.Tongue_roll
        fapAxis.add(new float[]{0f, -1f, 0f});    //48.Head_pitch
        fapAxis.add(new float[]{1f, 0f, 0f});     //49.Head_yaw
        fapAxis.add(new float[]{-1f, 0f, 0f});    //50.Head_roll
        fapAxis.add(new float[]{0f, -1f, 0f});    //51.Lower_t_midlip_o
        fapAxis.add(new float[]{0f, 1f, 0f});     //52.Raise_b_midlip_o
        fapAxis.add(new float[]{1f, 0f, 0f});     //53.Stretch_l_cornerlip_o
        fapAxis.add(new float[]{-1f, 0f, 0f});    //54.Stretch_r_cornerlip_o
        fapAxis.add(new float[]{0f, -1f, 0f});    //55.Lower_t_lip_lm_o
        fapAxis.add(new float[]{0f, -1f, 0f});    //56.Lower_t_lip_rm_o
        fapAxis.add(new float[]{0f, 1f, 0f});     //57.Raise_b_lip_lm_o
        fapAxis.add(new float[]{0f, 1f, 0f});     //58.Raise_b_lip_rm_o
        fapAxis.add(new float[]{0f, 1f, 0f});     //59.Raise_l_cornerlip_o
        fapAxis.add(new float[]{0f, 1f, 0f});     //60.Raise_r_cornerlip_o
        fapAxis.add(new float[]{1f, 0f, 0f});     //61.Stretch_l_nose
        fapAxis.add(new float[]{-1f, 0f, 0f});    //62.Stretch_r_nose
        fapAxis.add(new float[]{0f, 1f, 0f});     //63.Raise_nose
        fapAxis.add(new float[]{-1f, 0f, 0f});    //64.Bend_nose
        fapAxis.add(new float[]{0f, 1f, 0f});     //65.Raise_l_ear
        fapAxis.add(new float[]{0f, 1f, 0f});     //66.Raise_r_ear
        fapAxis.add(new float[]{1f, 0f, 0f});     //67.Pull_l_ear
        fapAxis.add(new float[]{-1f, 0f, 0f});    //68.Pull_r_ear

        fapuMap.add(FAPU.get(Fapus.NA.name()));     //1.viseme
        fapuMap.add(FAPU.get(Fapus.NA.name()));     //2.emotion
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //3.Open_jaw
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //4.Lower_t_midlip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //5.Raise_b_midlip
        fapuMap.add(FAPU.get(Fapus.MW0.name()));    //6.Stretch_l_cornerlip
        fapuMap.add(FAPU.get(Fapus.MW0.name()));    //7.Stretch_r_cornerlip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //8.Lower_t_lip_lm
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //9.Lower_t_lip_rm
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //10.Raise_b_lip_lm
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //11.Raise_b_lip_rm
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //12.Raise_l_cornerlip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //13.Raise_r_cornerlip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //14.Thrust_jaw
        fapuMap.add(FAPU.get(Fapus.MW0.name()));    //15.Shift_jaw
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //16.Push_b_lip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //17.Push_t_lip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //18.Depress_chin
        fapuMap.add(FAPU.get(Fapus.IRISD0.name())); //19.Close_t_l_eyelid
        fapuMap.add(FAPU.get(Fapus.IRISD0.name())); //20.Close_t_r_eyelid
        fapuMap.add(FAPU.get(Fapus.IRISD0.name())); //21.Close_b_l_eyelid
        fapuMap.add(FAPU.get(Fapus.IRISD0.name())); //22.Close_b_r_eyelid
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //23.Yaw_l_eyeball
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //24.Yaw_r_eyeball
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //25.Pitch_l_eyeball
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //26.Pitch_r_eyeball
        fapuMap.add(FAPU.get(Fapus.ES0.name()));    //27.Thrust_l_eyeball
        fapuMap.add(FAPU.get(Fapus.ES0.name()));    //28.Thrust_r_eyeball
        fapuMap.add(FAPU.get(Fapus.IRISD0.name())); //29.Dilate_l_pupil(grow)
        fapuMap.add(FAPU.get(Fapus.IRISD0.name())); //30.Dilate_r_pupil(grow)
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //31.Raise_l_i_eyebrow
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //32.Raise_r_i_eyebrow
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //33.Raise_l_m_eyebrow
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //34.Raise_r_m_eyebrow
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //35.Raise_l_o_eyebrow
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //36.Raise_r_o_eyebrow
        fapuMap.add(FAPU.get(Fapus.ES0.name()));    //37.Squeeze_l_eyebrow
        fapuMap.add(FAPU.get(Fapus.ES0.name()));    //38.Squeeze_r_eyebrow
        fapuMap.add(FAPU.get(Fapus.ES0.name()));    //39.Puff_l_cheek
        fapuMap.add(FAPU.get(Fapus.ES0.name()));    //40.Puff_r_cheek
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //41.Lift_l_cheek
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //42.Lift_r_cheek
        fapuMap.add(FAPU.get(Fapus.MW0.name()));    //43.Shift_tongue_tip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //44.Raise_tongue_tip
        fapuMap.add(FAPU.get(Fapus.MW0.name()));    //45.Thrust_tongue_tip
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));    //46.Raise_tongue
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //47.Tongue_roll
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //48.Head_pitch
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //49.Head_yaw
        fapuMap.add(FAPU.get(Fapus.AU.name()));     //50.Head_roll
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //51.Lower_t_midlip_o
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //52.Raise_b_midlip_o
        fapuMap.add(FAPU.get(Fapus.MW0.name()));    //53.Stretch_l_cornerlip_o
        fapuMap.add(FAPU.get(Fapus.MW0.name()));    //54.Stretch_r_cornerlip_o
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //55.Lower_t_lip_lm_o
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //56.Lower_t_lip_rm_o
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //57.Raise_b_lip_lm_o
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //58.Raise_b_lip_rm_o
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //59.Raise_l_cornerlip_o
        fapuMap.add(FAPU.get(Fapus.MNS0.name()));   //60.Raise_r_cornerlip_o
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //61.Stretch_l_nose
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //62.Stretch_r_nose
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //63.Raise_nose
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //64.Bend_nose
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //65.Raise_l_ear
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //66.Raise_r_ear
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //67.Pull_l_ear
        fapuMap.add(FAPU.get(Fapus.ENS0.name()));   //68.Pull_r_ear
    }

    public void loadFAPU(Node fapu) {
        FAPU.put(Fapus.ENS0.name(), Float.valueOf(fapu.getAttributes().getNamedItem(Fapus.ENS0.name()).getNodeValue()) / 1024);
        FAPU.put(Fapus.ES0.name(), Float.valueOf(fapu.getAttributes().getNamedItem(Fapus.ES0.name()).getNodeValue()) / 1024);
        FAPU.put(Fapus.IRISD0.name(), Float.valueOf(fapu.getAttributes().getNamedItem(Fapus.IRISD0.name()).getNodeValue()) / 1024);
        FAPU.put(Fapus.MNS0.name(), Float.valueOf(fapu.getAttributes().getNamedItem(Fapus.MNS0.name()).getNodeValue()) / 1024);
        FAPU.put(Fapus.MW0.name(), Float.valueOf(fapu.getAttributes().getNamedItem(Fapus.MW0.name()).getNodeValue()) / 1024);
        FAPU.put(Fapus.AU.name(), 0.00001f);
        FAPU.put(Fapus.NA.name(), 0f);
    }

    public void loadFaps() {
        InputStream in = context.getResources().openRawResource(R.raw.newfap);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line;
        boolean init = true;
        boolean isMask = true;
        int j = 0;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#"))
                    continue;
                else if (init) {
                    String[] sArray = line.split(" ");
                    version = sArray[0];
                    stupidname = sArray[1];
                    fps = Integer.parseInt(sArray[2]);
                    nFaps = Integer.parseInt(sArray[3]);
                    init = false;
                } else if (isMask) {
                    if (line.split(" ").length == 68) {
                        List<Integer> temp = new ArrayList<>();
                        String[] sArray = line.split(" ");
                        for (String s : sArray) {
                            temp.add(Integer.parseInt(s));
                        }
                        mask.add(temp);
                    }
                    isMask = false;
                } else {
                    ArrayList<String> sArray = new ArrayList<>();
                    sArray.addAll(Arrays.asList(line.split(" ")));
                    int frameIndex = Integer.parseInt(sArray.get(0));
                    sArray.remove(0);

                    List<Integer> temp = new ArrayList<>();
                    for (String s : sArray) {
                        temp.add(Integer.parseInt(s));
                    }
                    faps.add(temp);
                    isMask = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<float[]> getFapAxis() {
        return fapAxis;
    }

    public void setFapAxis(ArrayList<float[]> fapAxis) {
        this.fapAxis = fapAxis;
    }

    public List getFapuMap() {
        return fapuMap;
    }

    public void setFapuMap(List fapuMap) {
        this.fapuMap = fapuMap;
    }

    public Map<Integer, List<Influence>> getFdps() {
        return fdps;
    }

    public void setFdps(Map<Integer, List<Influence>> fdps) {
        this.fdps = fdps;
    }

    public Map<String, Float> getFAPU() {
        return FAPU;
    }

    public void setFAPU(Map<String, Float> FAPU) {
        this.FAPU = FAPU;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStupidname() {
        return stupidname;
    }

    public void setStupidname(String stupidname) {
        this.stupidname = stupidname;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getnFaps() {
        return nFaps;
    }

    public void setnFaps(int nFaps) {
        this.nFaps = nFaps;
    }

    public List<List<Integer>> getMask() {
        return mask;
    }

    public void setMask(List<List<Integer>> mask) {
        this.mask = mask;
    }

    public List<List<Integer>> getFaps() {
        return faps;
    }

    public void setFaps(List<List<Integer>> faps) {
        this.faps = faps;
    }
}
