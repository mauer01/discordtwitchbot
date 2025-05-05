package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;
import main.reactions.MessageReaction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordTwitchBot {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        final String token = dotenv.get("DISCORD_TOKEN");
        final String status = dotenv.get("DISCORD_STATUS");
        final String twitchClientId = dotenv.get("TWITCH_CLIENT_ID");
        final String twitchClientSecret = dotenv.get("TWITCH_CLIENT_SECRET");

        Categories currentcategories = new Categories();
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing(status));
        builder.addEventListeners(new MessageReaction(currentcategories));
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        JDA bot = builder.build();
        MyTwitch twitch = new MyTwitch(twitchClientId, twitchClientSecret);
        try {
            bot.awaitReady();
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        currentcategories.setparams(twitch, bot);
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
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Categories.txt wurde noch nicht angelegt, App funktioniert trotzdem");
        } catch (IOException e) {
            System.out.println(e + System.lineSeparator() + "");
        }
    }
}
