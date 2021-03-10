package jmetal.myutils;

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;
import jmetal.experiments.settings.MaPSO_Settings;
import jmetal.util.JMException;

public class CaptureConvergenceMain {

	public CaptureConvergenceMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws JMException, ClassNotFoundException {
		/*
		 * To run 
		 * 1. Change problemName in this class
		 * 2. Change the expName_ and gap_in class CaptureConvergence
		 * 3. run study class to get IGD values
		 * 4. Run CaptureConvergencPlot to get figures
		 */
//		String problemName = "CarCabDesign";// Note the expName_ and gap_in CaptureConvergence should be modified if necessary 
		 String [] problemNames = {
//				  				"DTLZ1","DTLZ2","DTLZ3","DTLZ4","ConvexDTLZ2","ScaledDTLZ1", "ScaledDTLZ2", 
//				  				"WFG1","WFG2","WFG3","WFG4","WFG5","WFG6","WFG7","WFG8","WFG9"
				                  "DTLZ1","DTLZ3", 
//				                  "ScaledDTLZ1",
				                  "WFG1",
//				                  "WFG2","WFG3",
		 						};
		 for (int i = 0; i < problemNames.length;i++) { // for each problem
			 String problemName = problemNames[i];		
			 
			 int noOfObjectives_ = 15;
			 
			 Algorithm [] algorithm = new Algorithm[5];	
			 
			  if (problemName.contains("WFG")) {
		    	    Object[] problemParams = {"Real", 2*(noOfObjectives_ - 1), 20, noOfObjectives_}; // WFG: solutionType, k, l, M
					/**
					 * Used in MOABCD study
					 */
//					algorithm[0] = new MOABCD_Settings(problemName,problemParams).configure();  
//		    	    algorithm[1] = new MOEADPBI_Settings(problemName,problemParams).configure(); 
//		    	    algorithm[2] = new MOEAD_DRA_Settings(problemName,problemParams).configure(); 
//		    	    algorithm[3] = new NSGAIII_MOABCD_Settings(problemName,problemParams).configure(); 
//		    	    algorithm[4] = new eMOABC_MOABCD_Settings(problemName,problemParams).configure(); 
					
	    	    /**
				 * Used in PAEA study
				 */
		    	  algorithm[0] = new MaPSO_Settings(problemName,problemParams).configure();     	  
//			      algorithm[0] = new MOEADD_Settings(problemName,problemParams).configure(); 
//			      algorithm[1] = new NSGAIII_Settings(problemName,problemParams).configure(); 
//			      algorithm[2] = new MOEAD_Settings(problemName,problemParams).configure(); 
		      }   
		      
		      if (problemName.contains("DTLZ")) {
		    	  int n ;
		    	  if (problemName.equalsIgnoreCase("DTLZ1") || problemName.equalsIgnoreCase("ScaledDTLZ1")) {
		    		 n = noOfObjectives_ + 5 - 1;// r = 5 for DTLZ1    		
		    	  } else {
		    		 n = noOfObjectives_ + 10 - 1;// r = 10 for DTLZ2-4
		    	  }    	 
		    	  	Object[] problemParams = {"Real", n, noOfObjectives_}; // DTLZ: solutionType,  n, m
		    	 
					/**
					 * Used in MOABCD study
					 */
//					algorithm[0] = new MOABCD_Settings(problemName,problemParams).configure();  
//		    	    algorithm[1] = new MOEADPBI_Settings(problemName,problemParams).configure(); 
//		    	    algorithm[2] = new MOEAD_DRA_Settings(problemName,problemParams).configure(); 
//		    	    algorithm[3] = new NSGAIII_MOABCD_Settings(problemName,problemParams).configure(); 
//		    	    algorithm[4] = new eMOABC_MOABCD_Settings(problemName,problemParams).configure(); 
	
		    	  
		    	    /**
					 * Used in PAEA study
					 */
//			    	  algorithm[0] = new PAEA_Settings(problemName,problemParams).configure();     	  
				      algorithm[0] = new MaPSO_Settings(problemName,problemParams).configure(); 
//				      algorithm[1] = new NSGAIII_Settings(problemName,problemParams).configure(); 
//				      algorithm[2] = new MOEAD_Settings(problemName,problemParams).configure();        
		      }       	  
					  
		      
			  SolutionSet solutionSet = algorithm [0].execute();
//			  solutionSet = algorithm [1].execute();
//			  solutionSet = algorithm [2].execute();
//			  solutionSet = algorithm [3].execute();
//			  solutionSet = algorithm [4].execute();
			  
		 }// for each problem

		  
	}

}
