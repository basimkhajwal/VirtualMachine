package net.net63.codearcade.VirtualMachine;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Machine {
	
	private Memory memory;
	private CPU cpu;
	
	private BufferedImage videoBuffer;
	
	private final int mask = 0b11;
	
	private int clockDeltaTime;
	private boolean justUpdated;
	
	public boolean[] keys;
	private boolean keyChanged;
	
	public Machine(){
		
		videoBuffer = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
		
		memory = new Memory(Constants.MEMORY_SIZE);
		
		//Add some test code
		String[] assemblyCode = new String[]{
				"1000000000000101",
				"0110000010010000",
				"1000000000000010",
				"0110000010000000"
		};
		
		for(int i = 0; i < assemblyCode.length; i++){
			
			memory.setWord(Constants.SEGMENTS.CODE.getAddress() + i * 2, Integer.parseInt(assemblyCode[i], 2));
		}
		
		//Test video memory
		memory.setByte(Constants.SEGMENTS.VIDEO.getAddress(), Integer.parseInt("11000011", 2));
		
		cpu = new CPU(memory);
		
		clockDeltaTime = 0;
		
		keys = new boolean[300];
	}
	
	public void update(int deltaTime){
		justUpdated = false;
		
		if(clockDeltaTime > Constants.CLOCK_TIME){
			justUpdated = true;
			
			updateInputBuffers();
			cpu.stepInstruction();
			updateOutputBuffers();
			
			clockDeltaTime = 0;
			
			System.out.println();
		}
		
		clockDeltaTime += deltaTime;
	}
	
	private void updateInputBuffers(){
		if(keyChanged){
			keyChanged = false;
			
			for(int i = 0; i < keys.length; i++){
				memory.setByte(Constants.SEGMENTS.KEYBOARD.getAddress() + i, (keys[i]) ? 0xFF: 0x00);
			}
		}
	}
	
	private void updateOutputBuffers(){
		int[] pixels = memory.getLength(Constants.SEGMENTS.VIDEO.getAddress(), Constants.SEGMENTS.VIDEO.getLength());
		
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
			
			
			
			videoBuffer.setRGB((i % 100), (int) (i / 100), (new Color(r, g, b, a)).getRGB());
		}
		
		videoBuffer.flush();
	}
	
	public BufferedImage getVideoBuffer(){
		return videoBuffer;
	}
	
	public boolean isUpdated(){
		return justUpdated;
	}
	
	public void keyPressed(int keycode){
		keys[keycode] = true;
		keyChanged = true;
	}
	
	public void keyReleased(int keycode){
		keys[keycode] = false;
		keyChanged = false;
	}
}
