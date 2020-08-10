package com.convexcreative.propres.event;

public abstract class ProPresEvent implements Runnable{

    private Object[] metadata;

    public Object[] getEventMetadata(){
        return metadata;
    }

    public void setEventMetadata(Object[] metadata){
        this.metadata = metadata;
    }

}
