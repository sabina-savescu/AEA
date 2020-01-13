package instance;

import util.Distance;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public interface Instance<E> extends Iterable<E>
{
	default public int getNodeCount()
	{
		return getLocationCount() + 1;
	}
	
	public int getLocationCount();
	
	public E getDepot();
	
	default public boolean isDepot(E e)
	{
		return e.equals(getDepot());
	}
	
	public List<E> getLocations();

	default public E getLocation(int index)
	{
		if (index == 0)
		{
			return getDepot();
		}
		List<E> locs = getLocations();
		if (index > locs.size() || index < 0)
		{
			throw new IndexOutOfBoundsException();
		}
		return locs.get(index-1);
	}
	
	public Instance<E> getSubInstance(Predicate<? super E> retain);
	
	public default Instance<E> getSubInstance(Collection<? super E> retain)
	{
		return getSubInstance(retain::contains);
	}

	public Distance<E> getDriveDistance();

	public Distance<E> getFlyDistance();
	
	@Override
	default public Iterator<E> iterator()
	{
		return new Iterator<E>()
		{

			Iterator<E> it = getLocations().iterator();
			
			@Override
			public boolean hasNext()
			{
				return it != null;
			}

			@Override
			public E next()
			{
				if (it == null)
				{
					return null;
				}
				if (!it.hasNext())
				{
					it = null;
					return getDepot();
				}
				return it.next();
			}
			
		};
	}
}
