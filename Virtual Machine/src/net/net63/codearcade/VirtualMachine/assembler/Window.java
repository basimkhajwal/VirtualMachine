package net.net63.codearcade.VirtualMachine.assembler;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class Window implements Runnable{
	
	private JButton compile, loadAssembly, saveAssembly, saveBinary;
	
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
		panel.setLayout(new BorderLayout());
		
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
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JTextPane codeText = new JTextPane();
		codeText.setSize(100, 100);
		
		JScrollPane scrollPane = new JScrollPane(codeText);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		panel.add(mainPanel, BorderLayout.CENTER);
		
		JPanel controlsPanel = new JPanel();
		
		JButton compile = new JButton("Compile Program");
		
		JButton saveAssembly = new JButton("Save Assembly");
		
		JButton saveBinary = new JButton("Save Binary");
		
		JButton loadAssembly = new JButton("Load Assembly Program");
		
		controlsPanel.add(loadAssembly);
		controlsPanel.add(saveAssembly);
		controlsPanel.add(compile);
		controlsPanel.add(saveBinary);
		panel.add(controlsPanel, BorderLayout.SOUTH);
	}
}
