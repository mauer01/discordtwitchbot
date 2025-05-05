package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Category extends Thread {

    private final Map<String, Integer> Streamer = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> ChannelIds = new ConcurrentLinkedQueue<>();
    private final String categoryname;
    private final MyTwitch twitchapi;
    private final AtomicBoolean isstopped = new AtomicBoolean(false);
    private final JDA bot;
    private List<String> currentstreamer = null;

    public Category(String categoryname, MyTwitch API, JDA discordapi) {

        this.categoryname = categoryname;
        this.twitchapi = API;
        this.bot = discordapi;

    }

    public boolean isstopped() {
        return this.isstopped.get();
    }

    public void addChannel(String channelid) {
        if (!channelidexists(channelid)) {
            ChannelIds.add(channelid);
            sendstreamer(channelid);
        }
    }

    public void sendstreamer(String channelid) {
        TextChannel channel = bot.getTextChannelById(channelid);
        if (channel == null) {
            return;
        }
        if (!Streamer.isEmpty()) {
            StringBuilder message = new StringBuilder("currently streaming: " + categoryname + ":");
            Streamer.forEach((streamer, i) -> {
                message.append("\nhttps://www.twitch.tv/").append(streamer);
            });
            channel.sendMessage(message.toString()).queue();
        } else if ((ChannelIds.size() > 1 && Streamer.isEmpty())) {
            channel.sendMessage("Nobody is currently Streaming: " + categoryname).queue();
        }
    }

    public void removeChannel(String channelid) {
        boolean removed = ChannelIds.remove(channelid);
        if (removed && ChannelIds.isEmpty()) {
            isstopped.set(true);
        }
    }

    @Override
    public void run() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleAtFixedRate(() -> task(), 0, 10, TimeUnit.SECONDS); // Initial verzögern und dann jede Sekunde ausführen
    }

    private void task() {
        List<String> keystoremove = new ArrayList<>();
        Streamer.forEach((e, i) -> {
            if (Streamer.get(e) > 5) {
                keystoremove.add(e);
            }
            Streamer.replace(e, i + 1);
        });
        keystoremove.forEach(Streamer::remove);
        currentstreamer = twitchapi.getstreamer(this.categoryname);
        if (!currentstreamer.isEmpty()) {
            for (String participant : currentstreamer) {
                addStreamer(participant);
            }
        }
    }

    public List<String> getChannelIds() {
        return new ArrayList<>(ChannelIds);
    }

    void addStreamer(String x) {
        if (!this.Streamer.containsKey(x)) {
            this.Streamer.put(x, 0);
            this.newStreamer(x);
        } else {
            this.Streamer.put(x, 0);
        }
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
