/**
 * This class reduces points in the returned FUN.X files
 * By Yi Xiang, Mar. 2, 2015
 */
package jmetal.myutils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.fastHypervolume.wfg.Front;
import jmetal.qualityIndicator.fastHypervolume.wfg.WFGHV;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

/**
 * @author Administrator
 *
 */
public class ReducePoints {
	private String experimentName_ ;
	private String[] algName_;
	private String[] problemNames_;
	private int numberofRuns_ ;
	private int objectives_;
	/**
	 * 
	 */
	public ReducePoints(String experimentName,String[] problemNames, int numberofRuns,
			String[] algName,int objectives) {
		
		experimentName_ = experimentName;
		problemNames_ = problemNames;
		numberofRuns_ = numberofRuns;	
		algName_ = algName;	
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
		String [] algNames = new String[]{
				"GrEA",
//				"GrEAnewDoublePopSize",

		};
		
		String [] problemNames = new String[]{

				"WFG1",
				"WFG2","WFG3",
				"WFG4","WFG5",
				"WFG6","WFG7",
				"WFG8","WFG9",
//				
				"DTLZ1",
				"DTLZ2",
				"DTLZ3",
				"DTLZ4",
				
//				"ConvexDTLZ2","ConvexDTLZ3","ConvexDTLZ4",
//	    		"ScaledDTLZ1","ScaledDTLZ2"

		};
				
		/*
		 * The following should be tuned manually
		 */
		int objectives = 20;		//
		String experimentName = "./jmetalExperiment/MOABCDStudyConvergenceReviseNew/M=" + objectives;
		int numberofRuns = 50;			
		/*
		 * End
		 */
		
		ReducePoints rp = new ReducePoints(experimentName,problemNames,numberofRuns,
				                    algNames,objectives);				
			
	
		rp.execute();
	}
	
	
	/**
	 * Execute 
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 * @throws JMException 
	 */
	
	 public void execute() throws ClassNotFoundException, IOException, JMException{	
		 
			MetricsUtil utils = new jmetal.qualityIndicator.util.MetricsUtil();
			int finalSize = 0;
			
		    if(objectives_ == 3||objectives_ == 5){	    	    	
			   finalSize = 100 ;  
		    }else if(objectives_ == 8 || objectives_ == 10){	   
		    	finalSize = 125 ;  
		    } else {
		    	finalSize = 200 ;  
		    }
			
		    System.out.println("Final size: " + finalSize) ;
		    
			 for (int k = 0; k < algName_.length;k++){ // for each algorithm
		    		
		    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
		    			
		    			for (int j = 0; j < numberofRuns_;j++) { // for each run
		    				String path = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/FUN."+ j;
		    				SolutionSet nondominatedSet = new SolutionSet();		    				 
		    				nondominatedSet =  utils.readNonDominatedSolutionSet(path);
		    			 
		    				System.out.println("*********" + nondominatedSet.size());
		    				
		    				String changePath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/BackupFUN."+ j;		
		    				ReadAndWrite.exchangeName(path, changePath);
		    				nondominatedSet.printFeasibleFUN(path);
		    				System.out.println("***The number of nondominated points = " + nondominatedSet.size());
		    				
		    				
//		    			    SolutionSet finalSet =  finalSelection(nondominatedSet,finalSize); 	    				    
//		    			    FileUtils.resetFile(path);
//		    			    finalSet.printObjectivesToFile(path);
//		    			    System.out.println("***Final selection size = " + finalSet.size());
		    			} // for j
		    			
		    		} // for i
		    	}// for k   
    	
    	
    	
    
	 }//execute

	 /* @param n: The number of solutions to return
	   * @return A solution set containing those elements
	   * 
	   */
	   SolutionSet finalSelection(SolutionSet population, int n) throws JMException {
	     SolutionSet res = new SolutionSet(n);
	     if (population.size() > n) { //    
	       
	       Distance distance_utility = new Distance();
	       int random_index = PseudoRandom.randInt(0, population.size()-1);
	       
	       // create a list containing all the solutions but the selected one (only references to them)
	       List<Solution> candidate = new LinkedList<Solution>();
	       candidate.add(population.get(random_index));
	       
	       
	       for (int i = 0; i< population.size(); i++) {
	         if (i != random_index)
	          candidate.add(population.get(i));
	       } // for
	       
	       while (res.size() < n) {
	         int index = 0;
	         Solution selected = candidate.get(0); // it should be a next! (n <= population size!)
	         double   distance_value = distance_utility.distanceToSolutionSetInObjectiveSpace(selected, res);
	         int i = 1;
	         while (i < candidate.size()) {
	           Solution next_candidate = candidate.get(i);
	           double aux = distance_value = distance_utility.distanceToSolutionSetInObjectiveSpace(next_candidate, res);
	           if (aux > distance_value) {
	             distance_value = aux;
	             index = i;
	           }
	           i++;
	         }
	         
	         // add the selected to res and remove from candidate list
	         res.add(new Solution(candidate.remove(index),true));           
	         
	       } // 
	       return res;
	     } else {
	    	 return population;
	     }
	     
	   }
	   
	 public String getExperimentName_() {
			return experimentName_;
		}

		public void setExperimentName_(String experimentName_) {
			this.experimentName_ = experimentName_;
		}

		public String[] getAlgName_() {
			return algName_;
		}

		public void setAlgName_(String[] algName_) {
			this.algName_ = algName_;
		}
			
		
		public int getNumberofRuns_() {
			return numberofRuns_;
		}

		public void setNumberofRuns_(int numberofRuns_) {
			this.numberofRuns_ = numberofRuns_;
		}		
	


	 }
