package main;

import java.util.Date;

public class Streamer {
    private final String name;
    private Date lastseen;

    public Streamer(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    public Date getLastseen(){
        return this.lastseen;
    }
    public void setLastseen(Date lastseen){
        this.lastseen = lastseen;
    }

}
