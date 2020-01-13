package algorithms;

import util.Distance;
import instance.Instance;
import util.Operation;
import util.Solution;
import util.Array3D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamicProgrammingSolver<E> implements Solver<E>
{
	private Instance<E> _instance;
	private List<E> _nodesList;
	private double [][] _distances;
	private Array3D _operations;
        private int _maxOpLength;
	
        public DynamicProgrammingSolver(){};
        
	public DynamicProgrammingSolver(Instance<E> i, List<E> nodesList)
	{
                _instance = i;
		_nodesList = nodesList;
		_maxOpLength = nodesList.size() + 1;
	}
	
	private void buildDistancesTable()
	{
		int n = _nodesList.size();
		Distance<E> distance = _instance.getDriveDistance();
		_distances = new double[n][n];
		for (int i=0; i < n; i++)
		{
			for (int j=i+1; j < n; j++)
			{
				E from = _nodesList.get(j-1);
				E to = _nodesList.get(j);
				_distances[i][j] = _distances[i][j-1] + distance.getContextFreeDistance(from, to, _distances[i][j-1]);
			}
		}
	}
	
	private void buildOperations()
	{
		int n = _nodesList.size();
		Distance<E> driveDistance = _instance.getDriveDistance();
		Distance<E> flyDistance = _instance.getFlyDistance();
                _operations = new Array3D(n,_maxOpLength,_maxOpLength); /* Keeps the cost of a path i-k-j, where k is the drone */
                
		for (int i=0; i < n; i++)
		{
			for (int j=i+1; j < n; j++)
			{
				_operations.set(i, j, i, _distances[i][j]); // Set truck path cost
				for (int k=i+1; k < j; k++)
				{
					E from = _nodesList.get(i);
					E to = _nodesList.get(j);
					E fly = _nodesList.get(k);
					E flyPrev = _nodesList.get(k-1);
					E flyNext = _nodesList.get(k+1);
					
					double driveCost = _distances[i][j] - driveDistance.getContextFreeDistance(flyPrev, fly)
                                                                    - driveDistance.getContextFreeDistance(fly, flyNext)
                                                                    + driveDistance.getContextFreeDistance(flyPrev, flyNext);
					double flyCost = flyDistance.getFlyDistance(from, to, fly);
					
					double cost = Math.max(driveCost, flyCost);
					_operations.set(i, j, k, cost); // Set drone path cost
				}
			}
		}		
	}
	
	private List<Operation<E>> runDynamicProgramming()
	{
		int n = _nodesList.size();
                double cost, bestCost = Double.POSITIVE_INFINITY;
                int bestDriveNode = -1, bestFlyNode = -1;
		double [] costs = new double[n];
		int [] bestDriveNodes = new int[n]; // Truck nodes
		int [] bestFlyNodes = new int[n]; // Drone nodes 

		for (int j=1; j < n; j++)
		{
			bestCost = Double.POSITIVE_INFINITY;
			bestDriveNode = -1;
			bestFlyNode = -1;
			for (int i=0; i < j; i++)
			{
				for (int k=i; k < j; k++)
				{
					cost = _operations.get(i, j, k); 
                                        /* Get the best cost from all possbile drone paths or truck paths */
					if (cost < bestCost)
					{
						bestCost = cost;
						bestDriveNode = i; 
						bestFlyNode = k; 
					}
				}
			}
			bestDriveNodes[j] = bestDriveNode;
			bestFlyNodes[j] = bestFlyNode; // It will be -1 if it can not fly 
			costs[j] = bestCost;
		}

		ArrayList<Operation<E>> result = new ArrayList<>(); // The final operations list 
		int currentNode = n-1;
                Operation<E> operation;
		while (currentNode != 0)
		{
			E flyNode = bestDriveNodes[currentNode] == bestFlyNodes[currentNode] ? null : _nodesList.get(bestFlyNodes[currentNode]);
			List<E> locations = new ArrayList<>();
			for (int i=bestDriveNodes[currentNode]; i <= currentNode; i++)
			{
				E location = _nodesList.get(i);
				if (location != flyNode)
				{
					locations.add(location);
				}
			}
                        
			if (flyNode != null)
			{
				operation = new Operation<>(locations,flyNode);
			}
			else
			{
				operation = new Operation<>(locations);
			}
                        
			result.add(operation);
			currentNode = bestDriveNodes[currentNode];
		}
		Collections.reverse(result);
		return result;
	}
	
	public Solution<E> getSolution()
	{
		buildDistancesTable();
		buildOperations();
		List<Operation<E>> operations = runDynamicProgramming();
		Solution<E> sol = new Solution<>(_instance, operations);
		return sol;
	}
        
	@Override
	public Solution<E> solve(Instance<E> instance, Solution<E> order)
	{
		DynamicProgrammingSolver<E> dp = new DynamicProgrammingSolver<E>(instance, order.getOrder());
		return dp.getSolution();
	}
}
