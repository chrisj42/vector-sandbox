public class Scalar {
	
	private float value;
	
	public Scalar() { this(0); }
	public Scalar(float value) {
		this.value = value;
	}
	
	public void set(float value) { this.value = value; }
	public float get() { return value; }
	
}
