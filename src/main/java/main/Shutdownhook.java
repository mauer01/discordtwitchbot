package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Shutdownhook extends Thread {

    ;
    private final List<Category> categorylist;

    public Shutdownhook(List<Category> categorylist) {
        this.categorylist = categorylist;

    }

    @Override
    public void run() {
        try {
            try (FileWriter categoriestext = new FileWriter("categories.txt")) {
                for (Category category : categorylist) {
                    categoriestext.write("category:" + category.getcategoryname() + "\n");
                    for (String channelid : category.getChannelIds()) {
                        categoriestext.write("id:" + channelid + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }

}
