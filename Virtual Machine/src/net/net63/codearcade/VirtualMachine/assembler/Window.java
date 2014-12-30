package net.net63.codearcade.VirtualMachine.assembler;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.Style;

public class Window implements Runnable{
	
	@Override
	public void run(){
		JFrame frame = new JFrame();
		
		setupFrame(frame);
		setupGUI((JPanel) frame.getContentPane());
	}
	
	private void setupFrame(JFrame frame){
		frame.setTitle("Assembler");
		frame.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void setupGUI(JPanel panel){
		JLabel title = new JLabel("Assembler");
		title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		title.setBounds(300, title.getY(), title.getWidth(), title.getHeight());
		panel.add(title, BorderLayout.NORTH);
	}
}
