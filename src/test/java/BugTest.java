import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.QueueStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
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
        Properties properties = new Properties();
        properties.put("binary", false);
        properties.put("memory-limit", 1000);
        properties.put("bulk-load", 4);
        queueStoreConfig.setStoreImplementation(new ChronicleMapQueueStore());
        queueStoreConfig.setProperties(properties);

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

        queue.drainTo(drained, 4);

        assert drained.size() == 4;
    }
}
