package jmetal.CHTs.ToM;

import jmetal.myutils.datacollection.CollectionDataForTest;


public class CollectionDataForTestMainCDTLZ {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws Exception {
		
		int objectives = 10;
		int       runs = 50;		
	    
		String shortExperimentName = "CMOEAStudyCDTLZ"; // The folder name
		
		String expPath = "./jmetalExperiment/" + shortExperimentName + "/M=" + objectives;
			
		
		String [] problems= new String[]{
//		           "C1DTLZ1", 
//		           "C1DTLZ3", 
//		           "C2DTLZ2", 
//		           "C3DTLZ1",
//		           "C3DTLZ4",
		           "DC1DTLZ1", 
		           "DC1DTLZ3",
		           "DC2DTLZ1", 
		           "DC2DTLZ3",
		           "DC3DTLZ1", 
		           "DC3DTLZ3",   			
				};
		
		String [] algorithms = new String[]{
				
	    		"MOEADToMRelease",
//	    		"MOEADToMWithoutAlpha", // use no alpha
	    		"MOEADToMUniformConstruct", // Construct population without considering types
//	    		"MOEADToMOnlyStage1",
//	    		"MOEADToMOnlyStage2",
	    		//------------------------------------------------------
	    		
//	    		"MOEADToM",
//	    		"MOEADCDP",
//	    		"MOEADSP",
//	    		"MOEADSR",
//	    		"MOEADEpsilon",
	    		
//	    		"NSGAIIToMR1",// Write into paper. when maintaining feasible solutions, add first feasible solutions in current population
////	    		"NSGAIIToM",
//	    		"NSGAIICDPNew",
//	    		"NSGAIISP",
//	    		"NSGAIISR",
//	    		"NSGAIIEpsilon",
	    		
	    		
	    		
	    		// Comprehensive comparisons
//	    		"MOEADToM", // write into paper
//	    		"NSGAIIToMR1",// Write into paper
//	    		"PPSMOEAD",
//	    		"CNSGAIII",    		
//	    		"C-MOEAD", 
//	    		"CMOEADD",    		
//	    		"CTAEA",    
	    		
//				"CTsMOEAR1Release", 
//	    		"CTsMOEAR1ReleaseF1", // CTsMOEADR1Release.java with updateArchive, with F1 only
//	    		"CTsMOEAR1ReleaseF2", // CTsMOEADR1Release.java with updateArchive, with F2 only
//	    		"CTsMOEAR1ReleaseCDP", // Consider always constraints, handled by CDP 
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
				"IGD+",
//				"PD",
		};
		
		
		(new CollectionDataForTest(objectives,runs, expPath, problems,
				algorithms,indicators)).execute();
	}
	
}
