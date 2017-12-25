import java.util.Scanner;

public class AudioInit {
	
	private static FFTGUI gui;
	private static FFTGUIController fftGuiController;
	private static Thread fftGuiControllerThread;
	private static Thread audioIOThread;
	public static Audio_IO audioIO;
	
	/**
	 * Kick-start method for the Audio feature.
	 * fromFile flag should be passed from the main GUI after a specific button is pressed.
	 * 
	 * @param boolean fromFile
	 */
	public static void audioStart(boolean fromFile, String fileName) {
		gui = new FFTGUI();
		fftGuiController = new FFTGUIController(gui);
		fftGuiControllerThread = new Thread(fftGuiController);
		
		audioIO = new Audio_IO(fftGuiController, fromFile, fileName);
		audioIOThread = new Thread(audioIO);

		audioIOThread.start();
		fftGuiControllerThread.start();
	}
	
	/**
	 * Used to terminate the audio interface lines and threads.
	 * This will provide re-usability in the main application (instead of restarting it every time).
	 */
	public static void audioStop() {
		audioIO.closeLines();
		audioIO.keepRunning = false;
		fftGuiController.keepRunning = false;
	}
}
