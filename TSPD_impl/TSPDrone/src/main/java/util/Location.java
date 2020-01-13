package util;

public class Location implements Comparable<Location>
{
	private final String name;
	public final double x;
	public final double y;
	
	public Location(double x, double y, String name)
	{
		this.x = x;
		this.y = y;
		this.name = name;
        }
	
	public Location(double x, double y)
	{
		this(x,y,"Point_x=" + x + "_y=" + y + "");
	}
	
	public String getName()
	{
		return name;
	}

        @Override
        public int compareTo(Location o) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        @Override
        public String toString()
        {
            return name;
        }
}
