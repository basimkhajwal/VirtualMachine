package net.net63.codearcade.VirtualMachine.machine;

/**
 * A utility class to implement some commonly used methods and ones available in Java 1.8 that aren't in Java 1.7
 * 
 * @author Basim
 */
public class IntegerUtils {
	
	public static String toUnsignedBinaryString(int n){
		return Long.toBinaryString((long) n).substring(0, 32);
	}
	
	public static String paddedBinaryString(int n){
		return String.format("%15s", toUnsignedBinaryString(n)).replace(' ', '0');
	}
}
