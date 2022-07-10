package co.shareleaf.instagram4j;

import org.junit.Assert;
import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.responses.IGResponse;

import lombok.extern.slf4j.Slf4j;
import serialize.SerializeTestUtil;

@Slf4j
public class TestTemplate {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testName() throws Exception {
        IGClient client = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        IGResponse response = null;
        Assert.assertEquals("ok", response.getStatus());
        log.debug("Success");
    }
}
