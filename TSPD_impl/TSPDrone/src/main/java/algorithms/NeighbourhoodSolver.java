package algorithms;

import instance.Instance;
import util.Operation;
import util.Solution;
import java.util.ArrayList;
import java.util.List;

public class NeighbourhoodSolver<E>
{
	private Solver<E> solver;
	private Instance<E> instance;
	private ArrayList<E> currentOrder;
	private Solution<E> currentSolution;
	
	private ReverseOrder<E> actions = new ReverseOrder<>();
       
	public NeighbourhoodSolver(Instance<E> instance, Solution<E> initial,Solver<E> solver)
	{
		this.solver = solver;
		this.instance = instance;
		this.actions = actions;
		this.currentOrder = new ArrayList<>();
                
		for (Operation<E> op : initial)
		{
			if (!instance.isDepot(op.getEnd()))
			{
				currentOrder.add(op.getEnd());
			}
		}
		recomputeSolution();
	}
        
	public Solution<E> getCurrentSolution()
	{
		return currentSolution;
	}
	
	private void recomputeSolution()
	{
		currentSolution = solver.solve(instance, addDepots(currentOrder, instance));
	}
	
	public void doAction(ReverseOrder<E> action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("The action cannot be null");
		}
		action.doAction(currentOrder);
		recomputeSolution();
	}
	
	public void undoAction(ReverseOrder<E> action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("The action cannot be null");
		}
		action.undoAction(currentOrder);
		recomputeSolution();
	}
        
	public ReverseOrder<E> getBestAction()
	{
		ReverseOrder<E> best = null;
		double bestSavings = 0;
		double curValue = currentSolution.getTotalCost();
		
		for (ReverseOrder<E> a : actions.getActions(currentOrder, instance))
		{
			a.doAction(currentOrder);
			Solution<E> newSol = solver.solve(instance, addDepots(currentOrder, instance));
			double savings = curValue - newSol.getTotalCost();
			if (savings > bestSavings)
			{
				best = a;
				bestSavings = savings;
			}
			a.undoAction(currentOrder);
		}
		return best;
	}
	
	public Solution<E> solve()
	{
		Solution<E> best = getCurrentSolution();
		boolean improve = true;
		while (improve)
		{	
			ReverseOrder<E> a = getBestAction();
			if (a != null)
			{
				doAction(a);
				Solution<E> newSol = getCurrentSolution();
				if (newSol.getTotalCost() >= best.getTotalCost())
				{
					undoAction(a);
					improve = false;
				}
				else
				{
					best = newSol;
				}
			}
			else
			{
				improve = false;
			}
		}
                return best;
        }
	
	private ArrayList<E> addDepots(List<E> l, Instance<E> i)
	{
		ArrayList<E> res = new ArrayList<E>(l.size() + 2);
		res.add(i.getDepot());
		res.addAll(l);
		res.add(i.getDepot());
		return res;
	}
         
        public class ReverseOrder<E>
        {
            public int from;
            public int to ;

            public ReverseOrder(){}

            public ReverseOrder(int f, int t)
            {
                    from = f;
                    to = t;
            }

            public List<ReverseOrder<E>> getActions(ArrayList<E> lst, Instance<E> e)
            {
                    int n = lst.size();
                    ArrayList<ReverseOrder<E>> result = new ArrayList<>((n*(n+1))/2);
                    for (int i=0; i < n; i++)
                    {
                            for (int j=i+1; j < n; j++)
                            {
                                    result.add(new ReverseOrder(i,j));
                            }
                    }
                    return result;
            }

            public void doAction(ArrayList<E> list)
            {
                    for (int i=0; from+i < to-i; i++)
                    {
                            E temp = list.get(to-i);
                            list.set(to-i, list.get(from+i));
                            list.set(from+i, temp);
                    }
            }

            public void undoAction(ArrayList<E> list)
            {
                    doAction(list);
            }
        }
}




