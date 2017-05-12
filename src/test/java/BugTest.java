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
    public void fillQueueStore() throws Exception {
        ChronicleMapQueueStore store = new ChronicleMapQueueStore();
        long i = 0;
        while(i < 10) {
            store.store(i, String.valueOf(i));
            i++;
        }

    }

    @Test
    public void bugTest() {

        // configure queuestore
        QueueStoreConfig queueStoreConfig = new QueueStoreConfig();
        queueStoreConfig.setEnabled(true);
//        Properties properties = new Properties();
//        properties.put("binary", false);
//        properties.put("memory-limit", 1);
//        properties.put("bulk-load", 2L);
//        queueStoreConfig.setProperties(properties);
        queueStoreConfig.setStoreImplementation(new ChronicleMapQueueStore());
        queueStoreConfig.setProperty("binary", "false");
        queueStoreConfig.setProperty("memory-limit", "0");
        queueStoreConfig.setProperty("bulk-load", "4");

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
        int i = 0;
        while(i < 10) {
            queue.offer(String.valueOf(i));
            i++;
        }

        List<String> drained = new ArrayList<String>();

        queue.drainTo(drained, 8);

        assert drained.size() == 8;
    }
}
