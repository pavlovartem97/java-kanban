package tests;

import org.junit.jupiter.api.*;
import server.KVClient;
import server.KVServer;

import java.io.IOException;

public class KVClientServerConnectionTest {
    private static final String url = "http://localhost:8078/";

    private static KVServer kvServer;

    @BeforeAll
    public static void beforeAll(){
        try {
            kvServer = new KVServer();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @BeforeEach
    public void beforeEach(){
        kvServer.start();
    }

    @AfterEach
    public void afterEach(){
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
    }
}
