/**
 * This class implements the fast WFG calculation of HV.
 * By Yi Xiang, Mar. 1, 2015
 */
package jmetal.myutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jmetal.qualityIndicator.fastHypervolume.wfg.Front;
import jmetal.qualityIndicator.fastHypervolume.wfg.Point;
import jmetal.qualityIndicator.fastHypervolume.wfg.WFGHV;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class WFG_HV {
	private String experimentName_ ;
	private String[] algName_;
	private String[] problemNames_;
	private int numberofRuns_ ;
	private int objectives_;
	/**
	 * 
	 */
	public WFG_HV(String experimentName,String[] problemNames, int numberofRuns,
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
				"eMOABC",
//				"VaEABest",
//				"MOEADD",
//	    		"NSGAIII", 
//	    		"MOEAD",
//	    		"GrEA",
//	    		"MOMBI2",   
		};
		
		String [] problemNames = new String[]{

//				"WFG1",
//				"WFG2","WFG3",
//				"WFG4","WFG5",
//				"WFG6","WFG7",
//				"WFG8","WFG9",
//				
//				"DTLZ1",
//				"DTLZ2",
				"DTLZ3",
//				"DTLZ4",
				
//				"ConvexDTLZ2","ConvexDTLZ3","ConvexDTLZ4",
//	    		"ScaledDTLZ1","ScaledDTLZ2"

		};
				
		/*
		 * The following should be tuned manually
		 */
		int objectives = 3;		// For the number of objectives beyond 10
		String experimentName = "./jmetalExperiment/MOABCDStudyRevise/M=" + objectives;
		int numberofRuns = 30;			
		/*
		 * End
		 */
		
		WFG_HV wfg_hv = new WFG_HV(experimentName,problemNames,numberofRuns,
				                    algNames,objectives);				
			
		wfg_hv.normalizeObjectives();			
		wfg_hv.execute();
	}
	
	/**
	 * Normalize each objective according to the range of each objective in a given problem.
	 */
    public void normalizeObjectives () {
    	
    	for (int k = 0; k < algName_.length;k++){ // for each algorithm
    		
    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
    			
    			for (int j = 0; j < numberofRuns_;j++) { // for each run
    				String readPath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/FUN."+ j;
    				
    				double [][] data = FileUtils.readMatrix(readPath);    				
    				    				
    	    		if (problemNames_[i].contains("WFG")){ 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					data[r][o] = data[r][o]/(2*(o+1));
        	    			}
    	    			}
    	    			
    	    		} else if  (problemNames_[i].equalsIgnoreCase("DTLZ1")){ 
		    			for (int r = 0; r < data.length; r ++) {
		    				for(int o = 0; o < objectives_;o++) {
		    					data[r][o] = data[r][o]/0.5;
	    	    			}
		    			}		    		
    	    			
    	    		} else if (problemNames_[i].contains("ScaledDTLZ1")){
    	    			double factor ;
	    				if (objectives_ == 3)
	    					factor = 10;
	    				else if (objectives_ == 5)
	    					factor = 10;
	    				else if (objectives_ == 8)
	    					factor = 3;
	    				else if (objectives_ == 10)
	    					factor = 2;
	    				else if (objectives_ == 15)
	    					factor = 1.2;
	    				else 
	    					factor = 1;
    	    			 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					
    	    					data[r][o] = data[r][o]/(Math.pow(factor, o));
    	    					
        	    			}
    	    			}
    	    		}else if (problemNames_[i].contains("ScaledDTLZ2")){
    	    			double factor ;
	    				if (objectives_ == 3)
	    					factor = 10;
	    				else if (objectives_ == 5)
	    					factor = 10;
	    				else if (objectives_ == 8)
	    					factor = 3;
	    				else if (objectives_ == 10)
	    					factor = 3;
	    				else if (objectives_ == 15)
	    					factor = 2;
	    				else 
	    					factor = 1;
    	    			 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					
    	    					data[r][o] = data[r][o]/(Math.pow(factor, o));
    	    					
        	    			}
    	    			}
    	    		} // if 
    			
    				
    				String writePath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/NormlizedFUN."+ j;
    				
    				FileUtils.writeMatrix(writePath, data,false);
    			} // for j
    			
    		} // for i
    	}// for k   
    } // normalizeObjectives
	
	/**
	 * Execute 
	 * @throws ClassNotFoundException
	 * @throws IOException 
	 */
	
	 public void execute() throws ClassNotFoundException, IOException{
		 /**
		  * Construct reference point
		  */		  
		    Point referencePoint ;
		    double [] points = new double[objectives_] ;
		    
		    for (int i = 1; i <= objectives_; i++)
		         points[i-1] = 1.0;		   	

		    referencePoint = new Point(points) ;
		    System.out.println("Using reference point: " + referencePoint) ;		    
		    
			 for (int k = 0; k < algName_.length;k++){ // for each algorithm
		    		
		    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
		    			
		    			for (int j = 0; j < numberofRuns_;j++) { // for each run
		    				String normlizedFUN_Path = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/NormlizedFUN."+ j;
		    				 
		    				Front front = new Front() ;	    				  
		    				front.readFront(normlizedFUN_Path);

	    				    WFGHV wfghv = new WFGHV(referencePoint.getNumberOfObjectives(), front.getNumberOfPoints(), referencePoint) ;
	    				    
	    				    System.out.println("hv = " + wfghv.getHV(front)) ;
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
