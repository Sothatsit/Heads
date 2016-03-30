package net.sothatsit.heads.config.cache;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.volatilecode.ItemNBT;

import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CachedHead {
    
    public static final String DEFAULT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE2M2RhZmFjMWQ5MWE4YzkxZGI1NzZjYWFjNzg0MzM2NzkxYTZlMThkOGY3ZjYyNzc4ZmM0N2JmMTQ2YjYifX19";
    
    private int id;
    private String category;
    private String name;
    private String texture;
    private double cost;
    
    public CachedHead() {
        this.id = -1;
        this.name = "";
        this.texture = DEFAULT_TEXTURE;
    }
    
    public CachedHead(int id, String category, String name, String texture) {
        this(id, category, name, texture, -1d);
    }
    
    public CachedHead(int id, String category, String name, String texture, double cost) {
        this.category = category;
        this.name = name;
        this.texture = texture;
        this.cost = cost;
    }
    
    public boolean isValid() {
        return !name.isEmpty();
    }
    
    public boolean hasId() {
        return id > 0;
    }
    
    public int getId() {
        return id;
    }
    
    protected void setId(int id) {
        this.id = id;
    }
    
    public String getCategory() {
        return category;
    }
    
    public String getName() {
        return name;
    }
    
    public String getTexture() {
        return texture;
    }
    
    public String getPermission() {
        return "heads.category." + category;
    }
    
    public boolean hasPermission(Player player) {
        return player.hasPermission(getPermission());
    }
    
    public double getCost() {
        return (hasCost() ? cost : Heads.getMainConfig().getDefaultHeadCost());
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
    
    public void load(MemorySection section) {
        this.id = section.getInt("id", -1);
        this.category = section.getString("category", "none");
        this.name = section.getString("name", "");
        this.texture = section.getString("texture", DEFAULT_TEXTURE);
        this.cost = section.getDouble("cost", -1d);
    }
    
    public void save(MemorySection section) {
        section.set("id", id);
        section.set("category", category);
        section.set("name", name);
        section.set("texture", texture);
        section.set("cost", cost);
    }
    
    @Override
    public String toString() {
        return "{\"name\":\"" + name + "\"}";
    }
}
