package main;

import java.io.FileWriter;
import java.util.List;

public class Shutdownhook extends Thread {

    ;
    private List<Category> categorylist;

    public Shutdownhook(List<Category> categorylist) {
        this.categorylist = categorylist;

    }

    public void run() {
        try {
            FileWriter categoriestext = new FileWriter("categories.txt");
            for (Category category : categorylist) {
                categoriestext.write("category:" + category.getcategoryname() + "\n");
                for (String channelid : category.getChannelIds()) {
                    categoriestext.write("id:" + channelid + "\n");
                }
            }

            categoriestext.close();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
