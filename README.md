# SmartMusicPlayer

SmartMusicPlayer is a Java Swing desktop music player application built for a final project. The program is designed for managing and playing WAV audio files through a graphical user interface. It combines music playback features with database storage, playlist management, search, sorting, import/export, and saved settings.

The goal of this project is not only to make a working music player, but also to demonstrate several important Java topics clearly in one application, especially GUI programming, database usage, and multithreading.

## Project Overview

This application works like a small local music library manager.

The user can:
- add songs manually
- scan a folder to import many songs
- play WAV songs
- pause, resume, stop, and seek within songs
- move to next and previous songs
- mark songs as favorites
- search and sort songs
- create and manage playlists
- export and import playlists as text files
- save some settings such as last opened playlist and volume

All important application data is stored in a local SQLite database so the data remains available even after the program is closed.

## Main Features

- WAV playback
- Play, stop, pause/resume
- Next and previous song
- Click-to-seek progress bar
- Volume slider
- Add one song manually
- Scan folder to import songs in bulk
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
- Export playlists to text file
- Import playlists from text file
- Save last opened playlist
- Save last volume setting

## Advanced Topics Used

### 1. GUI

The application uses Java Swing to build the graphical user interface. The interface includes:
- buttons
- labels
- text fields
- lists
- playlist panel
- song details panel
- progress bar
- dialogs
- sliders

### 2. Database

The application uses SQLite with JDBC for permanent local data storage. Instead of storing everything only in memory, the program saves information into a database file so the library remains available after restarting the app.

The database stores the main structure of the program, including:
- songs
- favorites
- play history
- playlists
- playlist-song relationships
- app settings such as last opened playlist and last volume

This means the application does not start as a brand new empty program every time it is opened. When the code runs, it connects to the existing SQLite database file, reads the previously saved data, and restores the library state from the last time the user used the app.

For example, after reopening the program, the user can still see:
- previously added songs
- saved playlists
- favorite songs
- play history
- the last opened playlist
- the last saved volume setting

The database is also used during normal program actions, such as:
- adding one song manually
- scanning a folder to import songs
- loading the music library at startup
- saving and loading playlists
- recording play history when songs are played
- restoring saved settings after reopening the app

Using SQLite makes the project more practical because the music library and user data remain available between sessions instead of being lost whenever the program closes.

### 3. Multithreading

The project uses multithreading to keep the GUI responsive while time-consuming tasks run in the background.

#### Folder Scanning Thread

When the user clicks **Scan Folder**, the program may need to search through many files and subfolders. If this task were done on the main GUI thread, the window could freeze until scanning finished. To avoid that, the program creates a background thread to scan the folder separately.

This means:
- the user interface stays responsive
- the window can still be moved or interacted with
- large folder imports do not block the whole application

After the scan finishes, the GUI is updated with the newly imported songs.

#### Playback Progress Update Thread

While a song is playing, the program continuously updates:
- the progress bar
- the current playback time
- the total time display

This is done in a separate background thread that checks the clip position repeatedly while the song is playing. Without this thread, the progress bar and timer would not update smoothly during playback.

#### Why Multithreading Is Important Here

Multithreading is important in this project because the application is interactive and GUI-based. The main Swing interface should remain responsive while background work is happening. By moving long-running tasks and repeated updates into separate threads, the program provides a smoother user experience and demonstrates practical use of multithreading in a desktop application.

## Project Structure

The main Java files are inside:

```text
src/musicplayer/
```

Main files:

- `Main.java`  
  Starts the application.

- `DBHelper.java`  
  Connects to SQLite and creates required database tables.

- `Song.java`  
  Represents one song object with its properties.

- `SongDAO.java`  
  Handles database operations such as song insert, playlist operations, history, and settings.

- `MusicPlayerFrame.java`  
  Main GUI window. Handles user interaction, playback, filtering, playlists, dialogs, progress bar, import/export, and saved settings.

## How the Code Works

### Song Library

Songs can be added one by one using the **Add One Song** button or in bulk using the **Scan Folder** button. The song information is stored in the SQLite database.

### Music Playback

The player currently supports WAV playback using Java's built-in audio support (`Clip`).

Users can:
- play songs
- stop songs
- pause/resume songs
- jump to another time using the progress bar
- move to previous or next songs

### Search and Sorting

The user can search songs by title or artist using the search bar. The user can also sort the visible song list by title or artist.

### Favorites

Songs can be marked as favorites. The interface can filter to show only favorite songs.

### Playlists

Users can create playlists and add songs into them. The playlist panel on the left allows users to:
- view playlist songs
- rename playlists
- delete playlists
- remove songs from playlists

### Import and Export

Playlists can be exported to a text file and imported back into the program later. This provides a simple file I/O feature in addition to database storage.

### Saved Settings

The application remembers:
- the last opened playlist
- the last volume slider value

These settings are stored in the database and restored when the app starts again.

## Requirements

To run this project, you need:
- Java JDK 21
- Eclipse IDE or another Java IDE
- SQLite JDBC driver jar  
  Example: `sqlite-jdbc-3.34.0.jar`
- WAV files for playback testing

## How to Run the Project

### In Eclipse

1. Open Eclipse.
2. Open or import the project folder.
3. Make sure the SQLite JDBC jar is added to the Java Build Path.
4. Make sure `app_icon.png` is in the project root folder if you want the custom icon.
5. Open:

```text
src/musicplayer/Main.java
```

6. Right click `Main.java`.
7. Choose:

```text
Run As -> Java Application
```

## How to Use the Program

### Add one song manually

1. Click **Add One Song**.
2. Choose a WAV file.
3. Enter title, artist, and album.
4. The song is saved into the database and shown in the song list.

### Import many songs

1. Click **Scan Folder**.
2. Choose a folder containing music files.
3. The application scans the folder in the background.
4. Matching files are added into the database.

### Play music

1. Select a song from the list.
2. Click **Play** or double-click the song.
3. Use pause, stop, next, previous, and progress bar click-to-seek as needed.

### Create and manage playlists

1. Click **Create Playlist**.
2. Add selected songs with **Add To Playlist**.
3. Click a playlist on the left to view its songs.
4. Use rename, remove, delete, export, and import options as needed.

## Notes

- Playback support is focused on WAV files.
- MP3 files may be scanned into the library, but playback support in this project is based on WAV.
- The database file keeps the user's library and playlist data between sessions.
- Export/import text files are used as a portable playlist format, while SQLite remains the main internal storage.

## Author

Peter Ng
