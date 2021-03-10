package jmetal.CHTs.ToM;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;

public class CaptureConvergenceMain {

	public CaptureConvergenceMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws JMException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		/*
		 * To run 
		 * 1. Change problemName in this class
		 * 2. Change the expName_ and gap_in class CaptureConvergence
		 * 3. run study class to get IGD values
		 * 4. Run CaptureConvergencPlot to get figures
		 */
//		String problemName = "CarCabDesign";// Note the expName_ and gap_in CaptureConvergence should be modified if necessary 
		 String [] problemNames = {
					"CF1II",
		    		"CF2II",
		    		"CF3II",
		    		"CF4II",
		      		"CF5II",
		    		"CF6II",
		    		"CF7II",
		    		"CF8II",
		      		"CF9II",
		    		"CF10II",
		    		"CF11II",
		    		"CF12II",
		      		"CF13II",
		    		"CF14II",
		    		"CF15II",
		    		"CF16II",
			  		"CF17II",
					"CF18II",
					"CF19II",
					"CF20II",
		 };
		 
		 int noOfObjectives_ = 8;
		 int               C = 30;  //C = 10, 30, 60		    
		 int noOfVariables_  = C + noOfObjectives_;   //D = C + M
		    
		 int numberOfAlgorithms = 5;		 
		 int noOfFEs_       =  noOfObjectives_  * 30000;  // FEs = m * 30000       
		 
		 //-----------------------------------------------------------------------------
		 for (int i = 0; i < problemNames.length;i++) { // for each problem
			 
			 String problemName = problemNames[i];		
			 Object[] problemParams = {"Real", noOfVariables_, noOfObjectives_}; // ZHX: solutionType,  n, m
			 
			 Algorithm [] algorithm = new Algorithm[numberOfAlgorithms];			
		     HashMap[] parameters = new HashMap[numberOfAlgorithms];

		      for (int j = 0; j < numberOfAlgorithms; j++) {
		        parameters[j] = new HashMap();
		        parameters[j].put("maxEvaluations_", noOfFEs_);
		      } // for
	
			  algorithm[0] = new CNSGAIII_Settings(problemName,problemParams).configure(parameters[0]);
			  
			  SolutionSet solutionSet = algorithm [0].execute();
			  solutionSet = algorithm [1].execute();
			  solutionSet = algorithm [2].execute();
			  solutionSet = algorithm [3].execute();
			  solutionSet = algorithm [4].execute();
//			  solutionSet = algorithm [5].execute();
//			  solutionSet = algorithm [6].execute();
		 }// for each problem

		  
	}

}
