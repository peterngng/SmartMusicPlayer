package musicplayer;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String album;
    private String filePath;
    private int duration;
    private boolean favorite;

    public Song(int id, String title, String artist, String album, String filePath, int duration, boolean favorite) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.filePath = filePath;
        this.duration = duration;
        this.favorite = favorite;
    }

    public Song(String title, String artist, String album, String filePath, int duration, boolean favorite) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.filePath = filePath;
        this.duration = duration;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public String toString() {
        String star = favorite ? "★ " : "";
        return star + title + " - " + artist;
    }
}