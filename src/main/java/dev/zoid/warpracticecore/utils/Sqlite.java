package dev.zoid.warpracticecore.utils;

import org.bukkit.plugin.java.JavaPlugin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Sqlite {
    private final String dbPath;
    private final JavaPlugin plugin;
    private final Object lock = new Object();

    public Sqlite(JavaPlugin plugin) throws SQLException {
        this.plugin = plugin;
        this.dbPath = plugin.getDataFolder().getPath() + "/tiers.db";
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS tiers (player TEXT PRIMARY KEY, tier TEXT NOT NULL)");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public synchronized String getTier(String player) {
        String sql = "SELECT tier FROM tiers WHERE player = ?;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, player);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tier");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting tier for " + player + ": " + e.getMessage());
        }
        return null;
    }

    public CompletableFuture<Boolean> setTierAsync(String player, String tier) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "INSERT OR REPLACE INTO tiers (player, tier) VALUES (?, ?);";
            synchronized (lock) {
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, player);
                    pstmt.setString(2, tier);
                    pstmt.executeUpdate();
                    future.complete(true);
                } catch (SQLException e) {
                    plugin.getLogger().severe("Error setting tier for " + player + ": " + e.getMessage());
                    future.complete(false);
                }
            }
        });
        return future;
    }

    public synchronized List<String> getPlayersLike(String partial) {
        List<String> players = new ArrayList<>();
        String sql = "SELECT player FROM tiers WHERE player LIKE ? LIMIT 10;";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, partial + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    players.add(rs.getString("player"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error fetching player suggestions: " + e.getMessage());
        }
        return players;
    }

    public CompletableFuture<Boolean> removeTierAsync(String player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "DELETE FROM tiers WHERE player = ?;";
            synchronized (lock) {
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, player);
                    pstmt.executeUpdate();
                    future.complete(true);
                } catch (SQLException e) {
                    plugin.getLogger().severe("Error removing tier for " + player + ": " + e.getMessage());
                    future.complete(false);
                }
            }
        });
        return future;
    }

    public void close() {
    }
}
