package com.convexcreative.propres.serializable;

import com.google.gson.annotations.SerializedName;

public class StageDisplayData {

    @SerializedName("acn")
    private String acn;

    @SerializedName("ary")
    private Slide[] slides;

    public String getAcn() {
        return acn;
    }

    public Slide getCurrentSlide(){
        slides[0].setSlideNote(slides[2]);
        return slides[0];
    }

    public Slide getNextSlide(){
        slides[1].setSlideNote(slides[3]);
        return slides[1];
    }
}
