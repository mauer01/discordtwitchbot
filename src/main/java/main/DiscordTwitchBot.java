package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.reactions.MessageReaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import io.github.cdimascio.dotenv.Dotenv;

public class DiscordTwitchBot {

    public static void main(String[] args) {
        Map<String, String> pingRoles = new ConcurrentHashMap<>();
        Dotenv environment = Dotenv.load();
        final String token = environment.get("DISCORD_TOKEN");
        final String status = environment.get("DISCORD_STATUS");
        final String twitchClientId = environment.get("TWITCH_CLIENT_ID");
        final String twitchClientSecret = environment.get("TWITCH_CLIENT_SECRET");

        Categories currentcategories = new Categories(pingRoles);
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.customStatus(status));
        builder.addEventListeners(new MessageReaction(currentcategories, pingRoles));
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        JDA bot = builder.build();
        MyTwitch twitch = new MyTwitch(twitchClientId, twitchClientSecret);
        try {
            bot.awaitReady();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        currentcategories.setParams(twitch, bot);
        try {
            List<String> lines = Files.readAllLines(Path.of("categories.txt"), StandardCharsets.US_ASCII);
            String[] linesArray = lines.toArray(String[]::new);
            String currentcategory = "";
            for (String line : linesArray) {
                String context = line.split(":")[0];
                String value = line.split(":")[1];
                if (context.equals("category")) {
                    currentcategory = value;
                } else if (context.equals("id")) {
                    currentcategories.addCategory(currentcategory, value);
                    pingRoles.put(value, line.split(":")[2]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Categories.txt wurde noch nicht angelegt, App funktioniert trotzdem");
        } catch (IOException e) {
            System.out.println(e + System.lineSeparator() + "");
        }

    }
}
