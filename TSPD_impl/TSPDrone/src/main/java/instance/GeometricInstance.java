package instance;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import util.Distance;
import util.Location;

public class GeometricInstance implements Instance<Location>
{
	private final Location _depot;
	private final List<Location> _locations;
	private final double _driveSpeed;
	private final double _flySpeed;
	private final Distance<Location> _drive;
	private final Distance<Location> _fly;

	public GeometricInstance(Location depot, List<Location> locs, double fly)
	{
		this(depot,locs,1,fly);
	}

	public GeometricInstance(Location depot, List<Location> locs, double drive, double fly)
	{
		_depot = depot;
		_locations = locs;
		
		_drive = (Location p1, Location p2,Distance.Action fromAction1, Distance.Action toAction1, double priorDistance1) -> euclideanDistance(p1,p2)*drive; 
		_fly = (Location p1, Location p2,Distance.Action fromAction1, Distance.Action toAction1, double priorDistance1) -> euclideanDistance(p1,p2)*fly;
		
		_driveSpeed = drive;
		_flySpeed = fly;
	}
	
	public double getFlySpeed()
	{
		return _flySpeed;
	}

	public double getDriveSpeed()
	{
		return _driveSpeed;
	}
	
	
	@Override
	public int getLocationCount()
	{
		return _locations.size();
	}

	@Override
	public Location getDepot()
	{
		return _depot;
	}

	@Override
	public List<Location> getLocations()
	{
		return Collections.unmodifiableList(_locations);
	}

	@Override
	public Distance<Location> getDriveDistance()
	{
		return _drive;
	}

	@Override
	public Distance<Location> getFlyDistance()
	{
		return _fly;
	}
	
	public static double euclideanDistance(Location p1, Location p2)
	{
		double dx = p1.x - p2.x;
		double dy = p1.y - p2.y;
		return Math.sqrt(dx*dx + dy*dy);
	}

	@Override
	public Instance<Location> getSubInstance(Predicate<? super Location> retain)
	{
		List<Location> newLocs = _locations.stream().filter(retain).collect(Collectors.toList());
		return new GeometricInstance(_depot,newLocs,_driveSpeed,_flySpeed);
	}
	
}
