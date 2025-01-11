package main.reactions;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import io.github.cdimascio.dotenv.Dotenv;
import main.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageReaction extends ListenerAdapter {
    Dotenv dotenv = Dotenv.load();
    private final String discord_bot_id = dotenv.get("DISCORD_BOT_ID");
    private Categories currentcategories;
    public MessageReaction(Categories current){
        this.currentcategories = current;

    }
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) return;
        String messagecontent = event.getMessage().getContentRaw();
        if (event.isFromGuild()) {
            if(messagecontent.equals("!ping")) {
                event.getChannel().sendMessage(event.getChannel().getId()).queue();
            }else if (messagecontent.contains("!getlist")) {
                event.getChannel().sendMessage(currentcategories.toString(event.getChannel().getId())).queue();
            }else if(messagecontent.contains("!add")){
                this.currentcategories.addCategory(event.getMessage().getContentRaw().substring(5), event.getChannel().getId());
            }else if(messagecontent.contains("!remove")){
                this.currentcategories.removeCategory(event.getMessage().getContentRaw().substring(8), event.getChannel().getId());
            }else if(messagecontent.contains("!exit")){
                System.exit(0);
            }else if(messagecontent.contains("!")){
                event.getChannel().sendMessage("no idea what you want from me").queue();
            }
        }
    }
}

