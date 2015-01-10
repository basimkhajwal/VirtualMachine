package net.net63.codearcade.VirtualMachine.assembler;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.net63.codearcade.VirtualMachine.assembler.AssemblerUtils.AssembleException;

public class Window implements Runnable{
	
	private JButton compile, loadAssembly, saveAssembly, saveBinary;
	private JTextPane codeText;
	
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
		
		JScrollPane scrollPane = new JScrollPane(codeText);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		panel.add(mainPanel, BorderLayout.CENTER);
		
		JPanel controlsPanel = new JPanel();
		
		compile = new JButton("Compile Program");
		compile.setEnabled(false);
		compile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					String ret = AssemblerUtils.compileSource(codeText.getText());
				
					System.out.println(ret);
				}catch(AssembleException ex){
					ex.printStackTrace();
				}
				
				
			}
		});
		
		saveAssembly = new JButton("Save Assembly");
		saveAssembly.setEnabled(false);
		
		saveBinary = new JButton("Save Binary");
		saveBinary.setEnabled(false);
		
		loadAssembly = new JButton("Load Assembly Program");
		loadAssembly.setEnabled(true);
		
		controlsPanel.add(loadAssembly);
		controlsPanel.add(saveAssembly);
		controlsPanel.add(compile);
		controlsPanel.add(saveBinary);
		panel.add(controlsPanel, BorderLayout.SOUTH);
	}
}
