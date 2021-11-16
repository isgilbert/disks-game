/**
 * An instance of the Disk class represents a disk
 * to be placed on the game board.
 */
public class Disk {
	// Disk's x and y coordinate, radius, and color.
	private double x;
	private double y;
	private double radius;
	private DiskColor color;

	/**
	 * Constructor.
	 */
	public Disk(double x, double y, double radius, DiskColor color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
	}
	
	/**
	 * @return the Disk's x coordinate
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @return the Disk's y coordinate
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return the Disk's radius
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * @return the Disk's color
	 */
	public DiskColor getColor() {
		return color;
	}
	
	/**
	 * Return true if this Disk overlaps
	 * the Disk given as the parameter, false otherwise.
	 */
	public boolean overlaps(Disk other) {
		double otherX = other.getX();
		double otherY = other.getY();
		double otherRadius = other.getRadius();
		
		// find distance between two disks, see if added radii is less than the distance
		double distanceCenters = Math.sqrt(Math.pow(otherX - this.x, 2) + Math.pow(otherY - this.y, 2));
		double totRadius = (otherRadius / 2) + (this.radius / 2);
		
		if (totRadius < distanceCenters) {
			return false;
		}
		else {
			return true;
		}
	} 

	/**
	 * Return true if this Disk is out of bounds, meaning that
	 * it is not entirely enclosed by rectangle whose width and
	 * height are given by the two parameters. 
	 * 
	 * @param width   the width of a rectangle
	 * @param height  the height of a rectangle
	 * @return false if the Disk fits entirely within the rectangle,
	 *         true if at least part of the Disk lies outside the
	 *         rectangle
	 */
	public boolean isOutOfBounds(double width, double height) {
		// center out of bounds:
		if ((this.x < 0) || (this.x > width)) {
			return true;
		}
		else if ((this.y < 0) || (this.y > height)) {
			return true;
		}
		// check x and y bounds: 
		else if (((this.x - 0) < (this.radius / 2)) || ((width - this.x) < (this.radius / 2))) {
			return true;
		}
		else if (((this.y - 0) < (this.radius / 2)) || ((height - this.y) < (this.radius / 2))) {
			return true;
		}
		else {
			return false;
		}
	}
}
