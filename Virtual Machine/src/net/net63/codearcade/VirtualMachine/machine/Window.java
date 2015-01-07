package net.net63.codearcade.VirtualMachine.machine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

public class Window implements Runnable, KeyListener{

	final int WIDTH = 1000;
	final int HEIGHT = 650;
	
	private JFrame frame;
	private JTable memoryTable;
	private JButton loadProgram, runProgram, pauseProgram, stopProgram, stepProgram;
	private JTextArea logText;
	private JTabbedPane tabbedPane;
	private JSlider frameRateSlider;
	private JLabel addressLabel, dataLabel, pcLabel;
	
	private MyModel tableModel;
	private Canvas canvas;
	private BufferStrategy bufferStrategy;
	
	private boolean machineSetup;
	private boolean machineRunning;
	
	private Machine machine;
	
	private void newMachineFromFile(File file){
		
		try {
			//Get the file reader
			InputStream reader = new FileInputStream(file);
			
			//Read the data into an array
			int[] code = new int[Constants.SEGMENTS.CODE.getLength()];
			for(int i = 0; i < code.length; i++){
				int next = reader.read();
				if(next < 0) break;
				code[i] = next;
			}
			
			//Close the reader
			reader.close();
			
			//Create a new machine
			machine = new Machine(logText);
			
			//Put the code into memory
			machine.loadCode(code);
			
			//The machine is now loaded
			machineSetup = true;
			machineRunning = false;
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frame, "Error loading this file, try again");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Error reading fromt the file specified, try again");
		} 
		
	}
	
	private void setupGUI(JPanel panel){
		panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		panel.setLayout(new BorderLayout());
		
		canvas = new Canvas();
		canvas.setBounds(0, 0, 450, 450);
		canvas.setIgnoreRepaint(true);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.setMaximumSize(new Dimension(WIDTH, 450));
		
		JPanel videoMemory = new JPanel();
		videoMemory.setSize(canvas.getWidth(), canvas.getHeight());
		videoMemory.add(canvas);
		tabbedPane.add("Video Memory", videoMemory);
		
		JPanel allMemory = new JPanel(new BorderLayout());
		
		tableModel = new MyModel();
		
		memoryTable = new JTable(tableModel);
		memoryTable.setPreferredScrollableViewportSize(new Dimension(400, 350));
		memoryTable.setFillsViewportHeight(true);
		
		allMemory.add(new JScrollPane(memoryTable), BorderLayout.NORTH);
		
		JPanel registerView = new JPanel();
		registerView.setLayout(new BorderLayout());
		registerView.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Registers", TitledBorder.CENTER, TitledBorder.TOP));
		
		GridLayout grid = new GridLayout(3,1);
		grid.setVgap(10);
		
		JPanel registerGrid = new JPanel(grid);
		registerGrid.setBorder(new EmptyBorder(10, 50, 10, 0));
		
		addressLabel = new JLabel("Address Register:");
		dataLabel = new JLabel("Data Register: ");
		pcLabel = new JLabel("Program Counter Register: ");
		
		registerGrid.add(addressLabel);
		registerGrid.add(dataLabel);
		registerGrid.add(pcLabel);
		
		registerView.add(registerGrid, BorderLayout.CENTER);
		
		allMemory.add(registerView, BorderLayout.CENTER);
		
		tabbedPane.add("Memory & Registers", allMemory);
		
		JPanel logPanel = new JPanel(new BorderLayout());
		logText = new JTextArea(11,30);
		logText.setEditable(false);
		logText.setMaximumSize(new Dimension(WIDTH - 30, 550));
		
		logPanel.add(new JScrollPane(logText));
		
		tabbedPane.add("Log", logPanel);
		
		panel.add(tabbedPane, BorderLayout.NORTH);
		
		JPanel controls = new JPanel();
		controls.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Controls", TitledBorder.CENTER, TitledBorder.TOP));
		controls.setLayout(new FlowLayout());
		controls.setSize(800 - tabbedPane.getWidth(), controls.getHeight());
		controls.setFocusable(false);
				
		frameRateSlider = new JSlider(0, 50, 1);
		frameRateSlider.setFocusable(false);
		frameRateSlider.setMajorTickSpacing(10);
		frameRateSlider.setMinorTickSpacing(2);
		frameRateSlider.setPaintTicks(true);
		frameRateSlider.setPaintLabels(true);
		frameRateSlider.setBorder( BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5,0,0,0), "Instructions Per Second:", TitledBorder.LEADING, TitledBorder.TOP));
		frameRateSlider.setPreferredSize(new Dimension(200, 50));
		
		stepProgram = new JButton("Step Instruction");
		stepProgram.setFocusable(false);
		stepProgram.setEnabled(false);
		stepProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				machine.stepInstruction();
			}
		});
		
		loadProgram = new JButton("Load Program");
		loadProgram.setFocusable(false);
		loadProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				
				if(fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
					newMachineFromFile(fileChooser.getSelectedFile());
					
					if(machineSetup){
						loadProgram.setEnabled(false);
						stopProgram.setEnabled(true);
						runProgram.setEnabled(true);
						stepProgram.setEnabled(true);
					}
				}
				
			}
		});
		loadProgram.setAlignmentY(Component.TOP_ALIGNMENT);
		
		runProgram = new JButton("Run Program");
		runProgram.setFocusable(false);
		runProgram.setEnabled(false);
		runProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(machineSetup){
					machineRunning = true;
					runProgram.setEnabled(false);
					pauseProgram.setEnabled(true);
					stopProgram.setEnabled(true);
					stepProgram.setEnabled(false);
					
				}else{
					JOptionPane.showMessageDialog(frame, "Please load a machine first!");
				}
			}
		});
		runProgram.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		pauseProgram = new JButton("Pause Program");
		pauseProgram.setFocusable(false);
		pauseProgram.setEnabled(false);
		pauseProgram.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				machineRunning = false;
				
				runProgram.setEnabled(true);
				pauseProgram.setEnabled(false);
				stepProgram.setEnabled(true);
			}
			
		});
		pauseProgram.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		stopProgram = new JButton("Close Program");
		stopProgram.setFocusable(false);
		stopProgram.setEnabled(false);
		stopProgram.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				machineRunning = false;
				machineSetup = false;
				machine = null;
				
				runProgram.setEnabled(false);
				stopProgram.setEnabled(false);
				pauseProgram.setEnabled(false);
				stepProgram.setEnabled(false);
				
				loadProgram.setEnabled(true);
			}
		});
		stopProgram.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		controls.add(loadProgram);
		controls.add(frameRateSlider);
		controls.add(runProgram);
		controls.add(stepProgram);
		controls.add(pauseProgram);
		controls.add(stopProgram);
		
		panel.add(controls, BorderLayout.CENTER);
	}
	
	private void updateTableData(){
		if(machine != null){
			machine.setTableData(tableModel);
			tableModel.fireTableDataChanged();
			
			memoryTable.repaint();
		}
	}
	
	public Window() {
		frame = new JFrame("Virtual Machine");
		
		machineRunning = false;
		machineSetup = false;
		
		JPanel panel = (JPanel) frame.getContentPane();
		
		setupGUI(panel); 	
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
		
		panel.addKeyListener(this);
		panel.setFocusable(true);
		panel.requestFocus();
		panel.requestFocusInWindow();
		
		canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();
		
		canvas.setFocusable(false);
	}

	private long desiredFPS = 60;
	private long desiredDeltaLoop = (1000 * 1000 * 1000) / desiredFPS;

	boolean running = true;

	@Override
	public void run() {

		long beginLoopTime;
		long endLoopTime;
		long currentUpdateTime = System.nanoTime();
		long lastUpdateTime;
		long deltaLoop;

		while (running) {
			beginLoopTime = System.nanoTime();
			if(canvas.isShowing()){
				render();
			}
			lastUpdateTime = currentUpdateTime;
			currentUpdateTime = System.nanoTime();
			update((int) ( (currentUpdateTime - lastUpdateTime) / (1000 * 1000) ) );

			endLoopTime = System.nanoTime();
			deltaLoop = endLoopTime - beginLoopTime;

			if (deltaLoop > desiredDeltaLoop) {
				
			} else {
				try {
					Thread.sleep((desiredDeltaLoop - deltaLoop) / (1000 * 1000));
				} catch (InterruptedException e) {
					
				}
			}
		}
	}

	private void render() {
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		
		render(g);
		
		g.dispose();
		bufferStrategy.show();
	}
	
	private void updateRegisterLabels(){
		int[] data = machine.getRegisterValues();
		
		pcLabel.setText("Program Counter Register: " + data[0]);
		addressLabel.setText("Address Register: " + data[1]);
		dataLabel.setText("Data Register: " + data[2]);
	}
	
	
	protected void update(int deltaTime) {
		if(machineRunning){
			machine.update(deltaTime);
			
			if(machine.isUpdated()){
				updateTableData();
				updateRegisterLabels();
			}
		}
	}

	protected void render(Graphics2D g) {
		g.clearRect(0, 0, WIDTH, HEIGHT);
		
		if(machineRunning && machine.isUpdated()){
			g.drawImage(machine.getVideoBuffer(), 0, 0, 450, 450, 0, 0, Constants.VIDEO_WIDTH, Constants.VIDEO_HEIGHT, null);
			
		}else if(!machineRunning){
			g.setColor(Color.RED);
			g.setFont(Font.getFont("Serif"));
			g.drawString("Machine Not Running", 50, 50);
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) {
		if(machineRunning){
			machine.keyPressed(e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(machineRunning){
			machine.keyReleased(e.getKeyCode());
		}
	}
	
	private class MyModel extends AbstractTableModel {
		
		private static final long serialVersionUID = -6604108493941342160L;

		private final String[] columns = new String[]{
				"Address",
				"Decimal",
				"Hexadecimal",
				"Binary"
		};
		
		private Object[][] tableData = new Object[Constants.MEMORY_SIZE][columns.length];
		
		@Override
		public int getRowCount() {
			return tableData.length;
		}
		
		@Override
		public String getColumnName(int col){
			return columns[col];
		}
		
		@Override
		public int getColumnCount() {
			return tableData[0].length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return tableData[rowIndex][columnIndex];
		}
		
		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex){
			super.setValueAt(value, rowIndex, columnIndex);
			
			tableData[rowIndex][columnIndex] = value;
			
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		
		
	}
}