package net.net63.codearcade.VirtualMachine.assembler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.net63.codearcade.VirtualMachine.assembler.AssemblerUtils.AssembleException;
import net.net63.codearcade.VirtualMachine.lib.TextLineNumber;

public class Window implements Runnable{
	
	private JFrame frame;
	private JButton compile, loadAssembly, saveAssembly, saveBinary;
	private JTextPane codeText, logText;
	private String binary;
	
	@Override
	public void run(){
		frame = new JFrame();
		
		setupFrame();
		setupGUI((JPanel) frame.getContentPane());
	}
	
	private void setupFrame(){
		frame.setTitle("Assembler");
		frame.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void setupGUI(JPanel panel){
		panel.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("Assembler");
		title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
		GridBagConstraints topConstraints = new GridBagConstraints();
		topConstraints.ipady = 20;
		topConstraints.anchor = GridBagConstraints.CENTER;
		JPanel topPanel = new JPanel(new GridBagLayout());
		
		topPanel.add(title, topConstraints);

		codeText = new JTextPane();
		codeText.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				compile.setEnabled(true);
				saveAssembly.setEnabled(true);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {	}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		codeText.setSize(100, 100);
		
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
		
		TextLineNumber tln = new TextLineNumber(codeText);
		tln.setUpdateFont(false);
		tln.setCurrentLineForeground(Color.RED);
		codeText.setFont(font);
		codeText.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(10, 0, 0, 0), "Assembly Code", TitledBorder.TOP, TitledBorder.CENTER));
		
		JScrollPane scrollPane = new JScrollPane(codeText);
		scrollPane.setRowHeaderView(tln);
		
		logText = new JTextPane();
		logText.setEditable(false);
		logText.setFont(font);
		logText.setBorder(BorderFactory.createTitledBorder(new EmptyBorder(10, 0, 0, 0), "Assembler Log", TitledBorder.TOP, TitledBorder.CENTER));
		log("Assembler version 1.0");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2, 50, 0));
		mainPanel.setBorder(new EmptyBorder(0, 30, 0, 30));
		mainPanel.add(scrollPane);
		mainPanel.add(new JScrollPane(logText));
		
		
		compile = new JButton("Compile Program");
		compile.setEnabled(false);
		compile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					binary = AssemblerUtils.compileSource(codeText.getText());
					
					
				}catch(AssembleException ex){
					ex.printStackTrace();
				}
				
				
			}
		});
		
		saveAssembly = new JButton("Save Assembly");
		saveAssembly.setEnabled(false);
		saveAssembly.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				
				if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION){
					
				}
			}
		});
		
		saveBinary = new JButton("Save Binary");
		saveBinary.setEnabled(false);
		
		loadAssembly = new JButton("Load Assembly Program");
		loadAssembly.setEnabled(true);
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.add(loadAssembly);
		controlsPanel.add(saveAssembly);
		controlsPanel.add(compile);
		controlsPanel.add(saveBinary);
		
		panel.add(mainPanel, BorderLayout.CENTER);
		panel.add(controlsPanel, BorderLayout.SOUTH);
		panel.add(topPanel, BorderLayout.NORTH);
	}
	
	
	/**
	 * Utility function to save the text into the file
	 * 
	 * @param text The text to save to the file
	 * @param file The file to save to
	 */
	private void saveToFile(String text, File file){
		try {
			log("Saving to file " + file.getName() + " ...");
			
			//Check if it exists and ask for a warning message
			if(file.exists()){
				if(JOptionPane.showConfirmDialog(frame, "The file already exists, would you like to overwrite it?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
					return;
				}
			}
			
			//Write the text to the file
			FileWriter writer = new FileWriter(file);
			writer.write(text);
			writer.flush();
			writer.close();
			
			//Send success message
			log("File " + file.getName() + " saved successfully");
		} catch (IOException e) {
			log("Error saving to file: " + file.getName());
			e.printStackTrace();
		}
	}
	
	private void log(String msg){
		logText.setText(logText.getText() + "\n" + msg);
	}
}
