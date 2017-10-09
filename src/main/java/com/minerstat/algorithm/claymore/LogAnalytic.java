package com.minerstat.algorithm.claymore;

import com.minerstat.algorithm.MinerCommon;
import com.minerstat.settings.Settings;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

public class LogAnalytic extends MinerCommon implements Callable<String> {

    private File directory;
    private int lastLineNumber;
    private File lastFile;

    LogAnalytic(File directory) {
        this.directory = directory;
        lastLineNumber = Integer.parseInt(Settings.getInstance().getProperties("LastLineNumber"));
        lastFile = null;
    }

    void setLastFile(File lastFile) {
        this.lastFile = lastFile;
    }

    void setLastLineNumber(int number) {
        this.lastLineNumber = number;
    }

    @Override
    public String call() throws Exception {
        String result = "";
        JSONObject parse = new JSONObject();
        JSONObject cardInfo = new JSONObject();
        try {
            lastFile = (lastFile == null) ? findLastFile() : lastFile;
            if (lastFile != null) {
                StringBuilder sb = readFile(lastFile, lastLineNumber);
                parse = analytic(sb);
                if (lastLineNumber == 0) {
                    cardInfo = getVideoCardInfo(sb);
                }
            }
        }
        finally {
            result = stringJson("log", parse, "info", cardInfo).toJSONString().replaceAll("\\\\", "");
        }

        return result;
    }

    /**
     * Find latest log file to parse.
     *
     * @return File
     */
    private File findLastFile() {
        File lastFile = null;

        try {
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith("_log.txt"));
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            int fileCount = files.length;
            if (fileCount >= 1) {
                lastFile = files[fileCount - 1];
            }
        }
        finally {
            String lastLogFile = Settings.getInstance().getProperties("LastLogFile");
            if (lastFile != null && !lastFile.getName().equals(lastLogFile)) {
                lastLineNumber = 0;
                Settings.getInstance().saveProperties("LastLogFile", lastFile.getName());
                Settings.getInstance().saveProperties("LastLineNumber", Integer.toString(lastLineNumber));
            }
        }

        return lastFile;
    }

    /**
     * Read file to buffer from last parse line number.
     *
     * @param fileToRead A file object to read.
     * @param lastLineNumber Last line number from previously parse.
     * @return Read file to string from last parse line number.
     * @throws FileNotFoundException
     */
    private StringBuilder readFile(File fileToRead, int lastLineNumber) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        try {
            String s;
            String ls = System.getProperty("line.separator");
            int numberLines = 0;

            try (BufferedReader in = new BufferedReader(new FileReader(fileToRead.getAbsoluteFile()))) {
                while ((s = in.readLine()) != null) {
                    numberLines++;
                    if (lastLineNumber != 0 && lastLineNumber >= numberLines) {
                        continue;
                    }

                    sb.append(s);
                    sb.append(ls);
                }

                Settings.getInstance().saveProperties("LastLineNumber", Integer.toString(numberLines));
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        return sb;
    }

    private JSONObject analytic(StringBuilder sb) {
        Map<Integer, Map<String, Integer>> statistics = new HashMap<>();

        Pattern pattern = Pattern.compile("([A-Z]{3}):\\s(\\d+/\\d+/\\d+-\\d+:\\d+:\\d+)\\s-\\sSHARE FOUND\\s-\\s\\(GPU\\s([0-9])\\)$");
        String[] lines = sb.toString().split(System.getProperty("line.separator"));
        for(String s: lines) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                String type = matcher.group(1);
                int key = Integer.parseInt(matcher.group(3));
                boolean isVideo = statistics.containsKey(key);

                // Initialize miner statistics by video card.
                if (!isVideo) {
                    Map<String, Integer> setStat = new HashMap<>();
                    setStat.put(type, 1);
                    statistics.put(key, setStat);
                }
                else {
                    // Updating miner statistics by video card.
                    Map<String, Integer> value = statistics.get(key);
                    boolean isType = value.containsKey(type);
                    if (!isType) {
                        value.put(type, 1);
                        statistics.put(key, value);
                    }
                    else {
                        int calcValue = value.get(type);
                        value.put(type, ++calcValue);
                        statistics.put(key, value);
                    }
                }
            }
        }

        return hashMapMap(statistics);
    }

    private JSONObject getVideoCardInfo(StringBuilder sb) {
        Map<Integer, CardInfo> info = new HashMap<>();
        Pattern patternGPU = Pattern.compile("GPU\\s\\#([0-9]+):\\s(.+available.+units)$");
        Pattern patternGPUSeries = Pattern.compile("GPU\\s\\#([0-9]+)\\srecognized as\\s(.+)");
        String[] lines = sb.toString().split(System.getProperty("line.separator"));

        for(String s: lines) {
            Matcher matcherGPU = patternGPU.matcher(s);
            Matcher matcherGPUSeries = patternGPUSeries.matcher(s);

            if (matcherGPU.find()) {
                List data = findMatchData(matcherGPU);
                int cardId = Integer.parseInt(data.get(0).toString());
                String infoData = data.get(1).toString();
                boolean isVideo = info.containsKey(cardId);
                if (isVideo) {
                    CardInfo CardInfo = info.get(cardId);
                    CardInfo.setValue(infoData);
                    info.put(cardId, CardInfo);
                }
                else {
                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setValue(infoData);
                    info.put(cardId, cardInfo);
                }
            }
            if (matcherGPUSeries.find()) {
                List data = findMatchData(matcherGPUSeries);
                int cardId = Integer.parseInt(data.get(0).toString());
                String infoData = data.get(1).toString();
                boolean isVideo = info.containsKey(cardId);
                if (isVideo) {
                    CardInfo CardInfo = info.get(cardId);
                    CardInfo.setModelName(infoData);
                    info.put(cardId, CardInfo);
                }
                else {
                    CardInfo cardInfo = new CardInfo();
                    cardInfo.setModelName(infoData);
                    info.put(cardId, cardInfo);
                }
            }
        }

        return hashMapCardInfo(info);
    }

    private List findMatchData(Matcher matcher) {
        List<String> result = new ArrayList<>();
        result.add(matcher.group(1));
        result.add(matcher.group(2));
        return result;
    }

}
