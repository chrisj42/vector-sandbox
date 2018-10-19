import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

public class Runner extends JPanel {
	
	private static final int dimX = 1200, dimY = 600;
	
	public static final float GRID_RADIUS = 5;
	
	/*
		On the very left is a list of the equations to choose from; all you see is their names.
		
		on the bottom to the right, but taking up perhaps a little under half the vertical space, is the list of inputs for the current equation. it progresses from the left, with the first column containing any and all scalar inputs, and each successive column containing a single vector. Note that the vectors should be color-coded somehow; perhaps the label can be colored, or a colored bar above the vector's panel.
		
		Above the inputs are the input and output vector grids, input to the left, output to the right.
		To the right of the output grid, the scalar outputs are listed.
		
		Note that the output vectors need to be labeled somehow; a color-label key might be in order. Perhaps they will go above the scalar outputs, though still right of the output vector grid.
	 */
	
	private Equation equation;
	private JPanel colorKeyPanel; // cached here b/c you can't just get it directly from the current equation.
	
	private final VectorGrid inputGrid, outputGrid;
	private final JPanel mainPanel, outputPanel; // holds scalar output values, and vector output color key.
	
	private Runner() {
		// create the vector grids
		inputGrid = new VectorGrid(dimY/2, GRID_RADIUS*2);
		outputGrid = new VectorGrid(dimY/2, GRID_RADIUS*2);
		
		
		// holds the list of equations.
		JPanel listPanel = new JPanel(new GridLayout(0, 1));//makeBoxPanel(BoxLayout.PAGE_AXIS);
		
		// create/add the equations
		
		listPanel.add(new Equation(this, "2 Vectors",
			null,
			new VectorInput[2],
			new String[] {"dot product", "2D \"cross product\""},
			new String[] {"sum", "difference"},
			(ins, inv, outs, outv) ->
			{
				Vector v1 = inv.get("Vector 1");
				Vector v2 = inv.get("Vector 2");
				
				outv.get("sum").set(v1.cpy().add(v2));
				outv.get("difference").set(v1.cpy().sub(v2));
				//outs.get("length").set(outv.get("sum").len());
				
				outs.get("dot product").set(v1.x*v2.x + v1.y*v2.y);
				outs.get("2D \"cross product\"").set(v1.x*v2.y - v1.y*v2.x);
			})
		);
		
		/*listPanel.add(new Equation(this, "subtract",
			null,
			new VectorInput[2],
			new String[] {"length"},
			new String[] {},
			(ins, inv, outs, outv) ->
			{
				outv.get("difference").set(inv.get("Vector 1").cpy().sub(inv.get("Vector 2")));
				outs.get("length").set(outv.get("difference").len());
			})
		);
		
		listPanel.add(new Equation(this, "dot product",
			null,
			new VectorInput[2],
			new String[] {},
			null,
			(ins, inv, outs, outv) ->
			{
				Vector v1 = inv.get("Vector 1");
				Vector v2 = inv.get("Vector 2");
				outs.get("product").set(v1.x*v2.x + v1.y*v2.y);
			})
		);
		
		listPanel.add(new Equation(this, "2D \"cross product\"",
			null,
			new VectorInput[2],
			new String[] {"product"},
			null,
			(ins, inv, outs, outv) ->
			{
				Vector v1 = inv.get("Vector 1");
				Vector v2 = inv.get("Vector 2");
				outs.get("product").set(v1.x*v2.y - v1.y*v2.x);
			})
		);*/
		
		/*listPanel.add(new Equation(this, "vector from xy",
			new ScalarInput[] {new ScalarInput("x"), new ScalarInput("y")},
			null,
			null,
			new String[] {"vector"},
			(ins, inv, outs, outv) ->
			{
				outv.get("vector").set(ins.get("x").get(), ins.get("y").get());
			})
		);*/
		
		listPanel.add(new Equation(this, "1 Vector",
			null,
			new VectorInput[1],
			null,
			new String[] {"normalized", "xy-flip", "right-hand perp"},
			(ins, inv, outs, outv) ->
			{
				Vector v = inv.get("Vector 1");
				outv.get("normalized").set(v.cpy().nor());
				//noinspection SuspiciousNameCombination
				outv.get("xy-flip").set(v.y, v.x);
				//noinspection SuspiciousNameCombination
				outv.get("right-hand perp").set(-v.y, v.x);
			})
		);
		
		
		add(listPanel);
		add(Box.createHorizontalStrut(10));
		
		
		// right main panel (contains grid panels, input panel, output panel)
		mainPanel = makeBoxPanel(BoxLayout.PAGE_AXIS);
		
		// top part of main panel
		JPanel top = makeBoxPanel(BoxLayout.LINE_AXIS);
		top.add(inputGrid);
		top.add(Box.createHorizontalStrut(10));
		top.add(outputGrid);
		top.add(Box.createHorizontalStrut(10));
		
		// make output panel (still top of main panel)
		outputPanel = new JPanel(new GridLayout(2, 1));//makeBoxPanel(BoxLayout.PAGE_AXIS);
		outputPanel.setPreferredSize(new Dimension(300, 300));
		top.add(outputPanel); // scalar panel and color key panel will be added in setEquation method.
		
		mainPanel.add(top);
		
		// bottom of main panel; just the input panel, though, so I can't really do anything as of yet.
		
		add(mainPanel);
	}
	
	public static JPanel makeBoxPanel(int axis) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, axis));
		return panel;
	}
	
	
	public void setEquation(Equation eq) {
		if(this.equation != null) {
			// remove old equation panels
			mainPanel.remove(equation.getInputPanel());
			outputPanel.remove(equation.getOutputPanel());
			outputPanel.remove(colorKeyPanel);
			
			equation.setBackground(null);
		}
		
		this.equation = eq;
		eq.setBackground(Color.ORANGE);
		
		eq.setInputVectors(inputGrid);
		colorKeyPanel = eq.setOutputVectors(outputGrid);
		
		outputPanel.add(eq.getOutputPanel());
		outputPanel.add(colorKeyPanel);
		mainPanel.add(eq.getInputPanel());
		
		revalidate();
		eq.recalcOutputs();
	}
	
	public Equation getEquation() { return equation; }
	
	@Override public Dimension getPreferredSize() { return new Dimension(dimX, dimY); }
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Vector Sandbox");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		frame.add(new Runner());
		
		frame.pack();
		frame.setVisible(true);
	}
}
