package com.github.rafael09ed.nMMModProfileExporter;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.github.rafael09ed.nMMModProfileExporter.ModProfile.getMods;
import static com.github.rafael09ed.nMMModProfileExporter.ModProfile.getProfileName;

/**
 * EGR 283 B01
 * PathFinder.java
 * Purpose:
 *
 * @author Rafael
 * @version 1.0 2/17/2017
 */
public class PathFinder {
    private final static String
            NEXUS_MOD_MANAGER_FOLDER_NAME = "Nexus Mod Manager",
            MOD_PROFILE_SUBPATH = "Mods\\ModProfiles";
    private final static List<String> LIKELY_PARENT_PATHS = Arrays.asList("Program Files", "Games");

    public static List<ModProfile> findModProfiles() {
        List<ModProfile> profiles = new LinkedList<>();
        for (String path : findPaths()) {
            try {
                profiles.add(new ModProfile(findGameNameFromPath(path), path, getProfileName(path), getMods(path)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profiles;
    }

    public static List<ModProfile> findModProfiles(String pathIn) {
        List<ModProfile> profiles = new LinkedList<>();
        List<String> pathsToTry = new LinkedList<>();
        pathsToTry.addAll(parentPathsToTry(pathIn));
        for (String path : pathsToTry) {
            try {
                profiles.add(new ModProfile(findGameNameFromPath(path), path, getProfileName(path), getMods(path)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profiles;
    }

    private static String findGameNameFromPath(String path) {
        List<String> pathParts = Arrays.asList(path.split("\\\\"));
        int gameNameIndex = pathParts.indexOf(NEXUS_MOD_MANAGER_FOLDER_NAME) + 1;
        if (pathParts.size() - 1 >= gameNameIndex)
            return pathParts.get(gameNameIndex);
        return null;
    }

    static List<String> findPaths() {
        List<String> validPaths = new LinkedList<>();
        for (File file : File.listRoots()) {
            for (String parentPath : LIKELY_PARENT_PATHS) {
                String path = file.getPath() + parentPath + "\\" + NEXUS_MOD_MANAGER_FOLDER_NAME;
                //System.out.println("path1: " + path);
                File[] directories = new File(path).listFiles(File::isDirectory);
                validPaths.addAll(findSubPaths(path));
            }
        }
        return validPaths;
    }

    static List<String> findSubPaths(String path) {
        List<String> pathList = new LinkedList<>();
        File[] directories = new File(path).listFiles(File::isDirectory);
        if (directories != null) { // implies directory exists
            for (File directory : directories) {
                String path2 = directory.getAbsolutePath() + "\\" + MOD_PROFILE_SUBPATH;
                //System.out.println("   Path2: " + path2);
                File[] profileDirectories = new File(path2).listFiles(File::isDirectory);
                if (profileDirectories != null && profileDirectories.length > 0)
                    for (File profileDirectory : profileDirectories)
                        if (ModProfile.containsModList(profileDirectory.getAbsolutePath()))
                            pathList.add(profileDirectory.getAbsolutePath());
            }
        }
        return pathList;
    }

    static List<String> parentPathsToTry(String givenPath) {
        List<String> paths = new LinkedList<>();
        if (givenPath.endsWith("\\")) {
            givenPath = givenPath.substring(0, givenPath.length() - 1);
        }
        if (ModProfile.containsModList(givenPath))
            paths.add(givenPath);
        paths.addAll(findSubPaths(givenPath));
        return paths;
    }

}
