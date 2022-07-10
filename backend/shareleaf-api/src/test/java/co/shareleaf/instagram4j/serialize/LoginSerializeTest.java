package co.shareleaf.instagram4j.serialize;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import co.shareleaf.instagram4j.IGClient;
import co.shareleaf.instagram4j.exceptions.IGLoginException;
import co.shareleaf.instagram4j.exceptions.IGResponseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginSerializeTest {
    @Test
    // Run SerializeTestUtil.serializeLogin first to generate saved sessions
    public void testName()
            throws IGLoginException, IGResponseException, ClassNotFoundException,
            FileNotFoundException, IOException {
        IGClient lib = SerializeTestUtil.getClientFromSerialize("igclient.ser", "cookie.ser");
        log.debug(lib.toString());
    }
}
