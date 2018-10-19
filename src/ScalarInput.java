import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScalarInput implements EquationTerm<Scalar> {
	
	private final FloatInputPanel inputPanel;
	private Equation equation = null;
	private final String label;
	
	private final Scalar scalar;
	
	public ScalarInput(String label) { this(label, -Runner.GRID_RADIUS, Runner.GRID_RADIUS, 0); }
	public ScalarInput(String label, float min, float max, float initial) {
		this(label, min, max, initial, 2);
	}
	
	public ScalarInput(String label, float min, float max, float initial, int numDecimals) {
		this(label, min, max, initial, numDecimals, val -> min, val -> max);
	}
	
	public ScalarInput(String label, float min, float max, float initial, int numDecimals, FloatInputPanel.OutOfBoundsBehavior belowFix, FloatInputPanel.OutOfBoundsBehavior aboveFix) {
		this.label = label;
		this.scalar = new Scalar(initial);
		inputPanel = new FloatInputPanel(label, applicatorTemplate(scalar::set), min, max, initial, numDecimals, belowFix, aboveFix);
	}
	
	@Override
	public void updateInput() { inputPanel.setValue(scalar.get()); }
	
	@Override public Scalar setEquation(Equation eq) { this.equation = eq; return scalar; }
	@Override public Equation getEquation() { return equation; }
	
	@Override public String getLabel() { return label; }
	
	
	public static JPanel makeScalarPanel(ScalarInput... inputs) {
		JPanel panel = Runner.makeBoxPanel(BoxLayout.PAGE_AXIS);
		
		panel.add(new JLabel("-- Scalars --"));
		
		for(ScalarInput input: inputs)
			panel.add(input.inputPanel);
		
		return panel;
	}
}
