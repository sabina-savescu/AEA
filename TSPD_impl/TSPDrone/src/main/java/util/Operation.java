package util;

import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Operation<E>
{
	private final E _start;
	private final E _end;
	private final List<E> _drive;
	private final E _fly;
	private Distance<E> _lastDriveDist;
	private Distance<E> _lastFlyDist;
	private double _lastDrive;
	private double _lastFly;
	
	public Operation(E start, E end, E fly)
	{
		this(start,Collections.emptyList(),end,fly);
	}
	
	public Operation(E start, E end)
	{
		this(start,Collections.emptyList(),end);
	}
	
	public Operation(List<E> drive)
	{
		this(drive,null);
	}
	
	public Operation(List<E> drive, E fly)
	{
		this(drive.get(0), drive.subList(1,drive.size()-1), drive.get(drive.size()-1), fly);
	}
	
        public Operation(E start, List<E> drive, E end)
	{
		this(start,drive,end,null);
	}
	
	public Operation(E start, List<E> drive, E end, E fly)
	{
		_start = start;
		_drive = new ArrayList<E>(drive);
		_end = end;
		_fly = fly;
	}
        
        	public E getFly()
	{
		return _fly;
	}
        
	public List<E> getDrive()
	{
		return _drive;
	}
        
        public boolean hasFly()
	{
		return _fly != null;
	}
        
        public List<E> getFlyPath()
	{
		if (_fly == null)
		{
			return Collections.emptyList();
		}
		List<E> res = new ArrayList<E>(3);
		res.add(_start);
		res.add(_fly);
		res.add(_end);
		return res;
	}
        
        public List<E> getDrivePath()
	{
		List<E> res = new ArrayList<E>(_drive.size()+2);
		res.add(_start);
		res.addAll(_drive);
		res.add(_end);
		return res;
	}
        
        public E getStart()
	{
		return _start;
	}
	
	public E getEnd()
	{
		return _end;
	}
	
	public double getCost(Instance<E> i)
	{
		return getCost(i.getDriveDistance(), i.getFlyDistance());
	}
	
	public double getCost(Distance<E> drive, Distance<E> fly)
	{
		getDriveCost(drive);
		getFlyCost(fly);
		_lastDriveDist = drive;
		_lastFlyDist = fly;
		return Math.max(_lastDrive,_lastFly);
	}
	
	public double getDriveCost(Instance<E> i)
	{
		return getDriveCost(i.getDriveDistance());
	}
	
	public double getDriveCost(Distance<E> drive)
	{
		
		if (_lastDriveDist != drive)
		{
			_lastDrive = drive.getPathDistance(_start, _end, _drive);
			_lastDriveDist = drive;
		}
		return _lastDrive;
	}
	
	public double getFlyCost(Instance<E> i)
	{
		return getFlyCost(i.getFlyDistance());
	}
	
	public double getFlyCost(Distance<E> fly)
	{
		if (_lastFlyDist != fly)
		{
			_lastFly = 0;
			if (_fly != null)
			{
				_lastFly = fly.getFlyDistance(_start, _end, _fly);
			}
		}
		_lastFlyDist = fly;
		return _lastFly;
	}
	
	public double getTruckWait(Instance<E> i)
	{
		return getTruckWait(i.getDriveDistance(), i.getFlyDistance());
	}
	
	public double getTruckWait(Distance<E> drive, Distance<E> fly)
	{
		getCost(drive,fly);
		return Math.max(_lastDrive, _lastFly) - _lastDrive;
	}
	
	public double getDroneWait(Instance<E> i)
	{
		return getDroneWait(i.getDriveDistance(), i.getFlyDistance());
	}
	
        public double getDroneWait(Distance<E> drive, Distance<E> fly)
	{
		if (!hasFly())
		{
			return 0;
		}
		getCost(drive,fly);
		return Math.max(_lastDrive, _lastFly) - _lastFly;
	}
	
	public Set<E> getNodeSet()
	{
		Set<E> result = new LinkedHashSet<>();
		result.add(_start);
		if (_fly != null)
		{
			result.add(_fly);
		}
		result.addAll(_drive);
		result.add(_end);
		return result;
	}
	
	public List<E> getInternalNodes(boolean includeFly)
	{
		ArrayList<E> res = new ArrayList<>(_drive);
		if (includeFly && hasFly())
		{
			res.add(_fly);
		}
		return res;
	}
}