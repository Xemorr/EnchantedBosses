package me.xemor.enchantedbosses;

import com.fasterxml.jackson.annotation.JsonAlias;
import me.xemor.configurationdata.JsonPropertyWithDefault;

public class LanguageConfig {

    @JsonPropertyWithDefault
    @JsonAlias("no_permission")
    private String noPermission = "<dark_red>You do not have permission to do this!";

    @JsonPropertyWithDefault
    @JsonAlias("invalid_boss")
    private String invalidBoss = "<dark_red>You have entered an invalid boss name!";

    @JsonPropertyWithDefault
    @JsonAlias("invalid_player")
    private String invalidPlayer = "<dark_red>You have entered an invalid player name!";

    @JsonPropertyWithDefault
    @JsonAlias("invalid_location")
    private String invalidLocation = "<dark_red>You have entered an invalid location";

    @JsonPropertyWithDefault
    @JsonAlias("not_enough_arguments")
    private String notEnoughArguments = "<dark_red>You have not entered enough arguments!";

    @JsonPropertyWithDefault
    @JsonAlias("boss_spawned")
    private String bossSpawned = "A <name> has spawned at <x>, <y>, <z> in world <world>!";

    @JsonPropertyWithDefault
    @JsonAlias("boss_time")
    private String bossTime = "A boss will spawn in <hour> hours, <minutes> minutes and <seconds> seconds!";

    @JsonPropertyWithDefault
    @JsonAlias("no_boss_time")
    private String noBossTime = "Bosses are not currently spawning on a timer!";

    @JsonPropertyWithDefault
    @JsonAlias("boss_spawn_fail")
    private String bossSpawnFail = "*CRACK* The boss egg failed for some reason!";

    @JsonPropertyWithDefault
    @JsonAlias("boss_death_broadcast")
    private String bossDeathBroadcast = "A <boss> has been killed by <player>";

    public String getNoPermission() {
        return noPermission;
    }

    public String getInvalidBoss() {
        return invalidBoss;
    }

    public String getInvalidPlayer() {
        return invalidPlayer;
    }

    public String getInvalidLocation() {
        return invalidLocation;
    }

    public String getBossSpawned() {
        return bossSpawned;
    }

    public String getBossTime() {
        return bossTime;
    }

    public String getNotEnoughArguments() {
        return notEnoughArguments;
    }

    public String getNoBossTime() {
        return noBossTime;
    }

    public String getBossSpawnFail() {
        return bossSpawnFail;
    }

    public String getBossDeathBroadcast() {
        return bossDeathBroadcast;
    }
}
