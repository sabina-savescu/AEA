package MST;

import util.Distance;
import instance.GeometricInstance;
import instance.GraphInstance;
import instance.Instance;
import util.Operation;
import util.Solution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MSTSolver<E> 
{
	private Instance<E> _lastInstance;
	private Solution<E> _lastSolution;
	private boolean tighterLB = false;
        private double lastMST;
        private boolean _nearestNeighbour;
	
	public MSTSolver()
	{
		this(false);
	}
	
	public MSTSolver(boolean nearestNeighbour)
	{
		_nearestNeighbour = nearestNeighbour;
		tighterLB = false;
	}
	
	public MSTSolver(boolean nearestNeighbour, boolean betterLB) {
		_nearestNeighbour = nearestNeighbour;
		tighterLB = betterLB;
	}
	
	public Solution<E> solve(Instance<E> instance)
	{
		if (instance == _lastInstance)
		{
			return _lastSolution;
		}
		_lastInstance = null;
		_lastSolution = null;
		
		List<Pair> l = null;
		if (instance instanceof GraphInstance<?>)
		{
			try
			{
				GraphInstance<E> gi = (GraphInstance<E>) instance;
				l = generateList(gi);
			}
			catch (Exception e)
			{
				// Do nothing
			}
		}
		if (l == null)
		{
			l = generateList(instance);
		}
		Map<E,List<E>> mst = mst(instance, l);
		List<E> seq = mstToSequence(instance,mst);
		E cur = null;
		List<Operation<E>> ops = new ArrayList<>(seq.size()-1);
		for (E e : seq)
		{
			if (cur != null)
			{
				Operation<E> op = new Operation<>(cur,e);
				ops.add(op);
			}
			cur = e;
		}
		Solution<E> sol = new Solution<E>(instance,ops);
		_lastInstance = instance;
		_lastSolution = sol;
		return sol;
	}
	
	private List<Pair> generateList(Instance<E> instance)
	{
		List<Pair> res = new ArrayList<>();
		int nc = instance.getNodeCount();
		for (int i=0; i < nc; i++)
		{
			for (int j=i+1; j < nc; j++)
			{
				E u = instance.getLocation(i);
				E v = instance.getLocation(j);
				Pair p = new Pair(u,v,instance.getDriveDistance().getContextFreeDistance(u, v));
				res.add(p);
			}
		}
		return res;
	}

	private List<Pair> generateList(GraphInstance<E> instance)
	{
		List<Pair> res = new ArrayList<>();
		for (GraphInstance<E>.Edge e : instance.getEdges())
		{
			Pair p = new Pair(e.left,e.right,e.drive);
			res.add(p);
		}
		return res;
	}

	private List<E> mstToSequence(Instance<E> i, Map<E,List<E>> mst)
	{
		Set<E> visited = new HashSet<E>();
		Set<E> processed = new HashSet<E>();
		ArrayList<List<E>> queue = new ArrayList<>();
		ArrayList<E> res = new ArrayList<E>();
		E depot = i.getDepot();
		E cur = depot;
		visited.add(cur);
		res.add(cur);
		for (E child : mst.get(cur))
		{
			visited.add(child);
			List<E> l = new ArrayList<>();
			l.add(child);
			queue.add(l);
		}
		processed.add(cur);
		while (!queue.isEmpty())
		{
			List<E> l = queue.get(queue.size()-1);
			if (l.isEmpty())
			{
				queue.remove(queue.size()-1);
				continue;
			}
			if (_nearestNeighbour)
			{
				Collections.sort(l, distanceOrder(cur, i.getDriveDistance()));
			}
			E node = l.remove(0);
			res.add(node);
			List<E> nl = new ArrayList<>();
			for (E child : mst.get(node))
			{
				if (!visited.contains(child))
				{
					visited.add(child);
					nl.add(child);
				}
			}
			queue.add(nl);
			processed.add(node);
		}
		res.add(depot);
		return res;
	}
	
	private Comparator<E> distanceOrder(E o, Distance<E> d)
	{
		return (E o1, E o2) -> (int)Math.signum(d.getContextFreeDistance(o, o1) - d.getContextFreeDistance(o, o2));
	}
	
	private Map<E,List<E>> mst(Iterable<E> locs, List<Pair> pairs)
	{
		Map<E,List<E>> res = new HashMap<>();
		UnionFind<E> uf = new UnionFind<>();
		for (E loc : locs)
		{
			uf.createSet(loc);
			res.put(loc, new ArrayList<>());
		}
		
		ArrayList<Pair> list = new ArrayList<Pair>(pairs);
		Collections.sort(list);
		lastMST = 0;
		for (Pair p : list)
		{
			if (!uf.sameSet(p.left, p.right))
			{
				res.get(p.left).add(p.right);
				res.get(p.right).add(p.left);
				uf.union(p.left, p.right);
				lastMST += p.cost;
			}
		}
		
		return res;
	}
	
	private class Pair implements Comparable<Pair>
	{
		public final E left;
		public final E right;
		public final double cost;
		
		public Pair(E l, E r, double c)
		{
			left = l;
			right = r;
			cost = c;
		}
		
		@Override
		public int compareTo(Pair arg0)
		{
			return (int)Math.signum(cost-arg0.cost);
		}
	}	
}
