package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

import main.exceptions.NotFound;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Category {
    private final ConcurrentLinkedQueue<String> ChannelIds = new ConcurrentLinkedQueue<>();
    private final String categoryname;
    private final MyTwitch twitchapi;
    private final JDA bot;
    private final Redis redis;
    private ScheduledFuture<?> future;

    public Category(String categoryname, MyTwitch API, JDA discordapi) {
        this.redis = new Redis(categoryname);
        this.categoryname = categoryname;
        this.twitchapi = API;
        this.bot = discordapi;
    }

    public void addFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    public void addChannel(String channelid) {
        if (!channelidexists(channelid)) {
            ChannelIds.add(channelid);
            sendstreamer(channelid);
        }
    }

    private List<String> getoldstreamer() throws NotFound {
        Set<String> getremaining = redis.getremaining();
        if (getremaining.isEmpty()) {
            throw new NotFound("no stuff");
        }
        return getremaining.stream()
                .map(key -> key.replace("discordtwitch:5dchess:", "")).toList();
    }

    public void sendstreamer(String channelid) {
        TextChannel channel = bot.getTextChannelById(channelid);
        if (channel == null || ChannelIds.size() < 1)
            return;
        try {
            List<String> currentstreamer = getoldstreamer();
            StringBuilder message = new StringBuilder("currently streaming: " + categoryname + ":");
            for (String streamer : currentstreamer) {
                message.append("\nhttps://www.twitch.tv/").append(streamer);
            }
            channel.sendMessage(message.toString()).queue();
        } catch (NotFound _e) {
            channel.sendMessage("Nobody is currently Streaming: " + categoryname).queue();
        }

    }

    public void removeChannel(String channelid) throws NotFound {
        ChannelIds.remove(channelid);
        if (ChannelIds.isEmpty()) {
            future.cancel(false);
            throw new NotFound("closing");
        }
    }

    public void task() {
        List<String> currentstreamer = twitchapi.getstreamer(this.categoryname);
        try {
            List<String> oldstreamer = getoldstreamer();
            for (String current : currentstreamer) {
                if (!oldstreamer.contains(current)) {
                    addStreamer(current, true);
                } else {
                    addStreamer(current, false);
                }
            }
        } catch (NotFound _e) {
            if (!currentstreamer.isEmpty()) {
                for (String participant : currentstreamer) {
                    addStreamer(participant, true);
                }
            }
        }
    }

    public List<String> getChannelIds() {
        return new ArrayList<>(ChannelIds);
    }

    void addStreamer(String x, boolean announcement) {
        redis.add(x, 10);
        if (announcement)
            newStreamer(x);
    }

    void newStreamer(String x) {
        StringBuilder message = new StringBuilder("New streamer is currently streaming " + categoryname + ":");
        message.append("\nhttps://www.twitch.tv/").append(x);
        ChannelIds.forEach((channelid) -> {
            TextChannel channel = bot.getTextChannelById(channelid);
            if (channel != null) {
                channel.sendMessage(message.toString()).queue();
            }
        });
    }

    String getcategoryname() {
        return this.categoryname;
    }

    boolean channelidexists(String channelid) {
        return ChannelIds.contains(channelid);
    }
}
