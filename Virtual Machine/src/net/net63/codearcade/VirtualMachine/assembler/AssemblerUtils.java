package net.net63.codearcade.VirtualMachine.assembler;

import java.awt.font.NumericShaper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AssemblerUtils {
	
	//Codes
	private static final String LINE_DELIMITER = "\n";
	
	private static final int CODE_LARGE = (1 << 16) + 1;
	private static final int CODE_INVALID_NUMBER = (1 << 16) + 2;
	private static final int CODE_INVALID_SYMBOL = (1 << 16) + 3;
	
	//Errors
	private static final String GENERIC_ERROR = "Error in line %d, ";
	
	private static final String SYMBOL_ERROR = GENERIC_ERROR + "invalid name for comment. It must contain only characters, case sensitive";
	private static final String NUMBER_TOO_LARGE = GENERIC_ERROR + "invalid size of number";
	private static final String INVALID_NUMBER_ARGUMENT = GENERIC_ERROR + "the pattern %s is not recognised as a binary, decimal or hex number";
	private static final String INVALID_SYMBOL = GENERIC_ERROR + "invalid symbol found";
	private static final String INVALID_DESTINATION = GENERIC_ERROR + "invalid destionation, can only be A, AM, AD, MD, or AMD";
	private static final String INVALID_COMPUTATION = GENERIC_ERROR + "invalid computation %s";
	
	/**
	 * A utility function to assemble my own assembly language to the respective binary
	 * 
	 * The steps are as follows:
	 * 1. Remove all comments (beginning with a double backslash)
	 * 2. Generate a list of all labels defined in it and remove those lines
	 * 3. Replace all occurrences of any of the symbols to the respective memory location
	 * 4. Loop through each line and generate the pseudo-binary
	 * 5. Loop through the binary and generate the bytes
	 * 
	 * @param source The source string containing valid assembly code separated by a newline character
	 * @return The binary string to be saved to a file
	 * @throws AssembleException
	 */
	@SuppressWarnings("unchecked")
	public static String compileSource(String source) throws AssembleException{
		//1. Remove all comments
		source = removeComments(source);
		
		//2. Generate the symbols
		Object[] returnValues = generateSymbols(source);
		HashMap<String, Integer> symbols = (HashMap<String, Integer>) returnValues[0];
		source = (String) returnValues[1];
		
		for(String s: symbols.keySet()){
			System.out.println(s + ": " + symbols.get(s));
		}
		
		//3. Replace symbols
		source = replaceSymbols(source, symbols);
		
		//4. Generate the pseudo-binary
		source = generatePsuedoBinary(source, symbols);
		
		
		
		
		//Return the final source
		return source;
	}
	
	/**
	 * Removes all comments (starting with a double backslash) in the source 
	 * 
	 * @param source The source to remove the comments from
	 * @return The modified source with the comments removed
	 */
	private static String removeComments(String source){
		//Declare the final string builder for the final source
		StringBuilder finalSource = new StringBuilder();
		
		for(String line: source.split(LINE_DELIMITER)){
			//Split the file with the double backslash and return the first value
			finalSource.append(line.split("//")[0].trim()).append(LINE_DELIMITER); //Only attach the first part from the first double slash in the line and a new line character
		}
		
		//Return the string value that we have created
		return finalSource.toString();
	}
	
	/**
	 * @param source The source lines with comments removed to generate the symbols from
	 * @return An 2-wide array of objects, the first is the a HashMap<String, Integer> of the generated symbols, the second is the final modified string with all the needed symbols removed
	 * @throws AssembleException
	 */
	private static Object[] generateSymbols(String source) throws AssembleException{
		//The final return objects
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		StringBuilder finalSource = new StringBuilder();
		
		//Counter variables , one is the currentByte at memory that the line would be at
		//the second one is the line physically in the source file that it is
		int currentByteOfMemory = 0;
		int lineNumber = 1;
		
		for(String line: source.split(LINE_DELIMITER)){
			
			//If line is a symbol declaration
			if(line.startsWith("(") && line.endsWith(")")){
				String sym = line.substring(1, line.length() - 1);
				
				//Check if it is a valid symbol and make sure that it isn't already defined
				if(isValidSymbol(sym) && ! map.containsKey(sym)){
					//Put the symbol with the current byte of memory
					map.put(sym, currentByteOfMemory);
					
					//Memory declarations are only one byte
					currentByteOfMemory += 1;
				}else{
					//Invalid symbol type
					throw new AssembleException(String.format(SYMBOL_ERROR, lineNumber));
				}
				
			}else{
				//Otherwise it is a normal two byte instruction
				finalSource.append(line + LINE_DELIMITER);
				currentByteOfMemory += 2;
			}
			
			//Iterate over to the next line
			lineNumber++;
		}
		
		//Return the array of return values
		return new Object[]{map, finalSource.toString()};
	}
	
	/**
	 * Explains it self :)
	 * 
	 * @param symbol The symbol to check
	 * @return Whether it is a valid symbol
	 */
	private static boolean isValidSymbol(String symbol){
		return symbol.matches("[a-zA-Z]+"); //The symbol can only contain letters
	}
	
	/**
	 * Function to replace all values in the source string that are keys of the hashmap with their respective integer values
	 * 
	 * @param source The original source file
	 * @param symbols The hashmap of symbols previously generated
	 * @return The modified source file
	 */
	private static String replaceSymbols(String source, HashMap<String, Integer> symbols){
		Iterator<String> it = symbols.keySet().iterator();

		while(it.hasNext()){
			String sym = it.next();
			source.replace(sym, ""+symbols.get(sym));
		}
		
		return source;
	}
	
	private static String generatePsuedoBinary(String source, HashMap<String, Integer> symbols) throws AssembleException{
		StringBuilder binary = new StringBuilder();
		
		int lineNum = 1;
		
		for(String line: source.split(LINE_DELIMITER)){
			if(line.startsWith("@")){
				int num = parseShort(line.substring(1), symbols);
				String errorMsg = null;
				
				if(num == -2){
					errorMsg = String.format(NUMBER_TOO_LARGE, lineNum);
				}else if(num == -1){
					errorMsg = String.format(INVALID_NUMBER_ARGUMENT, lineNum, line.substring(1));
				}else if( num == -3){
					errorMsg = String.format(INVALID_SYMBOL, lineNum);
				}
				
				if(errorMsg != null){
					throw new AssembleException(errorMsg);
				}else{
					binary.append(String.format("1%15s%s", Integer.toBinaryString(num), LINE_DELIMITER).replace(' ', '0'));
				}
				
				
			}else{
				//The binary builder for use
				char[] bitBuilder = new char[16];
				
				//Fill it with zero
				for(int i = 0; i < 16; i++){
					bitBuilder[i] = '0';
				}
				
				if(line.contains("=")){
					String[] sections = line.split("=");
					
					if(sections[0].matches("[A]?[M]?[D]?")){
						if(sections[0].contains("A")){
							bitBuilder[10] = '1';
						}
						
						if(sections[0].contains("D")){
							bitBuilder[11] = '1';
						}
						
						if(sections[0].contains("M")){
							bitBuilder[12] = '1';
						}
						
						if(parseControlBits(sections[1].split(";")[0], bitBuilder) != 0){
							throw new AssembleException(String.format(INVALID_COMPUTATION, lineNum, sections[1].split(";")[0]));
						}
					}else{
						throw new AssembleException(String.format(INVALID_DESTINATION, lineNum));
					}
					
					
				}
				
				
				
			}
			
			lineNum++;
		}
		
		return binary.toString();
	}
	
	
	/**
	 * Replaces all occurrences of newline, spaces and tabs with empty strings.
	 * 
	 * @param s The string to use
	 * @return The final modified string
	 */
	private static String removeWhitespace(String s){
		return s.replace("\n", "").replace(" ", "").replace("\t", "");
	}
	
	/**
	 * Another utility function that replaces the JMP section with the appropriate bits in the array
	 * 
	 * @param jmp The string of the jump section in the code
	 * @param bits
	 * @return A non-zero integer on error
	 */
	private static int parseJumpBits(String jmp, char[] bits){
		
		//Remove the whitespace
		jmp = removeWhitespace(jmp);
		
		String finalBits;
		
		switch(jmp){
			
			case "JMP":
				finalBits = "111";
				break;
			
			case "JLE":
				finalBits = "110";
				break;
				
			case "JNE":
				finalBits = "101";
				break;
				
			case "JLT":
				finalBits = "100";
				break;
				
			case "JGE":
				finalBits = "011";
				break;
				
			case "JEQ":
				finalBits = "010";
				break;
				
			case "JGT":
				finalBits = "001";
				break;
				
			case "":
				finalBits = "000";
				break;
				
			default:
				return -1;
		}
		
		//Finally put the bits into the array
		for(int i = 0; i < 3; i++){
			bits[i + 13] = finalBits.charAt(i);
		}
		
		//Return that it was a success
		return 0;
	}
	
	
	/**
	 * Utility function to parse the control section and fill the array with the correct bits
	 * 
	 * 
	 * @param comp The compute value of the line in assembly
	 * @param bits The array of bits to fill
	 * @return A non-zero integer on error
	 */
	private static int parseControlBits(String comp, char[] bits){
		
		//Remove the whitespace
		comp = removeWhitespace(comp);
		
		//The final bits to put in the array, from address 
		String finalBits;
		
		//Case statement that checks all possible cases that the computation can be
		//and for each one generated the correct bits to put in the instruction
		switch(comp){
			//When A=0, use the value of the A register
			case "0": case "":
				finalBits = "0101010";
				break;
				
			case "1":
				finalBits = "0111111";
				break;
			
			case "-1":
				finalBits = "0111010";
				break;
				
			case "D":
				finalBits = "0001100";
				break;
				
			case "A":
				finalBits = "0110000";
				break;
				
			case "!D":
				finalBits = "0001101";
				break;
				
			case "!A":
				finalBits = "0110001";
				break;
			
			case "-D":
				finalBits = "0001111";
				break;
			
			case "-A":
				finalBits = "0110011";
				break;
			
			case "D+1":
				finalBits = "0011111";
				break;
			
			case "A+1":
				finalBits = "0110001";
				break;
			
			case "D-1":
				finalBits = "0001110";
				break;
			
			case "A-1":
				finalBits = "0110010";
				break;
			
			case "D+A":
				finalBits = "0000010";
				break;
			
			case "D-A":
				finalBits = "0010011";
				break;
			
			case "A-D":
				finalBits = "0000111";
				break;
				
			case "D&A":
				finalBits = "0000000";
				break;
			
			case "D|A":
				finalBits = "0010101";
				break;
				
			//When A=1, use the memory at A register instead
			case "M":
				finalBits = "1110000";
				break;
			
			case "!M":
				finalBits = "1110001";
				break;
			
			case "-M":
				finalBits = "1110011";
				break;
			
			case "M+1":
				finalBits = "1110111";
				break;
				
			case "M-1":
				finalBits = "1110010";
				break;
				
			case "D+M":
				finalBits = "1000010";
				break;
				
			case "D-M":
				finalBits = "1010011";
				break;
				
			case "M-D":
				finalBits = "1000111";
				break;
				
			case "D&M":
				finalBits = "1000000";
				break;
				
			case "D|M":
				finalBits = "1010101";
				break;
			
			//If command isn't found, return an error
			default:
				return -1;
				
		}
		
		//Finally, put the bits in the char array
		for(int i = 0; i < 7; i++){
			bits[i + 3] = finalBits.charAt(i);
		}
		
		//Return that it was a success
		return 0;
	}
	
	/**
	 * A private utility function to compute the value of the body of an A-type instruction
	 * 
	 * @param s The short s to parse
	 * @param symbols The hash map of symbols generated earlier with the generateSymbols function
	 * @return An integer which is an error code if an error occurred otherwise the value of the number
	 */
	private static int parseShort(String s, HashMap<String,Integer> symbols){
		//Check if it contains any letters
		if(s.matches("[a-zA-Z]+")){
			
			//If it is a valid symbol use that value, otherwise return an error
			if(symbols.containsKey(s)){
				return symbols.get(s);
			}else{
				return CODE_INVALID_SYMBOL;
			}
			
		}
		
		//The final number to return
		int num;
		
		//Check it is hex,binary or decimal and then try parse, otherwise error
		if(s.startsWith("0x")){
			try{
				num = Integer.parseInt(s.substring(2), 16);
			}catch(NumberFormatException e){
				return CODE_INVALID_NUMBER;
			}
		}else if(s.startsWith("0b")){
			try{
				num = Integer.parseInt(s.substring(2), 2);
			}catch(NumberFormatException e){
				return CODE_INVALID_NUMBER;
			}
		}else{
			try{
				num = Integer.parseInt(s);
			}catch(NumberFormatException e){
				return CODE_INVALID_NUMBER;
			}
		}
		
		//Make sure that it isn't too large
		if(num > (1 << 16)){
			return CODE_LARGE;
		}
		
		//Return the final number
		return num;
	}
	
	/**
	 * A custom exception class which describes when a certain exception has occurred during a certain assembling stage
	 * 
	 * 
	 * @author Basim
	 *
	 */
	public static class AssembleException extends Exception{

		private static final long serialVersionUID = 1L;
		
		public AssembleException(String message){
			super(message);
		}
	}
}
