package dev.zoid.warpracticecore;

import dev.zoid.warpracticecore.commands.*;
import dev.zoid.warpracticecore.commands.tpa.TpaCommands;
import dev.zoid.warpracticecore.design.Chat;
import dev.zoid.warpracticecore.events.*;
import dev.zoid.warpracticecore.placeholders.TierPlaceholder;
import dev.zoid.warpracticecore.storage.SpawnData;
import dev.zoid.warpracticecore.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarPracticeCore extends JavaPlugin {

    private Sqlite sqlite;
    private TierUtil tierUtil;
    private static WarPracticeCore instance;

    @Override
    public void onEnable() {
        instance = this;
        initialize();
        register();
        loadWorlds();
        setupPlaceholders();
        startTasks();
    }

    private void initialize() {
        initializeDatabase();
        SpawnData.init();
        CommandRunner.init(this);
        Region.initialize(this);
        Region.loadRegions();
        RtpQueueUtil.preloadRandomLocations();
        RtpUtil.initialize(this);
    }

    private void register() {
        registerCommands();
        registerEvents();
    }

    private void loadWorlds() {
        WorldUtil.loadWorld("spawn");
        WorldUtil.loadWorld("desert");
        WorldUtil.loadWorld("badlands");
        WorldUtil.loadWorld("plains");
        WorldUtil.loadWorld("flat");
        WorldUtil.loadWorld("box");
    }

    private void setupPlaceholders() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TierPlaceholder(tierUtil).register();
        } else {
            getLogger().warning("PlaceholderAPI not found! Tier placeholders will not work.");
        }
    }

    private void startTasks() {
        getServer().getPluginManager().registerEvents(new DeathEvent(tierUtil), this);
        EntityClear entityClear = new EntityClear(this);
        entityClear.startClearSchedule();
    }

    private void registerCommands() {
        CommandManager.register(this,
                "setspawn", SetSpawnCommand.class,
                "discord", DiscordCommand.class,
                "flat", FlatCommand.class,
                "box", BoxCommand.class,
                "spawn", SpawnCommand.class,
                "ping", PingCommand.class,
                "rtp", RtpCommand.class,
                "rtpqueue", RtpQueueCommand.class,
                "tpa", TpaCommands.class,
                "tpahere", TpaCommands.class,
                "tpacancel", TpaCommands.class,
                "tpdeny", TpaCommands.class,
                "tpaccept", TpaCommands.class,
                "back", TpaCommands.class,
                "tier", TierCommand.class,
                "tierlist", TierlistCommand.class,
                "region", RegionCommand.class,
                "broadcast", BroadcastCommand.class
        );
        getCommand("tier").setTabCompleter(new dev.zoid.warpracticecore.commands.TierTabCompleter(tierUtil));
    }

    private void registerEvents() {
        EventManager.register(this,
                JoinEvent.class,
                LeaveEvent.class,
                BlockProtection.class,
                PvPProtection.class,
                EnderPearlProtection.class,
                RtpQueueUtil.class,
                RespawnEvent.class,
                CreatureSummon.class,
                Chat.class,
                InventoryEvents.class,
                RegionEventListener.class,
                RegionCommand.class
        );
    }

    private void initializeDatabase() {
        try {
            sqlite = new Sqlite(this);
            tierUtil = new TierUtil(sqlite, this);
        } catch (Exception e) {
            getLogger().severe("Error initializing SQLite. See stack trace for details.");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        shutdown();
    }

    private void shutdown() {
        if (sqlite != null) {
            sqlite.close();
        }
        instance = null;
        Region.saveRegions();
        EntityClear entityClear = new EntityClear(this);
        entityClear.stopClearSchedule();
    }

    public static WarPracticeCore plugin() {
        return instance;
    }

    public TierUtil getTierUtil() {
        return tierUtil;
    }
}