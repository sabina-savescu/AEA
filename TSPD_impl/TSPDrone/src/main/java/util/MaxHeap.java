package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MaxHeap<E> implements Iterable<E>
{	
	private int _size;
        private double [] _values;
	private int _maxSize;
	private Object [] _keys;
        private String _prevState;
        private static final boolean CHECKING_MODE = false;
	private static final double GROW_FACTOR = 2;
	
	public MaxHeap()
	{
		this(1024);
	}

	public MaxHeap(int maxSize)
	{
		_maxSize = maxSize;
		_values = new double[maxSize];
		_keys = new Object[maxSize];
	}

	@SuppressWarnings("unchecked")
	public E getKey(int index)
	{
		return (E) _keys[index];
	}

	public double getValue(int index)
	{
		return _values[index];
	}

	public int leftChild(int index)
	{
		return 2*index+1;
	}
	
	public int rightChild(int index)
	{
		return 2*index + 2;
	}
	
	public int bestChild(int index)
	{
		int lc = leftChild(index);
		int rc = rightChild(index);
		if (lc >= _size || rc >= _size || _values[lc] >= _values[rc])
		{
			return lc;
		}
		return rc;
	}

	public int parent(int index)
	{
		return (index-1) / 2;
	}
	
	public int add(E key, double value)
	{
		if (CHECKING_MODE)
		{
			_prevState = toString();
		}
		
		if (key == null)
		{
			throw new IllegalArgumentException("Adding null key...");
		}

		if (_size >= _maxSize)
		{
			int newSize = (int)Math.ceil(_maxSize * GROW_FACTOR);
			expand(newSize);
		}
		int index = _size;
		_keys[index] = key;
		_values[index] = value;
		_size++;
		index = bubbleUp(index);
		if (key instanceof HeapIndexChangedListener)
		{
			HeapIndexChangedListener l = (HeapIndexChangedListener) key;
			l.notifyHeapIndex(index);
		}
		if (CHECKING_MODE)
		{
			checkIntegrity("While adding "+key+" with value "+value);
		}
		return index;
	}
	
	public void remove(int index)
	{
		if (CHECKING_MODE)
		{
			_prevState = toString();
		}

		Object obj = _keys[index];
		if (index < _size-1)
		{
			swap(_size-1, index);
			_size--;
			_keys[_size] = null;
			_values[_size] = 0;
			index = bubbleDown(index);
			index = bubbleUp(index);
		}
		else
		{
			_size--;
		}
		
		if (obj instanceof HeapIndexChangedListener)
		{
			HeapIndexChangedListener l = (HeapIndexChangedListener) obj;
			l.notifyHeapIndex(-1);
		}
		
		if (CHECKING_MODE)
		{
			checkIntegrity("After removing "+index);
		}
	}
	
	public int size()
	{
		return _size;
	}

	public int update(int index, double value)
	{
		if (CHECKING_MODE)
		{
			_prevState = toString();
		}
		
		if (_keys[index] == null)
		{
			throw new IllegalStateException();
		}
		_values[index] = value;
		index = bubbleUp(index);
		index = bubbleDown(index);
		
		if (CHECKING_MODE)
		{
			checkIntegrity("While updating position "+index+" to value "+value);
		}
		
		return index;
	}
	
	private int bubbleUp(int index)
	{
		if (index < 1)
		{
			return index;
		}
		while (index > 0 && _values[parent(index)] < _values[index])
		{
			swap(index,parent(index));
			index = parent(index);
		}
		return index;
	}
	
	private int bubbleDown(int index)
	{
		int bestChild;
		while ((bestChild = bestChild(index)) < _size)
		{
			if (_values[bestChild] > _values[index])
			{
				swap(bestChild, index);
				index = bestChild;
			}
			else
			{
				return index;
			}
		}
		return index;
	}
	
	private void swap(int index, int otherIndex)
	{
		if (index < 0 || otherIndex < 0 || index >= _size || otherIndex >= _size)
		{
			throw new IndexOutOfBoundsException();
		}
		Object tmp = _keys[otherIndex];
		double tmpVal = _values[otherIndex];
		_keys[otherIndex] = _keys[index];
		_values[otherIndex] = _values[index];
		_keys[index] = tmp;
		_values[index] = tmpVal;
		if (_keys[index] instanceof HeapIndexChangedListener)
		{
			HeapIndexChangedListener l = (HeapIndexChangedListener) _keys[index];
			l.notifyHeapIndex(index);
		}
		if (_keys[otherIndex] instanceof HeapIndexChangedListener)
		{
			HeapIndexChangedListener l = (HeapIndexChangedListener) _keys[otherIndex];
			l.notifyHeapIndex(otherIndex);			
		}	
	}
	
	private void expand(int newSize)
	{
		if (newSize < _maxSize)
		{
			return;
		}
		double [] newValue = new double[newSize];
		Object [] newKeys = new Object[newSize];
		for (int t=0; t < _size; t++)
		{
			newValue[t] = _values[t];
			newKeys[t] = _keys[t];
		}
		_values = newValue;
		_keys = newKeys;
		_maxSize = newSize;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int t=0; t < _size; t++)
		{
			sb.append("(");
			sb.append(_keys[t]);
			sb.append(" => ");
			sb.append(_values[t]);
			sb.append(")");
		}
		sb.append("]");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<E> iterator()
	{
		List<E> res = new ArrayList<>(_size);
		for (int t=0; t < _size; t++)
		{
			res.add((E) _keys[t]);
		}
		return res.iterator();
	}
	
	private void checkIntegrity(String msg)
	{
		for (int t=0; t < _size; t++)
		{
			double val = _values[t];
			int lc = leftChild(t);
			int rc = rightChild(t);
			if (lc < _size && _values[lc] > val)
			{
				throw new IllegalArgumentException(msg+": Left Child of "+t+" at position "+lc+" has value "+_values[lc]+" while "+t+" has "+val);
			}
			if (rc < _size && _values[rc] > val)
			{
				throw new IllegalArgumentException(msg+": Right Child of "+t+" at position "+rc+" has value "+_values[rc]+" while "+t+" has "+val);
			}
		}
	}
	
	public static interface HeapIndexChangedListener
	{
		public void notifyHeapIndex(int index);
	}
}
