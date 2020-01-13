package main;

import MST.MSTSolver;
import algorithms.DynamicProgrammingSolver;
import algorithms.GreedySolver;
import algorithms.NeighbourhoodSolver;
import instance.GeometricInstance;
import io.InstanceIO;
import io.SolutionIO;
import java.io.IOException;
import util.Solution;
import util.Location;
import algorithms.Solver;

public class Main {

    public static void main(String [] args)
    {
	String input = "C:\\Users\\Serban\\Desktop\\TSPDrone\\src\\main\\java\\main\\input.txt";
	String output_greedy = "C:\\Users\\Serban\\Desktop\\TSPDrone\\src\\main\\java\\main\\output_greedy.txt";
        String output_dp = "C:\\Users\\Serban\\Desktop\\TSPDrone\\src\\main\\java\\main\\output_dp.txt";
		
	try
	{
            GeometricInstance instance = InstanceIO.readGeometricInstance(input);			
            MSTSolver<Location> tspSolver = new MSTSolver<>();
            Solution<Location> tspSolution = tspSolver.solve(instance);
                        
            Solver<Location> greedy = new GreedySolver<>();
            NeighbourhoodSolver<Location> greedy_solver = new NeighbourhoodSolver<>(instance,tspSolution,greedy);
            Solution<Location> greedy_solution = greedy_solver.solve();
            SolutionIO.writeSolution(greedy_solution, output_greedy);
                      
            Solver<Location> dp = new DynamicProgrammingSolver<>();
            NeighbourhoodSolver<Location> dp_solver = new NeighbourhoodSolver<>(instance,tspSolution,dp);
            Solution<Location> dp_solution = dp_solver.solve();
            SolutionIO.writeSolution(dp_solution, output_dp);
	}
	catch (IOException e)
	{
            System.out.println("An IO Exception occurred");
            e.printStackTrace();
	}
    }
}
