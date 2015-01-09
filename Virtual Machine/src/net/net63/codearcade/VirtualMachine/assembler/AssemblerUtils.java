package net.net63.codearcade.VirtualMachine.assembler;

import java.util.HashMap;
import java.util.Iterator;

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
		return symbol.matches("[a-zA-Z]+");
	}
	
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
				
				if(line.contains("=")){
					String[] sections = line.split("=");
					
				}
			}
			
			lineNum++;
		}
		
		return binary.toString();
	}
	
	/**
	 * A private utility function to compute the value of the body of an A-type instruction
	 * 
	 * @param s The short s to parse
	 * @param symbols The hashmap of symbols generated earlier with the generateSymbols function
	 * @return An integer which is an error code if an error occurred otherwise the value of the number
	 */
	private static int parseShort(String s, HashMap<String,Integer> symbols){
		if(s.matches("[a-zA-Z]+")){
			
			if(symbols.containsKey(s)){
				return symbols.get(s);
			}else{
				return CODE_INVALID_SYMBOL;
			}
			
		}
		
		int num;
		
		if(s.startsWith("0x")){
			try{
				num = Integer.parseInt(s.substring(2), 16);
			}catch(Exception e){
				return CODE_INVALID_NUMBER;
			}
		}else if(s.startsWith("0b")){
			try{
				num = Integer.parseInt(s.substring(2), 2);
			}catch(Exception e){
				return CODE_INVALID_NUMBER;
			}
		}else{
			try{
				num = Integer.parseInt(s);
			}catch(Exception e){
				return CODE_INVALID_NUMBER;
			}
		}
		
		if(num > (1 << 16)){
			return CODE_LARGE;
		}
		
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
