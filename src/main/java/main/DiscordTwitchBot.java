package main;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import main.reactions.NachrichtenReaktion;

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
        builder.addEventListeners(new NachrichtenReaktion(currentcategories));
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        JDA bot = builder.build();
        MyTwitch twitch = new MyTwitch(twitchClientId, twitchClientSecret);
        try {
            bot.awaitReady();
        } catch (Exception e) {
            System.err.println(e);
        }
        currentcategories.setparams(twitch, bot);
    }
}
