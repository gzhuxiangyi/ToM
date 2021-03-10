/**
 * This class plots given algorithms' Pareto fronts for comparing , often a control algorithm is needed,or a new algorithm
 * Created by Yi Xiang 2013/12/7
 */
package jmetal.myutils;

import java.io.File;

import jmetal.experiments.Experiment;
import jmetal.experiments.studies.StandardStudy;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class PlotFronts {
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
	public PlotFronts(String experimentName,String problemName, int numberofRuns,
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
		String baseFrontPath = "./paretoFronts/DTLZ&WFG/"; // "./paretoFronts/"; //
//		String baseFrontPath = "./paretoFronts/Constraint/"; // For constrained problems
//		String baseFrontPath = "./paretoFronts/"; 
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
    	Experiment  exp = new StandardStudy();
    	
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
				/**
				 * Used in OONStudy
				 */
//				"AdMOEA",
//				"AdMOEAnew",
//				"AdMOEASBX",
//				"AdMOEADE",
				/**
				 * Used in MaPSOStudy
				 */
	    		// after first-round of review
//	    		"MaPSOpaper", // write into pater 
//	    		"MaPSORevisedRand", // randomly select leaders from K ones for each particle 
//	    		"MaPSORevisedDE", // Use DE to generate leaders for each particle 
//	    		"MaPSORevisedarchive", // Use archive to select leaders for each particle 
	    		//-------------------------------------------------
	    		
//				"MaPSOpaper", // write into pater 
//	    		"NSGAIII", 
//	    		"VaEABest", 
//	    		"KnEA",	
//	    		"MOEAD",
//	    		"dMOPSO",
//	    		"hmopso",
//	    		"NSGAIIDE"
//				"MaPSOnew",
//				"MaPSOtest",
//				"VaEAbest",
//				"PAEAFinal",
				
				//---------------------------------------------------------------------
				// Used in PAEAStudy 
//				"PAEARevisedII"
//				"PAEAIIRevised",
//				"PAEARevised",//"GWASFGA",
//				"PAEAII",
//				"PAEARevisedWZF",//"PAEARevisedInitAngle",
//				"PAEAIIRevised2",
//				"PAEAbest",
//	    		"PAEARevisedMaxAngleFirst",
//	    		"MOEADD",
//				"NSGAIII", 
//				"MOEAD",    		
//				"1by1EA",    		
//	    		"GWASFGA",
//				"MOEADAWA",
//				"MOEADTPN",
				//---------------------------------------------------------------------
				
//	    		"NSGAIIDE",
//	    		"NSGAIII",

//	    		"MOEADDE",
//	    		"GDE3",  
//				"SMPSO",
//				"PAES",
//				"IBEA",
//				"MOCell",
//	    		"OMOPSO",
//				"theta0.0",   
//				"GWASFGA",
//				"GrEA"
//				"MOMBI2rw"
//	    		"MOABCDnew", 	
//	    		"MOEADDRAnew",
//	    		"GrEAnew",
//	    		"GrEAnewDoublePopSize",
//	    		"eMOABCnew",
//	    		"NSGAIIInew","MOEADnew",// add new means they are the newest ones  

//	    		"MOABCDArchive", 
//	    		"GrEAnewDoublePopSize",
//				"FMOEA",
//				"MaMOEAD",
//				"LSMOEA",
//				"VAMaEACompare",
//				"MOEADD",
//				"NSGAIII",
//				"MOEADDrw",
//				"NSGAIIIrw",
////				"CNSGAIII",
//				"CVaEAalpha0.01",
//				"CVaEAalpha0.02",
//				"CVaEAalpha0.03",
//				"CMOEADD"
//				"CVaEAInfeasible0.01LDon","CVaEAInfeasible0.01"
//				"CVaEAAR", 
//				"CVaEAAR1",
//				"CMOABCD",
//				"MOABCD(R=5limit=250D=3)",		
//				"MOEADTCH(D=3)",
//				"MOEADDRA(D=3)",
//				"MOEADPBI(D=3)",
//				"MOEADDE",
//				"MOABCLS","dMOABC"
//				"DMCMOABC",
//				"DMOABC",
//				"MOEAD",
//				"FMOEA",
//				"NSGAII",
//				"SPEA2",
//				"MOABCLSlimit=200Rounds=100cosize=40",
//				"dMOABC",	
//				"MOEADDRA"
//				"cMOABbestElite",
//	    		"cMOABinfElite",
//	    		"cMOABC",
//	    		"cMOABCnoElite",
//	    		"cMOABCrandElite"
//				"MCMOABC",
//				"NSGAII","PAES","SPEA2"
//				"SMPSO","GDE3","OMOPSO","NSGAII","SPEA2"
		};
		
		String [] problemNames = new String[]{
//				"CF8",//"CF9","CF10"
//				"CarSideImpactNew",
//				"Binh2","ConstrEx","Golinski","Osyczka2","Tanaka","Srinivas",
//				"C1DTLZ1",
//				"C1DTLZ3",
//				"C2DTLZ2",
//				"C2DTLZ2Convex",
//				"C3DTLZ1",
//		        "C3DTLZ4",		        
//		        "C1SDTLZ1", 
//	           "C1SDTLZ3", 
//	           "C2SDTLZ2", 
//	           "C2SDTLZ2Convex",
//	           "C3SDTLZ1",
//	           "C3SDTLZ4",
//				"DTLZ2Convex",
//				"MOTSPcp=0.4"
//				"UF1",	"UF2","UF3","UF4",
//				"UF5",
//				"UF6",
//				"UF7",
//				"UF8","UF9","UF10",
//				"CF1","CF2","CF3","CF4","CF5","CF6","CF7",
//				"CF8","CF9","CF10",
//				"ZDT1","ZDT2","ZDT3",
//				"ZDT4","ZDT6",
//				"LZ09_F1","LZ09_F2",//"LZ09_F3","LZ09_F4","LZ09_F5","LZ09_F7","LZ09_F8","LZ09_F9",
//				"UF1","UF2",//"UF3","UF4","UF5","UF6","UF7",
//				"CrashWorthinessDesign",
				"WFG1",
				"WFG2",
				"WFG3",
//				"WFG4","WFG5",
//				"WFG6",
//				"WFG7",
//				"WFG8",
//				"WFG9",
				"DTLZ1",
//				"DTLZ2",
				"DTLZ3",
//				"DTLZ4",
//				"DTLZ5","DTLZ6","DTLZ7"	,	
//				"ConvexDTLZ2",
//				"ConvexDTLZ3","ConvexDTLZ4",
//				"ScaledDTLZ1",
//				"ScaledDTLZ2",   
//				"UF8","UF9","UF10","LZ09_F6"
//				"MatrixMultO4",
//				"MinusDTLZ1",	"MinusDTLZ2",
//				"MinusDTLZ3",   "MinusDTLZ4",
//				
//				"MinusWFG1","MinusWFG2","MinusWFG3",
//	    		"MinusWFG4","MinusWFG5","MinusWFG6","MinusWFG7","MinusWFG8", "MinusWFG9",
//				"WZF1", "WZF2","WZF3","WZF4","WZF5","WZF6","WZF7",// 2 objectives
//				"WZF8", "WZF9",
		};
		
		String [] paretoFrontFile = new String[]{
//				"Binh2.M2.pf","ConstrEx.M2.pf","Golinski.M2.pf","Osyczka2.M2.pf","Tanaka.M2.pf","Srinivas.M2.pf",
//				"CF1.M2.pf","CF2.M2.pf",
//	    		"CF3.M2.pf",
//	    		"CF4.M2.pf","CF5.M2.pf", "CF6.M2.pf","CF7.M2.pf",
//				"CF8.M3.pf","CF9.M3.pf","CF10.M3.pf",
//				"CarSideImpactNew.rf",
//				"CrashWorthinessDesign.rf",
//				"WZF1.rf","WZF2.rf","WZF3.rf","WZF4.rf","WZF5.rf","WZF6.rf","WZF7.rf",// 2 objectives
//				"WZF8.rf","WZF9.rf",
//				"C1DTLZ1.M3.pf",
//	            "C1DTLZ234.M3.pf",
//	            "C2DTLZ2.M3.pf",
//	            "C2DTLZ2Convex.M3.pf",
//	            "C3DTLZ1.M3.pf",
//	            "C3DTLZ4.M3.pf",
//	            
//			    "C1SDTLZ1.M3.pf",
//	            "C1SDTLZ234.M3.pf",
//	            "C2SDTLZ2.M3.pf",
//	            "C2SDTLZ2Convex.M3.pf",
//	            "C3SDTLZ1.M3.pf",
//	            "C3SDTLZ4.M3.pf",
				
//				"C1SDTLZ1.M10.pf",
//	            "C1SDTLZ234.M10.pf",
//	            "C2SDTLZ2.M10.pf",
//	            "C2SDTLZ2Convex.M10.pf",
//	            "C3SDTLZ1.M10.pf",
//	            "C3SDTLZ4.M10.pf"
				
//				"MOTSPcp=0.4.rf",
//				"MatrixMultO4.rf",
	    	
//	    		"WFG1.M3.rf",
//	    		"WFG2.M3.rf",
//				"WFG3.M3.rf",
				
//				"WFG1.3D.pf",
//	    		"WFG2.3D.pf",
//				"WFG3.3D.pf",
				
//				"CrashWorthinessDesign.rf",
				"WFG1.M3.pf",
	    		"WFG2.M3.pf",
				"WFG3.M3.pf",				
//	    		"WFG4To9.M3.pf",
//	    		"WFG4To9.M3.pf",
//	    		"WFG4To9.M3.pf",
//	    		"WFG4To9.M3.pf","WFG4To9.M3.pf",
//	    		"WFG4To9.M3.pf",
				"DTLZ1.M3.pf",
	    		"DTLZ234.M3.pf", 
//	    		"DTLZ234.M3.pf",  "DTLZ234.M3.pf", 
//	    		"DTLZ5.3D.pf",  "DTLZ6.3D.pf","DTLZ7.3D.pf",  
//	    		"ConvexDTLZ234.M3.pf",
//	    		"ConvexDTLZ234.M3.pf","ConvexDTLZ234.M3.pf",
//	    		"ScaledDTLZ1.M3.pf",
//	    		"ScaledDTLZ234.M3.pf", 	
//				"UF8.pf","UF9.pf","UF10.pf","LZ09F6.pf",
//	    		
//	    		"ZDT1.pf", "ZDT2.pf","ZDT3.pf",
//				"ZDT4.pf","ZDT6.pf",//	    		
//	    		"LZ09F1.pf","LZ09F2.pf",//"LZ09F3.pf","LZ09F4.pf","LZ09F5.pf","LZ09F7.pf","LZ09F8.pf","LZ09F9.pf",
//				"UF1.pf","UF2.pf",//"UF3.pf","UF4.pf","UF5.pf","UF6.pf","UF7.pf",
//	            "WFG1.3D.pf","WFG2.3D.pf","WFG3.3D.pf","WFG4.3D.pf","WFG5.3D.pf","WFG6.3D.pf", "WFG7.3D.pf","WFG8.3D.pf","WFG9.3D.pf",//    		
//	            "DTLZ1.3D.pf", "DTLZ2.3D.pf","DTLZ3.3D.pf", "DTLZ4.3D.pf",
//	            "DTLZ5.3D.pf",  "DTLZ6.3D.pf","DTLZ7.3D.pf",   
//				"MinusDTLZ1" +".M3" +".rf",
//	    		"MinusDTLZ2" +".M3" +".rf",
//	    		"MinusDTLZ3" +".M3" +".rf",
//	    		"MinusDTLZ4" +".M3" +".rf",
//	    		
//	    		"MinusWFG1" +".M3" +".rf",
//	    		"MinusWFG2" +".M3" + ".rf",
//	    		"MinusWFG3" +".M3" + ".rf",
//	    		"MinusWFG4" +".M3" + ".rf",
//	    		"MinusWFG5" +".M3" + ".rf",
//	    		"MinusWFG6" +".M3" + ".rf",
//	    		"MinusWFG7" +".M3" + ".rf",
//	    		"MinusWFG8" +".M3" + ".rf",
//	    		"MinusWFG9" +".M3" + ".rf",

		};
		
		String [] property =new String[]{
//				"ro","bs","gd","c^","k>","mv"
				"ro","b*","md","b<","k>","mv"
		};
		/*
		 * The following should be tuned manually
		 */
		int objectives = 3;		
		String experimentName = "./jmetalExperiment/MaPSOStudy/M="+ objectives;
//		String experimentName = "E:/eclipse/workspace/jMetal-master/jmetal-exec/jmetalExperiments/MOABCDStudyRevise/M=3";
		int numberofRuns = 2;
		String indicatorName = "IGD";
		String controlAlg = algNames[0];//The studied algorithm.
		
		//Used in mQAP 
		String frontType = "true"; //For problems whose PF are known
//		String frontType = "approimate";// For problems whose PF are unknown
		
		
		for(int j=0;j<problemNames.length;j++){
			String problemName = problemNames[j];
			PlotFronts plotAll = new PlotFronts(experimentName,problemName,numberofRuns,controlAlg,indicatorName,
					                    algNames,property,paretoFrontFile[j],objectives);	
			plotAll.setFrontType(frontType);
			plotAll.execute();
			
		}			

	}
	
}
