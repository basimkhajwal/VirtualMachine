package net.net63.codearcade.VirtualMachine.machine;

import javax.swing.JTextPane;


public class Memory {
	
	
	
	private byte[] memory;
	
	private JTextPane logText;
	
	public Memory(int size, JTextPane logText){
		memory = new byte[size];
		this.logText = logText;
		
		log("Created size: " + size + "bytes");
		
		log("Code size: \t" + Constants.SEGMENTS.CODE.getLength());
		log("Video size: \t" + Constants.SEGMENTS.VIDEO.getLength());
		log("Keyboard size: \t" + Constants.SEGMENTS.KEYBOARD.getLength());
		log("Data size: \t" + Constants.SEGMENTS.DATA.getLength());
	}
	
	private void log(String message){
		logText.setText(logText.getText() + "\nMemory: \t" + message);
	}
	
	public int[] getLength(int address, int length){
		int[] ret = new int[length];
		
		for(int i = address; i < address + length; i++){
			ret[i - address] = memory[i];
		}
		
		return ret;
	}

	public void setLength(int address, int[] values) {
		for(int i = address;i < address + values.length; i++){
			memory[i] = (byte) values[i - address];
		}
		
	}

	public void setByte(int address, int value) {
		memory[address] = (byte) value;
	}

	public int getByte(int address) {
		return memory[address & 0x7FFF];
	}

	public void setWord(int address, int value) {
		setByte(address + 1, value & 0xFF);
		setByte(address, (value >> 8) & 0xFF);
		
	}

	public int getWord(int address) {
		return ((memory[address] << 8) & 0xFF00) | ( memory[address + 1] & 0xFF);
	}
	
}
