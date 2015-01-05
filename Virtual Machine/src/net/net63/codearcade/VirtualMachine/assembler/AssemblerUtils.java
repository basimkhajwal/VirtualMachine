package net.net63.codearcade.VirtualMachine.assembler;

public class AssemblerUtils {

	/**
	 * A utility function to assemble my own assembly language to the respective binary
	 * 
	 * The steps are as follows:
	 * 1. Remove all comments (beginning with a double backslash)
	 * 2. Generate a list of all labels defined in
	 * 
	 * @param source The source string containing valid assembly code separated by a newline character
	 * @return The binary string to be saved to a file
	 * @throws AssembleException
	 */
	public static String compileSource(String source) throws AssembleException{
		StringBuilder binary = new StringBuilder();
		
		//1. Remove all comments
		source = removeComments(source);
		
		return source;
	}
	
	private static String removeComments(String source){
		StringBuilder finalSource = new StringBuilder();
		
		for(String line: source.split("\n")){
			finalSource.append(line.split("//")[0]); //Only attach the first part from the first double slash in the line
		}
		
		return source;
	}
	
	public class AssembleException extends Exception{

		private static final long serialVersionUID = 1L;
		
		public AssembleException(String message){
			super(message);
		}
	}
}
