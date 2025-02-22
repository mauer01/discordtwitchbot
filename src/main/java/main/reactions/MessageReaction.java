package main.reactions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import main.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReaction extends ListenerAdapter {
    private final Categories currentcategories;
    private final String commandidentifier = "!";
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    public MessageReaction(Categories current) {
        this.currentcategories = current;

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String userId = event.getAuthor().getId();
        long currentTime = System.currentTimeMillis();

        if (event.getMember() == null || !event.isFromGuild())
            return;
        if (event.getAuthor().isBot())
            return;
        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER))
            return;
        if (!event.getMessage().getContentRaw().startsWith(commandidentifier))
            return;
        String messagecontent = event.getMessage().getContentRaw();
        if (cooldowns.containsKey(userId) && (currentTime - cooldowns.get(userId)) < 5000) {
            event.getChannel().sendMessage("Slow down! Wait a few seconds before using commands again.").queue();
            return;
        }
        cooldowns.put(userId, currentTime); // Add user to cooldown list
        if (event.isFromGuild()) {
            if (messagecontent.equals(commandidentifier + "ping")) {
                event.getChannel().sendMessage("Pong!").queue();
            } else if (messagecontent.contains(commandidentifier + "getlist")) {
                event.getChannel().sendMessage(currentcategories.toString(event.getChannel().getId())).queue();
            } else if (messagecontent.contains(commandidentifier + "add") && messagecontent.length() > 5) {
                this.currentcategories.addCategory(event.getMessage().getContentRaw().substring(5),
                        event.getChannel().getId());
            } else if (messagecontent.contains(commandidentifier + "remove") && messagecontent.length() > 8) {
                this.currentcategories.removeCategory(event.getMessage().getContentRaw().substring(8),
                        event.getChannel().getId());
            }
        }
    }
}
