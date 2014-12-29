package net.net63.codearcade.VirtualMachine;

public class CPU{
	
	private Memory RAM;
	
	//Registers
	private int pcRegister;
	private short addressRegister;
	private short dataRegister;
	
	//Flags
	private boolean flagZero;
	private boolean flagLessThan;
	
	//BIT Masks
	private class MASKS{
		public static final int SHORT =  0x0000FFFF;
		public static final int USHORT = 0x00007FFF;
	}
	
	private class BITS{
		//Instruction bits
		public static final int INSTRUCTION_TYPE = 15;
		public static final int A = 12;
		public static final int C1 = 11;
		public static final int C2 = 10;
		public static final int C3 = 9;
		public static final int C4 = 8;
		public static final int C5 = 7;
		public static final int C6 = 6;
		public static final int D1 = 5;
		public static final int D2 = 4;
		public static final int D3 = 3;
		public static final int J1 = 2;
		public static final int J2 = 1;
		public static final int J3 = 0;
		
		//General purpose bits
		public static final int SHORT_SIGN_BIT = 15;
	}
	
	private int currentInstruction;
	
	
	public CPU(Memory memory){
		this.RAM = memory;
		
		pcRegister = Constants.SEGMENTS.CODE.getAddress();
		
		log("Created");
		System.out.println();
	}
	
	private void log(String message){
		System.out.println("CPU: \t\t" + message);
	}
	
	private void execute(){
		if(getBit(currentInstruction, BITS.INSTRUCTION_TYPE)){
			log("Executing Address Intruction");
			
			//Set the address
			addressRegister = (short) currentInstruction;
			
			//Remove the first bit
			addressRegister = (short) (addressRegister & MASKS.USHORT);
			
			log("Changed Address To: "  + addressRegister + ", " + String.format("%15sh", Integer.toUnsignedString(addressRegister, 2) ) .replace(' ', '0')  );
			
		}else{
			log("Executing Compute Intruction");
			
			//Integers for unsigned arithmetic and holding values
			int a;
			int b;
			int out;
			
			//If A bit is set then use memory at address register otherwise use register itself
			if(getBit(currentInstruction, BITS.A)){
				a = RAM.getWord(addressRegister);
			}else{
				a = addressRegister;
			}
			
			//Set b to the dataRegister
			b = dataRegister;
			
			//If bit is set then set A to 0
			if(getBit(currentInstruction, BITS.C1)){
				a = 0;
			}
			
			//If bit is set then set A to not A, mask to make sure that unsigned shorts don't become negative
			if(getBit(currentInstruction, BITS.C2)){
				a = (~a) & MASKS.SHORT;
			}
			
			//If bit is set then set B to 0
			if(getBit(currentInstruction, BITS.C3)){
				a = 0;
			}
			
			//If bit is set then set B to not B and mask to make sure that unsigned shorts don't become negative
			if(getBit(currentInstruction, BITS.C4)){
				a = (~a) & MASKS.SHORT;
			}
			
			//If function is set then ADD A and B otherwise AND them
			if(getBit(currentInstruction, BITS.C5)){
				out = a + b;
			}else{
				out = a & b;
			}
			
			//If negate bit is there then negate output
			if(getBit(currentInstruction, BITS.C6)){
				out = (~out) & MASKS.SHORT;
			}
			
			//Set all the flags
			flagZero = out == 0;
			flagLessThan = getBit(out, BITS.SHORT_SIGN_BIT);
			
			//Store the outputs
			log("OUTPUT: " + out);
			
			//If A bit is set then store it in the A-register
			if(getBit(currentInstruction, BITS.D1)){
				addressRegister = (short) out;
			}
			
			//If D bit is set then store it in the D-register
			if(getBit(currentInstruction, BITS.D2)){
				dataRegister = (short) out;
			}
			
			//If A[M] bit is set then store it in the A[M]-register
			if(getBit(currentInstruction, BITS.D3)){
				RAM.setWord(addressRegister & 0x7FFF, (short) out);
			}
			
			boolean jump = false;
			
			//Jump flags
			if(getBit(currentInstruction, BITS.J1) && flagLessThan){
				jump = true;
			}
			
			if(getBit(currentInstruction, BITS.J2) && flagZero){
				jump = true;
			}
			
			if(getBit(currentInstruction, BITS.J3) && !flagLessThan){
				jump = true;
			}
			
			//JUMP if jump is true
			if(jump){
				pcRegister = addressRegister;
			}
		}
	}
	
	private boolean getBit(int value, int position){
		return ((value >> position) & 0x01) == 1;
	}
	
	private void fetch(){
		currentInstruction = RAM.getWord(pcRegister);
		log(String.format("%15sh", Integer.toUnsignedString(currentInstruction, 2) ) .replace(' ', '0'));
		incPC();
	}

	public void stepInstruction() {
		fetch();
		execute();
	}
	
	private void incPC(){
		pcRegister += 2;
	}
	
	public int getProgramCounter(){
		return pcRegister;
	}
	
	public int getAddressRegister(){
		return addressRegister;
	}
	
	public int getDataRegister(){
		return dataRegister;
	}
	
	public int getCurrentInstruction(){
		return currentInstruction;
	}
}
