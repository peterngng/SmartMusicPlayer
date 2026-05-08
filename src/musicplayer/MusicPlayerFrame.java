package musicplayer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MusicPlayerFrame extends JFrame {
    private SongDAO dao;
    private DefaultListModel<Song> songListModel;
    private JList<Song> songList;

    private DefaultListModel<String> playlistListModel;
    private JList<String> playlistList;

    private Clip currentClip;
    private Song currentSong;
    private boolean isPaused = false;
    private boolean manuallyStopped = false;

    private JLabel nowPlayingLabel;
    private JLabel currentTimeLabel;
    private JLabel totalTimeLabel;
    private JProgressBar progressBar;
    private JTextField searchField;
    private JCheckBox favoritesOnlyCheckBox;
    private JTextArea songDetailsArea;
    private JSlider volumeSlider;

    private String sortMode = "title";
    private boolean showingPlaylistSongs = false;
    private String currentPlaylistName = null;

    private ImageIcon appIcon;
    private ImageIcon dialogIcon;

    public MusicPlayerFrame() {
        dao = new SongDAO();

        appIcon = new ImageIcon("./app_icon.png");

        if (appIcon.getIconWidth() > 0) {
            Image windowImage = appIcon.getImage();
            setIconImage(windowImage);

            Image smallDialogImage = windowImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            dialogIcon = new ImageIcon(smallDialogImage);
        } else {
            System.out.println("app_icon.png not found.");
            dialogIcon = null;
        }

        setTitle("Smart Music Player");
        setSize(1400, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);

        playlistListModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistListModel);

        nowPlayingLabel = new JLabel("Now Playing: None");
        currentTimeLabel = new JLabel("00:00");
        totalTimeLabel = new JLabel("00:00");

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        searchField = new JTextField();
        favoritesOnlyCheckBox = new JCheckBox("Show Favorites Only");

        songDetailsArea = new JTextArea();
        songDetailsArea.setEditable(false);
        songDetailsArea.setLineWrap(true);
        songDetailsArea.setWrapStyleWord(true);
        songDetailsArea.setText("Select a song to see details.");

        volumeSlider = new JSlider(0, 100, 80);

        JButton addSongButton = new JButton("Add Song");
        JButton refreshButton = new JButton("Refresh");
        JButton favoriteButton = new JButton("Toggle Favorite");
        JButton historyButton = new JButton("Show History");
        JButton scanFolderButton = new JButton("Scan Folder");
        JButton playButton = new JButton("Play");
        JButton stopButton = new JButton("Stop");
        JButton pauseResumeButton = new JButton("Pause / Resume");
        JButton deleteSongButton = new JButton("Delete Song");
        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JButton sortTitleButton = new JButton("Sort By Title");
        JButton sortArtistButton = new JButton("Sort By Artist");
        JButton clearSearchButton = new JButton("Clear Search");

        JButton createPlaylistButton = new JButton("Create Playlist");
        JButton addToPlaylistButton = new JButton("Add To Playlist");
        JButton showAllSongsButton = new JButton("Show All Songs");
        JButton removeFromPlaylistButton = new JButton("Remove From Playlist");
        JButton deletePlaylistButton = new JButton("Delete Playlist");
        JButton renamePlaylistButton = new JButton("Rename Playlist");
        JButton exportPlaylistButton = new JButton("Export Playlist");
        JButton importPlaylistButton = new JButton("Import Playlist");

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("Search by title or artist: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel searchRightPanel = new JPanel(new GridLayout(1, 2));
        searchRightPanel.add(clearSearchButton);
        searchRightPanel.add(favoritesOnlyCheckBox);
        searchPanel.add(searchRightPanel, BorderLayout.EAST);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nowPlayingLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4));
        buttonPanel.add(addSongButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(favoriteButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(scanFolderButton);
        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(pauseResumeButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(sortTitleButton);
        buttonPanel.add(sortArtistButton);
        buttonPanel.add(createPlaylistButton);
        buttonPanel.add(addToPlaylistButton);
        buttonPanel.add(showAllSongsButton);
        buttonPanel.add(removeFromPlaylistButton);
        buttonPanel.add(renamePlaylistButton);
        buttonPanel.add(deletePlaylistButton);
        buttonPanel.add(exportPlaylistButton);
        buttonPanel.add(importPlaylistButton);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(currentTimeLabel, BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(totalTimeLabel, BorderLayout.EAST);

        JPanel volumePanel = new JPanel(new BorderLayout());
        volumePanel.add(new JLabel("Volume"), BorderLayout.WEST);
        volumePanel.add(volumeSlider, BorderLayout.CENTER);

        JPanel bottomCenterPanel = new JPanel(new BorderLayout());
        bottomCenterPanel.add(progressPanel, BorderLayout.NORTH);
        bottomCenterPanel.add(volumePanel, BorderLayout.CENTER);
        bottomCenterPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bottomCenterPanel, BorderLayout.CENTER);
        bottomPanel.add(deleteSongButton, BorderLayout.EAST);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.add(new JLabel("Song Details"), BorderLayout.NORTH);
        detailsPanel.add(new JScrollPane(songDetailsArea), BorderLayout.CENTER);

        JPanel playlistPanel = new JPanel(new BorderLayout());
        playlistPanel.add(new JLabel("Playlists"), BorderLayout.NORTH);
        playlistPanel.add(new JScrollPane(playlistList), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(playlistPanel, BorderLayout.WEST);
        add(new JScrollPane(songList), BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        addSongButton.addActionListener(e -> addSong());
        refreshButton.addActionListener(e -> {
            loadPlaylists();
            restoreSavedSettings();
            reloadCurrentView();
        });
        favoriteButton.addActionListener(e -> toggleFavorite());
        historyButton.addActionListener(e -> showHistory());
        scanFolderButton.addActionListener(e -> scanFolderInBackground());
        playButton.addActionListener(e -> playSelectedSong());
        stopButton.addActionListener(e -> stopCurrentSong());
        pauseResumeButton.addActionListener(e -> pauseOrResumeSong());
        deleteSongButton.addActionListener(e -> deleteSelectedSong());
        previousButton.addActionListener(e -> playPreviousSong());
        nextButton.addActionListener(e -> playNextSong());

        sortTitleButton.addActionListener(e -> {
            sortMode = "title";
            reloadCurrentView();
        });

        sortArtistButton.addActionListener(e -> {
            sortMode = "artist";
            reloadCurrentView();
        });

        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            reloadCurrentView();
        });

        createPlaylistButton.addActionListener(e -> createPlaylist());
        addToPlaylistButton.addActionListener(e -> addSelectedSongToPlaylist());
        showAllSongsButton.addActionListener(e -> showAllSongsView());
        removeFromPlaylistButton.addActionListener(e -> removeSelectedSongFromPlaylist());
        deletePlaylistButton.addActionListener(e -> deleteSelectedPlaylist());
        renamePlaylistButton.addActionListener(e -> renameSelectedPlaylist());
        exportPlaylistButton.addActionListener(e -> exportSelectedPlaylist());
        importPlaylistButton.addActionListener(e -> importPlaylistFromTextFile());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                reloadCurrentView();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                reloadCurrentView();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                reloadCurrentView();
            }
        });

        favoritesOnlyCheckBox.addActionListener(e -> reloadCurrentView());

        songList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateSongDetails();
                }
            }
        });

        songList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Song selectedSong = songList.getSelectedValue();
                    if (selectedSong != null) {
                        playSong(selectedSong);
                    }
                }
            }
        });

        playlistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedPlaylist = playlistList.getSelectedValue();
                if (selectedPlaylist != null) {
                    showPlaylistSongs(selectedPlaylist);
                    dao.saveSetting("last_opened_playlist", selectedPlaylist);
                }
            }
        });

        volumeSlider.addChangeListener(e -> {
            updateVolume();
            dao.saveSetting("last_volume", String.valueOf(volumeSlider.getValue()));
        });

        progressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seekToMousePosition(e.getX());
            }
        });

        loadPlaylists();
        loadSongs();
        restoreSavedSettings();
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE,
            dialogIcon
        );
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Warning",
            JOptionPane.WARNING_MESSAGE,
            dialogIcon
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE,
            dialogIcon
        );
    }

    private int showConfirm(String message, String title) {
        return JOptionPane.showConfirmDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            dialogIcon
        );
    }

    private void seekToMousePosition(int mouseX) {
        if (currentClip == null || !currentClip.isOpen()) {
            showWarning("No song is loaded.");
            return;
        }

        int barWidth = progressBar.getWidth();
        if (barWidth <= 0) {
            return;
        }

        if (mouseX < 0) {
            mouseX = 0;
        }
        if (mouseX > barWidth) {
            mouseX = barWidth;
        }

        double clickPercent = (double) mouseX / barWidth;
        long totalMicroseconds = currentClip.getMicrosecondLength();
        long targetMicroseconds = (long) (clickPercent * totalMicroseconds);

        boolean wasRunning = currentClip.isRunning();

        currentClip.setMicrosecondPosition(targetMicroseconds);

        int currentSeconds = (int) (targetMicroseconds / 1_000_000);
        int totalSeconds = (int) (totalMicroseconds / 1_000_000);
        int progressValue = (int) (clickPercent * 100);

        currentTimeLabel.setText(formatTime(currentSeconds));
        totalTimeLabel.setText(formatTime(totalSeconds));
        progressBar.setValue(progressValue);

        if (wasRunning && !isPaused) {
            currentClip.start();
        }
    }

    private void restoreSavedSettings() {
        String savedVolume = dao.getSetting("last_volume");
        if (savedVolume != null) {
            try {
                volumeSlider.setValue(Integer.parseInt(savedVolume));
            } catch (NumberFormatException e) {
                volumeSlider.setValue(80);
            }
        }

        String lastPlaylist = dao.getSetting("last_opened_playlist");
        if (lastPlaylist != null && !lastPlaylist.isBlank() && dao.getAllPlaylists().contains(lastPlaylist)) {
            playlistList.setSelectedValue(lastPlaylist, true);
            showPlaylistSongs(lastPlaylist);
        }
    }

    private void addSong() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            String defaultTitle = file.getName();
            int dotIndex = defaultTitle.lastIndexOf('.');
            if (dotIndex > 0) {
                defaultTitle = defaultTitle.substring(0, dotIndex);
            }

            String title = JOptionPane.showInputDialog(this, "Enter song title:", defaultTitle);
            if (title == null) return;
            title = title.trim();
            if (title.isEmpty()) title = defaultTitle;

            String artist = JOptionPane.showInputDialog(this, "Enter artist name:", "Unknown Artist");
            if (artist == null) return;
            artist = artist.trim();
            if (artist.isEmpty()) artist = "Unknown Artist";

            String album = JOptionPane.showInputDialog(this, "Enter album name:", "Unknown Album");
            if (album == null) return;
            album = album.trim();
            if (album.isEmpty()) album = "Unknown Album";

            int duration = getAudioDurationSeconds(file);

            Song song = new Song(title, artist, album, file.getAbsolutePath(), duration, false);
            dao.addSong(song);
            reloadCurrentView();

            showInfo("Song added successfully.");
        }
    }

    private void loadPlaylists() {
        playlistListModel.clear();
        List<String> playlists = dao.getAllPlaylists();
        for (String playlist : playlists) {
            playlistListModel.addElement(playlist);
        }
    }

    private void loadSongs() {
        showingPlaylistSongs = false;
        currentPlaylistName = null;

        songListModel.clear();
        List<Song> songs = dao.getAllSongs();
        applySortAndFiltersToSongList(songs);
    }

    private void showPlaylistSongs(String playlistName) {
        showingPlaylistSongs = true;
        currentPlaylistName = playlistName;

        songListModel.clear();
        List<Song> songs = dao.getSongsInPlaylist(playlistName);
        applySortAndFiltersToSongList(songs);
    }

    private void applySortAndFiltersToSongList(List<Song> songs) {
        if ("artist".equals(sortMode)) {
            songs.sort(Comparator.comparing(song -> song.getArtist().toLowerCase()));
        } else {
            songs.sort(Comparator.comparing(song -> song.getTitle().toLowerCase()));
        }

        String keyword = searchField.getText().trim().toLowerCase();
        boolean favoritesOnly = favoritesOnlyCheckBox.isSelected();

        for (Song song : songs) {
            String title = song.getTitle().toLowerCase();
            String artist = song.getArtist().toLowerCase();

            boolean matchesKeyword = keyword.isEmpty()
                    || title.contains(keyword)
                    || artist.contains(keyword);

            boolean matchesFavorite = !favoritesOnly || song.isFavorite();

            if (matchesKeyword && matchesFavorite) {
                songListModel.addElement(song);
            }
        }

        updateSongDetails();
    }

    private void showAllSongsView() {
        playlistList.clearSelection();
        dao.saveSetting("last_opened_playlist", "");
        loadSongs();
    }

    private void reloadCurrentView() {
        if (showingPlaylistSongs && currentPlaylistName != null) {
            showPlaylistSongs(currentPlaylistName);
        } else {
            loadSongs();
        }
    }

    private void createPlaylist() {
        String playlistName = JOptionPane.showInputDialog(this, "Enter playlist name:");

        if (playlistName == null) return;
        playlistName = playlistName.trim();

        if (playlistName.isEmpty()) {
            showWarning("Playlist name cannot be empty.");
            return;
        }

        dao.createPlaylist(playlistName);
        loadPlaylists();
        showInfo("Playlist created successfully.");
    }

    private void addSelectedSongToPlaylist() {
        Song selectedSong = songList.getSelectedValue();
        if (selectedSong == null) {
            showWarning("Please select a song first.");
            return;
        }

        List<String> playlists = dao.getAllPlaylists();
        if (playlists.isEmpty()) {
            showWarning("Please create a playlist first.");
            return;
        }

        String selectedPlaylist = (String) JOptionPane.showInputDialog(
                this,
                "Choose a playlist:",
                "Add To Playlist",
                JOptionPane.PLAIN_MESSAGE,
                dialogIcon,
                playlists.toArray(),
                playlists.get(0)
        );

        if (selectedPlaylist != null) {
            dao.addSongToPlaylist(selectedPlaylist, selectedSong.getId());
            showInfo("Song added to playlist.");
            if (showingPlaylistSongs && selectedPlaylist.equals(currentPlaylistName)) {
                showPlaylistSongs(selectedPlaylist);
            }
        }
    }

    private void removeSelectedSongFromPlaylist() {
        if (!showingPlaylistSongs || currentPlaylistName == null) {
            showWarning("Please open a playlist first.");
            return;
        }

        Song selectedSong = songList.getSelectedValue();
        if (selectedSong == null) {
            showWarning("Please select a song in the playlist first.");
            return;
        }

        int choice = showConfirm(
            "Remove this song from playlist?\n" + selectedSong.getTitle(),
            "Remove From Playlist"
        );

        if (choice == JOptionPane.YES_OPTION) {
            dao.removeSongFromPlaylist(currentPlaylistName, selectedSong.getId());
            showPlaylistSongs(currentPlaylistName);
            showInfo("Song removed from playlist.");
        }
    }

    private void deleteSelectedPlaylist() {
        String selectedPlaylist = playlistList.getSelectedValue();
        if (selectedPlaylist == null) {
            showWarning("Please select a playlist first.");
            return;
        }

        int choice = showConfirm(
            "Delete this playlist?\n" + selectedPlaylist,
            "Delete Playlist"
        );

        if (choice == JOptionPane.YES_OPTION) {
            dao.deletePlaylist(selectedPlaylist);
            loadPlaylists();
            showAllSongsView();
            showInfo("Playlist deleted successfully.");
        }
    }

    private void renameSelectedPlaylist() {
        String selectedPlaylist = playlistList.getSelectedValue();
        if (selectedPlaylist == null) {
            showWarning("Please select a playlist first.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Enter new playlist name:", selectedPlaylist);
        if (newName == null) return;

        newName = newName.trim();
        if (newName.isEmpty()) {
            showWarning("Playlist name cannot be empty.");
            return;
        }

        dao.renamePlaylist(selectedPlaylist, newName);
        loadPlaylists();

        if (showingPlaylistSongs && selectedPlaylist.equals(currentPlaylistName)) {
            currentPlaylistName = newName;
            showPlaylistSongs(newName);
        }

        dao.saveSetting("last_opened_playlist", newName);
        showInfo("Playlist renamed successfully.");
    }

    private void exportSelectedPlaylist() {
        String selectedPlaylist = playlistList.getSelectedValue();
        if (selectedPlaylist == null) {
            showWarning("Please select a playlist first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(selectedPlaylist + ".txt"));
        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            List<Song> songs = dao.getSongsInPlaylist(selectedPlaylist);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("PLAYLIST:" + selectedPlaylist);
                writer.newLine();

                for (Song song : songs) {
                    writer.write(song.getTitle() + "|"
                            + song.getArtist() + "|"
                            + song.getAlbum() + "|"
                            + song.getFilePath() + "|"
                            + song.getDuration());
                    writer.newLine();
                }

                showInfo("Playlist exported successfully.");

            } catch (IOException e) {
                showError("Error exporting playlist.");
                e.printStackTrace();
            }
        }
    }

    private void importPlaylistFromTextFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String firstLine = reader.readLine();

                if (firstLine == null || !firstLine.startsWith("PLAYLIST:")) {
                    showError("Invalid playlist file.");
                    return;
                }

                String playlistName = firstLine.substring("PLAYLIST:".length()).trim();
                dao.createPlaylist(playlistName);

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 5) {
                        String title = parts[0];
                        String artist = parts[1];
                        String album = parts[2];
                        String filePath = parts[3];
                        int duration = 0;

                        try {
                            duration = Integer.parseInt(parts[4]);
                        } catch (NumberFormatException e) {
                            duration = 0;
                        }

                        Song existingSong = dao.getSongByFilePath(filePath);
                        if (existingSong == null) {
                            Song song = new Song(title, artist, album, filePath, duration, false);
                            dao.addSong(song);
                            existingSong = dao.getSongByFilePath(filePath);
                        }

                        if (existingSong != null) {
                            dao.addSongToPlaylist(playlistName, existingSong.getId());
                        }
                    }
                }

                loadPlaylists();
                playlistList.setSelectedValue(playlistName, true);
                showPlaylistSongs(playlistName);
                dao.saveSetting("last_opened_playlist", playlistName);

                showInfo("Playlist imported successfully.");

            } catch (IOException e) {
                showError("Error importing playlist.");
                e.printStackTrace();
            }
        }
    }

    private void updateSongDetails() {
        Song selectedSong = songList.getSelectedValue();

        if (selectedSong == null) {
            songDetailsArea.setText("Select a song to see details.");
            return;
        }

        String details =
                "Title: " + selectedSong.getTitle() + "\n\n" +
                "Artist: " + selectedSong.getArtist() + "\n\n" +
                "Album: " + selectedSong.getAlbum() + "\n\n" +
                "Duration: " + formatTime(selectedSong.getDuration()) + "\n\n" +
                "Favorite: " + (selectedSong.isFavorite() ? "Yes" : "No") + "\n\n" +
                "File Path:\n" + selectedSong.getFilePath();

        songDetailsArea.setText(details);
        songDetailsArea.setCaretPosition(0);
    }

    private void toggleFavorite() {
        Song selectedSong = songList.getSelectedValue();
        if (selectedSong == null) {
            showWarning("Please select a song first.");
            return;
        }

        dao.markFavorite(selectedSong.getId(), !selectedSong.isFavorite());
        reloadCurrentView();
    }

    private void showHistory() {
        List<String> history = dao.getRecentHistory();

        if (history.isEmpty()) {
            showWarning("No play history yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String row : history) {
            sb.append(row).append("\n");
        }

        JOptionPane.showMessageDialog(
            this,
            sb.toString(),
            "Recent Play History",
            JOptionPane.INFORMATION_MESSAGE,
            dialogIcon
        );
    }

    private void scanFolderInBackground() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();

            new Thread(() -> {
                scanFolder(folder);

                SwingUtilities.invokeLater(() -> {
                    reloadCurrentView();
                    showInfo("Folder scan complete.");
                });
            }).start();
        }
    }

    private void scanFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file);
            } else {
                String name = file.getName().toLowerCase();

                if (name.endsWith(".mp3") || name.endsWith(".wav")) {
                    String title = file.getName();
                    int dotIndex = title.lastIndexOf('.');
                    if (dotIndex > 0) {
                        title = title.substring(0, dotIndex);
                    }

                    int duration = getAudioDurationSeconds(file);

                    Song song = new Song(
                        title,
                        "Unknown Artist",
                        "Unknown Album",
                        file.getAbsolutePath(),
                        duration,
                        false
                    );
                    dao.addSong(song);
                }
            }
        }
    }

    private int getAudioDurationSeconds(File file) {
        String fileName = file.getName().toLowerCase();

        if (!fileName.endsWith(".wav")) {
            return 0;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            long frames = audioStream.getFrameLength();
            float frameRate = audioStream.getFormat().getFrameRate();

            if (frameRate > 0) {
                return (int) (frames / frameRate);
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void playSelectedSong() {
        Song selectedSong = songList.getSelectedValue();
        if (selectedSong == null) {
            showWarning("Please select a song first.");
            return;
        }

        playSong(selectedSong);
    }

    private void playSong(Song selectedSong) {
        String filePath = selectedSong.getFilePath().toLowerCase();
        if (!filePath.endsWith(".wav")) {
            showWarning("Only WAV File is supported right now.");
            return;
        }

        stopCurrentSongInternal(false);

        try {
            File audioFile = new File(selectedSong.getFilePath());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            currentClip = AudioSystem.getClip();
            currentClip.open(audioStream);

            currentClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (currentClip != null) {
                        long currentMicro = currentClip.getMicrosecondPosition();
                        long totalMicro = currentClip.getMicrosecondLength();

                        boolean finishedNaturally = !manuallyStopped && !isPaused && totalMicro > 0
                                && currentMicro >= totalMicro - 1000;

                        if (finishedNaturally) {
                            SwingUtilities.invokeLater(this::playNextSongAutomatically);
                        }
                    }
                }
            });

            manuallyStopped = false;
            currentClip.start();

            currentSong = selectedSong;
            isPaused = false;

            nowPlayingLabel.setText("Now Playing: " + selectedSong.getTitle() + " - " + selectedSong.getArtist());

            int totalSeconds = (int) (currentClip.getMicrosecondLength() / 1_000_000);
            totalTimeLabel.setText(formatTime(totalSeconds));
            currentTimeLabel.setText("00:00");
            progressBar.setValue(0);

            updateVolume();

            dao.addPlayHistory(selectedSong.getId());

            startProgressUpdater();

        } catch (UnsupportedAudioFileException e) {
            showError("Unsupported WAV file.");
            e.printStackTrace();
        } catch (IOException e) {
            showError("Error reading audio file.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            showError("Audio line unavailable.");
            e.printStackTrace();
        }
    }

    private void updateVolume() {
        if (currentClip == null) return;

        try {
            FloatControl gainControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);

            int sliderValue = volumeSlider.getValue();
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();

            float gain;
            if (sliderValue == 0) {
                gain = min;
            } else {
                gain = min + (max - min) * (sliderValue / 100.0f);
            }

            gainControl.setValue(gain);

        } catch (IllegalArgumentException e) {
        }
    }

    private void stopCurrentSong() {
        stopCurrentSongInternal(true);
    }

    private void stopCurrentSongInternal(boolean markManualStop) {
        if (markManualStop) {
            manuallyStopped = true;
        }

        if (currentClip != null) {
            if (currentClip.isRunning()) {
                currentClip.stop();
            }
            currentClip.close();
            currentClip = null;
        }

        currentSong = null;
        isPaused = false;
        nowPlayingLabel.setText("Now Playing: None");
        currentTimeLabel.setText("00:00");
        totalTimeLabel.setText("00:00");
        progressBar.setValue(0);
    }

    private void pauseOrResumeSong() {
        if (currentClip == null) {
            showWarning("No song is loaded.");
            return;
        }

        if (isPaused) {
            currentClip.start();
            isPaused = false;

            if (currentSong != null) {
                nowPlayingLabel.setText("Now Playing: " + currentSong.getTitle() + " - " + currentSong.getArtist());
            }
        } else {
            currentClip.stop();
            isPaused = true;

            if (currentSong != null) {
                nowPlayingLabel.setText("Paused: " + currentSong.getTitle() + " - " + currentSong.getArtist());
            }
        }
    }

    private void deleteSelectedSong() {
        Song selectedSong = songList.getSelectedValue();
        if (selectedSong == null) {
            showWarning("Please select a song first.");
            return;
        }

        int choice = showConfirm(
            "Are you sure you want to delete this song?\n" + selectedSong.getTitle(),
            "Confirm Delete"
        );

        if (choice == JOptionPane.YES_OPTION) {
            if (currentSong != null && selectedSong.getId() == currentSong.getId()) {
                stopCurrentSong();
            }

            dao.deleteSong(selectedSong.getId());
            reloadCurrentView();

            showInfo("Song deleted successfully.");
        }
    }

    private void playNextSong() {
        int currentIndex = songList.getSelectedIndex();

        if (currentIndex == -1) {
            showWarning("Please select a song first.");
            return;
        }

        if (currentIndex < songListModel.getSize() - 1) {
            int nextIndex = currentIndex + 1;
            songList.setSelectedIndex(nextIndex);
            songList.ensureIndexIsVisible(nextIndex);

            Song nextSong = songList.getSelectedValue();
            if (nextSong != null) {
                playSong(nextSong);
            }
        } else {
            showWarning("This is the last song in the list.");
        }
    }

    private void playPreviousSong() {
        int currentIndex = songList.getSelectedIndex();

        if (currentIndex == -1) {
            showWarning("Please select a song first.");
            return;
        }

        if (currentIndex > 0) {
            int previousIndex = currentIndex - 1;
            songList.setSelectedIndex(previousIndex);
            songList.ensureIndexIsVisible(previousIndex);

            Song previousSong = songList.getSelectedValue();
            if (previousSong != null) {
                playSong(previousSong);
            }
        } else {
            showWarning("This is the first song in the list.");
        }
    }

    private void playNextSongAutomatically() {
        int currentIndex = songList.getSelectedIndex();

        if (currentIndex >= 0 && currentIndex < songListModel.getSize() - 1) {
            int nextIndex = currentIndex + 1;
            songList.setSelectedIndex(nextIndex);
            songList.ensureIndexIsVisible(nextIndex);

            Song nextSong = songList.getSelectedValue();
            if (nextSong != null) {
                playSong(nextSong);
            }
        } else {
            stopCurrentSongInternal(false);
        }
    }

    private void startProgressUpdater() {
        new Thread(() -> {
            while (currentClip != null && currentClip.isOpen()) {
                if (!isPaused && currentClip.isRunning()) {
                    long currentMicro = currentClip.getMicrosecondPosition();
                    long totalMicro = currentClip.getMicrosecondLength();

                    int currentSeconds = (int) (currentMicro / 1_000_000);
                    int totalSeconds = (int) (totalMicro / 1_000_000);

                    int progress = 0;
                    if (totalMicro > 0) {
                        progress = (int) ((currentMicro * 100) / totalMicro);
                    }

                    final int finalCurrentSeconds = currentSeconds;
                    final int finalTotalSeconds = totalSeconds;
                    final int finalProgress = progress;

                    SwingUtilities.invokeLater(() -> {
                        currentTimeLabel.setText(formatTime(finalCurrentSeconds));
                        totalTimeLabel.setText(formatTime(finalTotalSeconds));
                        progressBar.setValue(finalProgress);
                    });
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}