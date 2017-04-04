package io.dantetam.github.audio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;

import static javax.sound.sampled.AudioSystem.getAudioInputStream;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;

public class AudioTest
{
	public static File randomAudioFileInFolder(String directoryName) {
		File[] listOfFiles = getFilesInFolder(directoryName);
		int index = (int)(Math.random()*listOfFiles.length);
		File result = listOfFiles[index];
		if (result.getName().contains(".txt")) {
			return randomAudioFileInFolder(directoryName);
		}
		return result;
	}

	public static File[] getFilesInFolder(String directoryName) {
		File folder = new File(directoryName);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}

	private static AudioTest player;
    public static void main(String[] args) {
    	player = new AudioTest();
    	File audioFile = randomAudioFileInFolder("data/audio/miranda_sorted/positive");
        //player.playAudioFile(System.getProperty("user.dir") + "/data/audio/miranda_sorted/positive/" + audioFile.getName()); 
    	player.playAudioFile(System.getProperty("user.dir") + "/data/audio/miranda_sorted/positive/" + audioFile.getName()); 
    	player.playAudioFile(System.getProperty("user.dir") + "/data/thanks_stereo.ogg"); 
        System.out.println("Played file");
    }
 
    public void playAudioFile(File file) {
    	try (final AudioInputStream in = getAudioInputStream(file)) {
            
            final AudioFormat outFormat = getOutFormat(in.getFormat());
            final Info info = new Info(SourceDataLine.class, outFormat);
 
            try (final SourceDataLine line =
                     (SourceDataLine) AudioSystem.getLine(info)) {
 
                if (line != null) {
                    line.open(outFormat);
                    line.start(); 
                    AudioInputStream inputMystream = AudioSystem.getAudioInputStream(outFormat, in);
                    stream(inputMystream, line);
                    line.drain();
                    line.stop();  
                }
            }
            
        } catch (UnsupportedAudioFileException 
               | LineUnavailableException 
               | IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void playAudioFile(String filePath) {
        final File file = new File(filePath);
        playAudioFile(file);
    }
 
    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }
 
    private void stream(AudioInputStream in, SourceDataLine line) 
        throws IOException {
        final byte[] buffer = new byte[4096];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }

}
