package net.net63.codearcade.VirtualMachine;

public class Test {
	
	public static void main(String[] args){
		Memory mem = new Memory(10);
		
		mem.setWord(0, 1 << 10);
		
		System.out.println(mem.getWord(0));
	}
	
}
