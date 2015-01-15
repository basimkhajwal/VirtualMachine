package net.net63.codearcade.VirtualMachine.machine;

/**
 * A utility class to implement some commonly used methods and ones available in Java 1.8 that aren't in Java 1.7
 * 
 * @author Basim
 */
public class IntegerUtils {
	
	public static String toUnsignedBinaryString(int n){
		return Long.toBinaryString((long) n);
	}
	
	public static String toUnsignedHexString(int n){
		return Long.toHexString((long) n);
	}
	
	public static String paddedBinaryString(int n){
		return String.format("%16s", toUnsignedBinaryString(n)).replace(' ', '0');
	}
	
	public static String paddedHexString(int n){
		return String.format("%4s", toUnsignedHexString(n)).replace(' ', '0').toUpperCase();
	}
}
