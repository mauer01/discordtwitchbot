package main.reactions;

import main.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReaction extends ListenerAdapter {
    private final Categories currentcategories;
    private final String commandidentifier = "!";
    public MessageReaction(Categories current){
        this.currentcategories = current;

    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMember() == null) return;
        if (event.getAuthor().isBot()) return;
        
        if (event.getMember() == null || !event.getMember().hasPermission(Permission.MANAGE_SERVER)) return;
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
            }
        }
    }
}

