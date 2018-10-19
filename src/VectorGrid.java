import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

public class VectorGrid extends JPanel {
	
	/*
		So the grid displays a number of vectors. Some are input values, and one could be an output value for the current vector function.
		
		The grid is affected by changes to the given vectors.
		
		The grid is actually given the vectors used in controllers. This allows me to pass it vectors that get defined by input sliders, and ones that may be defined other ways such as through equation solving.
	 */
	
	private final ArrayList<Vector> vectors = new ArrayList<>();
	private final ArrayList<Color> colors = new ArrayList<>();
	private final Object listLock = new Object();
	
	private final int gridSize;
	private final float virtualSize;
	private final float pixelsPerUnit, unitsPerPixel;
	
	public VectorGrid(int gridScreenSize, float virtualSize) {
		gridSize = gridScreenSize;
		this.virtualSize = virtualSize;
		pixelsPerUnit = gridScreenSize / virtualSize;
		unitsPerPixel = virtualSize / gridScreenSize;
	}
	
	private int toPixels(float units) { return (int) (units * pixelsPerUnit + gridSize/2); }
	private float toUnits(int pixels) { return (pixels-gridSize/2) * unitsPerPixel; }
	
	public void addVector(Vector v, Color c) {
		synchronized (listLock) {
			for(Vector has: vectors)
				if(has == v)
					return;
			vectors.add(v);
			colors.add(c);//new Color(c.getRed(), c.getGreen(), c.getBlue(), 255*3/4));
		}
		repaint();
	}
	
	/*public void removeVector(Vector v) {
		synchronized (listLock) {
			int idx = 0;
			for(Vector has: vectors) {
				if(has == v)
					break;
				idx++;
			}
			if(idx < vectors.size()) {
				vectors.remove(idx);
				colors.remove(idx);
			}
		}
		repaint();
	}*/
	
	public void clearVectors() {
		synchronized (listLock) {
			vectors.clear();
			colors.clear();
		}
		repaint();
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, gridSize, gridSize);
		
		g.setColor(Color.LIGHT_GRAY);
		for(int i = 0; i < virtualSize; i++) {
			int px = toPixels(i-virtualSize/2);
			g.drawLine(px, 0, px, gridSize);
			g.drawLine(0, px, gridSize, px);
		}
		
		g.setColor(Color.BLACK);
		int px = toPixels(0);
		g.drawLine(px, 0, px, gridSize);
		g.drawLine(0, px, gridSize, px);
		
		g.setColor(Color.DARK_GRAY);
		g.drawOval(toPixels(-1), toPixels(-1), (int)(2*pixelsPerUnit), (int)(2*pixelsPerUnit));
		
		// now draw the vectors
		synchronized (listLock) {
			for(int i = 0; i < vectors.size(); i++) {
				g.setColor(colors.get(i));
				Vector v = vectors.get(i);
				g.drawLine(toPixels(0), toPixels(0), toPixels(v.x), toPixels(-v.y));
			}
		}
	}
	
	@Override
	public Dimension getPreferredSize() { return new Dimension(gridSize, gridSize); }
	
}
