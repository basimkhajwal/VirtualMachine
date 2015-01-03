package net.net63.codearcade.VirtualMachine.assembler;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;

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
		JPanel topPanel = new JPanel();
		GridBagLayout topLayout = new GridBagLayout();
		topPanel.setLayout(topLayout);
		
		title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
		GridBagConstraints topConstraints = new GridBagConstraints();
		topConstraints.ipady = 20;
		topConstraints.anchor = GridBagConstraints.CENTER;
		topPanel.add(title, topConstraints);
		
		panel.add(topPanel, BorderLayout.NORTH);
		
		JPanel mainPanel = new JPanel();
		
		JTextPane codeText = new JTextPane();
		codeText.setSize(700, 500);
		
		JScrollPane scrollPane = new JScrollPane(codeText);
		scrollPane.setSize();
		
		mainPanel.add(codeText);
		
		panel.add(mainPanel, BorderLayout.CENTER);
	}
}
