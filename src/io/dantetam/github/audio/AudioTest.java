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
import javax.sound.sampled.UnsupportedAudioFileException;


public class AudioTest
{
	public static void main(String[] args) throws Exception
	{
		File randomAudio = randomFileInFolder("data/audio/miranda_sorted/positive");
		InputStream in = new FileInputStream(randomAudio);
		BufferedInputStream bin = new BufferedInputStream(in);

		Clip clip = AudioSystem.getClip();
		AudioInputStream inputStream = AudioSystem.getAudioInputStream(bin);
		clip.open(inputStream);
		clip.start(); 
	}

	public static File randomFileInFolder(String directoryName) {
		File[] listOfFiles = getFilesInFolder(directoryName);
		int index = (int)(Math.random()*listOfFiles.length);
		return listOfFiles[index];
	}

	public static File[] getFilesInFolder(String directoryName) {
		File folder = new File(directoryName);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}

	public static AudioInputStream createOggMp3(File fileIn) throws IOException, Exception {
		AudioInputStream audioInputStream=null;
		AudioFormat targetFormat=null;
		try {
			AudioInputStream in=null;
			if(fileIn.getName().endsWith(".ogg")) {
				VorbisAudioFileReader vb=new VorbisAudioFileReader();
				in=vb.getAudioInputStream(fileIn);
			}
			else if(fileIn.getName().endsWith(".mp3")) {
				MpegAudioFileReader mp=new MpegAudioFileReader();
				in=mp.getAudioInputStream(fileIn);
			}
			AudioFormat baseFormat=in.getFormat();
			targetFormat=new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),
					16,
					baseFormat.getChannels(),
					baseFormat.getChannels() * 2,
					baseFormat.getSampleRate(),
					false);
			audioInputStream=AudioSystem.getAudioInputStream(targetFormat, in);
		}
		catch(UnsupportedAudioFileException ue) { System.out.println("\nUnsupported Audio"); }
		return audioInputStream;
	}

}
