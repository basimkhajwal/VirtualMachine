package net.net63.codearcade.VirtualMachine;


public class Memory implements IMemory{
	
	
	
	private byte[] memory;
	
	
	public Memory(int size){
		memory = new byte[size];
		
		log("Created size: " + size + "bytes");
		
		log("Code size: \t" + Constants.SEGMENTS.CODE.getLength());
		log("Video size: \t" + Constants.SEGMENTS.VIDEO.getLength());
		log("Keyboard size: \t" + Constants.SEGMENTS.KEYBOARD.getLength());
		log("Data size: \t" + Constants.SEGMENTS.DATA.getLength());
	}
	
	private void log(String message){
		System.out.println("Memory: \t" + message);
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
