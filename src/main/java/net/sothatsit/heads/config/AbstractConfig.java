package net.sothatsit.heads.config;

public abstract class AbstractConfig {
    
    public abstract void reload();
    
    public abstract ConfigFile getConfigFile();
    
    public void checkReload() {
        ConfigFile config = getConfigFile();
        if (config.shouldReload()) {
            reload();
        }
    }
    
}
