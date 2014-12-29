package net.net63.codearcade.VirtualMachine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileTester {

	public static void main(String[] args) {
		/*String[] assemblyCode = new String[]{
				"1000000000000101",
				"0110000010010000",
				"1000000000000010",
				"0110000010000000"
		};
		
		byte[] mem = new byte[assemblyCode.length * 2]; //Two bytes per instruction
		
		for(int i = 0; i < assemblyCode.length; i++){
			int op = Integer.parseInt(assemblyCode[i], 2);
			
			mem[i * 2] = (byte) (op >> 8);
			mem[i * 2 + 1] = (byte) (op & 0xFF);
		}
		
		try {
			OutputStream out = new FileOutputStream("test.txt" , false);
			out.write(mem);
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}*/
		
		
		try {
			InputStream in = new FileInputStream("test.txt");
			int next;
			while((next = in.read()) != -1){
				System.out.println(next);
				System.out.println(Integer.toUnsignedString(next, 2));
				System.out.println();
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
