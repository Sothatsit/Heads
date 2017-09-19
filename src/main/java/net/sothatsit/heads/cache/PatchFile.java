package net.sothatsit.heads.cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PatchFile {

    private final String name;
    private final List<HeadPatch> patches = new ArrayList<>();

    public PatchFile(String name, List<HeadPatch> patches) {
        this.name = name;
        this.patches.addAll(patches);
    }

    public String getName() {
        return name;
    }

    public void applyPatches(Map<UUID, CacheHead> heads) {
        for(HeadPatch patch : patches) {
            CacheHead head = heads.get(patch.getUniqueId());

            if(head == null)
                continue;

            patch.applyPatch(head);
        }
    }

    public void write(ObjectOutputStream stream) throws IOException {
        stream.writeInt(1);
        stream.writeUTF(name);

        stream.writeInt(patches.size());
        for(HeadPatch patch : patches) {
            patch.write(stream);
        }
    }

    public static PatchFile read(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.readInt();

        String name = stream.readUTF();

        int patchCount = stream.readInt();
        List<HeadPatch> patches = new ArrayList<>(patchCount);
        for(int index = 0; index < patchCount; ++index) {
            patches.add(HeadPatch.read(stream));
        }

        return new PatchFile(name, patches);
    }

}
