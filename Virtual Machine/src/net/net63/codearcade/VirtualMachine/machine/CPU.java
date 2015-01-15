package net.net63.codearcade.VirtualMachine.machine;

import javax.swing.JTextArea;

/**
 * 
 * A CPU class to abstract the inner mechanism of the virtual machine
 * 
 * @author Basim
 *
 */
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
		public static final int INSTRUCTION_TYPE = 15; 		// If set then it is a A-type instruction otherwise a C-type
		public static final int A = 12; 					// Whether to use A or memory at A
		public static final int C1 = 11; 					// Set A to 0
		public static final int C2 = 10; 					// Set A to NOT A
		public static final int C3 = 9; 					// Set B to 0
		public static final int C4 = 8; 					// Set B to NOT B
		public static final int C5 = 7; 					// If set OUT is A + B otherwise it is A & B
		public static final int C6 = 6;						// Set OUT to NOT OUT
		public static final int D1 = 5;						// Set AdressRegister to OUT
		public static final int D2 = 4;						// Set DataRegister to OUT
		public static final int D3 = 3;						// Set Memory at A to OUT
		public static final int J1 = 2;						// JUMP if less than
		public static final int J2 = 1;						// JUMP if zero
		public static final int J3 = 0;						// JUMP if greater than
															// JUMP changes PC to value at A
		//General purpose bits
		public static final int SHORT_SIGN_BIT = 15;
	}
	
	private int currentInstruction;
	
	private JTextArea logText;
	
	public CPU(Memory memory, JTextArea logText){
		this.RAM = memory;
		this.logText = logText;
		
		pcRegister = Constants.SEGMENTS.CODE.getAddress();
		
		log("Created");
	}
	
	/**
	 * Writes a log message
	 * 
	 * @param message The message to log
	 */
	private void log(String message){
		logText.setText(logText.getText() + "\nCPU: \t\t" + message);
	}
	
	/**
	 * Executes the value in the current instruction
	 * 
	 */
	private void execute(){
		if(getBit(currentInstruction, BITS.INSTRUCTION_TYPE)){
			log("Executing Address Intruction");
			
			//Set the address
			addressRegister = (short) currentInstruction;
			
			//Remove the first bit
			addressRegister = (short) (addressRegister & MASKS.USHORT);
			
			log("Changed Address To: "  + addressRegister + ", 0b" + IntegerUtils.paddedBinaryString(addressRegister));
			
		}else{
			log("Executing Compute Intruction");
			
			//Integers for unsigned arithmetic and holding values
			int a;
			int b;
			int out;
			
			//If A bit is set then use memory at address register otherwise use register itself
			if(getBit(currentInstruction, BITS.A)){
				b = RAM.getWord(addressRegister);
			}else{
				b = addressRegister;
			}
			
			//Set b to the dataRegister
			a = dataRegister;
			
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
				b = 0;
			}
			
			//If bit is set then set B to not B and mask to make sure that unsigned shorts don't become negative
			if(getBit(currentInstruction, BITS.C4)){
				b = (~b) & MASKS.SHORT;
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
				log("Storing in address register");
				addressRegister = (short) out;
			}
			
			//If D bit is set then store it in the D-register
			if(getBit(currentInstruction, BITS.D2)){
				log("Storing in data register");
				dataRegister = (short) out;
			}
			
			//If A[M] bit is set then store it in the A[M]-register
			if(getBit(currentInstruction, BITS.D3)){
				log("Storing in memory location " + addressRegister);
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
	
	/**
	 * Gets a certain bit from an integer
	 * 
	 * @param value The integer to get the bit from
	 * @param position The index of the bit with 0 being the rightmost bit
	 * @return true if the bit is set false otherwise
	 */
	private boolean getBit(int value, int position){
		return ((value >> position) & 0x01) == 1;
	}
	
	/**
	 * Runs the fetch part, loads the next instruction into the currentInstruction register
	 */
	private void fetch(){
		currentInstruction = RAM.getWord(pcRegister);
		log(IntegerUtils.paddedBinaryString(currentInstruction));
		incPC();
	}

	/**
	 * Step one instruction in the CPU consisting of a fetch-execute cycle
	 */
	public void stepInstruction() {
		fetch();
		execute();
	}
	
	/**
	 * Move the PC counter onwards
	 */
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
