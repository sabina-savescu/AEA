package MST;

import java.util.HashMap;
import java.util.Map;

public class UnionFind<E>
{
	private Map<E,SetNode> _pointers;

	public UnionFind()
	{
		_pointers = new HashMap<>();
	}
	
	public boolean sameSet(E e1, E e2)
	{
		SetNode sn1 = _pointers.get(e1);
		SetNode sn2 = _pointers.get(e2);
		if (sn1 == null)
		{
			throw new IllegalArgumentException("First argument was never registered as a set!");
		}
		if (sn2 == null)
		{
			throw new IllegalArgumentException("Second argument was never registered as a set!");
		}
		return sn1.getRoot().element.equals(sn2.getRoot().element);
	}

	public void createSet(E e)
	{
		if (!_pointers.containsKey(e))
		{
			SetNode sn = new SetNode(e);
			_pointers.put(e, sn);
		}
		else
		{
			throw new IllegalArgumentException("There is already a set for element "+e);
		}
	}

	public void union(E e1, E e2)
	{
		SetNode sn1 = _pointers.get(e1);
		SetNode sn2 = _pointers.get(e2);
		if (sn1 == null)
		{
			throw new IllegalArgumentException("First argument was never registered as a set!");
		}
		if (sn2 == null)
		{
			throw new IllegalArgumentException("Second argument was never registered as a set!");
		}
		sn1.getRoot().setParent(sn2);
	}
	
	private class SetNode
	{
		public final E element;
		private SetNode _parent;
		
		public SetNode(E e)
		{
			element = e;
		}
		
		public SetNode getRoot()
		{
			if (_parent == null)
			{
				return this;
			}
			SetNode cur = _parent;
			while (cur._parent != null)
			{
				cur = cur._parent;
			}
			_parent = cur;
			return cur;
		}
		
		public void setParent(SetNode parent)
		{
			if (_parent != null)
			{
				throw new IllegalArgumentException("Cannot set a parent, as one was already set!");
			}
			_parent = parent;
		}
	}
}
