package main;

import java.util.*;

import main.exceptions.NotFound;
import net.dv8tion.jda.api.JDA;

public class Categories{
    private List<Category> categorylist = new ArrayList<>();
    private MyTwitch twitch;
    private JDA bot;
    public Categories(){
    }
    public void setparams(MyTwitch x, JDA y){
        this.twitch = x;
        this.bot = y;
    }
    
    public void addCategory(String x,String channelid){
        try {
            Category y = getcategorybyname(x);
            y.addChannel(channelid);
        } catch(NotFound e) {
            Category temp = new Category(x,twitch,bot);
            temp.addChannel(channelid);
            this.categorylist.add(temp);
        }
    }
    public List<Category> getCategories(){
        return this.categorylist;
    }
    public void removeCategory(String name, String channel){
        Category x;
        try {
            x = getcategorybyname(name);
            x.removeChannel(channel);
            
            if(x.isstopped()){
                categorylist.remove(x);
            }
        } catch (NotFound e) {
            System.out.println("\""+ name + "\" "  + e.getMessage());
        }
    }

    public Category getcategorybyname(String x) throws NotFound{
        for (Category category : categorylist) {
            if (category.getcategoryname().equals(x)){
                return category;
            }
        }
        throw new NotFound("category not in list");
    }

    public String toString(){
        StringBuilder message = new StringBuilder();
        if (!categorylist.isEmpty()){
            for (Category category : categorylist) {
                message.append(category.getcategoryname());
                message.append(System.getProperty("line.separator"));
            }
        }else{
            message.append("there are no categories set yet.");
        }
        return message.toString();
    }
    public String toString(String channelId){
        StringBuilder message = new StringBuilder();
        if (!categorylist.isEmpty()){
            for (Category category : categorylist) {
                if (category.channelidexists(channelId).get()){
                    message.append(category.getcategoryname());
                    message.append(System.getProperty("line.separator"));
                }else{
                    message.append("there are no categories set for this channel.");
                }
            }
        }else{
            message.append("there are no categories set for this channel.");
        }
        return message.toString();
    }
}