package tests;

import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVClient;
import server.KVServer;

import java.io.IOException;

public class KVClientServerConnectionTest {
    private static final String url = Managers.getDefaultUrl();

    private KVServer kvServer;

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterEach
    public void afterEach() {
        kvServer.stop();
    }

    @Test
    public void testRegistration() throws IOException, InterruptedException {
        KVClient kvClient = new KVClient(url);
        Assertions.assertNotNull(kvClient.getApiToken());
    }

    @Test
    public void testSaveLoad() throws IOException, InterruptedException {
        KVClient kvClient = new KVClient(url);
        kvClient.put("key", "value");
        Assertions.assertEquals(kvClient.load("key"), "value");

        kvClient.put("key", "new_value");
        Assertions.assertEquals(kvClient.load("key"), "new_value");
    }
}
