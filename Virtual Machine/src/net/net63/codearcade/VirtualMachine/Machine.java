package net.net63.codearcade.VirtualMachine;

public class Machine {
	
	Memory memory;
	CPU cpu;
	
	private int clockDeltaTime;
	
	public Machine(){
		
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
