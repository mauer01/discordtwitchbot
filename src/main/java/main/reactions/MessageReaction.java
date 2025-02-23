package main.reactions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        String messageContent = event.getMessage().getContentRaw();
        if (cooldowns.containsKey(userId) && (currentTime - cooldowns.get(userId)) < 5000) {
            event.getChannel().sendMessage("Slow down! Wait a few seconds before using commands again.").queue();
            return;
        }
        Pattern pattern = Pattern.compile("!([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(messageContent);

        String command = matcher.group(1).toLowerCase();
        cooldowns.put(userId, currentTime); // Add user to cooldown list
        if (event.isFromGuild()) {
            if (command.equals("ping")) {
                event.getChannel().sendMessage("Pong!").queue();
            } else if (command.contains("getlist")) {
                event.getChannel().sendMessage(currentcategories.toString(event.getChannel().getId())).queue();
            } else if (command.contains("add") && messageContent.length() > 5) {
                this.currentcategories.addCategory(event.getMessage().getContentRaw().substring(5),
                        event.getChannel().getId());
            } else if (command.contains("remove") && messageContent.length() > 8) {
                this.currentcategories.removeCategory(event.getMessage().getContentRaw().substring(8),
                        event.getChannel().getId());
            }
        }
    }
}
