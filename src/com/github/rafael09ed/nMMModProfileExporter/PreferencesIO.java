package com.github.rafael09ed.nMMModProfileExporter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * PreferencesIO.java
 *
 * @author Rafael
 * @version 1.0 3/19/2017
 */
public class PreferencesIO {
    private final static String PREFERENCES_FILE_PATH = new File(".").getAbsolutePath() + "\\NMMProfileExporterPreferences.txt";
    private final Map<String, String> gameToUrlPath = new HashMap<>();
    private final static String HEADER__GAME_TO_URL_PATH = "#GameToNexusURLS";

    public PreferencesIO() {
        if (new File(PREFERENCES_FILE_PATH).exists())
            try (BufferedReader br = new BufferedReader(new FileReader(PREFERENCES_FILE_PATH))) {
                String line;
                while ((line = br.readLine()) != null) {
                    switch (line.trim()) {
                        case HEADER__GAME_TO_URL_PATH:
                            while ((line = br.readLine()) != null && !line.trim().equals("")) {
                                String[] vals = line.split(" ");
                                if (vals.length >= 2)
                                    gameToUrlPath.put(vals[0], vals[1]);
                            }
                            break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        else {
            gameToUrlPath.put("skyrimse", "skyrimspecialedition");
        }

    }

    public String getUrlFromGamePath(String gamePath) {
        return gameToUrlPath.get(gamePath.toLowerCase());
    }

    public void setUrlForGamePath(String game, String url) {
        if (url.trim().equals(""))
            url = null;
        gameToUrlPath.put(game, url);
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PREFERENCES_FILE_PATH))) {
            bw.write(HEADER__GAME_TO_URL_PATH);
            bw.newLine();

            for (String key : gameToUrlPath.keySet()) {
                bw.write(key + " " + gameToUrlPath.get(key));
                bw.newLine();
            }
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
