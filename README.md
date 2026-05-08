# SmartMusicPlayer

SmartMusicPlayer is a Java Swing desktop music player application built for a final project.  
The project allows users to manage and play WAV audio files through a graphical user interface.  
It also supports playlists, favorites, search, sorting, import/export of playlists, and saved settings.

This project was designed to demonstrate multiple advanced Java topics, including GUI development, database integration, and multithreading.

## Project Features

- WAV music playback
- Play, stop, pause/resume
- Next and previous song
- Click-to-seek progress bar
- Volume slider
- Add one song manually
- Scan folder to import multiple songs
- Mark songs as favorite
- Show recent play history
- Search by title or artist
- Sort by title or artist
- Show song details
- Create playlists
- Add songs to playlists
- Remove songs from playlists
- Rename playlists
- Delete playlists
- Export playlists to text files
- Import playlists from text files
- Save last opened playlist
- Save last volume setting

## Advanced Topics Used

This project uses the following advanced topics:

### 1. GUI

The program uses Java Swing to build the full desktop interface, including:

- buttons
- labels
- text fields
- lists
- progress bar
- dialogs
- playlist panel
- song details panel

### 2. Database

The program uses SQLite with JDBC to store application data permanently.  
The database stores:

- songs
- favorites
- play history
- playlists
- playlist-song relationships
- saved settings such as last volume and last opened playlist

### 3. Multithreading

The program uses a background thread for folder scanning so the GUI does not freeze while importing many files.  
It also uses a background thread to update the playback progress bar and current time while music is playing.

## Project Structure

The main Java files are inside:

```text
src/musicplayer/
```

Main files:

- `Main.java`  
  Starts the program.

- `DBHelper.java`  
  Creates the SQLite database connection and initializes required tables.

- `Song.java`  
  Song model class that stores song information such as title, artist, album, file path, duration, and favorite status.

- `SongDAO.java`  
  Handles database operations such as adding songs, loading songs, playlist operations, play history, and saved settings.

- `MusicPlayerFrame.java`  
  Main GUI window of the application. Handles playback, user interaction, playlist management, search, sorting, dialogs, and other interface actions.

## How the Code Works

### Song Library

Songs can be added one by one using the **Add One Song** button or in bulk using the **Scan Folder** button.  
The song information is stored in the SQLite database.

### Music Playback

The player currently supports WAV playback using Java's built-in audio support, `Clip`.
Users can:

- play songs
- stop songs
- pause/resume songs
- jump to another time using the progress bar
- move to previous or next songs

### Search and Sorting

The user can search songs by title or artist using the search bar.  
The user can also sort the visible song list by title or artist.

### Favorites

Songs can be marked as favorites.  
The interface can filter to show only favorite songs.

### Playlists

Users can create playlists and add songs into them.  
The playlist panel on the left allows users to:

- view playlist songs
- rename playlists
- delete playlists
- remove songs from playlists

### Import and Export

Playlists can be exported to a text file and imported back into the program later.  
This provides a simple file I/O feature in addition to database storage.

### Saved Settings

The application remembers:

- the last opened playlist
- the last volume slider value

These settings are stored in the database and restored when the app starts again.

## Requirements

To run this project, you need:

- Java JDK 21
- Eclipse IDE, or another Java IDE
- SQLite JDBC driver jar  
  Example used in this project: `sqlite-jdbc-3.34.0.jar`

## How to Run the Code

### In Eclipse

1. Open Eclipse.
2. Import or open the project folder.
3. Make sure the SQLite JDBC jar is added to the project build path.
4. Make sure `app_icon.png` is in the project root folder if you want the custom icon to appear.
5. Open:

```text
src/musicplayer/Main.java
```

6. Right click `Main.java`.
7. Choose:

```text
Run As -> Java Application
```

## Notes

- The player currently supports WAV playback only.
- MP3 files may be scanned into the library, but playback support is focused on WAV.
- The app uses SQLite for local storage, so data remains after closing and reopening the program.

## Author

Peter Ng
