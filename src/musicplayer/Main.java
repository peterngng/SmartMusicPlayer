package musicplayer;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        DBHelper.initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            MusicPlayerFrame frame = new MusicPlayerFrame();
            frame.setVisible(true);
        });
    }
}