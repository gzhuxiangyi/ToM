package jmetal.CHTs.ToM;

import jmetal.myutils.datacollection.CollectionDataForTest;


public class CollectionDataForTestMainMW {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws Exception {
		
		int objectives = 2;
		int       runs = 50;		
		
		String shortExperimentName = "CMOEAStudyMW"; // The folder name
		
		String expPath = "./jmetalExperiment/" + shortExperimentName + "/M=" + objectives;
		
		String [] problems= new String[]{
				// m=2
				"MW1", 	"MW2", 	"MW3",  "MW5", "MW6", "MW7", "MW9",	"MW10",	"MW11",	"MW12", "MW13",
	    		
				// m = 3 or higher
//	    		"MW4",  "MW8",	 "MW14",				
				};
		
		String [] algorithms = new String[]{
				
	    		"MOEADToMRelease",
//	    		"MOEADToMWithoutAlpha", // use no alpha
	    		"MOEADToMUniformConstruct", // Construct population without considering types
//	    		"MOEADToMOnlyStage1",
//	    		"MOEADToMOnlyStage2",
	    		
	    		
//				"MOEADToM",  
//	    		"MOEADCDP",
//	    		"MOEADSP",
//	    		"MOEADSR",
//	    		"MOEADEpsilon",
	    		
//	    		"NSGAIIToMR1",   		
//	    		"NSGAIICDP",
//	    		"NSGAIISP",
//	    		"NSGAIISR",
//	    		"NSGAIIEpsilon",
				
				// Comprehensive comparisons
//	    		"MOEADToMLargeEVs",  // m=2, 3 Write into paper.	
//	    		"NSGAIIToMLargeEVs",  // m=2,3 Write into paper.	
	    		
//	    		"MOEADToM", // m=5,10 Write into paper.	
//	    		"NSGAIIToMR1",  // m=5,10 Write into paper.	
//	    		"PPSMOEAD",
//	    		"CNSGAIII",    		
//	    		"C-MOEAD", 
//	    		"CMOEADD",    		
//	    		"CTAEA",  
	    		
	    		
//				"CTsMOEAR1Release", 
//				"CTsMOEAR1ReleaseF1", // CTsMOEADR1Release.java with updateArchive, with F1 only
//	    		"CTsMOEAR1ReleaseF2", // CTsMOEADR1Release.java with updateArchive, with F2 only
//				"CTAEA",
//	    		"PPSMOEAD",	    	    
//	    		"CNSGAIII",   
//				"MOEADCDP",
//	    		"NSGAIICDP", 	
//	    		"CMOEADD",

		};
		
		String [] indicators = new String [] {
//				"IGD+",	
//				"GSPREAD",	
//				"HypE Hypervolume",
				"HV",
//				"HV2",
//				"DCI",
//				"GD",
//				"runtime",
//				"IGD+",
//				"PD",
		};
		
		
		(new CollectionDataForTest(objectives,runs, expPath, problems,
				algorithms,indicators)).execute();
	}
	
}
