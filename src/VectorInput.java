import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;

public class VectorInput extends JPanel implements EquationTerm<Vector> {
	
	private static final float DEFAULT_BOUND = Runner.GRID_RADIUS;
	
	private final Vector vector;
	private Equation equation = null;
	private final String label;
	
	private final JLabel vectorName;
	
	private final FloatInputPanel xInput, yInput, angleInput, lengthInput;
	
	public VectorInput(String label) { this(label, -DEFAULT_BOUND, DEFAULT_BOUND, -DEFAULT_BOUND, DEFAULT_BOUND); }
	public VectorInput(String label, float minX, float maxX, float minY, float maxY) {
		vector = new Vector(1, 0);
		
		this.label = label;
		/* have:
			- label
			- normalize button
			- vector axis panel	
		 */
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		
		JPanel header = Runner.makeBoxPanel(BoxLayout.LINE_AXIS);
		
		vectorName = new JLabel(label);
		vectorName.setOpaque(true);
		header.add(vectorName);
		header.add(Box.createHorizontalStrut(10));
		
		JButton normBtn = new JButton("normalize");
		normBtn.addActionListener(e -> {
			vector.nor();
			updateInput();
			getEquation().recalcOutputs();
		});
		header.add(normBtn);
		
		add(header);
		
		// x, y, angle(degrees), length
		
		xInput = addInput(new FloatInputPanel("x", applicatorTemplate(vector::setX), minX, maxX, vector.x, 2));
		yInput = addInput(new FloatInputPanel("y", applicatorTemplate(vector::setY), minY, maxY, vector.y, 2));
		angleInput = addInput(new FloatInputPanel("degrees", applicatorTemplate(vector::setDirection), 0, 360, vector.getDegrees(), 2));
		lengthInput = addInput(new FloatInputPanel("length", applicatorTemplate(vector::setLength), 0, Math.min(maxX, maxY), vector.len(), 2, val -> 0, val -> val));
	}
	
	private FloatInputPanel addInput(FloatInputPanel panel) { add(panel); return panel; }
	
	// ensures that the input reflects the value of the vector.
	@Override
	public void updateInput() {
		xInput.setValue(vector.x);
		yInput.setValue(vector.y);
		if(vector.getDegrees() != 0 || angleInput.getValue() != 360)
			angleInput.setValue(vector.getDegrees());
		else
			angleInput.setValue(360);
		lengthInput.setValue(vector.len());
	}
	
	public void setVectorColor(Color color) { vectorName.setBackground(color); vectorName.repaint(); }
	
	@Override public Vector setEquation(Equation eq) { this.equation = eq; return vector; }
	@Override public Equation getEquation() { return equation; }
	
	@Override public String getLabel() { return label; }
	
}
