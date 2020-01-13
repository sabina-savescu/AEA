package io;

import instance.Instance;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Operation;
import util.Solution;

public class SolutionIO
{
	public static <E> void writeSolution(Solution<E> sol, String filename) throws IOException
	{
                File file = new File(filename);
            	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		writeSolution(sol, pw);
		pw.flush();
		pw.close();
	}

	public static <E> void writeSolution(Solution<E> sol, PrintWriter pw)
	{
		Instance<E> i = sol.getInstance();
		Map<E,Integer> indices = new HashMap<>();
		for (int t=0; t <= i.getLocationCount(); t++)
		{
			indices.put(i.getLocation(t), t);
		}
		
		List<Operation<E>> ops = sol.getOperations();
		pw.println("StartNode\tCombinedNode\tDroneNode\tTruckPath\t\tDronePath");
		for (Operation<E> op : ops)
		{
			pw.print(indices.get(op.getStart()));
			pw.print("\t\t");
			pw.print(indices.get(op.getEnd()));
			pw.print("\t\t");
			if (op.hasFly())
			{
				pw.print(indices.get(op.getFly()));
			}
			else
			{
				pw.print("-1");
			}
			pw.print("\t\t");
			pw.print(op.getDrivePath());
                        pw.print("\t\t");
                        pw.print(op.getFlyPath());
                       
                    
			pw.println();
		}
                
		pw.println("Total cost : "+sol.getTotalCost());
		pw.flush();
	}
}
