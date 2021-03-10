/**
 * This class plots given algorithms' Pareto fronts for comparing , often a control algorithm is needed,or a new algorithm
 * Created by Yi Xiang 2013/12/7
 */
package jmetal.problems.CEC20;

import java.io.File;

import jmetal.CHTs.ToM.CMOEAStudy;
import jmetal.CHTs.ToM.MultiPlot;
import jmetal.experiments.Experiment;
import jmetal.myutils.QSort;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class PlotFronts3 {
	private String experimentName_ ;
	private String[] algName_;
	private String controlAlg_;
	private String problemName_="" ;
	private String[] frontPath_ ;
	private String trueFront_ ;
	private String storePath_;
	private int numberofRuns_ ;
	private String indicatorName_;
	private String[] property_;
	private int objectives_;
	private String frontType;
	/**
	 * 
	 */
	public PlotFronts3(String experimentName,String problemName, int numberofRuns,
			String controlAlg, String indicatorName,String[] algName,String[] property, String trueFront, int objectives) {
		
		experimentName_ = experimentName;
		problemName_ = problemName;
		numberofRuns_ = numberofRuns;
		indicatorName_ = indicatorName;
		algName_ = algName;
		property_ = property;
		frontPath_ = new String [algName_.length];
		trueFront_ = trueFront;
		controlAlg_ = controlAlg;
		objectives_ = objectives;
		// TODO Auto-generated constructor stub
	}
	
	 public void execute() throws ClassNotFoundException{
		MetricsUtil meticsUtil = new MetricsUtil();		
//		String baseFrontPath = "./paretoFronts/ZHX/"; // "./paretoFronts/"; //
//		String baseFrontPath = "./paretoFronts/Constraint/"; // For constrained problems
		String baseFrontPath = "./paretoFronts/CEC20/"; 
    	for (int k = 0; k < algName_.length;k++){
	    	String indicatorPath = experimentName_ + "/data/" + algName_[k] + "/" + problemName_+"/"+ indicatorName_;
	    	System.out.println(indicatorPath);
	    	double [][] indicatorValues = meticsUtil.readFront(indicatorPath);
	    	
			/* Method 1
			 *  Find the best indicator 
			 */
//	    	double bestValue ;
//	    	int bestIndex;
//	    	
//	    	bestValue = indicatorValues[0][0];
//			bestIndex = 0;
//		
//	    	for(int i = 1;i<indicatorValues.length;i++){
//	    		 if (indicatorName_.equals("HV")){
//		    		  if (indicatorValues[i][0]> bestValue){
//		    			  bestValue = indicatorValues[i][0];
//		    			  bestIndex = i;
//		    		  }
//		    	  }else {
//		    		  if (indicatorValues[i][0]< bestValue){
//		    			  bestValue = indicatorValues[i][0];
//		    			  bestIndex = i;
//		    		  }
//		    	  }    	    	  
//	    	}//for  
	    	
	    	/* Method 2
			 *  Find the median indicator 
			 */
	    	double [] values = new double [indicatorValues.length];
	    	int [] index = new int  [indicatorValues.length];
	    	
	    	for (int i = 0; i < numberofRuns_;i ++) {
	    		values [i] = indicatorValues[i][0];
	    	}
	    	
	    	QSort.quicksort(values, index, 0, indicatorValues.length - 1);
	    	
	    	
	    	double maxVal, minVal;
	    	int bestIndex;
	    	
	    	maxVal =  values[indicatorValues.length - 1];
	    	minVal =  values[0];
	    	
	    	
	    	double median ;
	    	
	    	// Median
			if (indicatorValues.length % 2 != 0) {
				median = (Double) values[indicatorValues.length/2];
			} else {
				median = ((Double) values[indicatorValues.length/2 - 1] +
						(Double)values[indicatorValues.length/2]) / 2.0;
			} // if
	    	
	    	double bestDiff = Math.abs(indicatorValues[0][0] - median);
	    	bestIndex = 0;
	    	
	    	for (int i = 1; i < numberofRuns_;i++){
	    		 double diff = Math.abs(indicatorValues[i][0]  - median);
	    		 if (diff < bestDiff){
	    			 bestDiff = diff;
	    			 bestIndex = i;
	    		  }
	    	}    
	    	// End of method 2
	    	
//	    	System.out.println("bestDiff " + bestDiff);
//	    	System.out.println("median " + median);
//	    	System.out.println("bestIndex value " + bestIndex);
	       frontPath_[k] = experimentName_ + "/data/" + algName_[k] + "/" + problemName_+"/FUN." + bestIndex;
//	       System.out.print(frontPath_[k]);
    	}//for 
    	    	    	
    	
//    		if (problemName_.contains("WFG")){   
//    			trueFrontPath = trueFrontPath + problemName_;
//    			trueFrontPath = trueFrontPath + ".M3.pf";
//    		}else if(problemName_.contains("DTLZ")&& problemName_.substring(0, 1).equals("C")){
//    			trueFrontPath = trueFrontPath + problemName_;
//    			trueFrontPath = trueFrontPath + ".M3.pf";
//    		}else if(problemName_.contains("DTLZ")){
//    			trueFrontPath = trueFrontPath + problemName_;
//    			trueFrontPath = trueFrontPath + ".M3.pf";    		
//    		}else if (problemName_.contains("mQAP")){    
//    			/**
//    			 * For true pareto fronts
//    			 */
//    			if(frontType.equalsIgnoreCase("true")){
//	    			trueFrontPath = trueFrontPath + problemName_.substring(4);
//	    			trueFrontPath = trueFrontPath + ".pf";
//    		    }else {
//    		    	trueFrontPath = experimentName_ + "/referenceFronts/" + problemName_+".rf";
////    		    	System.out.println(trueFrontPath);
//    		    }
//    		    	
//    			/**
//    			 * 
//    			 */
////    			System.out.println(trueFrontPath);
//    		}
//    		else {
//    			if (frontType.equalsIgnoreCase("true")) {
//    				trueFrontPath = trueFrontPath + problemName_ + ".pf";   
//    			} else {
//    				trueFrontPath = experimentName_ + "/referenceFronts/" + problemName_+".rf";
//    			}
//    		}  
//    		
    	String trueFrontPath;
    	
    	if (frontType.equalsIgnoreCase("true")) {
			trueFrontPath = baseFrontPath + trueFront_;   
		} else {
			trueFrontPath = experimentName_ + "/referenceFronts/" + problemName_+".M" + objectives_ +".rf";
		}
    		
    	if(problemName_.contains("mQAP"))	{
	    	String newproblemName_ = problemName_.replace("-", "_");
	    	storePath_ = experimentName_ + "/figures/"+ controlAlg_ + "/"+ newproblemName_ +"_FrontFig_all.m";
    	}else {
    		storePath_ = experimentName_ + "/figures/"+ controlAlg_ + "/";
    	}
    	
    	File experimentDirectory = new File(storePath_);
    	if (experimentDirectory.exists()) {
			System.out.println("Experiment directory exists");
			if (experimentDirectory.isDirectory()) {
				System.out.println("Experiment directory is a directory");
			} else {
				System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
			}
			experimentDirectory.delete();
			new File(storePath_).mkdirs();
		} // if
		else {
			System.out.println("Experiment directory does NOT exist. Creating");
			new File(storePath_).mkdirs();
		} // else
    
    	
    	storePath_ = storePath_ + problemName_+ "_FrontFig_all45.m";
    	Experiment  exp = new CMOEAStudy();
    	
    	exp.resetFile(storePath_);
    	
    	MultiPlot plotUtil = new MultiPlot(trueFrontPath,frontPath_,
    			storePath_ ,problemName_,algName_,property_,objectives_);
		
    	plotUtil.plotFront();	
		 
		System.out.println("File has been written!");		
//        System.out.println(storePath_);
    	
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

		public String getProblemName_() {
			return problemName_;
		}

		public void setProblemName_(String problemName_) {
			this.problemName_ = problemName_;
		}

		public String[] getFrontPath_() {
			return frontPath_;
		}

		public void setFrontPath_(String[] frontPath_) {
			this.frontPath_ = frontPath_;
		}

		public String getStorePath_() {
			return storePath_;
		}

		public void setStorePath_(String storePath_) {
			this.storePath_ = storePath_;
		}
		
		public int getNumberofRuns_() {
			return numberofRuns_;
		}

		public void setNumberofRuns_(int numberofRuns_) {
			this.numberofRuns_ = numberofRuns_;
		}

		public String getIndicatorName_() {
			return indicatorName_;
		}

		public void setIndicatorName_(String indicatorName_) {
			this.indicatorName_ = indicatorName_;
		}
		
		public String getFrontType() {
			return frontType;
		}

		public void setFrontType(String frontType) {
			this.frontType = frontType;
		}

	/**
	 * @param args
	 * @throws JMException 
	 * @throws ClassNotFoundException 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws JMException, ClassNotFoundException {
		String [] algNames = new String[]{	
				"CTsMOEAR1Release", 
		};
		
		int objectives = 2;		
		
		String [] problemList_ = new String[]{
	    		"CMOP1", 
	    		"CMOP2",  
	    		"CMOP3", 
	    		"CMOP4", 
	    		"CMOP5",  
	    		"CMOP6", 
	    		"CMOP7", 
	    		"CMOP8",  
	    		"CMOP9", 
	    		"CMOP10", 
		};
		
		int numberOfDif_level = 20;
		    
	    	
	    String [] paretoFrontFile_ = new String[]{
	    		"PF1.dat", 
	    		"PF2.dat", 
	    		"PF3.dat", 
	      		"PF4.dat", 
	    		"PF5.dat", 
	    		"PF6.dat", 
	    		"PF7.dat",
	     		"PF8.dat",
	    		"PF9.dat",
	    		"PF10.dat",
		};
	   
		    	
		
		String [] property =new String[]{
//				"ro","bs","gd","c^","k>","mv"
				"ro","bs","md","b<","k>","mv"
		};
		/*
		 * The following should be tuned manually
		 */

		String experimentName = "./jmetalExperiment/CEC2020Study";

		int numberofRuns = 20;
		String indicatorName = "IGD";
		String controlAlg = algNames[0];//The studied algorithm.
		
		//Used in mQAP 
		String frontType = "true"; //For problems whose PF are known
//		String frontType = "approimate";// For problems whose PF are unknown
		
		
		for(int j=0;j<problemList_.length;j++){
			String problemName = problemList_[j];
			PlotFronts3 plotAll = new PlotFronts3(experimentName,problemName,numberofRuns,controlAlg,indicatorName,
					                    algNames,property,paretoFrontFile_[j],objectives);	
			plotAll.setFrontType(frontType);
			plotAll.execute();
			
		}			

	}
	
}
