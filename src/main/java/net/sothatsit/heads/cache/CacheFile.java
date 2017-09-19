package net.sothatsit.heads.cache;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.IOUtils;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class CacheFile {

    private final String name;
    private final Set<String> addons = new HashSet<>();
    private final Set<String> patches = new HashSet<>();
    private final List<CacheHead> heads = new ArrayList<>();
    private final Map<Integer, CacheHead> headsById = new HashMap<>();
    private final Map<UUID, CacheHead> headsByUniqueId = new HashMap<>();
    private final Map<String, List<CacheHead>> categories = new HashMap<>();

    public CacheFile(String name) {
        this(name, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    public CacheFile(String name, Set<String> addons, Set<String> patches, Iterable<CacheHead> heads) {
        Checks.ensureNonNull(name, "name");
        Checks.ensureNonNull(addons, "addons");
        Checks.ensureNonNull(patches, "patches");
        Checks.ensureNonNull(heads, "heads");

        this.name = name;
        this.addons.addAll(addons);
        this.patches.addAll(patches);

        addHeads(heads);
    }

    public String getName() {
        return name;
    }

    public int getHeadCount() {
        return heads.size();
    }

    public List<CacheHead> getHeads() {
        return Collections.unmodifiableList(heads);
    }

    public Set<String> getCategories() {
        return Collections.unmodifiableSet(categories.keySet());
    }

    public List<CacheHead> getCategoryHeads(String category) {
        List<CacheHead> list = categories.getOrDefault(category.toLowerCase(), Collections.emptyList());

        Collections.sort(list);

        return Collections.unmodifiableList(list);
    }

    public List<CacheHead> searchHeads(String search) {
        List<CacheHead> matches = new ArrayList<>();

        for(CacheHead head : heads) {
            if(!head.matches(search))
                continue;

            matches.add(head);
        }

        return matches;
    }

    public CacheHead findHead(int id) {
        return headsById.get(id);
    }

    public CacheHead findHead(UUID uniqueId) {
        return headsByUniqueId.get(uniqueId);
    }

    public CacheHead getRandomHead(Random random) {
        return heads.get(random.nextInt(heads.size()));
    }

    public void addHeads(Iterable<CacheHead> heads) {
        for(CacheHead head : heads) {
            addHead(head);
        }
    }

    private int getMaxId() {
        int max = -1;

        for(CacheHead head : heads) {
            max = Math.max(max, head.getId());
        }

        return max;
    }

    public void addHead(CacheHead head) {
        head = head.copy();
        head.setId(getMaxId() + 1);

        String category = head.getCategory().toLowerCase();

        heads.add(head);
        headsById.put(head.getId(), head);
        headsByUniqueId.put(head.getUniqueId(), head);
        categories.computeIfAbsent(category, c -> new ArrayList<>()).add(head);
    }

    public void removeHead(CacheHead head) {
        heads.remove(head);
        headsById.remove(head.getId(), head);
        headsByUniqueId.remove(head.getUniqueId(), head);
        categories.compute(head.getCategory(), (key, category) -> {
            if(category == null)
                return null;

            category.remove(head);

            return (category.size() > 0 ? category : null);
        });
    }

    public boolean hasAddon(String addon) {
        return addons.contains(addon);
    }

    public void installAddons(AddonsFile addons) {

    }

    public void installAddon(CacheFile addon) {
        if(hasAddon(addon.getName()))
            return;

        addons.add(addon.getName());
        addHeads(addon.heads);
    }

    public boolean hasPatch(String patch) {
        return patches.contains(patch);
    }

    public void installPatch(PatchFile patch) {
        patches.add(patch.getName());
        patch.applyPatches(headsByUniqueId);
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
        stream.writeInt(1);
        stream.writeUTF(name);

        IOUtils.writeStringSet(stream, addons);
        IOUtils.writeStringSet(stream, patches);

        stream.writeInt(heads.size());
        for(CacheHead head : heads) {
            head.write(stream);
        }
    }

    public static CacheFile read(File file) throws IOException {
        if(file.isDirectory())
            throw new IOException("File " + file + " is a directory");

        if(!file.exists())
            throw new IOException("File " + file + " does not exist");

        try(FileInputStream stream = new FileInputStream(file)) {
            return readCompressed(stream);
        }
    }

    public static CacheFile readResource(String resource) throws IOException {
        try(InputStream stream = Heads.getInstance().getResource(resource)) {
            return readCompressed(stream);
        }
    }

    public static CacheFile readCompressed(InputStream is) throws IOException {
        try(GZIPInputStream zis = new GZIPInputStream(is);
            ObjectInputStream stream = new ObjectInputStream(zis)) {

            return read(stream);
        }
    }

    public static CacheFile read(ObjectInputStream stream) throws IOException {
        stream.readInt();
        String name = stream.readUTF();

        Set<String> addons = IOUtils.readStringSet(stream);
        Set<String> patches = IOUtils.readStringSet(stream);

        int headCount = stream.readInt();
        List<CacheHead> heads = new ArrayList<>(headCount);
        for(int index = 0; index < headCount; ++index) {
            heads.add(CacheHead.read(stream));
        }

        return new CacheFile(name, addons, patches, heads);
    }

}
