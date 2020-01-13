package algorithms;

import instance.Instance;
import util.Solution;
import java.util.List;

public interface Solver<E>
{
    default public Solution<E> solve(Instance<E> instance, List<E> order)
    {
	return solve(instance, new Solution<E>(order, instance));
    }
	
    Solution<E> solve(Instance<E> instance, Solution<E> order);
}
