import com.hazelcast.core.QueueStore;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChronicleMapQueueStore implements QueueStore<String> {

    ChronicleMap<Long, String> chronicleMap;

    public ChronicleMapQueueStore() {
        try {
            final File file = new File("chroniclemapstore.dat");
            ChronicleMapBuilder<Long, String> builder = ChronicleMapBuilder.of(Long.class, String.class);
            chronicleMap = builder.entries(1000L).averageValue("xxx").createPersistedTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store(Long aLong, String s) {
        chronicleMap.put(aLong, s);
    }

    public void storeAll(Map<Long, String> map) {
        for (Long aLong : map.keySet()) {
            chronicleMap.put(aLong, map.get(aLong));
        }
    }

    public void delete(Long aLong) {
        chronicleMap.remove(aLong);
    }

    public void deleteAll(Collection<Long> collection) {
        for (Long aLong : collection) {
            chronicleMap.remove(aLong);
        }
    }

    public String load(Long aLong) {
        return chronicleMap.get(aLong);
    }

    public Map<Long, String> loadAll(Collection<Long> collection) {
        return new HashMap<Long, String>(chronicleMap);
    }

    public Set<Long> loadAllKeys() {
        return chronicleMap.keySet();
    }
}
