package utils;

public class Point {
	private int x;
	private int y;
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Point(Point other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	public boolean isNextTo(Point other){
		int xs = other.X();
		int ys = other.Y();
		
		if(x == xs && y == ys){
			return false;
		}
		if(x == xs || x == xs-1 || x == xs + 1){
			if(y == ys || y-1 == ys || y+1 == ys){
				return true;
			}
			else{
				return false; }
		}
		else {
			return false; 
		}
	}
	
	public int X(){
		return this.x;
	}
	
	public int Y(){
		return this.y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
