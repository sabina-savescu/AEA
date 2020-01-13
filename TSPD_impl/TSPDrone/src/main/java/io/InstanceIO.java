package io;

import instance.GeometricInstance;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import util.Location;

public class InstanceIO
{
	public static GeometricInstance readGeometricInstance(String filename) throws IOException
	{
                File file = new File(filename);
        	StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			sb.append(line);	
			sb.append("\n");
		}
		br.close();
                
		return readGeometricInstanceFromData(sb.toString());
        }

	public static GeometricInstance readGeometricInstanceFromData(String inputData)
	{
		TokenScanner ts = new TokenScanner(inputData);
		double drive = ts.nextDouble();
		double fly = ts.nextDouble();
		int n = ts.nextInt();
		ArrayList<Location> points = new ArrayList<>();
		Location depot = null;
		for (int t=0; t < n; t++)
		{
			double x = ts.nextDouble();
			double y = ts.nextDouble();
			String name = ts.nextIdentifier();
			Location p = new Location(x,y,name);
			if (depot == null)
			{
				depot = p;
			}
			else
			{
				points.add(p);
			}
		}
		return new GeometricInstance(depot,points,drive,fly);
	}
}
