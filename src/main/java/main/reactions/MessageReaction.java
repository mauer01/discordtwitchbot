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
    private final String commandidentifier = "!";
    public MessageReaction(Categories current){
        this.currentcategories = current;

    }
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) return;
        String messagecontent = event.getMessage().getContentRaw();
        if (event.isFromGuild()) {
            if(messagecontent.equals(commandidentifier + "ping")) {
                event.getChannel().sendMessage(event.getChannel().getId()).queue();
            }else if (messagecontent.contains(commandidentifier + "getlist")) {
                event.getChannel().sendMessage(currentcategories.toString(event.getChannel().getId())).queue();
            }else if(messagecontent.contains(commandidentifier + "add")){
                this.currentcategories.addCategory(event.getMessage().getContentRaw().substring(5), event.getChannel().getId());
            }else if(messagecontent.contains(commandidentifier + "remove")){
                this.currentcategories.removeCategory(event.getMessage().getContentRaw().substring(8), event.getChannel().getId());
            }            }
        }
    }
}

