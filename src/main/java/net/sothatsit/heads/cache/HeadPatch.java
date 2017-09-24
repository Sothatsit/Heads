package net.sothatsit.heads.cache;

import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.IOUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;

public final class HeadPatch {

    private final UUID uniqueId;

    private boolean category = false;
    private String fromCategory = null;
    private String toCategory = null;

    private boolean tags = false;
    private List<String> fromTags = null;
    private List<String> toTags = null;

    public HeadPatch(CacheHead head) {
        this(head.getUniqueId());
    }

    public HeadPatch(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public HeadPatch withCategory(String from, String to) {
        Checks.ensureNonNull(from, "from");
        Checks.ensureNonNull(to, "to");

        this.category = true;
        this.fromCategory = from;
        this.toCategory = to;

        return this;
    }

    public HeadPatch withTags(List<String> from, List<String> to) {
        Checks.ensureNonNull(from, "from");
        Checks.ensureNonNull(to, "to");

        this.tags = true;
        this.fromTags = from;
        this.toTags = to;

        return this;
    }

    public void applyPatch(CacheFile cache) {
        for(CacheHead head : cache.findHeads(uniqueId)) {
            applyPatch(cache, head);
        }
    }

    public void applyPatch(CacheFile cache, CacheHead head) {
        if(category && head.getCategory().equalsIgnoreCase(fromCategory)) {
            cache.removeHead(head);

            head = head.copyWithCategory(toCategory);

            cache.addHead(head);
        }

        if(tags && head.getTags().equals(fromTags)) {
            head.setTags(toTags);
        }
    }

    public void write(ObjectOutputStream stream) throws IOException {
        IOUtils.writeUUID(stream, uniqueId);

        stream.writeBoolean(category);
        if(category) {
            stream.writeUTF(fromCategory);
            stream.writeUTF(toCategory);
        }

        stream.writeBoolean(tags);
        if(tags) {
            IOUtils.writeStringList(stream, fromTags);
            IOUtils.writeStringList(stream, toTags);
        }
    }

    public static HeadPatch read(ObjectInputStream stream) throws IOException {
        UUID uniqueId = IOUtils.readUUID(stream);

        HeadPatch patch = new HeadPatch(uniqueId);

        boolean category = stream.readBoolean();
        if(category) {
            String from = stream.readUTF();
            String to = stream.readUTF();

            patch.withCategory(from, to);
        }

        boolean tags = stream.readBoolean();
        if(tags) {
            List<String> from = IOUtils.readStringList(stream);
            List<String> to = IOUtils.readStringList(stream);

            patch.withTags(from, to);
        }

        return patch;
    }

}
