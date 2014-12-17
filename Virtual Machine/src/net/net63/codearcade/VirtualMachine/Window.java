package net.net63.codearcade.VirtualMachine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window implements Runnable {

	final int WIDTH = 800;
	final int HEIGHT = 600;
	
	private final int mask = 0b11;
	
	JFrame frame;
	Canvas canvas;
	BufferStrategy bufferStrategy;
	
	Machine machine;

	public Window() {
		frame = new JFrame("Virtual Machine");

		JPanel panel = (JPanel) frame.getContentPane();
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(null);

		canvas = new Canvas();
		canvas.setBounds(0, 0, 400, 400);
		canvas.setIgnoreRepaint(true);

		panel.add(canvas);	

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);

		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		canvas.requestFocus();
		
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
		g.clearRect(0, 0, WIDTH, HEIGHT);
		render(g);
		g.dispose();
		bufferStrategy.show();
	}
	
	
	
	protected void update(int deltaTime) {
		machine.update(deltaTime);
	}

	protected void render(Graphics2D graphics) {
		int[] pixels = machine.memory.getLength(Constants.SEGMENTS.VIDEO.getAddress(), Constants.SEGMENTS.VIDEO.getLength());
		
		for(int i = 0; i < pixels.length; i++){
			int pixel = pixels[i];			
			int r,g,b,a;
			
			r = pixel & mask;
			g = (pixel >> 2) & mask;
			b = (pixel >> 4) & mask;
			a = (pixel >> 6) & mask;
			
			r = Constants.COLOR_VALUES[r];
			g = Constants.COLOR_VALUES[g];
			b = Constants.COLOR_VALUES[b];
			a = Constants.COLOR_VALUES[a];
			
			graphics.setColor(new Color(r, g, b, a));
			graphics.fillRect((i % 100) * 4, ((int)(i / 100)) * 4, 4, 4);
		}
	}

}