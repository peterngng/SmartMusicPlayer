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

### 3. Multithreading
The project uses multithreading to keep the GUI responsive while time-consuming tasks run in the background.

#### Folder Scanning Thread
When the user clicks **Scan Folder**, the program may need to search through many files and subfolders.  
If this task were done on the main GUI thread, the window could freeze until scanning finished.  
To avoid that, the program creates a background thread to scan the folder separately.

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

This is done in a separate background thread that checks the clip position repeatedly while the song is playing.  
Without this thread, the progress bar and timer would not update smoothly during playback.

#### Why Multithreading Is Important Here
Multithreading is important in this project because the application is interactive and GUI-based.  
The main Swing interface should remain responsive while background work is happening.  
By moving long-running tasks and repeated updates into separate threads, the program provides a smoother user experience and demonstrates practical use of multithreading in a desktop application.

## Database Design and How It Works

The database is one of the most important parts of this project. The program uses a local SQLite database file named:

```text
musicplayer.db
```

This file is created automatically when the program starts for the first time.

The database connection and table creation are handled in `DBHelper.java`.

### Database Tables

The application uses several tables.

#### 1. `songs`

This table stores the main music library.

Columns:

- `id` = unique song ID
- `title` = song title
- `artist` = artist name
- `album` = album name
- `file_path` = full path to the song file on the computer
- `duration` = duration in seconds
- `is_favorite` = whether the song is marked as favorite

This means the program does not need to rescan everything every time. Once a song is added, its information is saved in the database.

#### 2. `play_history`

This table stores playback history.

Columns:

- `id` = history record ID
- `song_id` = the song that was played
- `played_at` = timestamp of when the song was played

Every time a song starts playing, a new history record is inserted.

#### 3. `playlists`

This table stores playlist names.

Columns:

- `id` = playlist ID
- `name` = playlist name

#### 4. `playlist_songs`

This table connects songs and playlists.

Columns:

- `playlist_id`
- `song_id`

This is needed because:

- one playlist can contain many songs
- one song can belong to many playlists

So this table works like a relationship table between the `songs` table and the `playlists` table.

#### 5. `app_settings`

This table stores small settings for the application.

Examples:

- last opened playlist
- last volume value

This allows the app to restore some state when it starts again.

## How Songs Are Added to the Database

There are two main ways to add songs.

### 1. Add One Song

The user clicks the **Add One Song** button.

Then the program:

1. opens a file chooser
2. lets the user choose one file
3. asks the user to enter:
   - title
   - artist
   - album
4. calculates duration if the file is WAV
5. inserts the song into the `songs` table

This is the more accurate method because the user can type the song information manually.

### 2. Scan Folder

The user clicks the **Scan Folder** button.

Then the program:

1. opens a folder chooser
2. scans the chosen folder and subfolders
3. looks for `.wav` and `.mp3` files
4. creates song records automatically
5. inserts them into the database

For scanned songs, title usually comes from the file name, while artist and album are set to default values such as `Unknown Artist` and `Unknown Album`.

This feature is faster when importing many files at once.

## How the Program Prevents Duplicate Songs

The `songs` table uses the `file_path` column as a unique value.

This means the same file path cannot be inserted many times. If the user tries to add the same file again, SQLite ignores the duplicate entry.

This helps keep the music library cleaner.

## How Playlists Work in the Database

When the user creates a playlist:

- a new row is inserted into the `playlists` table

When the user adds a song to a playlist:

- the program looks up the playlist ID
- then inserts a row into `playlist_songs`

When the user opens a playlist:

- the program uses a SQL join between `songs` and `playlist_songs`
- it loads only the songs connected to that playlist

When the user deletes a playlist:

- rows in `playlist_songs` for that playlist are removed
- the playlist row itself is removed from `playlists`

The actual songs in the main library remain safe unless the user separately deletes a song.

## How Import and Export Work

The project also includes file I/O features in addition to database storage.

### Export Playlist

The user can export a playlist into a text file.

The file format is simple and readable. The first line stores the playlist name, and the next lines store song details such as:

- title
- artist
- album
- file path
- duration

This allows the playlist to be saved outside the database.

### Import Playlist

The user can import a playlist text file.

The program:

1. reads the playlist name
2. creates the playlist if needed
3. reads each song line
4. checks whether the song already exists in the database by file path
5. adds the song if it does not already exist
6. links the song to the playlist

This means the database remains the main storage, while the text file acts as an import/export format.

## How Music Playback Works

Playback is handled in `MusicPlayerFrame.java` using Java audio classes and `Clip`.

The current implementation focuses on WAV playback.

When a user plays a song:

1. the selected song file path is read from the database-backed song object
2. the WAV file is opened
3. the clip starts playing
4. play history is inserted into the database
5. the progress bar and time labels update while the song is playing

The player also supports:

- stop
- pause/resume
- next/previous
- auto play next
- click-to-seek

## How Search and Filters Work

The song list shown on screen can be filtered by:

- search text
- favorites only
- playlist view

The search checks:

- song title
- artist name

So if the user types part of a title or artist, the list updates to show only matching songs.

## How Sorting Works

The user can sort the visible song list by:

- title
- artist

The application sorts the loaded songs in memory before displaying them in the list.

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

6. Right click `Main.java`
7. Choose:

```text
Run As -> Java Application
```

## How to Use the Program

### Add one song manually

1. Click **Add One Song**
2. Choose a WAV file
3. Enter title, artist, and album
4. The song is saved into the database and shown in the song list

### Import many songs

1. Click **Scan Folder**
2. Choose a folder containing music files
3. The application scans the folder in the background
4. Matching files are added into the database

### Play music

1. Select a song from the list
2. Click **Play** or double-click the song
3. Use pause, stop, next, previous, and progress bar click-to-seek as needed

### Create and manage playlists

1. Click **Create Playlist**
2. Add selected songs with **Add To Playlist**
3. Click a playlist on the left to view its songs
4. Use rename, remove, delete, export, and import options as needed

## Notes

- Playback support is focused on WAV files.
- MP3 files may be scanned into the library, but playback support in this project is based on WAV.
- The database file keeps the user's library and playlist data between sessions.
- Export/import text files are used as a portable playlist format, while SQLite remains the main internal storage.

## Author

Peter Ng
