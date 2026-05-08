package musicplayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongDAO {

    public void addSong(Song song) {
        String sql = """
            INSERT OR IGNORE INTO songs(title, artist, album, file_path, duration, is_favorite)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getArtist());
            pstmt.setString(3, song.getAlbum());
            pstmt.setString(4, song.getFilePath());
            pstmt.setInt(5, song.getDuration());
            pstmt.setInt(6, song.isFavorite() ? 1 : 0);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Song getSongByFilePath(String filePath) {
        String sql = "SELECT * FROM songs WHERE file_path = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, filePath);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Song(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    rs.getString("file_path"),
                    rs.getInt("duration"),
                    rs.getInt("is_favorite") == 1
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM songs ORDER BY title";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                songs.add(new Song(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    rs.getString("file_path"),
                    rs.getInt("duration"),
                    rs.getInt("is_favorite") == 1
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public void markFavorite(int songId, boolean favorite) {
        String sql = "UPDATE songs SET is_favorite = ? WHERE id = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, favorite ? 1 : 0);
            pstmt.setInt(2, songId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayHistory(int songId) {
        String sql = "INSERT INTO play_history(song_id) VALUES (?)";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, songId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getRecentHistory() {
        List<String> history = new ArrayList<>();
        String sql = """
            SELECT s.title, s.artist, h.played_at
            FROM play_history h
            JOIN songs s ON h.song_id = s.id
            ORDER BY h.played_at DESC
            LIMIT 10
        """;

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String row = rs.getString("title") + " - "
                           + rs.getString("artist") + " at "
                           + rs.getString("played_at");
                history.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public void deleteSong(int songId) {
        String deleteHistorySql = "DELETE FROM play_history WHERE song_id = ?";
        String deletePlaylistSongsSql = "DELETE FROM playlist_songs WHERE song_id = ?";
        String deleteSongSql = "DELETE FROM songs WHERE id = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt1 = conn.prepareStatement(deleteHistorySql);
             PreparedStatement pstmt2 = conn.prepareStatement(deletePlaylistSongsSql);
             PreparedStatement pstmt3 = conn.prepareStatement(deleteSongSql)) {

            pstmt1.setInt(1, songId);
            pstmt1.executeUpdate();

            pstmt2.setInt(1, songId);
            pstmt2.executeUpdate();

            pstmt3.setInt(1, songId);
            pstmt3.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlaylist(String playlistName) {
        String sql = "INSERT OR IGNORE INTO playlists(name) VALUES (?)";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playlistName);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllPlaylists() {
        List<String> playlists = new ArrayList<>();
        String sql = "SELECT name FROM playlists ORDER BY name";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                playlists.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }

    public Integer getPlaylistIdByName(String playlistName) {
        String sql = "SELECT id FROM playlists WHERE name = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playlistName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addSongToPlaylist(String playlistName, int songId) {
        Integer playlistId = getPlaylistIdByName(playlistName);
        if (playlistId == null) {
            return;
        }

        String sql = "INSERT OR IGNORE INTO playlist_songs(playlist_id, song_id) VALUES (?, ?)";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, songId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Song> getSongsInPlaylist(String playlistName) {
        List<Song> songs = new ArrayList<>();
        Integer playlistId = getPlaylistIdByName(playlistName);

        if (playlistId == null) {
            return songs;
        }

        String sql = """
            SELECT s.*
            FROM songs s
            JOIN playlist_songs ps ON s.id = ps.song_id
            WHERE ps.playlist_id = ?
            ORDER BY s.title
        """;

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                songs.add(new Song(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("artist"),
                    rs.getString("album"),
                    rs.getString("file_path"),
                    rs.getInt("duration"),
                    rs.getInt("is_favorite") == 1
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public void removeSongFromPlaylist(String playlistName, int songId) {
        Integer playlistId = getPlaylistIdByName(playlistName);
        if (playlistId == null) {
            return;
        }

        String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, songId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlaylist(String playlistName) {
        Integer playlistId = getPlaylistIdByName(playlistName);
        if (playlistId == null) {
            return;
        }

        String deletePlaylistSongsSql = "DELETE FROM playlist_songs WHERE playlist_id = ?";
        String deletePlaylistSql = "DELETE FROM playlists WHERE id = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt1 = conn.prepareStatement(deletePlaylistSongsSql);
             PreparedStatement pstmt2 = conn.prepareStatement(deletePlaylistSql)) {

            pstmt1.setInt(1, playlistId);
            pstmt1.executeUpdate();

            pstmt2.setInt(1, playlistId);
            pstmt2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void renamePlaylist(String oldName, String newName) {
        String sql = "UPDATE playlists SET name = ? WHERE name = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setString(2, oldName);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSetting(String key, String value) {
        String sql = """
            INSERT INTO app_settings(setting_key, setting_value)
            VALUES (?, ?)
            ON CONFLICT(setting_key) DO UPDATE SET setting_value = excluded.setting_value
        """;

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSetting(String key) {
        String sql = "SELECT setting_value FROM app_settings WHERE setting_key = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, key);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("setting_value");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}