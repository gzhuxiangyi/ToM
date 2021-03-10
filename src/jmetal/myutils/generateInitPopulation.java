/** generateInitPopulation.java
 * Generate initial population, and save it in files 
 * For each run, the same population is used.
 * last modified 2018/8/24
 */

package jmetal.myutils;

import java.io.IOException;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.problems.ProblemFactory;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class generateInitPopulation {
	private String experimentName_ ;
	private String[] problemNames_;
	private int objectives_;
	
	/**
	 * 
	 */
	public generateInitPopulation(String experimentName,String[] problemNames, int objectives) {
		
		experimentName_ = experimentName;
		problemNames_ = problemNames;	
		objectives_ = objectives;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param args
	 * @throws JMException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws JMException, ClassNotFoundException, IOException {

		String [] problemNames = new String[]{

				"DTLZ1", 
				"DTLZ2", "DTLZ3", "DTLZ4", 
				"DTLZ5","DTLZ6",
				"DTLZ7",			
				"ConvexDTLZ2",//	"ConvexDTLZ3","ConvexDTLZ4",			
				"ScaledDTLZ1",	
	    		"ScaledDTLZ2", 	
	    		
				"MinusDTLZ1",	
				"MinusDTLZ2",
				"MinusDTLZ3",   
				"MinusDTLZ4",	
				
				"WFG1",	"WFG2",	"WFG3",	"WFG4", "WFG5",	
				"WFG6",	"WFG7",	"WFG8",	"WFG9",				
			
				"MinusWFG1","MinusWFG2","MinusWFG3",
	    		"MinusWFG4","MinusWFG5",
	    		"MinusWFG6","MinusWFG7","MinusWFG8", 
	    		"MinusWFG9",

		};
				
		/*
		 * The following should be tuned manually
		 */
		int objectives = 15;		//
		String experimentName = "./jmetalExperiment/PAEAStudy/M=" + objectives;

		/*
		 * End
		 */
		
		generateInitPopulation gp = new generateInitPopulation(experimentName,problemNames,objectives);			
		gp.execute();
	}
	
	
	/**
	 * Execute 
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 * @throws JMException 
	 */
	
	 public void execute() throws ClassNotFoundException, IOException, JMException{	
		 
		int populationSize_ = 0;
		Problem problem_ = null;
		
			
		//-------------------------Setting of populationSize_-------------------
	    if(objectives_ == 3){	    	    	
	    	populationSize_ = 92   ;	    				
	    	    
	    }else if(objectives_ == 5){	    
	    	
	    	populationSize_ = 212   ;	    	
		
	    }else if(objectives_ == 8){
	    	populationSize_ = 156   ;	    	
		
	    } else if (objectives_ == 9){	    	
	    	
	    	populationSize_ = 210   ;
	    	
	    } else if(objectives_ == 10){	
	    	
	    	populationSize_ = 276   ;    
			
	    }else if(objectives_ == 15){	    
	    	
	    	populationSize_ = 136   ;	    	
		
	    }else if(objectives_ == 20){	    
	    	
	    	populationSize_ = 200   ;	    	
			
	    }else if(objectives_ == 25){	    
	    	
	    	populationSize_ = 300   ;	    			
	    }
	  //-------------------------Setting of populationSize_ end-------------------

		for (int i = 0; i < problemNames_.length; i++) { // for each problem 
			
			String problemName = problemNames_[i];
			
			 
			 if (problemName.contains("WFG")) {
				 Object[] problemParams = {"Real", 2*(objectives_ - 1), 20, objectives_}; // WFG: solutionType, k, l, M
				 
				try {
					    problem_ = (new ProblemFactory()).getProblem(problemName, problemParams);
			    } catch (JMException e) {
				    // TODO Auto-generated catch block
				    e.printStackTrace();
			    }
				 
		      } else  if (problemName.contains("DTLZ")) {
		    	  int n ;
		    	  if (problemName.equalsIgnoreCase("DTLZ1") || problemName.equalsIgnoreCase("ScaledDTLZ1")) {
		    		 n = objectives_ + 5 - 1;// r = 5 for DTLZ1    
		    	  } else if (problemName.equalsIgnoreCase("DTLZ7")) {
		    		 n = objectives_ + 20 - 1;// r = 20 for DTLZ7    
		    	  } else {
		    		 n = objectives_ + 10 - 1;// r = 10 for DTLZ2-6
		    	  }    	 
		    	  
		    	  Object[] problemParams = {"Real", n, objectives_}; // DTLZ: solutionType,  n, m
		    	  
		    	    try {
					    problem_ = (new ProblemFactory()).getProblem(problemName, problemParams);
				    } catch (JMException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
				    }
       
		      } else {		    	  
		    	  Object[] problemParams = {"Real"}  ; 

		    	  try {
					    problem_ = (new ProblemFactory()).getProblem(problemName, problemParams);
				    } catch (JMException e) {
					    // TODO Auto-generated catch block
					    e.printStackTrace();
				    }
		    	  
		      }
			 
			 
			 // Generate a set of random solutions
			SolutionSet population_ = new SolutionSet(populationSize_);

			for (int j = 0; j < populationSize_; j++) {
				Solution newSolution = new Solution(problem_);

				problem_.evaluate(newSolution);
				problem_.evaluateConstraints(newSolution);
	
				population_.add(newSolution);
			} // for
			
			String path = experimentName_ + "/InitialPopulation/";
			
			FileUtils.CheckDir(path);
			
			String funPath = path +  problemName +".FUN";
			FileUtils.resetFile(funPath);			
			population_.printObjectivesToFile(funPath);
			
			
			String varPath = path +  problemName +".VAR";
			FileUtils.resetFile(varPath);
			population_.printVariablesToFile(varPath);
			 
		} // for (int i = 0; i < problemNames_.length; i++) 
    	
    	
    
	 }//execute

	
	   
	 public String getExperimentName_() {
			return experimentName_;
		}

		public void setExperimentName_(String experimentName_) {
			this.experimentName_ = experimentName_;
		}
	


}



