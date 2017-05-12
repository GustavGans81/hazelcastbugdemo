import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class BugTest {

    @Test
    public void bugTest() {
        QueueConfig queueConfig = new QueueConfig();
        queueConfig.setName("buggyQueue");

        Config hzconfig = new Config();
        hzconfig.setInstanceName("buggyInstance");
        hzconfig.addQueueConfig(queueConfig);

        HazelcastInstance instance = Hazelcast.newHazelcastInstance();

        IQueue<String> queue = instance.getQueue("buggyQueue");

        queue.offer("01");
        queue.offer("02");

        List<String> drained = new ArrayList<String>();

        queue.drainTo(drained, 1);

        assert drained.size() == 2;
    }


}
