package net.sothatsit.heads.cache;

import net.sothatsit.heads.Heads;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class AddonsFile {

    private final List<CacheFile> addons = new ArrayList<>();

    public AddonsFile() {
        this(Collections.emptyList());
    }

    public AddonsFile(List<CacheFile> addons) {
        addons.forEach(this::addAddon);
    }

    public Set<String> getAddonNames() {
        return addons.stream()
                .map(CacheFile::getName)
                .collect(Collectors.toSet());
    }

    public int installAddons(CacheFile cache) {
        int headsBefore = cache.getHeadCount();

        for(CacheFile addon : addons) {
            cache.installAddon(addon);
        }

        return cache.getHeadCount() - headsBefore;
    }

    public void addAddon(CacheFile addon) {
        addons.add(addon);
    }

    public void write(File file) throws IOException {
        if(file.isDirectory())
            throw new IOException("File " + file + " is a directory");

        if (!file.exists() && !file.createNewFile())
            throw new IOException("Unable to create file " + file);

        try(FileOutputStream stream = new FileOutputStream(file)) {
            writeCompressed(stream);
        }
    }

    public void writeCompressed(OutputStream os) throws IOException {
        try(GZIPOutputStream zos = new GZIPOutputStream(os);
            ObjectOutputStream stream = new ObjectOutputStream(zos)) {

            write(stream);

            stream.flush();
        }
    }

    public void write(ObjectOutputStream stream) throws IOException {
        AddonsFileHeader header = new AddonsFileHeader(1, getAddonNames());

        header.write(stream);

        stream.writeInt(addons.size());
        for(CacheFile addon : addons) {
            addon.write(stream);
        }
    }

    public static AddonsFile readResource(String resource) throws IOException {
        try(InputStream stream = Heads.getInstance().getResource(resource)) {
            return readCompressed(stream);
        }
    }

    public static AddonsFile readCompressed(InputStream is) throws IOException {
        try(GZIPInputStream zis = new GZIPInputStream(is);
            ObjectInputStream stream = new ObjectInputStream(zis)) {

            return read(stream);
        }
    }

    public static AddonsFile read(ObjectInputStream stream) throws IOException {
        AddonsFileHeader header = AddonsFileHeader.read(stream);

        if(header.getVersion() != 1)
            throw new UnsupportedOperationException("Unknown addons.caches file version " + header.getVersion());

        int addonCount = stream.readInt();
        List<CacheFile> addons = new ArrayList<>(addonCount);
        for(int index = 0; index < addonCount; ++index) {
            addons.add(CacheFile.read(stream));
        }

        return new AddonsFile(addons);
    }

}
