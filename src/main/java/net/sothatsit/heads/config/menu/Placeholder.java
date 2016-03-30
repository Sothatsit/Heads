package net.sothatsit.heads.config.menu;

public class Placeholder {
    
    private String replace;
    private String with;
    
    public Placeholder(String replace, String with) {
        this.replace = replace;
        this.with = with;
    }
    
    public String getReplace() {
        return replace;
    }
    
    public String getWith() {
        return with;
    }
    
    public String apply(String str) {
        return str.replace(replace, with);
    }
    
    public static Placeholder valid(String valid) {
        return new Placeholder("%valid%", valid);
    }
    
    public static Placeholder category(String category) {
        return new Placeholder("%category%", category);
    }
    
    public static Placeholder length(int length) {
        return new Placeholder("%length%", Integer.toString(length));
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
    
}
