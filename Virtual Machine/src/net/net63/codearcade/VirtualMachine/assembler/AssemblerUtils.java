package net.net63.codearcade.VirtualMachine.assembler;

import java.util.HashMap;
import java.util.Iterator;

public class AssemblerUtils {
	
	//Errors
	private static final String SYMBOL_ERROR = "Error in line %d, invalid name for comment. It must start with a character or underscore and contain only characters and numbers";
	private static final String NUMBER_TOO_LARGE = "Error in line %d, invalid size of number";
	
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
		
		//3. Replace symbols
		source = replaceSymbols(source, symbols);
		
		//4. Generate the pseudo-binary
		source = generatePsuedoBinary(source);
		
		
		
		
		
		return source;
	}
	
	private static String removeComments(String source){
		StringBuilder finalSource = new StringBuilder();
		
		for(String line: source.split("\n")){
			finalSource.append(line.split("//")[0].trim()).append("\n"); //Only attach the first part from the first double slash in the line and a new line character
		}
		
		return finalSource.toString();
	}
	
	private static Object[] generateSymbols(String source) throws AssembleException{
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		StringBuilder finalSource = new StringBuilder();
		
		int currentByteOfMemory = 0;
		int lineNumber = 1;
		
		for(String line: source.split("\n")){
			String sym = line.substring(1, line.length() - 1);
			
			if(line.startsWith("(") && line.endsWith(")")){
				if(isValidSymbol(sym)){
					map.put(sym, currentByteOfMemory);
					currentByteOfMemory += 1;
				}else{
					throw new AssembleException(String.format(SYMBOL_ERROR, lineNumber));
				}
			}else{
				finalSource.append(line + "\n");
				currentByteOfMemory += 2;
			}
			
			lineNumber++;
		}
		
		return new Object[]{map, finalSource.toString()};
	}
	
	private static boolean isValidSymbol(String symbol){
		return symbol.matches("^[a-zA-Z_][a-zA-Z_0-9]");
	}
	
	private static String replaceSymbols(String source, HashMap<String, Integer> symbols){
		Iterator<String> it = symbols.keySet().iterator();

		while(it.hasNext()){
			String sym = it.next();
			source.replace(sym, ""+symbols.get(sym));
		}
		
		return source;
	}
	
	private static String generatePsuedoBinary(String source) throws AssembleException{
		StringBuilder binary = new StringBuilder();
		
		for(String line: source.split("\n")){
			if(line.startsWith("@")){
				int num = parseShort(line.substring(1));
				
				if(num == -2){
					
				}else if(num == -1){
					
				}else{
					
				}
				
			}else{
				
			}
		}
		
		return binary.toString();
	}
	
	private static int parseShort(String s){
		int num;
		
		if(s.startsWith("0x")){
			try{
				num = Integer.parseInt(s.substring(2), 16);
			}catch(Exception e){
				return -1;
			}
		}else if(s.startsWith("0b")){
			try{
				num = Integer.parseInt(s.substring(2), 2);
			}catch(Exception e){
				return -1;
			}
		}else{
			try{
				num = Integer.parseInt(s);
			}catch(Exception e){
				return -1;
			}
		}
		
		if(num > (1 << 16)){
			return -2;
		}
		
		return num;
	}
	
	public static class AssembleException extends Exception{

		private static final long serialVersionUID = 1L;
		
		public AssembleException(String message){
			super(message);
		}
	}
}
