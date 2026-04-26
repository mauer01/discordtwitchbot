package main.reactions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Categories;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageReaction extends ListenerAdapter {

    private final Categories currentcategories;
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();
    private final Map<String, String> pingroles;

    public MessageReaction(Categories current, Map<String, String> pingroles) {
        this.pingroles = pingroles;
        this.currentcategories = current;

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String userId = event.getAuthor().getId();
        Member member = event.getMember();
        long currentTime = System.currentTimeMillis();
        String commandidentifier = "!";

        if (member == null || !event.isFromGuild()) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentRaw().startsWith(commandidentifier)) {
            return;
        }
        String messageContent = event.getMessage().getContentRaw();
        Pattern pattern = Pattern.compile("!([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(messageContent);

        if (cooldowns.containsKey(userId) && (currentTime - cooldowns.get(userId)) < 5000) {
            event.getChannel().sendMessage("Slow down! Wait a few seconds before using commands again.").queue();
            return;
        }
        if (matcher.find()) {
            String command = matcher.group(1).toLowerCase();
            if (command.equals("getrole")) {
                String roleId = pingroles.get(event.getChannel().getId());

                if (roleId == null || roleId.isBlank()) {
                    event.getChannel().sendMessage("No role has been set for this channel.").queue();
                    return;
                }
                roleId = roleId.replaceAll("[^0-9]", "");
                Role role = event.getGuild().getRoleById(roleId);

                if (role == null) {
                    event.getChannel().sendMessage("The configured role could not be found.").queue();
                    return;
                }

                event.getGuild().addRoleToMember(member, role).queue(
                        success -> event.getChannel().sendMessage("Role added!").queue(),
                        error -> event.getChannel().sendMessage("I could not add that role. Check my permissions.").queue()
                );
                return;
            }
        }
        if (!member.hasPermission(Permission.MANAGE_SERVER)) {
            return;
        }
        if (matcher.find()) {
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
                } else if (command.contains("role")) {
                    String substring = event.getMessage().getContentRaw().substring(6);
                    pingroles.put(event.getChannel().getId(), substring);
                    event.getChannel().sendMessage(substring + " role set!").queue();
                }
            }
        }
    }
}
