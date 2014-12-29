package net.net63.codearcade.VirtualMachine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Window implements Runnable, KeyListener{

	final int WIDTH = 800;
	final int HEIGHT = 600;
	
	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy bufferStrategy;
	
	private boolean machineSetup;
	private boolean machineRunning;
	
	private Machine machine;
	
	private void newMachineFromFile(File file){
		
		try {
			//Get the file reader
			InputStream reader = new FileInputStream(file);
			
			//Read the data into an array
			int[] code = new int[Constants.SEGMENTS.CODE.getLength()];
			for(int i = 0; i < code.length; i++){
				int next = reader.read();
				if(next < 0) break;
				code[i] = next;
			}
			
			//Close the reader
			reader.close();
			
			//Create a new machine
			machine = new Machine();
			
			//Put the code into memory
			machine.loadCode(code);
			
			//The machine is now loaded
			machineSetup = true;
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frame, "Error loading this file, try again");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Error reading fromt the file specified, try again");
		} 
		
	}
	
	private void setupGUI(JPanel panel){
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(new BorderLayout());
		
		canvas = new Canvas();
		canvas.setBounds(0, 0, 400, 400);
		canvas.setIgnoreRepaint(true);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel videoMemory = new JPanel();
		videoMemory.setSize(canvas.getWidth(), canvas.getHeight());
		videoMemory.add(canvas);
		tabbedPane.add("Memory", videoMemory);
		
		panel.add(tabbedPane, BorderLayout.LINE_START);
		
		JPanel controls = new JPanel();
		
		GridLayout grid = new GridLayout(4, 1);
		grid.setHgap(100);
		
		controls.setLayout(grid);
		controls.setSize(800 - tabbedPane.getWidth(), controls.getHeight());
		controls.setFocusable(false);
		
		JButton loadProgram = new JButton("Load Program");
		loadProgram.setFocusable(false);
		loadProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				
				if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
					newMachineFromFile(fileChooser.getSelectedFile());
				}
				
			}
		});
		
		JButton runProgram = new JButton("Run Program");
		runProgram.setFocusable(false);
		runProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(machineSetup){
					machineRunning = true;
				}else{
					JOptionPane.showMessageDialog(frame, "Please load a machine first!");
				}
			}
		});
		
		JButton pauseProgram = new JButton("Pause Program");
		pauseProgram.setFocusable(false);
		
		JButton stopProgram = new JButton("Stop Program");
		stopProgram.setFocusable(false);
		
		controls.add(loadProgram);
		controls.add(runProgram);
		controls.add(pauseProgram);
		controls.add(stopProgram);
		
		panel.add(controls, BorderLayout.CENTER);
	}
	
	public Window() {
		frame = new JFrame("Virtual Machine");
		
		machineRunning = false;
		machineSetup = false;
		
		JPanel panel = (JPanel) frame.getContentPane();
		setupGUI(panel); 	
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		
		
		panel.addKeyListener(this);
		panel.setFocusable(true);
		panel.requestFocus();
		panel.requestFocusInWindow();
		
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		
		canvas.setFocusable(false);
	}

	private long desiredFPS = 60;
	private long desiredDeltaLoop = (1000 * 1000 * 1000) / desiredFPS;

	boolean running = true;

	@Override
	public void run() {

		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		while (running) {
			beginLoopTime = System.nanoTime();

			render();

			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			update((int) ( (currentUpdateTime - lastUpdateTime) / (1000 * 1000) ) );

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;

			if (deltaLoop > desiredDeltaLoop) {
				
			} else {
				try {
					Thread.sleep((desiredDeltaLoop - deltaLoop) / (1000 * 1000));
				} catch (InterruptedException e) {
					
				}
			}
		}
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		
		render(g);
		
		g.dispose();
		bufferStrategy.show();
	}
	
	
	
	protected void update(int deltaTime) {
		if(machineRunning){
			machine.update(deltaTime);
		}
	}

	protected void render(Graphics2D g) {
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		if(machineRunning && machine.isUpdated()){
			g.drawImage(machine.getVideoBuffer(), 0, 0, 400, 400, 0, 0, 100, 100, null);
		}else if(!machineRunning){
			g.setColor(Color.RED);
			g.drawString("Machine Not Loaded", 50, 50);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) {
		if(machineRunning){
			machine.keyPressed(e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(machineRunning){
			machine.keyReleased(e.getKeyCode());
		}
	}
	
	

}