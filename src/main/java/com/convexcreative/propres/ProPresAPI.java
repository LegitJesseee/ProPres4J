package com.convexcreative.propres;

import com.convexcreative.ezlogger.ConvexLogger;
import com.convexcreative.propres.event.ProPresEvent;
import com.convexcreative.propres.event.server.ServerStatusChangeEvent;
import com.convexcreative.propres.socket.SDWebSocketClientV6;
import com.convexcreative.propres.socket.SDWebSocketClientV7;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.util.*;

public class ProPresAPI {

    private static ProPresAPI instance;

    private static final boolean LOG = true;
    private static final Random RANDOM = new Random();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private WebSocketClient curClient;
    private HashMap<Integer,ProPresEvent> tasks = new HashMap<>();
    private ArrayList<Integer> queuedForDeletion = new ArrayList<>();

    private ProPresAPI initialize(ProPresAPIConfig config){

        final String uri = "ws://" + config.getHost() + "/stagedisplay";

        log("Initializing API...");

        if(tasks == null){
            tasks = new HashMap<>();
        }

        if(queuedForDeletion == null){
            queuedForDeletion = new ArrayList<>();
        }

        triggerEvent(ServerStatusChangeEvent.class, ServerStatusChangeEvent.CONNECTING);

        log("Initialization complete!");

        return this;
    }

    public WebSocketClient buildSocket(ProPresAPIConfig con){

        WebSocketClient result;

        final String uri = "ws://" + con.getHost() + "/stagedisplay";

        switch(con.getApiVersion()){
            case 7:
                result = new SDWebSocketClientV7(URI.create(uri), this, con.getPassword());
                break;
            case 6:
            default:
                result = new SDWebSocketClientV6(URI.create(uri), this, con.getPassword());
        }

        return result;

    }

    public Thread threaderBuilder(){
        return new Thread(() -> {
            curClient.connect();
            curClient.setConnectionLostTimeout(0);
        });
    }

    public void openConnection(ProPresAPIConfig config){
        curClient = buildSocket(config);
        threaderBuilder().start();
    }

    public void closeConnection(){
        if(curClient != null){
            curClient.close();
        }
    }

    private void reinitialize(ProPresAPIConfig config){
        if(instance == null){
            initialize(new ProPresAPIConfig());
            return;
        }
        closeConnection();
        initialize(config);
        openConnection(config);
    }


    public static ProPresAPI getInstance(ProPresAPIConfig... config){
        if(instance == null){
            if(config.length < 1){
                instance = new ProPresAPI().initialize(new ProPresAPIConfig());
            }else{
                instance = new ProPresAPI().initialize(config[0]);
            }
        }
        return instance;
    }


    public int registerRecurringEvent(ProPresEvent event){
        final int key = getNewKey();
        tasks.put(key,event);
        return key;
    }

    public int registerEvent(ProPresEvent event){
        final int key = getNewKey() + 100000;
        tasks.put(key,event);
        return key;
    }

    public boolean unregisterEvent(int key){
        if(!tasks.containsKey(key)){
            return false;
        }
        queuedForDeletion.add(key);
        return true;
    }

    public void clearRegisteredEvents(){
        tasks.clear();
    }

    public Set<Integer> getRegisteredEvents(){
        return tasks.keySet();
    }

    public void triggerEvent(Class clazz, Object... eventMetadata) {

        queuedForDeletion.forEach((key) -> tasks.remove(key));
        queuedForDeletion.clear();

        Class superestClass = furthestSuper(clazz);

        if (!(superestClass.isInstance(ProPresEvent.class))) {
            ProPresAPI.log("Event triggered, but it does not implement ProPresEvent...");
            return;
        }

        final HashMap<Integer, ProPresEvent> curTasks = tasks;

        tasks.values().stream().filter(clazz::isInstance).forEach((e) -> {
            ProPresEvent pE = (ProPresEvent) e;
            pE.setEventMetadata(eventMetadata);
            pE.run();

            int key = getKey(curTasks,e).intValue();
            if(key >= 100000){
              queuedForDeletion.add(key);
            }
        });
    }

    public static void log(String msg){
        ConvexLogger.log("ProPresAPI", msg);
    }

    private static Class furthestSuper(Class clazz){

        if(clazz.getSuperclass() == null){
            return clazz;
        }

        Class curClass = clazz;

        while(curClass.getSuperclass() != null){
            curClass = curClass.getSuperclass();
        }

        return curClass;
    }

    public int getNewKey(){
        int key;
        do{
            key = RANDOM.nextInt(10000);
        }while(tasks.containsKey(key));
        return key;
    }

    private static  <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
