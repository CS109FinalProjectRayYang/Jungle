package structures;

import network.Client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PlaySound {
    public static void run() {
        try {
            Clip bgm = AudioSystem.getClip();
            File musicFile = new File("data/music/music1.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(musicFile);
            bgm.open(ais);
            bgm.start();
            bgm.loop(Clip.LOOP_CONTINUOUSLY);
            while (true) {}
        } catch (Exception ignore) {

        }
    }
}
