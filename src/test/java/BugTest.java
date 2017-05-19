import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BugTest {

    @Test
    public void bugTest() {

        // configure queuestore
        QueueStoreConfig queueStoreConfig = new QueueStoreConfig();
        queueStoreConfig.setEnabled(true);
        //setPropertiesByPropertiesObject(queueStoreConfig); // This does not work!
        setPropertiesDirectly(queueStoreConfig); // This works!
        queueStoreConfig.setStoreImplementation(new ChronicleMapQueueStore());

        // configure queue
        QueueConfig queueConfig = new QueueConfig();
        queueConfig.setName("buggyQueue");
        queueConfig.setQueueStoreConfig(queueStoreConfig);

        // configure hazelcast instance
        Config hzconfig = new Config();
        hzconfig.setInstanceName("buggyInstance");
        hzconfig.addQueueConfig(queueConfig);
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(hzconfig);

        // this is where the test begins
        IQueue<String> queue = instance.getQueue("buggyQueue");
        System.out.println("Putting some items in the queue");
        int i = 0;
        while(i < 10) {
            queue.offer(String.valueOf(i));
            i++;
        }

        int itemsToDrain = 8;
        System.out.println("Draining " + itemsToDrain + " items from the queue.");
        List<String> drained = new ArrayList<String>();
        queue.drainTo(drained, itemsToDrain);

        assert drained.size() == itemsToDrain;
    }

    @Test
    public void fillQueueStore() throws Exception {
        ChronicleMapQueueStore store = new ChronicleMapQueueStore();
        long i = 0;
        while(i < 10) {
            store.store(i, String.valueOf(i));
            i++;
        }

    }

    private void setPropertiesDirectly(QueueStoreConfig queueStoreConfig) {
        queueStoreConfig.setProperty("binary", "false");
        queueStoreConfig.setProperty("memory-limit", "0");
        queueStoreConfig.setProperty("bulk-load", "4");
    }

    private void setPropertiesByPropertiesObject(QueueStoreConfig queueStoreConfig) {
        Properties properties = new Properties();
        properties.put("binary", false);
        properties.put("memory-limit", 1);
        properties.put("bulk-load", 2L);
        queueStoreConfig.setProperties(properties);
    }
}
