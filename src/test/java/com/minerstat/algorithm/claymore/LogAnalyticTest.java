package com.minerstat.algorithm.claymore;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("fast")
public class LogAnalyticTest {
    private LogAnalytic log;
    private File directory;

    @Before
    public void setUp() throws Exception {
        URL currentTestResourceFolder = this.getClass().getResource("/");
        directory = new File(currentTestResourceFolder.toURI());
        log = new LogAnalytic(directory);
        log.setLastFile(new File(this.getClass().getResource("/miner.txt").getFile()));
        log.setLastLineNumber(0);
    }

    @Test
    @DisplayName("😱")
    public void call() throws Exception {
        String result = log.call();
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(result);
        Set keys = obj.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("log"));
        assertTrue(keys.contains("info"));

        // Log
        Map logMap = (Map) obj.get("log");
        assertEquals(3, logMap.size());
        assertTrue(logMap.containsKey("0"));
        assertTrue(logMap.containsKey("1"));
        assertTrue(logMap.containsKey("4"));

        // Info
        Map infoMap = (Map) obj.get("info");
        assertEquals(4, infoMap.size());
        assertTrue(infoMap.containsKey("0"));
        assertTrue(infoMap.containsKey("1"));
        assertTrue(infoMap.containsKey("2"));
        assertTrue(infoMap.containsKey("3"));
    }

}