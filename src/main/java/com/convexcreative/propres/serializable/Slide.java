package com.convexcreative.propres.serializable;

import com.convexcreative.propres.SlideType;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Slide {

    private transient Slide slideNote;

    @SerializedName("acn")
    private String slideType;

    @SerializedName("uid")
    private String slideId;

    @SerializedName("txt")
    private String slideText;


    public SlideType getSlideType(){
        switch (slideType){
            case "cs":
            case "ns":
                return SlideType.SLIDE;
            case "csn":
            case "nsn":
                return SlideType.NOTE;
            default:
                return SlideType.UNKNOWN;
        }
    }

    public UUID getSlideId(){
        try{
            UUID uuid = UUID.fromString(slideId);
            return UUID.fromString(slideId);
        } catch (IllegalArgumentException exception){
            return UUID.fromString("00000000-0000-0000-0000-000000000000");

        }

    }

    public String getSlideText(){
        return slideText.replace("\n"," ");
    }

    public String[] getSplitSlideText(){
        return slideText.split("\n");
    }

    public Slide getSlideNote(){
        return slideNote;
    }

    public void setSlideNote(Slide slideNote) {
        this.slideNote = slideNote;
    }
}
