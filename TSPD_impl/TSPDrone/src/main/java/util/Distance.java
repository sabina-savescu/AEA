package util;
import java.util.List;

public interface Distance<E>
{
	public enum Action
        {
            DEPARTURE,
            VISIT,
            ARRIVAL,
            UNDEFINED;
        }
        
	public double getDistance(E from, E to, Action fromAction, Action toAction, double priorDistance);
	
	public default double getDepartVisit(E from, E to)
	{
		return getDistance(from, to, Action.DEPARTURE, Action.VISIT, 0);
	}

	public default double getDepartArrive(E from, E to)
	{
		return getDistance(from, to, Action.DEPARTURE, Action.ARRIVAL, 0);
	}

	public default double getVisitTwice(E from, E to, double prior)
	{
		return getDistance(from, to, Action.VISIT, Action.VISIT, prior);
	}

	public default double getVisitArrive(E from, E to, double prior)
	{
		return getDistance(from, to, Action.VISIT, Action.ARRIVAL, prior);
	}
	
	public default double getContextFreeDistance(E from, E to)
	{
		return getDistance(from, to, Action.UNDEFINED, Action.UNDEFINED, -1);
	}
	
	public default double getContextFreeDistance(E from, E to, double prior)
	{
		return getDistance(from, to, Action.UNDEFINED, Action.UNDEFINED, prior);
	}
	
	public default double getFlyDistance(E from, E to, E fly)
	{
		double step1 = getDepartVisit(from, fly);
		return step1 + getVisitArrive(fly, to, step1);
	}
	
	public default double getPathDistance(E start, E end, List<E> intermediate)
	{
		double result = 0;
		if (intermediate.isEmpty())
		{
			return getDepartArrive(start, end);
		}
		E prev = null;
		for (E e : intermediate)
		{
			if (prev == null)
			{
				result += getDepartVisit(start, e);
			}
			else
			{
				result += getVisitTwice(prev, e, result);
			}
			prev = e;
		}
		result += getVisitArrive(prev, end, result);
		return result;
	}
}
