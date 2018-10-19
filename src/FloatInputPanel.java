import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FloatInputPanel extends JPanel {
	
	// controls one "axis" of a vector, in some way. There will be one of these for x, y, angle, and length of each vector.
	
	/*
		- label, textfield, slider set.
		- for: x, y, angle(degrees), length of vectors; and scalars.
		- each slider updates the grid when changed
		- each textfield permits only numbers, and updates the sliders and grid as you type; but it doesn't prevent you from typing anything unless it's not a number. Even if it is out of bounds. Well, when out of bounds, it will either allow it and set the slider to the max/min, or allow it but internally set it to something else, such as the actual max/min or have it wrap around. upon defocusing, the text field will show what the actual value is.
	 */
	
	@FunctionalInterface
	interface OutOfBoundsBehavior {
		float getValue(float inputValue);
	}
	
	/*@FunctionalInterface
	interface FloatFetcher {
		float getValue(Vector v);
	}*/
	
	//private final VectorInput input;
	private final FloatChangeApplicator applicator;
	//private final FloatFetcher valueFetcher;
	private final FloatField field;
	private final FloatSlider slider;
	
	// by default, clamp the value if it goes out of bounds
	public FloatInputPanel(String label, FloatChangeApplicator applicator, float min, float max, float initial, int numDecimals) {
		this(label, applicator, min, max, initial, numDecimals, val -> min, val -> max);
	}
	public FloatInputPanel(String label, FloatChangeApplicator applicator, float min, float max, float initial, int numDecimals, OutOfBoundsBehavior belowFix, OutOfBoundsBehavior aboveFix) {
		
		// do NOT use the applicator in the constructor! It WILL NOT work, and will probably throw an exception; most implementations try to recalculate the equation after applicator is called, but at this point, there is no equation set. So attempts to recalculate won't go over well. (i.e. NullPointerException)
		
		//this.valueFetcher = valueFetcher;
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		this.applicator = applicator;
		
		add(new JLabel(label + ": "));
		
		field = new FloatField(min, max, belowFix, aboveFix);
		
		slider = new FloatSlider(min, max, initial, numDecimals);
		
		add(field);
		add(slider);
		
		field.setText(slider.getValueString());
	}
	
	public float getValue() { return slider.getDecimalValue(); }
	
	public void setValue(float value) {
		slider.setValue(value);
		if(!field.hasFocus())
			field.setText(slider.getValueString());
	}
	
	private class FloatSlider extends JSlider {
		
		private final int numDecimals;
		private int prevValue;
		
		public FloatSlider(float min, float max, float initial, int numDecimals) {
			super(getSliderValue(min, numDecimals), getSliderValue(max, numDecimals), getSliderValue(initial, numDecimals));
			
			this.numDecimals = numDecimals;
			prevValue = getValue();
			
			addChangeListener(e -> {
				int curValue = getValue();
				if(curValue != prevValue) {
					prevValue = curValue; // technically not necessary, but could prevent some threading problems
					applicator.applyChange(getDecimalValue());
				}
			});
		}
		
		public float getDecimalValue() { return (float) (getValue() / Math.pow(10, numDecimals)); }
		public String getValueString() {
			if(numDecimals <= 0) return Math.round(getDecimalValue())+"";
			return getDecimalValue()+"";
		}
		
		// this only changes the displayed value, without calling the changeApplicator, just as with setting the text field text.
		public void setValue(float value) {
			prevValue = getSliderValue(value, numDecimals);
			setValue(prevValue);
		}
	}
	
	private static int getSliderValue(float val, int numDecimals) {
		return (int) (val * Math.pow(10, numDecimals));
	}
	
	private class FloatField extends JTextField {
		
		public FloatField(float min, float max, OutOfBoundsBehavior lowerBoundBehavior, OutOfBoundsBehavior upperBoundBehavior) {
			super(4);
			
			addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
					String prevText = getText();
					SwingUtilities.invokeLater(() -> {
						String text = getText();
						if(text.matches("^\\-?\\.?$")) return; // no action
						try {
							float num = Float.parseFloat(text);
							if(num < min)
								num = lowerBoundBehavior.getValue(num);
							else if(num > max)
								num = upperBoundBehavior.getValue(num);
							
							slider.setValue(num);
							applicator.applyChange(num);
							
						} catch(NumberFormatException ex) {
							setText(prevText);
						}
					});
				}
				@Override public void keyPressed(KeyEvent e) {}
				@Override public void keyReleased(KeyEvent e) {}
			});
			
			addFocusListener(new FocusListener() {
				@Override public void focusGained(FocusEvent e) {}
				@Override
				public void focusLost(FocusEvent e) {
					setText(slider.getValueString());
				}
			});
		}
		
		@Override public Dimension getMaximumSize() { return new Dimension(300, super.getPreferredSize().height); }
	}
}
