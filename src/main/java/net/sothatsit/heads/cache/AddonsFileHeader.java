package net.sothatsit.heads.cache;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.util.IOUtils;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class AddonsFileHeader {

    private final int version;
    private final Set<String> addonNames = new HashSet<>();

    public AddonsFileHeader(int version, Set<String> addonNames) {
        this.version = version;
        this.addonNames.addAll(addonNames);
    }

    public int getVersion() {
        return version;
    }

    public Set<String> getAddonNames() {
        return addonNames;
    }

    public int getUninstalledAddons(CacheFile cache) {
        int newAddons = 0;

        for(String addon : addonNames) {
            if(cache.hasAddon(addon))
                continue;

            ++newAddons;
        }

        return newAddons;
    }

    public void write(ObjectOutputStream stream) throws IOException {
        stream.writeInt(version);

        IOUtils.writeStringSet(stream, addonNames);
    }

    public static AddonsFileHeader readResource(String resource) throws IOException {
        try(InputStream stream = Heads.getInstance().getResource(resource)) {
            return readCompressed(stream);
        }
    }

    public static AddonsFileHeader readCompressed(InputStream is) throws IOException {
        try(GZIPInputStream zis = new GZIPInputStream(is);
            ObjectInputStream stream = new ObjectInputStream(zis)) {

            return read(stream);
        }
    }

    public static AddonsFileHeader read(ObjectInputStream stream) throws IOException {
        int version = stream.readInt();
        Set<String> addonNames = IOUtils.readStringSet(stream);

        return new AddonsFileHeader(version, addonNames);
    }

}
