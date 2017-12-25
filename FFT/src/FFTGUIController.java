public class FFTGUIController implements Runnable {
	public FFTGUI gui;
	
	private double[] signal, signal2;
	private double amplitude, amplitude2;
	private int range, range2;
	private String unit ,unit2;
	private float offset, offset2, height, height2;
	public boolean keepRunning;
	
	public FFTGUIController(FFTGUI gui) {
		this.gui = gui;
		this.gui.setTitle("Audio IO");
		keepRunning = true;
		
		this.signal = new double[0];
		this.amplitude = 0.0;
		this.range = 0;
		this.unit = "";
		this.offset = 0;
		this.height = 0;
		
		this.signal2 = new double[0];
		this.amplitude2 = 0.0;
		this.range2 = 0;
		this.unit2 = "";
		this.offset2 = 0;
		this.height2 = 0;
	}
	
	public void plot(double[] signal, double amplitude, int value, String unit, float offset, float height) {
		this.signal = signal;
		this.amplitude = amplitude;
		this.range = value;
		this.unit = unit;
		this.offset = offset;
		this.height = height;
	}
	
	public void plot2(double[] signal2, double amplitude2, int value2, String unit2, float offset2, float height2) {
		this.signal2 = signal2;
		this.amplitude2 = amplitude2;
		this.range2 = value2;
		this.unit2 = unit2;
		this.offset2 = offset2;
		this.height2 = height2;
	}
	
	@Override
	public void run() {
		while(keepRunning) {
			gui.plot(signal, amplitude, range, unit, offset, height);
			gui.plot(signal2, amplitude2, range2, unit2, offset2, height2);
			gui.update();
		}
	}
}
