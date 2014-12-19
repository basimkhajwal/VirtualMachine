package net.net63.codearcade.VirtualMachine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class Window implements Runnable, KeyListener{

	final int WIDTH = 800;
	final int HEIGHT = 600;
	
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	
	Machine machine;
	
	protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
	
	public void setupGUI(JPanel panel){
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JPanel videoMemory = new JPanel();
		videoMemory.setSize(canvas.getWidth(), canvas.getHeight());
		videoMemory.add(canvas);
		tabbedPane.add("Video Memory", videoMemory);
		
		
		
		panel.add(tabbedPane, BorderLayout.LINE_START);
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

		//panel.add(canvas);	

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
		
		machine = new Machine();
	}

	long desiredFPS = 60;
	long desiredDeltaLoop = (1000 * 1000 * 1000) / desiredFPS;

	boolean running = true;

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
		machine.keyPressed(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		machine.keyReleased(e.getKeyCode());
	}
	
	

}