package Visemes;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import FapUtils.FapUtil;

/**
 * Created by jonat on 2017. 04. 23..
 */

public class Visemes {

    private Context context;
    private FapUtil fapUtil;
    private String text;
    private ArrayList<ArrayList<Phoneme>> syllables;
    private ArrayList<Integer> duration;
    private List<Integer> maskList;
    private List<Integer> fapList;

    private String aah = " A: AI A { al aU Q ";
    private String B_M_P = " b m m= p ";
    private String bigaah = " @U ";
    private String ch_J_sh = " dZ S tS Z ";
    private String D_S_T = " d D l l= 5 s t T z ";
    private String N = " n n= N ";
    private String ee = " eI ";
    private String eh = " E @  E@ 3: V EI ";
    private String F_V = " f v ";
    private String i = " j I i: I@ i ";
    private String K = " g h k ";
    private String oh = " O: OI O ";
    private String ooh_Q = " U u: U@ u ";
    private String R = " r\\ r= r ";
    private String W = " w ";

    public Visemes(Context context, FapUtil fapUtil, String text) {
        this.fapUtil = fapUtil;
        this.context = context;
        this.text = text;

        syllables = new ArrayList<>();
        duration = new ArrayList<>();
        maskList = new ArrayList();
        fapList = new ArrayList();

        getPhonemes(text);
    }

    public void getPhonemes(String text) {

        File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "Mary");
        File f = new File(cacheDir, "proba.txt");

        try {
            FileInputStream inputStream = new FileInputStream(f);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList syllablesNode = doc.getElementsByTagName("syllable");
            for (int i = 0; i < syllablesNode.getLength(); i++) {
                Node phs = syllablesNode.item(i);
                ArrayList<Phoneme> phonemes = new ArrayList();

                for (int j = 0; j < phs.getChildNodes().getLength(); j++) {
                    if (phs.getChildNodes().item(j).getNodeName().equals("ph")) {
                        Phoneme phoneme = new Phoneme();
                        Node node = phs.getChildNodes().item(j);
                        phoneme.setP(node.getAttributes().getNamedItem("p").getNodeValue());
                        if (node.getAttributes().getNamedItem("f0") != null)
                            phoneme.setFreq(node.getAttributes().getNamedItem("f0").getNodeName());
                        phoneme.setDuration(Integer.valueOf(node.getAttributes().getNamedItem("d").getNodeValue()));
                        phoneme.setEnd(Float.valueOf(node.getAttributes().getNamedItem("end").getNodeValue()));
                        phonemes.add(phoneme);

                        duration.add(phoneme.getDuration());
                    }
                }
                syllables.add(phonemes);
            }
            fapUtil.setnFaps(0);
            fapUtil.setFps(25);
            fapUtil.setVersion("2.1");
            fapUtil.setStupidname("ownFap");
            for (int i = 0; i < syllables.size(); i++) {
                for (int j = 0; j < syllables.get(i).size(); j++) {
                    getViseme(syllables.get(i).get(j).getP());
                    List<List<Integer>> tempFaps = fapUtil.getFaps();
                    tempFaps.add(fapList.subList(1, fapList.size()));
                    fapUtil.setFaps(tempFaps);
                    List<List<Integer>> m = fapUtil.getMask();
                    m.add(maskList);
                    fapUtil.setMask(m);
                    fapUtil.setnFaps(fapUtil.getnFaps() + 1);
                }
            }
            // hozzá rakjuk a relaxált állapotot
            getViseme("relax");
            List<List<Integer>> tempFaps = fapUtil.getFaps();
            tempFaps.add(fapList.subList(1, fapList.size()));
            fapUtil.setFaps(tempFaps);
            List<List<Integer>> m = fapUtil.getMask();
            m.add(maskList);
            fapUtil.setMask(m);
            fapUtil.setnFaps(fapUtil.getnFaps() + 1);
//            fill();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fill() {
        FapUtil retFAPs = new FapUtil();
    }

    public void getViseme(String p) {
        if (aah.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 420, -72, -449, -156, -152, -623, -404, -56, -54, -5, -23, 2, -6, -27, -26, -49, 10, 3, -210, -56, -56);
        } else if (B_M_P.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 2, 166, 150, 206, 2, 174, 1, 50, 38, 487, -53);
        } else if (bigaah.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 805, -76, -800, -55, -55, -814, -689, -271, -276, -217, 733, 290, -172, -107, 4, -86, 34, -595, -140, -160, -565, -1535, -56, -101);
        } else if (ch_J_sh.contains(p)) {
            maskList = Arrays.asList(0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, -163, -318, -89, -146, -229, -60, -246, -139, 8, 53, -179, -142, -227, -212, 36, 61, -196, -290, -255, -293, -28, -46, -48, -3, -1);
        } else if (D_S_T.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, -32, -85, -177, 126, 137, -15, -17, -167, -166, -47, -44, -52, 19, 72, 72, 19, 21, -91, 114, 122, -8, -24, -145, -47, -47);
        } else if (ee.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 123, -226, -285, 68, 69, -400, -360, -361, -283, 96, 94, 133, -175, 4, 93, 94, 80, 79, -214, 10, 49, 28, -350, -367, -194, 197, 242);
        } else if (eh.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 418, -78, -315, 63, -20, 33, 10, -764, -579, -406, -232, 1547, 4, 41, 6, -65, -64, -82, 57, 69, 46, 13, -171, -200, -179);
        } else if (F_V.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, -38, -270, -59, 70, -379, -401, 119, 55, -48, -44, -938, -22, 3, -117, -36, -33, -50, -74, -47, -47);
        } else if (i.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 252, -198, -284, -17, -6, -411, -431, -285, -281, 1, 12, -9, 85, 47, 34, 53, 45, -69, 1, 4, -36, -48, -82, -4, -5);
        } else if (K.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, -108, -198, -284, 233, 135, -557, -446, -391, -255, 199, 172, 68, -51, 1, 3, 74, 72, 75, 76, -117, 111, 120, -73, -83, -198, 165, 255);
        } else if (N.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 62, -20, -409, -81, -75, 22, 13, -524, -456, -74, -54, 12, -99, -140, -2, -23, -40, -144, -114, -5, -16, -205, -34, -45);
        } else if (oh.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 373, 57, -441, -179, -175, 362, 319, -387, -309, -282, -104, -32, -29, -218, -159, -14, 26, -358, -316, 155, 156, -151, -97, -106);
        } else if (ooh_Q.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 6, 94, -190, -22, -96, 7, -3, -312, -109, 107, 49, 507, 103, -21, -50, -199, -182, 67, 100, 311, 102, 98);
        } else if (R.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, -38, -44, -172, -349, -238, -24, -28, -46, -41, 4, 34, -86, -90, -134, -64, -151, -146, -11, -1, -30, 69, 54);
        } else if (W.contains(p)) {
            maskList = Arrays.asList(0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 6, 116, -46, -94, -129, 12, 11, -48, -37, 3, 20, 868, 287, -40, -11, -5, -60, -198, -172, -2, -2, -28, -28, 5);
        } else if ("relax".equals(p)) {
            maskList = Arrays.asList(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            fapList = Arrays.asList(0, 0);
        }
    }
}
