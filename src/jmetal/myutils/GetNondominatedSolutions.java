/**
 * This class reduces points in the returned FUN.X files
 * By Yi Xiang, Mar. 2, 2015
 */
package jmetal.myutils;

import java.io.IOException;

import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class GetNondominatedSolutions {
	private String experimentName_ ;
	private String[] algName_;
	private String[] problemNames_;
	private int numberofRuns_ ;
	private int objectives_;
	/**
	 * 
	 */
	public GetNondominatedSolutions(String experimentName,String[] problemNames, int numberofRuns,
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
//				"GrEA",
//				"MOMBI2rw",
//				"GrEAnewDoublePopSize",
				"MOEADAWA",

		};
		
		String [] problemNames = new String[]{

//				"MinusDTLZ1",	
				"MinusDTLZ2",
//				"MinusDTLZ3",   
				"MinusDTLZ4",	
		
//			
				"MinusWFG1","MinusWFG2","MinusWFG3",
	    		"MinusWFG4","MinusWFG5",
	    		"MinusWFG6","MinusWFG7","MinusWFG8", 
	    		"MinusWFG9",
	    		
				"DTLZ1",
				"DTLZ2",
				"DTLZ3",
				"DTLZ4",
				"DTLZ5",
				"DTLZ6",
				"DTLZ7",
				"ConvexDTLZ2",//"ConvexDTLZ3","ConvexDTLZ4",
	    		"ScaledDTLZ1",//"ScaledDTLZ2"
				
				"WFG1",	"WFG2",	"WFG3",	"WFG4", "WFG5",	
				"WFG6",	"WFG7",	"WFG8",	"WFG9",		

		};
				
		/*
		 * The following should be tuned manually
		 */
		int objectives = 15;		//
		String experimentName = "./jmetalExperiment/PAEAStudy/M=" + objectives;
		int numberofRuns = 20;			
		/*
		 * End
		 */
		
		GetNondominatedSolutions rp = new GetNondominatedSolutions(experimentName,problemNames,numberofRuns,
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

		
			 for (int k = 0; k < algName_.length;k++){ // for each algorithm
		    		
		    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
		    			
		    			for (int j = 0; j < numberofRuns_;j++) { // for each run
		    				String path = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/FUN."+ j;
		    				SolutionSet nondominatedSet = new SolutionSet();		    				 
		    				nondominatedSet =  utils.readNonDominatedSolutionSet(path);	
		    				
		    				FileUtils.resetFile(path);		    				
		    				nondominatedSet.printFeasibleFUN(path);
		    				System.out.println("***The number of nondominated points = " + nondominatedSet.size());		    				

		    			} // for j
		    			
		    		} // for i
		    	}// for k  
    
	 }//execute
	
	   
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
