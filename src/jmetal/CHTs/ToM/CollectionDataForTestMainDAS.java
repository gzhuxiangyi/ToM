package jmetal.CHTs.ToM;

import jmetal.myutils.datacollection.CollectionDataForTest;


public class CollectionDataForTestMainDAS {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws Exception {
		
		int objectives = 2;
		int       runs = 50;		
	    
		String shortExperimentName = "CMOEAStudyDAS"; // The folder name
		
		String expPath = "./jmetalExperiment/" + shortExperimentName + "/M=" + objectives;
		
		String [] problemsStrings= new String[]{
				// M = 2
	    		"DASCMOP1", 
	    		"DASCMOP2", 
	    		"DASCMOP3",
	    		"DASCMOP4", 
	    		"DASCMOP5",
	    		"DASCMOP6",
//				// M = 3 
//	    		"DASCMOP7", "DASCMOP8", "DASCMOP9"
	    		
	    		// many-objective
//	    		"DASCMaOP1", "DASCMaOP2","DASCMaOP3",
//	    		 "DASCMaOP4",//
//	    		 "DASCMaOP5", "DASCMaOP6", 
//	    		"DASCMaOP7", "DASCMaOP8",
//	    		 "DASCMaOP9", 			
				};
		
		
		String [] algorithms = new String[]{
				
	    		"MOEADToMRelease",
//	    		"MOEADToMWithoutAlpha", // use no alpha
	    		"MOEADToMUniformConstruct", // Construct population without considering types
//	    		"MOEADToMOnlyStage1",
//	    		"MOEADToMOnlyStage2",
	    		
	    		
//	    		"MOEADToMFinalAlpha10",//    		write into paper,
//	    		"MOEADCDP",
//	    		"MOEADSP",
//	    		"MOEADSR",
//	    		"MOEADEpsilon",
				
//	    		"NSGAIIToMFinalAlpha10", 		//    		write into paper,	    		
//	    		"NSAIIToMR1",  //    		write into paper,	  before
//	    		"NSAIICDP",
//	    		"NSAIISP",
//	    		"NSAIISR",
//	    		"NSAIIEpsilon",
	    		
//	         	"CTaMOEADSBXR1", 
//	    		"CTaMOEADSBXStage1R1",
//	    		"CTaMOEADSBXStage2R1",
	    		
//		     	"CTaMOEADSBXR1", 
//		    	"PPSMOEADSBX",
//		    	"CNSGAIIISBX",    
//	    		"MOEADCDPSBX",
//		    	"NSGAIICDPSBX",		
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
		
		int numberOfDif_level = 3;
		    
		String [] problems     = new String[problemsStrings.length * numberOfDif_level];  	
	    	    
	    for (int i = 0; i < problemsStrings.length; i++) {
	        for (int k = 0; k < numberOfDif_level; k++) {
	        	problems[i * numberOfDif_level + k] = problemsStrings[i] + "_" + (k+5);	        
	        } // for k        
	        
	    } // for i
		    
		(new CollectionDataForTest(objectives,runs, expPath, problems,
				algorithms,indicators)).execute();
	}
	
}
