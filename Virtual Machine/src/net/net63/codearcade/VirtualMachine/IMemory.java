package net.net63.codearcade.VirtualMachine;


public interface IMemory {
	
	public void setByte(int address, int value);
	
	public int getByte(int address);
	
	public void setWord(int address, int value);
	
	public int getWord(int address);
	
	public int[] getLength(int address, int length);
	
	public void setLength(int address, int[] values);
}
