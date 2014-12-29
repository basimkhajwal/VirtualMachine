package net.net63.codearcade.VirtualMachine.machine;

public class Segment {
	
	private int address;
	private int length;
	
	public Segment(int address, int length){
		this.address = address;
		this.length = length;
	}
	
	public int getAddress(){
		return address;
	}
	
	public int getEndPoint(){
		return address + length;
	}
	
	public int getLength(){
		return length;
	}
	
}
