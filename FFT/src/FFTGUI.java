import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class FFTGUI extends JFrame {
	public final float dx = (float) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public final float dy = (float) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

	private final Graphics2D g;
	private final BufferedImage frame;

	private double st = time();
	private String sfps;

	public FFTGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
		setUndecorated(true); // no frame around
		getContentPane().setBackground(Color.BLACK);
		setVisible(true);
		
		frame = new BufferedImage((int) dx, (int) dy, 1);
		
		g = ((Graphics2D) frame.getGraphics());
		g.setBackground(Color.BLACK);
		g.setFont(Font.decode("Monospaced"));
		g.setStroke(new java.awt.BasicStroke(2.0F));

		addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					onClose();
				}
			}
		});
	}
	
	public void onClose() {
		AudioInit.audioStop();
		this.dispose();
	}
	
	public void plot(double[] signal, double amplitude, int value, String unit, float offset, float height) {
		int mark = (int) min(10, dx / (9 * 7 + 6));
		
		for (;;) {
			if (dx / mark < 18 * 7 + 12)
				break;
			
			mark *= 2;
		}
		
		for (int i = 0; i < mark; i++) {
			double x1 = dx * i / mark - dx / 2. + 1;
			drawLabel(Color.ORANGE, value * i / mark + unit, x1 + 5, -dy / 4 + height);
			drawLine(Color.ORANGE, x1, -dy / 4 + 11 + height, x1, -dy / 4 + height);
		}

		for (int i = 0; i < signal.length - 1; i++) {
			double x1 = dx * i / (signal.length - 1) - dx / 2 + 1;
			double x2 = dx * (i + 1) / (signal.length - 1) - dx / 2 + 1;
			double y1 = (dy / 2 - 2 * 11 - 5) * min((signal[i] + 1.0E-6) / amplitude, 1) + offset + height;
			double y2 = (dy / 2 - 2 * 11 - 5) * min((signal[(i + 1)] + 1.0E-6) / amplitude, 1) + offset	+ height;
			drawLine(Color.GREEN, x1, y1, x2, y2);
		}
	}
	
	private void drawLine(Color color, double x1, double y1, double x2, double y2) {
		g.setColor(color);
		g.drawLine((int) (dx / 2 + x1), (int) (dy / 2 - y1), (int) (dx / 2 + x2), (int) (dy / 2 - y2));
	}

	private void drawLabel(Color color, String s, double x, double y) {
		g.setColor(color);
		g.drawString(s, (int) (dx / 2 + x), (int) (dy / 2 - y) - 2);
	}

	public void update() {
		sfps = ("  " + (int) (1 / (time() - st)));
		sfps = sfps.substring(sfps.length() - 3, sfps.length());
		drawLabel(Color.GRAY, sfps, dx / 2 - 21 - 1, -dy / 2);
		getContentPane().getGraphics().drawImage(frame, 0, 0, null);
		g.clearRect(0, 0, (int) dx, (int) dy);
		pause(0.016666666666666666 - time() + st);
		st = time();
	}

	private double time() {
		return System.nanoTime() * 1.0E-9;
	}

	private void pause(double s) {
		if (s > 0.0) {
			try {
				Thread.sleep((int) (s * 1000));
			} catch (Exception localException) {
			}
		}
	}

	private double min(double x, double y) {
		return x < y ? x : y;
	}
}
