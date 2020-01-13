package util;

public class Array3D
{
	private final double [] _data;
	private final int _xSize;
        private final int _ySize;
        private final int _zSize;
	private final boolean _isSafe;
	
	public Array3D(int x, int y, int z)
	{
		this(x,y,z,true);
	}

	public Array3D(int x, int y, int z, boolean safe)
	{
		if (x < 1)
		{
			throw new IllegalArgumentException("X-dimension must have size at least 1.");
		}
		if (y < 1)
		{
			throw new IllegalArgumentException("Y-dimension must have size at least 1.");
		}
		if (z < 1)
		{
			throw new IllegalArgumentException("Z-dimension must have size at least 1.");
		}
		_xSize = x;
		_ySize = y;
		_zSize = z;
		_data = new double[x*y*z];
		_isSafe = safe;
	}
	
	public void set(int x, int y, int z, double val)
	{
		if (_isSafe)
		{
			if (x < 0 || x >= _xSize)
			{
				throw new IndexOutOfBoundsException();
			}
			if (y < 0 || y >= _ySize)
			{
				throw new IndexOutOfBoundsException();
			}
			if (z < 0 || z >= _zSize)
			{
				throw new IndexOutOfBoundsException();
			}
		}
		_data[getIndex(x,y,z)] = val;
	}
	
	private int getIndex(int x, int y, int z)
	{
		return x * (_ySize * _zSize) + y * (_zSize) + z;
	}
	
	public double get(int x, int y, int z)
	{
		if (_isSafe)
		{
			if (x < 0 || x >= _xSize)
			{
				throw new IndexOutOfBoundsException();
			}
			if (y < 0 || y >= _ySize)
			{
				throw new IndexOutOfBoundsException();
			}
			if (z < 0 || z >= _zSize)
			{
				throw new IndexOutOfBoundsException();
			}
		}
		return _data[getIndex(x,y,z)];
	}
        
	public int xDim()
	{
		return _xSize;
	}
	
	public int yDim()
	{
		return _ySize;
	}

	public int zDim()
	{
		return _zSize;
	}
}
