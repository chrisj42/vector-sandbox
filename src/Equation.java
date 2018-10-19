import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Equation extends JPanel {
	
	private static final Color[] vectorColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.GRAY};
	
	@FunctionalInterface
	interface EquationSolver {
		void solve(HashMap<String, Scalar> ins, HashMap<String, Vector> inv, HashMap<String, Scalar> outs, HashMap<String, Vector> outv);
	}
	
	private final Runner runner;
	
	private final EquationSolver solver;
	private final LinkedHashMap<String, Scalar> ins = new LinkedHashMap<>(), outs = new LinkedHashMap<>();
	private final LinkedHashMap<String, Vector> inv = new LinkedHashMap<>(), outv = new LinkedHashMap<>();
	
	private final JPanel inputPanel, outputPanel;
	private final JLabel[] outputLabels;
	
	public Equation(Runner runner, String label, ScalarInput[] scalarInputs, VectorInput[] vectorInputs, String[] scalarOutputs, String[] vectorOutputs, EquationSolver solver) {
		this.runner = runner;
		this.solver = solver;
		
		if(scalarInputs == null) scalarInputs = new ScalarInput[0];
		if(vectorInputs == null) vectorInputs = new VectorInput[0];
		if(scalarOutputs == null) scalarOutputs = new String[0];
		if(vectorOutputs == null) vectorOutputs = new String[0];
		
		// if you just need generic vector names, and default range, then you can specify an array of null VectorInputs and this will fill them in for you.
		if(vectorInputs.length > 0 && vectorInputs[0] == null)
			for(int i = 0; i < vectorInputs.length; i++)
				vectorInputs[i] = new VectorInput("Vector "+(i+1));
		
		// create all the input/output scalars/vectors.
		for(ScalarInput input : scalarInputs) ins.put(input.getLabel(), input.setEquation(this));
		for(VectorInput input : vectorInputs) inv.put(input.getLabel(), input.setEquation(this));
		for(String output : scalarOutputs) outs.put(output, new Scalar());
		for(String output : vectorOutputs) outv.put(output, new Vector());
		
		
		// add all the input terms to an input panel.
		inputPanel = /*new JPanel(new GridLayout(1, 0));*/Runner.makeBoxPanel(BoxLayout.LINE_AXIS);
		
		if(scalarInputs.length > 0)
			inputPanel.add(ScalarInput.makeScalarPanel(scalarInputs));
		for(int i = 0; i < vectorInputs.length; i++) {
			inputPanel.add(vectorInputs[i]);
			vectorInputs[i].setVectorColor(vectorColors[i]);
		}
		//inputPanel.add(Box.createHorizontalGlue());
		
		
		// add the output scalars to an output panel.
		outputPanel = Runner.makeBoxPanel(BoxLayout.PAGE_AXIS);
		if(outs.size() > 0)
			outputPanel.add(new JLabel("Scalar Output Values:"));
		
		outputLabels = new JLabel[outs.size()];
		int i = 0;
		for(String outLabel: outs.keySet()) {
			outputLabels[i] = new JLabel(outLabel);
			outputPanel.add(outputLabels[i]);
			i++;
		}
		
		
		// add the equation label to this JPanel; it will be added to a list of equations.
		add(new JLabel(label));
		
		addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) { runner.setEquation(Equation.this); }
			@Override
			public void mouseEntered(MouseEvent e) { if(selected()) return; Equation.this.setBackground(Color.YELLOW); repaint(); }
			@Override
			public void mouseExited(MouseEvent e) { if(selected()) return; Equation.this.setBackground(null); repaint(); }
			
			@Override public void mouseClicked(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
		});
	}
	
	private boolean selected() { return runner.getEquation() == this; }
	
	public JPanel getInputPanel() { return inputPanel; }
	public JPanel getOutputPanel() { return outputPanel; }
	
	public void setInputVectors(VectorGrid grid) {
		grid.clearVectors();
		
		int colorIdx = 0;
		for(String label: inv.keySet())
			grid.addVector(inv.get(label), vectorColors[colorIdx++]);
	}
	
	// returns a JPanel containing a color key that shows the label for each color of vector.
	public JPanel setOutputVectors(VectorGrid grid) {
		grid.clearVectors();
		
		JPanel keyPanel = Runner.makeBoxPanel(BoxLayout.PAGE_AXIS);
		if(outv.size() == 0) return keyPanel;
		
		keyPanel.add(new JLabel("Output Vector Color Key:"));
		
		int colorIdx = inv.size();
		for(String label: outv.keySet()) {
			Color color = vectorColors[colorIdx];
			colorIdx++;
			
			grid.addVector(outv.get(label), color);
			
			JLabel vectorLabel = new JLabel(label);
			vectorLabel.setOpaque(true);
			vectorLabel.setBackground(color);
			keyPanel.add(vectorLabel);
		}
		
		return keyPanel;
	}
	
	public void recalcOutputs() {
		solver.solve(ins, inv, outs, outv);
		
		// this may still have worked with a regular HashMap, as opposed to a LinkedHashMap, since I think while the order of the elements is random, it's also consistent once they're all in the map. But, it would be nice to have the inputs and outputs appear in the same order as they were put during instantiation, so I'm going to use one anyway.
		int i = 0;
		for(String label: outs.keySet())
			outputLabels[i++].setText(label+" = "+outs.get(label).get());
		
		runner.repaint();
	}
	
}
