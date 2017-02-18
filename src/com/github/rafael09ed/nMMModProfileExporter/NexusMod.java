package com.github.rafael09ed.nMMModProfileExporter;

/**
 * EGR 283 B01
 * NexusMod.java
 * Purpose:
 *
 * @author Rafael
 * @version 1.0 2/17/2017
 */
public class NexusMod {
    private final String modId, modName, modVersion;

    public NexusMod(String modId, String modName, String modVersion) {
        this.modId = modId;
        this.modName = modName;
        this.modVersion = modVersion;
    }

    public String getModId() {
        return modId;
    }

    public String getModName() {
        return modName;
    }

    public String getModVersion() {
        return modVersion;
    }

    public String getModURL(String gameName) {
        return "http://www.nexusmods.com/"
                + gameName.replaceAll("\\s+", "").toLowerCase()
                + "/mods/" + modId;
    }
}
