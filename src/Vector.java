/** @noinspection UnusedReturnValue, WeakerAccess */
public class Vector {
	
	public float x, y;
	private double radians;
	
	public Vector() { this(0, 0); }
	public Vector(Vector model) { set(model); }
	public Vector(float x, float y) { set(x, y); }
	
	public Vector cpy() { return new Vector(this); }
	
	public Vector add(Vector o) { return add(o.x, o.y); }
	public Vector add(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public Vector sub(Vector o) { return sub(o.x, o.y); }
	public Vector sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}
	
	public float dot(Vector o) { return dot(o.x, o.y); }
	public float dot(float x, float y) { return this.x * x + this.y * y; }
	
	public float crs(Vector o) { return crs(o.x, o.y); }
	public float crs(float x, float y) { return this.x * y - this.y * x; }
	
	public float dst(Vector o) { return dst(o.x, o.y); }
	public float dst(float x, float y) {
		return (float) Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
	}
	
	public Vector scl(float amt) {
		x *= amt;
		y *= amt;
		return this;
	}
	
	public Vector nor() {
		float len = len();
		x /= len;
		y /= len;
		return this;
	}
	
	public Vector rev() {
		x = -x;
		y = -y;
		return this;
	}
	
	// returns the right-handed, perpendicular vector.
	public Vector perp() {
		//noinspection SuspiciousNameCombination
		set(y, -x); // "-" is b/c right handed
		return this;
	}
	
	// leaves only the components normal/ to the given vector
	/*public Vector perp(Vector v) {
		
	}*/
	
	// leaves only the components tangent to the given vector
	/*public Vector para(Vector v) {
		Vector axis = v.cpy().nor();
		return axis.scl(dot(axis));
	}*/
	
	public float len() {
		return (float) (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
	}
	
	public boolean isZero() { return x == 0 && y == 0; }
	
	public Vector setZero() {
		setRadians();
		x = 0;
		y = 0;
		return this;
	}
	
	public Vector set(Vector v) { return set(v.x, v.y); }
	public Vector set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vector setX(float x) { return set(x, y); }
	public Vector setY(float y) { return set(x, y); }
	
	public Vector setLength(float len) {
		setRadians();
		
		// use radians variable to determine x and y.
		x = (float) (len*Math.cos(radians));
		y = (float) (len*Math.sin(radians));
		
		return this;
	}
	
	public Vector setDirection(float degrees) { return setDirection(degrees, false); }
	public Vector setDirection(float amt, boolean radians) {
		this.radians = radians ? amt : (float) Math.toRadians(amt);
		float len = len();
		x = (float) Math.cos(this.radians);
		y = (float) Math.sin(this.radians);
		setLength(len);
		return this;
	}
	private double last = radians;
	private void setRadians() {
		if(len() == 0) return;
		
		if(y == 0 && x < 0) {
			radians = Math.PI;
			return;
		}
		
		double rad = Math.acos(x/len());
		if(y < 0)
			rad = -rad;
		
		if(rad < 0) rad += 2*Math.PI;
		
		//if(Math.abs(rad - last) > 1) {
			//System.out.println("deg = " + Math.toDegrees(rad));
		//	last = rad;
		//}
		radians = rad;//(rad + 3*Math.PI/2) % (2*Math.PI);
		
		/*double pi = Math.PI;
		double rad = 0;
		if(x <= 0 && y > 0) rad = pi/2;
		if(x < 0 && y <= 0) rad = pi;
		if(x >= 0 && y < 0) rad = -pi/2;
		
		if(x != 0 && y != 0) {
			if((x > 0) == (y > 0))
				rad += Math.atan(y / x);
			else
				rad += (pi / 2) - Math.atan(y / x);
		}
		
		this.radians = rad;*/
	}
	
	public float getDegrees() { return getAngle(false); }
	public float getRadians() { return getAngle(true); }
	public float getAngle(boolean radians) {
		if(len() > 0) setRadians();
		return (float) (radians ? this.radians : Math.toDegrees(this.radians));
	}
	
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Vector)) return false;
		Vector v = (Vector) other;
		return x == v.x && y == v.y;
	}
	
	@Override
	public int hashCode() {
		return Float.hashCode(x) * 31 + Float.hashCode(y) * 17;
	}
	
	@Override
	public String toString() { return "("+x+","+y+")"; }
}
