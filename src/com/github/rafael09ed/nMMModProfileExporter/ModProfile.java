package com.github.rafael09ed.nMMModProfileExporter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * EGR 283 B01
 * ModProfile.java
 * Purpose:
 *
 * @author Rafael
 * @version 1.0 2/16/2017
 */
public class ModProfile {
    private final static String PROFILE_FILE_NAME = "profile.xml", MOD_LIST_FILE_NAME = "modlist.xml";
    private final static String MOD_ID_STRING = "modId", MOD_NAME_STRING = "modName", MOD_VERSION_STRING = "FileVersion";
    private final static List<String> modTagsToGrab = Arrays.asList(MOD_ID_STRING, MOD_NAME_STRING, MOD_VERSION_STRING);

    private final String gameName, profilePath, profileName;
    private final List<NexusMod> mods;

    public ModProfile(String gameName, String profilePath, String profileName, List<NexusMod> mods) {
        this.gameName = gameName;
        this.profilePath = profilePath;
        this.profileName = profileName;
        this.mods = mods;
    }

    public String getGameName() {
        return gameName;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getProfileName() {
        return profileName;
    }

    public List<NexusMod> getMods() {
        return mods;
    }

    public static String getProfileName(String path) {
        try {
            File inputFile = new File(path + "\\" + PROFILE_FILE_NAME);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc.getDocumentElement().getAttribute("profileName");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<NexusMod> getMods(String path) {
        List<NexusMod> mods = new ArrayList<>();
        try {
            for (Map<String, String> xmlNodes : getXMLNodes(path + "\\" + MOD_LIST_FILE_NAME, "modInfo", modTagsToGrab)) {
                mods.add(new NexusMod(
                        xmlNodes.get(MOD_ID_STRING),
                        xmlNodes.get(MOD_NAME_STRING),
                        xmlNodes.get(MOD_VERSION_STRING)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mods;
    }

    private static List<Map<String, String>> getXMLNodes(String path, String tag, List<String> attribute) throws ParserConfigurationException, IOException, SAXException {
        List<Map<String, String>> listMap = new ArrayList<>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(tag);
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Map<String, String> modMap = new HashMap<>();
                for (String attributeString : attribute)
                    modMap.put(attributeString, eElement.getAttribute(attributeString));
                listMap.add(modMap);
            }
        }
        return listMap;
    }
    public static boolean containsModList(String path){
        return new File(path + "\\" + MOD_LIST_FILE_NAME).exists();
    }
}
