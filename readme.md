# Nexus Mod Manager Mod Profile Exporter
**Author:** Rafael09ED

Preview: https://gfycat.com/SafeGlisteningAnaconda


* Automatically finds mod profiles and lists them
    * Optional method of entering paths if your mod profile isn't listed
* The format of the list can be editing in the layout panel where mod information is specified using keywords denoted by a leading $.
    * There is a quick preset for loading the markdown format for sharing on Reddit
    * Automatically updates output mod list when the format is edited.
* Option for using a simplified mod name that removes repeated parts.
* Button for copying the generated mod list.

----

Here are some example outputs:

**Demo Preset:**

    Mod Name Simplified: $modSimpleName
    Full Mod Name:       $modName
    Nexus Mod ID:        $modID
    Nexus Download Link: $modURL
    Mod Version:         $modVersion

Generates:

    Mod Name Simplified: Snap'n Build v1.9
    Full Mod Name:       Snap'n Build - Snap'n Build v1.9
    Nexus Mod ID:        7393
    Nexus Download Link: http://www.nexusmods.com/fallout4/mods/7393
    Mod Version:         1.9    

**Markdown Preset:**

    [$modsimplename]($modurl)

Generates:

    [Snap'n Build v1.9](http://www.nexusmods.com/fallout4/mods/7393)

These outputs will be generated for each mod in the profile's mod list. The use of the presets are not required and the format can be edited at anytime.

----

Download: https://github.com/Rafael09ED/NMMModProfileExporter/releases

### Version History
**1.0 - Initial Release**
**1.1 - Custom Nexus URL Sub Paths for games**