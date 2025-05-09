package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import main.exceptions.NotFound;
import net.dv8tion.jda.api.JDA;

public class Categories {

    private final List<Category> categorylist = new ArrayList<>();
    private MyTwitch twitch;
    private JDA bot;
    ScheduledThreadPoolExecutor executor;

    public Categories() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdownhook();
        }, "Shutdown-thread"));
        executor = new ScheduledThreadPoolExecutor(1);
    }

    public void shutdownhook() {
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

    public void setparams(MyTwitch x, JDA y) {
        this.twitch = x;
        this.bot = y;
    }

    public void addCategory(String x, String channelid) {
        try {
            Category y = getcategorybyname(x);
            y.addChannel(channelid);
        } catch (NotFound e) {
            executor.setCorePoolSize(executor.getCorePoolSize() + 1);
            Category temp = new Category(x, twitch, bot);
            temp.addChannel(channelid);
            this.categorylist.add(temp);
            ScheduledFuture<?> taskHandle = executor.scheduleAtFixedRate(() -> temp.task(), 0, 10, TimeUnit.SECONDS);
            temp.addFuture(taskHandle);
        }
    }

    public List<Category> getCategories() {
        return this.categorylist;
    }

    public void removeCategory(String name, String channel) {
        Category x;
        try {
            x = getcategorybyname(name);
            try {
                x.removeChannel(channel);
            } catch (NotFound e) {
                categorylist.remove(x);
            }
        } catch (NotFound e) {
            System.out.println("\"" + name + "\" " + e.getMessage());
        }
    }

    public Category getcategorybyname(String x) throws NotFound {
        for (Category category : categorylist) {
            if (category.getcategoryname().equals(x)) {
                return category;
            }
        }
        throw new NotFound("category not in list");
    }

    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        if (!categorylist.isEmpty()) {
            for (Category category : categorylist) {
                message.append(category.getcategoryname());
                message.append(System.getProperty("line.separator"));
            }
        } else {
            message.append("there are no categories set yet.");
        }
        return message.toString();
    }

    public String toString(String channelId) {
        StringBuilder message = new StringBuilder();
        for (Category category : categorylist) {
            if (category.channelidexists(channelId)) {
                message.append(category.getcategoryname());
                message.append(System.lineSeparator());
            }
        }
        if (message.toString().equals("")) {
            message.append("There are no categories set for this channel!");
        }
        return message.toString();
    }
}
