package util;

import instance.Instance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Solution<E> implements Iterable<Operation<E>>
{
	private Instance<E> _instance;
	private List<Operation<E>> _operations;
        private Set<E> _fly;
	private Set<E> _drive;
	private double _totalCost;
	private double _totalTruckCost;
	private double _totalDroneCost;
	private double _totalTruckWait;
	private double _totalDroneWait;
	private double _maxTruckCost;
	private double _maxDroneCost;
	private double _maxTruckWait;
	private double _maxDroneWait;
	
	public Solution(Instance<E> i, List<Operation<E>> ops)
	{
		_operations = new ArrayList<>(ops.size());
		_instance = i;
		_totalCost = 0;
		_totalTruckCost = 0;
		_totalDroneCost = 0;
		_totalTruckWait = 0;
		_totalDroneWait = 0;
		_maxTruckCost = 0;
		_maxDroneCost = 0;
		_maxTruckWait = 0;
		_maxDroneWait = 0;
		_fly = new HashSet<E>();
		_drive = new HashSet<E>();
		
		for (Operation<E> op : ops)
		{
			_totalCost += op.getCost(_instance);			
			_totalTruckCost += op.getDriveCost(_instance);
			_totalDroneCost += op.getFlyCost(_instance);
			_totalTruckWait += op.getTruckWait(_instance);
			_totalDroneWait += op.getDroneWait(_instance);
			_maxTruckCost = Math.max(_maxTruckCost, op.getDriveCost(_instance));
			_maxDroneCost = Math.max(_maxDroneCost, op.getFlyCost(_instance));
			_maxTruckWait = Math.max(_maxTruckWait, op.getTruckWait(_instance));
			_maxDroneWait = Math.max(_maxDroneWait, op.getDroneWait(_instance));
			_operations.add(op);
			if (op.hasFly())
			{
				_fly.add(op.getFly());
			}
			_drive.addAll(op.getDrivePath());
		}
	}
	
	public Solution(List<E> order, Instance<E> i)
	{
		this(i, convert(order));
	}
        
        public Instance<E> getInstance()
	{
		return _instance;
	}
        
        public double getTotalCost()
	{
		return _totalCost;
	}
	
	public double getMaxDroneCost()
	{
		return _maxDroneCost;
	}
	
	public double getMaxTruckCost()
	{
		return _maxTruckCost;
	}
	
	public double getDroneCost()
	{
		return _totalDroneCost;
	}
	
	public double getTruckCost()
	{
		return _totalTruckCost;
	}
	
	public double getDroneWait()
	{
		return _totalDroneWait;
	}
	
	public double getTruckWait()
	{
		return _totalTruckWait;
	}
	
	public double getMaxTruckWait()
	{
		return _maxTruckWait;
	}
	
	public double getMaxDroneWait()
	{
		return _maxDroneWait;
	}
	
	private static <F> List<Operation<F>> convert(List<F> order)
	{
		ArrayList<Operation<F>> ops = new ArrayList<>();
		F prev = null;
		for (F loc : order)
		{
			if (prev != null)
			{
				Operation<F> op = new Operation<F>(prev,loc);
				ops.add(op);
			}
			prev = loc;
		}
		return ops;
	}
	
	public List<Operation<E>> getOperations()
	{
		return Collections.unmodifiableList(_operations);
	}
	
	public List<E> getOrder()
	{
		ArrayList<E> order = new ArrayList<>();
		for (Operation<E> op : _operations)
		{
			if (op.hasFly())
			{
				throw new IllegalStateException("Cannot get the order if there are flight nodes...");
			}
			if (order.isEmpty())
			{
				order.add(op.getStart());
			}
			for (E loc : op.getInternalNodes(true))
			{
				order.add(loc);
			}
			order.add(op.getEnd());
		}
		return order;
	}
	
	@Override
	public Iterator<Operation<E>> iterator()
	{
		return Collections.unmodifiableList(_operations).iterator();
	}
	
	@Override
	public String toString()
	{
		return "Solution, total cost: "+_totalCost+", operations: "+_operations;
	}
}
