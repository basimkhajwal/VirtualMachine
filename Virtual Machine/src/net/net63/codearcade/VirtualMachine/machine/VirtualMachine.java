package net.net63.codearcade.VirtualMachine.machine;

public class VirtualMachine {

	public static void main(String[] args) {
		Window window = new Window();
		Thread t = new Thread(window);
		
		t.start();
	}

}
