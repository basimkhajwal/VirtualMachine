package net.net63.codearcade.VirtualMachine;

public class Constants {
	
	public static final int WORD_SIZE = 16;
	
	public static final int MEMORY_SIZE = (int) Math.pow(2, WORD_SIZE - 1);
	
	public static final int VIDEO_WIDTH = 100;
	public static final int VIDEO_HEIGHT = 100;
	
	public static final int[] COLOR_VALUES = new int[]{0,64,128,255};
	
	public static class SEGMENTS{
		public static final Segment CODE = new Segment(0, 10000);
		public static final Segment VIDEO = new Segment(CODE.getEndPoint(), 10000);
		public static final Segment KEYBOARD = new Segment(VIDEO.getEndPoint(), 300);
		public static final Segment DATA = new Segment(KEYBOARD.getEndPoint(), MEMORY_SIZE - VIDEO.getEndPoint() );
	
	}
	
	public static final float CLOCK_SPEED = 1f;
	public static final float CLOCK_TIME = 1000 / CLOCK_SPEED;
}
