package com.github.rafael09ed.nMMModProfileExporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Rafael
 * @version 1.0 2/17/2017
 */
public class TextOutputFormater {
    private final static String
            REGEX = "\\$[\\w]*\\b";
    private final static String
            MOD_NAME_SIMPLE = "$modsimplename",
            MOD_NAME = "$modname",
            MOD_ID = "$modid",
            MOD_NEXUS_LINK = "$modurl",
            MOD_VERSION = "$modversion";
    public final static String DEMO_VALUE =
            "Mod Name Simplified: " + MOD_NAME_SIMPLE + '\n' +
                    "Full Mod Name:       " + MOD_NAME + '\n' +
                    "Nexus Mod ID:        " + MOD_ID + '\n' +
                    "Nexus Download Link: " + MOD_NEXUS_LINK + '\n' +
                    "Mod Version:         " + MOD_VERSION + "\n\n",
            MARKDOWN_VALUE = "[" + MOD_NAME_SIMPLE + "](" + MOD_NEXUS_LINK + ")\n\n";

    public static String makeTextOutput(String layout, ModProfile profile) {
        //StringBuilder output = new StringBuilder();
        List<String> list = splitAroundRegex(layout, REGEX);
        return profile.getMods().parallelStream().map(mod -> {
            boolean skip = false;
            StringBuilder output = new StringBuilder();
            for (String s : list) {
                if (skip = !skip) {
                    output.append(s);
                } else {
                    switch (s.toLowerCase()) {
                        case MOD_NAME_SIMPLE:
                            output.append(simpleTitle(mod.getModName()));
                            break;
                        case MOD_NAME:
                            output.append(mod.getModName());
                            break;
                        case MOD_ID:
                            output.append(mod.getModId());
                            break;
                        case MOD_NEXUS_LINK:
                            output.append(mod.getModURL(profile.getGameName().replaceAll("\\s+", "")));
                            break;
                        case MOD_VERSION:
                            output.append(mod.getModVersion());
                            break;
                        default:
                            output.append(s);
                            break;
                    }
                }
            }
            return output.toString();
        }).collect(Collectors.joining());
    }

    private static String simpleTitle(String modTitle) {
        List<String> list = new ArrayList<>(Arrays.asList(modTitle.split(" - "))), newList = new LinkedList<>();
        if (list.size() == 1)
            return modTitle;
        for (String potentialTitleSegment : list) {
            boolean contains = false;
            for (int i = 0; i < newList.size(); i++) {
                String assignedTitleValue = newList.get(i);
                if (prepCompare(potentialTitleSegment).contains(prepCompare(assignedTitleValue))) {
                    newList.set(i, potentialTitleSegment);
                    contains = true;
                    break;
                }
            }
            if (!contains)
                newList.add(potentialTitleSegment);
        }
        return String.join(" - ", newList);
    }

    private static String prepCompare(String string) {
        return string.trim().toLowerCase();
    }

    private static List<String> splitAroundRegex(String toSplit, String regex) {
        List<String> list = new LinkedList<>();
        String[] parts = toSplit.split(regex);
        Matcher matcher = Pattern.compile(regex).matcher(toSplit);
        int i = 0, l = 0;
        while (matcher.find()) {
            l += parts[i].length() + matcher.group().length();
            list.add(parts[i++]);
            list.add(matcher.group());
        }
        if (toSplit.length() != l)
            list.add(parts[i]);
        return list;
    }
}
