package Visemes;

/**
 * Created by jonat on 2017. 04. 23..
 */

public class Phoneme {
    private String p;
    private String freq;
    private Integer duration;
    private Float end;

    public Phoneme() {
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Float getEnd() {
        return end;
    }

    public void setEnd(Float end) {
        this.end = end;
    }
}
