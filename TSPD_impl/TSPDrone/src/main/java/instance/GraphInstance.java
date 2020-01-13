package instance;

import util.Distance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GraphInstance<E> implements Instance<E>
{
	private E _depot;
	private List<E> _locations;
	private Map<E,Map<E,Edge>> _edges;
	private HashSet<Edge> _edgeSet;
	private List<Edge> _edgeList;
	private Map<E,Integer> _indices;
	private double [][] _driveDistances;
	private double [][] _flyDistances;
	private boolean _recalcDistances;
	private Distance<E> _customDriveDistance;
	private Distance<E> _customFlyDistance;
	
	public GraphInstance(E depot, List<E> locations)
	{
        	init(depot,locations);
		int n = _locations.size() + 1;
		_flyDistances = new double[n][n];
		_driveDistances = new double[n][n];
		_recalcDistances = true;
        }
	
	public GraphInstance(E depot, List<E> locations, Distance<E> drive, Distance<E> fly)
	{
		init(depot,locations);
		_customDriveDistance = drive;
		_customFlyDistance = fly;
	}
	
	private void init(E depot, List<E> locations)
	{
		_depot = depot;
		_locations = new ArrayList<E>(locations);
		_indices = new HashMap<>();
		_edges = new HashMap<>();
		_edgeSet = new HashSet<>();
		_edgeList = new ArrayList<>();
		_indices.put(depot, 0);
		for (E loc : _locations)
		{
			_edges.put(loc, new HashMap<>());
			_indices.put(loc, _indices.size());
		}
		_edges.put(depot, new HashMap<>());
		_recalcDistances = true;		
	}	
	
	public List<Edge> getEdges()
	{
		return Collections.unmodifiableList(_edgeList);
	}
	
	@Override
	public int getLocationCount()
	{
		return _locations.size();
	}

	@Override
	public E getDepot()
	{
		return _depot;
	}

	@Override
	public List<E> getLocations()
	{
		return Collections.unmodifiableList(_locations);
	}

	@Override
	public Distance<E> getDriveDistance()
	{
		if (_customDriveDistance != null)
		{
			return _customDriveDistance;
		}
		computeDistances();
                
		Distance<E> result = (E e1, E e2, Distance.Action fromAction1, Distance.Action toAction1, double priorDistance1) 
                        -> _driveDistances[_indices.get(e1)][_indices.get(e2)];
		return result;
	}

	@Override
	public Distance<E> getFlyDistance()
	{
		if (_customFlyDistance != null)
		{
			return _customFlyDistance;
		}
		computeDistances();
		Distance<E> result = (E e1, E e2, Distance.Action fromAction1, Distance.Action toAction1, double priorDistance1) 
                        -> _flyDistances[_indices.get(e1)][_indices.get(e2)];
		return result;
	}
	
	private void computeDistances()
	{
		if (!_recalcDistances)
		{
			return;
		}
		int n = _driveDistances.length;
		for (int i=0; i < n; i++)
		{
			for (int j=0; j < n; j++)
			{
				if (i==j)
				{
					_driveDistances[i][j] = 0;
					_flyDistances[i][j] = 0;
				}
				else
				{
					_driveDistances[i][j] = Double.POSITIVE_INFINITY;
					_flyDistances[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (Edge e : _edgeSet)
		{
			int i = _indices.get(e.left);
			int j = _indices.get(e.right);
			_driveDistances[i][j] = e.drive;
			_flyDistances[i][j] = e.fly;
			if (e.bidirectional)
			{
				_driveDistances[j][i] = e.drive;
				_flyDistances[j][i] = e.fly;	
			}
		}
		allPairsShortestPath(_driveDistances);
		allPairsShortestPath(_flyDistances);
		_recalcDistances = false;
	}
	
	private void allPairsShortestPath(double [][] matrix)
	{
		int n = matrix.length;
		for (int k=0; k < n; k++)
		{
			boolean improve = false;
			for (int i=0; i < n; i++)
			{
				for (int j=0; j < n; j++)
				{
					if (matrix[i][j] > matrix[i][k] + matrix[k][j])
					{
						matrix[i][j] = matrix[i][k] + matrix[k][j];
						improve = true;
					}
				}
			}
			if (!improve)
			{
				break;
			}
		}
	}

	public class Edge
	{
		public final E left;
		public final E right;
		public final double fly;
		public final double drive;
		public final boolean bidirectional;
		
		private Edge(E l, E r, double d, double f, boolean b)
		{
			left = l;
			right = r;
			fly = f;
			drive = d;
			bidirectional = b;
		}
		
		public E getOther(E first)
		{
			if (left.equals(first))
			{
				return right;
			}
			if (right.equals(first))
			{
				return left;
			}
			throw new IllegalArgumentException("The argument should be at least one of the vertices!");
		}
	}

	@Override
	public Instance<E> getSubInstance(Predicate<? super E> retain)
	{
		List<E> newLocs = _locations.stream().filter(retain).collect(Collectors.toList());
		return new GraphInstance<E>(_depot, newLocs, getDriveDistance(), getFlyDistance());
	}
}
