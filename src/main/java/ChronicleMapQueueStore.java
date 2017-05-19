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
            File file = new File("chroniclemapstore.dat");
            file.deleteOnExit();
            ChronicleMapBuilder<Long, String> builder = ChronicleMapBuilder.of(Long.class, String.class);
            chronicleMap = builder.entries(1000L).averageValue("xxx").createPersistedTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void store(Long aLong, String s) {
        chronicleMap.put(aLong, s);
        System.out.println("Calling store for: " + s);
    }

    public void storeAll(Map<Long, String> map) {
        System.out.println("Calling storeAll for: " + map.size() + " entries");
        for (Long aLong : map.keySet()) {
            chronicleMap.put(aLong, map.get(aLong));
        }
    }

    public void delete(Long aLong) {
        System.out.println("Calling delete for: " + aLong);
        chronicleMap.remove(aLong);
    }

    public void deleteAll(Collection<Long> collection) {
        for (Long aLong : collection) {
            chronicleMap.remove(aLong);
        }
        System.out.println("Calling     deleteAll for " + collection.size() + " entries");
    }

    public String load(Long aLong) {
        System.out.println("Calling load for: " + aLong);
        return chronicleMap.get(aLong);
    }

    public Map<Long, String> loadAll(Collection<Long> collection) {
        final Map<Long, String> result = new HashMap<Long, String>();
        for (Long aLong : collection) {
            result.put(aLong, chronicleMap.get(aLong));
        }
        System.out.println("Calling loadAll for: " + collection);
        return result;
    }

    public Set<Long> loadAllKeys() {
        System.out.println("loadAllKeys");
        return chronicleMap.keySet();
    }
}
