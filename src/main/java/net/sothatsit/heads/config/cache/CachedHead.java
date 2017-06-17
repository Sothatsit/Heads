package net.sothatsit.heads.config.cache;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.volatilecode.ItemNBT;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Base64;

public class CachedHead implements Comparable<CachedHead> {
    
    private static final String DEFAULT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE2M2RhZmFjMWQ5MWE4YzkxZGI1NzZjYWFjNzg0MzM2NzkxYTZlMThkOGY3ZjYyNzc4ZmM0N2JmMTQ2YjYifX19";
    
    private int id;
    private String category;
    private String name;
    private String texture;
    private String[] tags;
    private double cost;
    
    public CachedHead() {
        this.id = -1;
        this.category = "";
        this.name = "";
        this.tags = new String[0];
        this.texture = DEFAULT_TEXTURE;
    }
    
    public CachedHead(int id, String category, String name, String texture, String[] tags) {
        this(id, category, name, texture, tags, -1d);
    }
    
    public CachedHead(int id, String category, String name, String texture, String[] tags, double cost) {
        this.category = category;
        this.name = name;
        this.texture = texture;
        this.tags = tags;
        this.cost = cost;
    }
    
    public boolean isValid() {
        return !this.name.isEmpty();
    }
    
    public boolean hasId() {
        return this.id > 0;
    }
    
    public int getId() {
        return this.id;
    }
    
    protected void setId(int id) {
        this.id = id;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTexture() {
        return this.texture;
    }

    public String getTextureURL() {
        String decoded = this.texture;

        try {
            decoded = new String(Base64.getDecoder().decode(this.texture));
            JsonObject json = new JsonParser().parse(decoded).getAsJsonObject();
            JsonObject textures = json.getAsJsonObject("textures");
            JsonObject skin = textures.getAsJsonObject("SKIN");
            return skin.get("url").getAsString();
        } catch(Exception e) {
            new RuntimeException(this.id + " - " + decoded, e).printStackTrace();
            return DEFAULT_TEXTURE;
        }
    }

    public String[] getTags() {
        return this.tags;
    }

    public String getPermission() {
        return "heads.category." + category;
    }
    
    public boolean hasPermission(Player player) {
        return player.hasPermission(getPermission());
    }
    
    public double getCost() {
        return (hasCost() ? cost : Heads.getMainConfig().getCategoryCost(this.category));
    }
    
    public boolean hasCost() {
        return cost != -1d;
    }
    
    public String getCostString() {
        return getCostString(getCost());
    }
    
    public static String getCostString(double cost) {
        return (cost <= 0 ? "Free" : Double.toString(cost));
    }
    
    public ItemStack getItemStack() {
        return ItemNBT.createHead(this, null);
    }
    
    public ItemStack getItemStack(String name) {
        return ItemNBT.createHead(this, name);
    }
    
    public ItemStack applyTo(ItemStack item) {
        return ItemNBT.applyHead(this, item);
    }
    
    public Placeholder[] getPlaceholders() {
        return new Placeholder[] {
                new Placeholder("%name%", name),
                new Placeholder("%cost%", getCostString()),
                new Placeholder("%category%", category),
                new Placeholder("%id%", Integer.toString(id)),
                new Placeholder("%cost%", getCostString())
        };
    }
    
    protected void setCategory(String category) {
        this.category = category;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    public void removeCost() {
        this.cost = -1d;
    }
    
    public void load(ConfigurationSection section) {
        this.id = section.getInt("id", -1);
        this.category = section.getString("category", "none");
        this.name = section.getString("name", "");
        this.texture = section.getString("texture", DEFAULT_TEXTURE);
        this.cost = section.getDouble("cost", -1d);

        if(section.isSet("tags") && section.isString("tags")) {
            this.tags = new String[] {section.getString("tags")};
        } else if(section.isSet("tags") && section.isList("tags")) {
            this.tags = section.getStringList("tags").toArray(new String[0]);
        }
    }
    
    public void save(ConfigurationSection section) {
        section.set("id", id);
        section.set("category", category);
        section.set("name", name);
        section.set("texture", texture);
        section.set("tags", Arrays.asList(tags));
        section.set("cost", cost);
    }
    
    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\"}";
    }

    @Override
    public int compareTo(@Nonnull CachedHead other) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.name, other.name);
    }

}
