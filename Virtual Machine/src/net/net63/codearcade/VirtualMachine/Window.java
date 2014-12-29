package net.net63.codearcade.VirtualMachine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Window implements Runnable, KeyListener{

	final int WIDTH = 800;
	final int HEIGHT = 600;
	
	private JFrame frame;
	private Canvas canvas;
	private BufferStrategy bufferStrategy;
	
	private boolean machineRunning;
	
	private Machine machine;
	
	private void newMachineFromFile(File file){
		
	}
	
	private void setupGUI(JPanel panel){
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel videoMemory = new JPanel();
		videoMemory.setSize(canvas.getWidth(), canvas.getHeight());
		videoMemory.add(canvas);
		tabbedPane.add("Video Memory", videoMemory);
		
		
		
		panel.add(tabbedPane, BorderLayout.LINE_START);
		
		
		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
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
		
		controls.add(loadProgram);
		controls.add(runProgram);
		
		panel.add(controls, BorderLayout.LINE_END);
	}
	
	public Window() {
		frame = new JFrame("Virtual Machine");
		
		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(new BorderLayout());
		
		canvas = new Canvas();
		canvas.setBounds(0, 0, 400, 400);
		canvas.setIgnoreRepaint(true);
		
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
		
		machine = new Machine();
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
		machine.update(deltaTime);
	}

	protected void render(Graphics2D g) {
		if(machine.isUpdated()){
			g.clearRect(0, 0, WIDTH, HEIGHT);
			g.drawImage(machine.getVideoBuffer(), 0, 0, 400, 400, 0, 0, 100, 100, null);
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