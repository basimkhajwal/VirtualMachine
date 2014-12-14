package net.net63.codearcade.VirtualMachine;

public class Machine {
	
	Memory memory;
	CPU cpu;
	
	private int clockDeltaTime;
	
	public Machine(){
		
		memory = new Memory(Constants.MEMORY_SIZE);
		memory.setWord(Constants.SEGMENTS.CODE.getAddress(), 0x0001);
		
		cpu = new CPU(memory);
		
		clockDeltaTime = 0;
	}
	
	public void update(int deltaTime){
		if(clockDeltaTime > Constants.CLOCK_TIME){
			cpu.stepInstruction();
			
			clockDeltaTime = 0;
		}
		
		clockDeltaTime += deltaTime;
	}
	
}
