package net.sothatsit.heads.config.lang;

import java.util.Arrays;

import org.bukkit.ChatColor;

import org.bukkit.command.CommandSender;

public class LangMessage {
    
    private String[] messages;
    
    public LangMessage(String... messages) {
        this.messages = messages;
    }
    
    public void send(CommandSender sender, Placeholder... placeholders) {
        if (messages.length > 0) {
            for (String message : get(placeholders)) {
                sender.sendMessage(message);
            }
        }
    }
    
    public String getSingle(Placeholder... placeholders) {
        String[] list = get(placeholders);
        return (list.length == 0 ? "" : list[0]);
    }
    
    public String getSingleRaw() {
        return (messages.length > 0 ? messages[0] : "");
    }
    
    public String[] getRaw() {
        return messages;
    }
    
    public Object getConfigValue() {
        return (messages.length == 0 ? "" : (messages.length == 1 ? messages[0] : Arrays.asList(messages)));
    }
    
    public String[] get(Placeholder... placeholders) {
        String[] list = new String[messages.length];
        
        for (int i = 0; i < list.length; i++) {
            list[i] = ChatColor.translateAlternateColorCodes('&', messages[i]);
            
            for (Placeholder placeholder : placeholders) {
                list[i] = placeholder.apply(list[i]);
            }
        }
        
        return list;
    }
    
}
