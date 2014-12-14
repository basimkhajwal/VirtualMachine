package net.net63.codearcade.VirtualMachine;

public class CPU implements ICPU{
	
	private Memory RAM;
	
	//Registers
	private int pcRegister;
	private int addressRegister;
	private int dataRegsiter;
	
	private int currentInstruction;
	
	
	public CPU(Memory memory){
		this.RAM = memory;
		
		pcRegister = Constants.SEGMENTS.CODE.getAddress();
		
		log("Created");
	}
	
	private void log(String message){
		System.out.println("CPU: \t\t" + message);
	}
	
	private void execute(){
		if(getBit(currentInstruction, 0) == 0){
			log("Executing Compute Intruction");
			
			
			
		}else{
			log("Executing Address Intruction");
		}
	}
	
	private int getBit(int value, int position){
		return (value >> position) & 0x01;
	}
	
	private void fetch(){
		currentInstruction = RAM.getWord(pcRegister);
		incPC();
	}

	public void stepInstruction() {
		fetch();
		execute();
	}
	
	private void incPC(){
		pcRegister +=2;
	}
	
}
