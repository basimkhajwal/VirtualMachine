package net.net63.codearcade.VirtualMachine;

public class Machine {
	
	Memory memory;
	CPU cpu;
	
	private int clockDeltaTime;
	
	public Machine(){
		
		memory = new Memory(Constants.MEMORY_SIZE);
		
		memory.setWord(Constants.SEGMENTS.CODE.getAddress(), Integer.parseUnsignedInt("0000000011000011", 2));
		memory.setWord(Constants.SEGMENTS.CODE.getAddress() + 2, Integer.parseUnsignedInt("0000000100000110", 2));
		
		cpu = new CPU(memory);
		
		clockDeltaTime = 0;
	}
	
	public void update(int deltaTime){
		if(clockDeltaTime > Constants.CLOCK_TIME){
			cpu.stepInstruction();
			
			clockDeltaTime = 0;
			
			System.out.println();
		}
		
		clockDeltaTime += deltaTime;
	}
	
}
