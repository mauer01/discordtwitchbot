package main;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.dv8tion.jda.api.*;

public class Category extends Thread{
    private ConcurrentLinkedQueue<String> Streamer = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<String> ChannelIds = new ConcurrentLinkedQueue<>();
    private String categoryname;
    private MyTwitch twitchapi;
    private final AtomicBoolean isstopped = new AtomicBoolean(false);
    private JDA bot;
    public Category(String categoryname, MyTwitch API,JDA discordapi){

        this.categoryname = categoryname;
        this.twitchapi = API;
        this.bot = discordapi;
        this.start();
    }
    public boolean isstopped(){
        return this.isstopped.get();
    }
    public void addChannel(String channelid){
        if (!channelidexists(channelid).get()){    
            ChannelIds.add(channelid);
            sendstreamer(channelid);
        }
    }

    public void sendstreamer(String channelid){
        if(!Streamer.isEmpty()){    
            StringBuilder message = new StringBuilder("These currently streaming: " + categoryname + ":");
            Streamer.forEach((streamer) -> {
                message.append("\nhttps://www.twitch.tv/").append(streamer);
            });
            bot.getTextChannelById(channelid).sendMessage(message.toString()).queue();
        }
        bot.getTextChannelById(channelid).sendMessage("Nobody is currently Streaming: " + categoryname).queue();
    }

    public void removeChannel(String channelid) {
        boolean removed = ChannelIds.remove(channelid);
        if (removed && ChannelIds.isEmpty()) { 
            isstopped.set(true); 
        }
    }

    public void run(){
        List<String> currentstreamer;
        while(!isstopped()){
            try{
                Thread.sleep(10000);
            } catch(Exception e){
                System.err.println(e);
            }
            currentstreamer = twitchapi.getstreamer(this.categoryname);
            if(currentstreamer.size()>0){
                for (String participant:currentstreamer){
                    addStreamer(participant);
                }
            } 
            if(Streamer.size()>0){
                Streamer.retainAll(currentstreamer);  
            }
        }

    }

    void addStreamer(String x){
        if(!this.Streamer.contains(x)){
            this.Streamer.add(x);
            this.newStreamer(x);
        }
    }
    void stopthread(){
        isstopped.set(true);
    }
    void newStreamer(String x){
        StringBuilder message = new StringBuilder("New streamer is currently streaming " + categoryname + ":");
        message.append("\nhttps://www.twitch.tv/").append(x);
        ChannelIds.forEach((channelid) -> bot.getTextChannelById(channelid).sendMessage(message.toString()).queue());
    }

    void removeStreamer(String x){
        if(this.Streamer.contains(x)){
            this.Streamer.remove(x);
        }
    }

    ConcurrentLinkedQueue<String> getStreamer(){
        return this.Streamer;
    }

    String getcategoryname(){
        return this.categoryname;
    }
    AtomicBoolean channelidexists(String channelid){
        AtomicBoolean bool = new AtomicBoolean(false);
        if (ChannelIds.contains(channelid)){
            bool.set(true);
        }
        return bool;

    }
}
