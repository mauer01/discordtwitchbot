package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.exceptions.NotFound;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Categories {

    private static final Logger log = LoggerFactory.getLogger(Categories.class);
    private final List<Category> categorylist = new ArrayList<>();
    private MyTwitch twitch;
    private JDA bot;
    private final Map<String, String> pingRole;

    public Categories(Map<String, String> pingRoles) {
        this.pingRole = pingRoles;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownHook, "Shutdown-thread"));
    }

    public void shutdownHook() {
        try {
            try (FileWriter categoriesText = new FileWriter("categories.txt")) {
                for (Category category : categorylist) {
                    categoriesText.write("category:" + category.getcategoryname() + "\n");
                    for (String channelId : category.getChannelIds()) {
                        categoriesText.write("id:" + channelId + ":" + pingRole.get(channelId) + "\n");
                    }
                }
            }
        } catch (IOException e) {
            log.error("e: ", e);
        }
    }

    public void setParams(MyTwitch x, JDA y) {
        this.twitch = x;
        this.bot = y;
    }

    public void addCategory(String x, String channelId) {
        try {
            Category y = findCategoryByName(x);
            y.addChannel(channelId);
        } catch (NotFound e) {
            Category temp = new Category(x, twitch, bot, pingRole);
            temp.addChannel(channelId);
            this.categorylist.add(temp);
            temp.start();
        }
    }


    public void removeCategory(String name, String channel) {
        Category x;
        try {
            x = findCategoryByName(name);
            x.removeChannel(channel);

            if (x.isstopped()) {
                categorylist.remove(x);
            }
        } catch (NotFound e) {
            System.out.println("\"" + name + "\" " + e.getMessage());
        }
    }

    public Category findCategoryByName(String x) throws NotFound {
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
                message.append(System.lineSeparator());
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
        if (message.toString().isEmpty()) {
            message.append("There are no categories set for this channel!");
        }
        return message.toString();
    }
}
