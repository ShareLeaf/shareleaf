package co.shareleaf.instagram4j.accounts;

import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.requests.qe.QeSyncRequest;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.extern.slf4j.Slf4j;
import serialize.SerializeTestUtil;

@Slf4j
public class QeSyncTest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testName() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response = new QeSyncRequest().execute(client).join();
        Assert.assertEquals("ok", response.getStatus());
        response.getExtraProperties().keySet().forEach(log::debug);
        log.debug("Success");
    }
}
