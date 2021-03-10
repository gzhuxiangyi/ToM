/**
 * This class implements the calculation of GD analytically.
 * By Yi Xiang
 * 7-May-2016
 */
package jmetal.myutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.experiments.Experiment;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class GetGD {
	private String experimentName_ ;
	private String[] algName_;
	private String[] problemNames_;
	private int numberofRuns_ ;
	private int objectives_;
	/**
	 * 
	 */
	public GetGD(String experimentName,String[] problemNames, int numberofRuns,
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
	 * @throws InterruptedException 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws JMException, ClassNotFoundException, InterruptedException {
		String [] algNames = new String[]{
				/**
				 * Used in PAEAStudy
				 */
//    		    "MOEAD", "MOEADrw"
//				"MOEADDrw",
//				"NSGAIIIrw",
//				"GrEA",
//				"MOMBI2",
//				"MOMBI2rw",
				"PAEAbestv1v2",
				"PAEANoV1V2",
				"PAEAV1",
				"PAEAV2"

		};
		
		String [] problemNames = new String[]{

//				"WFG1",
//				"WFG2","WFG3",
				"WFG4",
				"WFG5",
				"WFG6",
				"WFG7",
				"WFG8",
				"WFG9",
				
				"DTLZ1",
				"DTLZ2",
				"DTLZ3",
				"DTLZ4",
        
//				"ConvexDTLZ2",
//				"ConvexDTLZ3","ConvexDTLZ4",
	    		"ScaledDTLZ1",
	    		"ScaledDTLZ2"
				
//				"NormalWFG4", 
//				"NormalWFG5",
//				"NormalWFG6",
//				"NormalWFG7",
//				"NormalWFG8",
//				"NormalWFG9",

		};
				
		/*
		 * The following should be tuned manually
		 */
		int objectives = 15;		// 
		String experimentName = "./jmetalExperiment/PAEAStudy/M=" + objectives;
		
		int numberofRuns = 15;			
		/*
		 * End
		 */
		
		GetGD gdData = new GetGD(experimentName,problemNames,numberofRuns,
				                    algNames,objectives);				
		gdData.execute();			
					

	}
	
	/**
	 * Execute 
	 * @throws ClassNotFoundException
	 * @throws InterruptedException 
	 */
	
	 public void execute() throws ClassNotFoundException, InterruptedException{
		

    	for (int k = 0; k < algName_.length;k++){ // for each algorithm
    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
    			
    			String writePath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/GD";    		
    			FileUtils.resetFile(writePath);
    			
    			for (int j = 0; j < numberofRuns_;j++) { // for each run
    				String readPath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/FUN."+ j;

    				double [][] data = FileUtils.readMatrix(readPath);    				
    				    				
    	    		if (problemNames_[i].substring(0,3).equalsIgnoreCase("WFG")){ 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					data[r][o] = data[r][o]/(2*(o+1));
        	    			}
    	    			}  
    	    			 
    	    		    GDHyperSphere(writePath, data);
    	    			
    	    		} else if  (problemNames_[i].equalsIgnoreCase("DTLZ1")){ 
		    			for (int r = 0; r < data.length; r ++) {
		    				for(int o = 0; o < objectives_;o++) {
		    					data[r][o] = data[r][o]/0.5;
	    	    			}
		    			}		
		    			
		    			 GDHyperPlane(writePath, data);	
		    			
    	    		} else if (problemNames_[i].equalsIgnoreCase("ScaledDTLZ1")){
    	    			double factor ;
	    				if (objectives_ == 3)
	    					factor = 10.0;
	    				else if (objectives_ == 5)
	    					factor = 10.0;
	    				else if (objectives_ == 8)
	    					factor = 3.0;
	    				else if (objectives_ == 10)
	    					factor = 2.0;
	    				else if (objectives_ == 15)
	    					factor = 1.2;
	    				else if (objectives_ == 20)
	    					factor = 1.2;
	    				else if (objectives_ == 25)
	    					factor =  1.1;
	    				else 
	    					factor = 1.0;
    	    			 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					
    	    					data[r][o] = data[r][o]/(Math.pow(factor, o));
    	    					data[r][o] = data[r][o]/0.5;
        	    			}
    	    			}
    	    			
    	    			GDHyperPlane(writePath, data);	    	    			 
    	    			 
    	    		}else if (problemNames_[i].equalsIgnoreCase("ScaledDTLZ2")){
    	    			double factor ;
	    				if (objectives_ == 3)
	    					factor = 10.0;
	    				else if (objectives_ == 5)
	    					factor = 10.0;
	    				else if (objectives_ == 8)
	    					factor = 3.0;
	    				else if (objectives_ == 10)
	    					factor = 3.0;
	    				else if (objectives_ == 15)
	    					factor = 2.0;
	    				else if (objectives_ == 20)
	    					factor = 1.2;
	    				else if (objectives_ == 25)
	    					factor =  1.1;
	    				else 
	    					factor = 1.0;
    	    			 
    	    			for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					
    	    					data[r][o] = data[r][o]/(Math.pow(factor, o));
    	    					
        	    			}
    	    			}
    	    			
    	    			GDHyperSphere(writePath, data);
    	    			 
    	    		}   else {
    	    			
    	    			GDHyperSphere(writePath, data);
    	    			
    	    		}
    	    		    				
    				
    			} // For each run 
    			
    		} // for i
    	}// for k       	
    	
	 }//execute
	 
	 /**
	  * Calculate the GD value for a given set whose PF is a hyper plane
	  */
	public void GDHyperPlane(String qualityIndicatorFile, double [][] data) {
		
		 double value = 0.0;
		 
		 for (int r = 0; r < data.length; r++ ) { // for each row
			 double sum = 0.0;
			 double OriginalDistance = 0.0; // The distance from the original points to the ideal point
			 double CrossDistance = 0.0; // The distance from the cross points to the ideal point
			 
			 
			 for (int obj = 0; obj < objectives_;obj++) { // for each objective 
				 sum = sum + data[r][obj];
				 OriginalDistance = OriginalDistance + data[r][obj] * data[r][obj];
			 }
			 
			 OriginalDistance =  Math.sqrt(OriginalDistance); 
			 
			 for (int obj = 0; obj < objectives_;obj++) { // for each objective 
				 data[r][obj] = data[r][obj]/sum;
			 }
			 
			 for (int obj = 0; obj < objectives_;obj++) { // for each objective 				
				 CrossDistance = CrossDistance + data[r][obj] * data[r][obj];
			 }
			 
			 CrossDistance =  Math.sqrt(CrossDistance); 			 
			
			 
			 value  = value + (OriginalDistance - CrossDistance);
		 }// for r
		 
		 value = value /  data.length;
		 
		 // Write to file 
		  FileWriter os;
	      try {
	          os = new FileWriter(qualityIndicatorFile, true);
	          os.write("" + value + "\n");
	          os.close();
	      } catch (IOException ex) {
	          Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
	      }
	      
	}// GDHyperPlane
	
	 /**
	  * Calculate the GD value for a given set whose PF is a hyper sphere
	  */
	public void GDHyperSphere(String qualityIndicatorFile, double [][] data) {
		
		 double value = 0.0;
		 
		 for (int r = 0; r < data.length; r++ ) { // for each row
			 double distance = 0.0;
			 
			 for (int obj = 0; obj < objectives_;obj++) { // for each objective 
				 distance = distance + data[r][obj] * data[r][obj];
			 }
			 
			 distance = Math.sqrt(distance);	
			 distance = distance - 1.0;
			 
			 value  = value + distance;
		 }// for r
		 
		 value = value / data.length;
		 
		 // Write to file 
		  FileWriter os;
	      try {
	          os = new FileWriter(qualityIndicatorFile, true);
	          os.write("" + value + "\n");
	          os.close();
	      } catch (IOException ex) {
	          Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
	      }
	      
	}// GDHyperSphere

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
