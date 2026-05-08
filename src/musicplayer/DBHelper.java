package musicplayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHelper {
    private static final String URL = "jdbc:sqlite:musicplayer.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createSongsTable = """
            CREATE TABLE IF NOT EXISTS songs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                artist TEXT,
                album TEXT,
                file_path TEXT NOT NULL UNIQUE,
                duration INTEGER DEFAULT 0,
                is_favorite INTEGER DEFAULT 0
            );
        """;

        String createHistoryTable = """
            CREATE TABLE IF NOT EXISTS play_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                song_id INTEGER NOT NULL,
                played_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(song_id) REFERENCES songs(id)
            );
        """;

        String createPlaylistsTable = """
            CREATE TABLE IF NOT EXISTS playlists (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
        """;

        String createPlaylistSongsTable = """
            CREATE TABLE IF NOT EXISTS playlist_songs (
                playlist_id INTEGER NOT NULL,
                song_id INTEGER NOT NULL,
                PRIMARY KEY (playlist_id, song_id),
                FOREIGN KEY(playlist_id) REFERENCES playlists(id),
                FOREIGN KEY(song_id) REFERENCES songs(id)
            );
        """;

        String createSettingsTable = """
            CREATE TABLE IF NOT EXISTS app_settings (
                setting_key TEXT PRIMARY KEY,
                setting_value TEXT
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createSongsTable);
            stmt.execute(createHistoryTable);
            stmt.execute(createPlaylistsTable);
            stmt.execute(createPlaylistSongsTable);
            stmt.execute(createSettingsTable);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}