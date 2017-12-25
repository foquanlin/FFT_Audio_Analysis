import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Main extends JFrame implements ActionListener {
	
	private String fileName;
	private JButton file1;
	private JButton file2;
	private JButton mic;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		setLocation(0, 0);
		setSize(400,400);
		setVisible(true);
		
		init();
	}
	
	public void init() {
		file1 = new JButton("FFT from File 1");
		file2 = new JButton("FFT from File 2");
		mic = new JButton("FFT from Mic Input");
		
		file1.addActionListener(this);
		file2.addActionListener(this);
		mic.addActionListener(this);
		
		getContentPane().setLayout(new GridLayout(3,1));
		getContentPane().add(file1);
		getContentPane().add(file2);
		getContentPane().add(mic);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(file1)) {
			fileName = "prodigy.wav";
			AudioInit.audioStart(true, fileName);
		}
		
		if(e.getSource().equals(file2)) {
			fileName = "zhu.wav";
			AudioInit.audioStart(true, fileName);
		}
		
		if(e.getSource().equals(mic)) {
			AudioInit.audioStart(false, "");
		}
	}
}
