package net.sothatsit.heads.config.lang;

import net.sothatsit.heads.util.Checks;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class Placeholder {
    
    private final String replace;
    private final String with;
    
    public Placeholder(String replace, String with) {
        Checks.ensureNonNull(replace, "replace");
        Checks.ensureNonNull(with, "with");

        this.replace = replace;
        this.with = with;
    }

    public Placeholder(String replace, Object with) {
        this(replace, Objects.toString(with));
    }
    
    public String getReplace() {
        return replace;
    }
    
    public String getWith() {
        return with;
    }
    
    public String apply(String text) {
        Checks.ensureNonNull(text, "text");

        return text.replace(replace, with);
    }

    public static String applyAll(String text, Placeholder... placeholders) {
        Checks.ensureNonNull(text, "text");
        Checks.ensureArrayNonNull(placeholders, "placeholders");

        for (Placeholder placeholder : placeholders) {
            text = placeholder.apply(text);
        }

        return text;
    }

    public static String[] applyAll(String[] lines, Placeholder... placeholders) {
        Checks.ensureArrayNonNull(lines, "lines");
        Checks.ensureArrayNonNull(placeholders, "placeholders");

        String[] replaced = new String[lines.length];

        for(int index = 0; index < lines.length; index++) {
            replaced[index] = applyAll(lines[index], placeholders);
        }

        return replaced;
    }

    public static String[] colourAll(String... lines) {
        Checks.ensureArrayNonNull(lines, "lines");

        String[] translated = new String[lines.length];

        for(int index = 0; index < lines.length; index++) {
            Checks.ensureTrue(lines[index] != null, " lines cannot contain a null value, lines[" + index + "] is null");

            translated[index] = ChatColor.translateAlternateColorCodes('&', lines[index]);
        }

        return translated;
    }

    public static String[] filter(String[] lines, Function<String, Boolean> accept) {
        Checks.ensureArrayNonNull(lines, "lines");

        if(accept == null)
            return lines;

        List<String> filtered = new ArrayList<>();

        for(int index = 0; index < lines.length; index++) {
            if(accept.apply(lines[index])) {
                filtered.add(lines[index]);
            }
        }

        return filtered.toArray(new String[0]);
    }

    public static String[] filterAndApplyAll(String[] lines, Function<String, Boolean> accept, Placeholder... placeholders) {
        return Placeholder.applyAll(Placeholder.filter(lines, accept), placeholders);
    }
    
    public static Placeholder valid(String valid) {
        return new Placeholder("%valid%", valid);
    }
    
    public static Placeholder category(String category) {
        return new Placeholder("%category%", category);
    }
    
    public static Placeholder length(int length) {
        return new Placeholder("%length%", length);
    }
    
    public static Placeholder name(String name) {
        return new Placeholder("%name%", name);
    }
    
    public static Placeholder number(String number) {
        return new Placeholder("%number%", number);
    }
    
    public static Placeholder id(String id) {
        return new Placeholder("%id%", id);
    }
    
    public static Placeholder head(String head) {
        return new Placeholder("%head%", head);
    }
    
    public static Placeholder amount(String amount) {
        return new Placeholder("%amount%", amount);
    }
    
    public static Placeholder command(String command) {
        return new Placeholder("%command%", command);
    }
    
    public static Placeholder description(String description) {
        return new Placeholder("%description%", description);
    }
    
    public static Placeholder cost(String cost) {
        return new Placeholder("%cost%", cost);
    }

    public static Placeholder page(int page) {
        return new Placeholder("%page%", page);
    }
    
}
