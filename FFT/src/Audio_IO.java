import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Audio_IO implements Runnable {
	private FFTGUIController guiController;
	private final int SAMPLE_RATE = 44100;
	private final int SAMPLE_SIZE = 4096;
	private final int BANDWIDTH = 5000;
	private final int MULTIPLIER = 12;
	
	private File audioFile;
	private AudioFileFormat audioFileFormat;
	private AudioFormat audioFormat;
	private AudioInputStream audioInputStream;

	private TargetDataLine microphoneLine;
	private SourceDataLine speakerLine;
	
	private byte[] buffer;
	private double[] wave;
	private Complex[] c;
	private double[] freq;
	
	private boolean fromFile;
	
	public boolean keepRunning;
	
	/**
	 * Used to setup all parameters and audio interfaces required for this to run.
	 * Based on the fromFile flag, either an audio file will be loaded, or a microphone interface
	 * will be started.
	 * 
	 * @param guiController
	 * @param fromFile
	 */
	public Audio_IO(FFTGUIController guiController, boolean fromFile, String fileName) {
		this.guiController = guiController;
		this.fromFile = fromFile;
		keepRunning = true;
		
		try {
			if(fromFile) {
				String dir = System.getProperty("user.dir");
				audioFile = new File(dir + "\\" + fileName);
				audioFileFormat = AudioSystem.getAudioFileFormat(audioFile);
				audioFormat = audioFileFormat.getFormat();
				
				audioInputStream = AudioSystem.getAudioInputStream(audioFile);
				
				speakerLine = AudioSystem.getSourceDataLine(audioFormat);
				speakerLine.open(audioFormat, SAMPLE_SIZE);
				speakerLine.start();
				
				buffer = new byte[SAMPLE_SIZE * audioFormat.getFrameSize()];
			} else {
				audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
				microphoneLine = AudioSystem.getTargetDataLine(audioFormat);
				microphoneLine.open(audioFormat, SAMPLE_SIZE);
				microphoneLine.start();
				
				buffer = new byte[SAMPLE_SIZE];
			}
			
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
		wave = new double[buffer.length / 2];
		c = new Complex[buffer.length / 2];
		freq = new double[c.length * BANDWIDTH / SAMPLE_RATE];
	}
	
	/**
	 * Thread method to run the calculations.
	 * Based on the fromFile flag, the input will either be received from an audio file (and played through speakers)
	 * or it will be received from the microphone (speaker play-back in this case is not necessary).
	 */
	@Override
	public void run() {
		while(keepRunning) {
			int readBytes;
			
			try {
				if(fromFile) {
					readBytes = audioInputStream.read(buffer);
				} else {
					readBytes = microphoneLine.read(buffer, 0, SAMPLE_SIZE);
				}
				wave = convertToDoubles(wave, buffer, readBytes);
				
				for (int i = 0; i < wave.length; i++) {
					c[i] = new Complex(wave[i], 0.0);
				}
				
				c = FFT.fft(c);
				
				for (int i = 1; i < freq.length; i++) {
					freq[i] = (c[i].abs() / SAMPLE_RATE / SAMPLE_SIZE * MULTIPLIER);
				}

				guiController.plot(wave, 100000, 1000 * SAMPLE_SIZE / SAMPLE_RATE, "ms", 0, guiController.gui.dy / 4);
				guiController.plot2(freq, 1, BANDWIDTH, "Hz", -guiController.gui.dy / 4 + 11 + 1, -guiController.gui.dy / 4);

				if(fromFile) {
					speakerLine.write(buffer, 0, buffer.length);
				}
			} catch (IOException e) {
				
			}
		}		
	}

	/**
	 * Used to covert bytes received from the line-in interface into doubles
	 * (to work with real values during calculations).
	 * 
	 * @param wave
	 * @param buffer
	 * @param readBytes
	 * @return
	 */
	private double[] convertToDoubles(double[] wave, byte[] buffer, int readBytes) {
		double[] byteDoubles = new double[wave.length];
		
		for (int i = wave.length - 1; i >= readBytes / 2; i--) {
			byteDoubles[i] = wave[(i - readBytes / 2)];
		}
		
		for (int i = 0; i < readBytes / 2; i++) {
			byteDoubles[i] = (buffer[(readBytes - 1 - 2 * i)] << 8 | buffer[(readBytes - 2 - 2 * i)] & 0xFF);
		}
		
		return byteDoubles;
	}
	
	/**
	 * Used to terminate the audio interfaces.
	 */
	public void closeLines() {
		if(fromFile) {
			speakerLine.stop();
			speakerLine.close();
			speakerLine.flush();
		} else {
			microphoneLine.stop();
			microphoneLine.close();
			microphoneLine.flush();
		}
	}
}
