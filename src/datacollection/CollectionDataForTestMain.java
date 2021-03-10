package jmetal.myutils.datacollection;


public class CollectionDataForTestMain {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MatlabConnectionException 
	 * @throws MatlabInvocationException 
	 */
	public static void main(String[] args) throws Exception {
		int objectives = 3;
		int runs = 20;
//		String expPath = "./jmetalExperiment/MaPSOStudy/M=" + objectives;
		
		String expPath = "./jmetalExperiment/NewTestProblemsStudy/D=13FEs=100000/M=" + objectives;
		
		String [] problems= new String[]{
				/**
				 * New test problems 
				 */
//	    		"F1","F2", "F9","F10", // 四个测试问题，n=m
				
	    		"F1","F2","F3","F4",// Griewank + without Penalty
	    		"F5","F6","F7","F8", // Rosenbrock + without Penalty
	    		"F9","F10","F11","F12", // Griewank + with Penalty
	    		"F13","F14","F15","F16", // Rosenbrock + with Penalty
	    		
//				"ZHX11","ZHX12",  "ZHX13", 	"ZHX14", 
//				"ZHX21", "ZHX22", "ZHX23", "ZHX24", 
//				"ZHX31", "ZHX32", "ZHX33", "ZHX34", 
//	    		
//	    		"ZHXII11", "ZHXII12", "ZHXII13", "ZHXII14", 
//				"ZHXII21", "ZHXII22", "ZHXII23", "ZHXII24", 
//				"ZHXII31", "ZHXII32", "ZHXII33", "ZHXII34",
				
	    		/**
	    		 * 2 objectives
	    		 */
//	    		"LDTLZ1", 
//	    		"LDTLZ2",	"LDTLZ3",    		
//	    		"JYF1",
//	    		"JYF2",
//	    		"JYF3",
//	    		"UF4",
	    		
	    		/**
	    		 * 3 objectives
	    		 */
//				"CrashWorthinessDesign",// M=3
//	    		"LDTLZ4", 
//	    		"LDTLZ5",
//	    		"JYF4",
//	    		"JYmF4",
//	    		"JYF5",
//	    		"JYF6",
//	    		"DTLZ5","DTLZ6","DTLZ7",
//	    		"MinusWFG1","MinusWFG2","MinusWFG3",
	    		/**
	    		 * Many-objective
	    		 */
//				"CarCabDesign",// M = 9
//				"DTLZ1", "DTLZ3",  "ScaledDTLZ1", "WFG1","WFG2","WFG3", // selected 
//				"DTLZ1",   
//				"DTLZ2",   
//				"DTLZ3",  
//				"DTLZ4",  
//				"DTLZ5",
//				"DTLZ6",
//				"DTLZ7",			
//				"ConvexDTLZ2",
////				"ConvexDTLZ3",
////				"ConvexDTLZ4",	
//				"ScaledDTLZ1",	
//				"ScaledDTLZ2",
				
//				"MinusDTLZ1",
//				"MinusDTLZ2",
//				"MinusDTLZ3",  
//				"MinusDTLZ4",
//				
//				"WFG1",	"WFG2","WFG3",
//				"WFG4","WFG5",
//				"WFG6",	"WFG7","WFG8","WFG9",	
//	    		
//	    		"MinusWFG1","MinusWFG2","MinusWFG3","MinusWFG4","MinusWFG5",
//	    		"MinusWFG6","MinusWFG7","MinusWFG8", "MinusWFG9",
//	    		"WZF1","WZF2","WZF3",
//	    		"WZF4","WZF5","WZF6","WZF7",// 2 objectives
//				"WZF8","WZF9",
				};
		
		String [] algorithms = new String[]{
				
				/**
				 * Used in NewTestProblemsStudy
				 */
				
	    		"MaEABest",
	    		"ThetaDEA", 
	    		"NSGAIII",  	    		
	    		"MOEAD",
	    		"NSGAII",    
	    		"SPEA2", 
	    		
				/**
				 * Used in AdMOEA study 
				 */
//				"AdMOEAMAF",//   
//
////	    		"AdMOEAreleaseNad",//  use the real nad as reference point
////	    		"AdMOEArelease",//     		
//				"NSGAIII", 
//	    		"VaEA",
//				"MOEAD",
//				"ThetaDEA",
//				"1by1EA",
//	    		"GWASFGA", 
//	    		"MOEADIPBI"
				/**
				 * Used in MaPSO study 
				 */		
				//-------------R3------------------
//	    		"MaPSOpaper", // 求解DTLZ,WFG 采用的算法的数据	 
//	    		"MaPSOR3EA", //采用EA产生子代
//	    		"MaPSOR3MAF", //采用PSO产生子代,但是采用MAF在目标空间维护多样性   
				
//				"MaPSOR2", // 求解复杂PF的问题    
//	    		"NSGAIII", "VaEA", 
//	    		"MOEAD", 
//	    		"dMOPSO",
//	    		"MaPSOR3Ideal",// 先采用ideal 在采用nadir，
//	    		"MaPSOR3Nadir",// 只采用天底点
				
//	    		"MaPSOR2", // 求解复杂PF的问题 
//	    		"MaPSOR3Ideal",// 只采用理想点
//	    		"MaPSOR3Nadir",// 只采用天底点
//	    		"MaPSOR3RandRP",// 不采用曲率选择RP，而是随机选择    
	    		
				//-------------R3------------------
				
				//-------------R2------------------
//	    		"MaPSOR2", // theta_ = 0.5 theta_对算法性能影响不是很大，这个是最终选择的版本   	
//	    		"NSGAIII", "VaEA", 
//	    		"MOEAD", 
//	    		"dMOPSO",
//	    		"MOEADTPN",
//	    		"MOEADAWA",
	    		//-------------R2------------------
				
				//-------------revised 1------------------
//	    		"MaPSOpaper", // write into pater 
//	    		"MaPSORevisedRand", // randomly select leaders from K ones for each particle 
//	    		"MaPSORevisedDE", // Use DE to generate leaders for each particle 
//	    		"MaPSORevisedarchive", // Use archive to select leaders for each particle 
	    		//-------------revised 1------------------
	    		
//				"MaPSOpaper", // write into pater 
//	    		"NSGAIII", 
//	    		"VaEABest", 
//	    		"KnEA",	
//	    		"MOEAD",
//	    		"dMOPSO",
//	    		"hmopso",
				
				/**
				 * Used in PAEA study 
				 */				
//				"PAEARevised", 	    		
//	    		"PAEAIIRevised", // Modified version of PAEAII，采用了夹角聚类的方法,费时但是效果好，作为一节进行比较
//	    		"PAEARevisedWZF", // 求解WZF函数，相应算法PAEA4ZWF
//	    		/**
//	    		 * 种群初始化方法比较
//	    		 */
//	    		"PAEARevisedInitDominanceAngle", // 初始化阶段，根据支配关系和夹角选择个体
//	    		"PAEARevisedInitAngle",// 初始化阶段，根据最大夹角优先选择个体
//	    		// 剔除方法的比较
//	    		"PAEARevisedMaxAngleFirst",
//	    		"MOEADD",
//				"NSGAIII", 
//				"MOEAD",
//				"1by1EA",
//	    		"GWASFGA",
	    		
//	    		"MOEADAWA", // Added algorithm after the second review，TC
//	    		"PAEARevisedII", 	    
//	    		"MOEADTPN", // Added algorithm after the second review，TC
	    		
//	    		"PAEARevisedV1",
//	    		"PAEARevisedV2",
//	    		"VaEABest"
//	    		"PAEAbestv1v2",
//	    		"PAEANoV1V2",
//	    		"PAEAV1",
//	    		"PAEAV2",
//	    		"PAEARevisedV1new",
//	    		"PAEARevisedV2new",
				/**
				 * Used in VaEA study
				 */
//				"VAMaEACompare",	
//				"GrEA",				
//				"MOEADD",
//				"NSGAIII",
//	    		"SPEA2+SDE"
//				"CVaEAalpha0.01",//That is CVaEA	    		
//	      		"CNSGAIII", 
//	    		"CMOEADD",   
				
//	    		"MOABCDArchive", 
//	    		"MOABCDnew", 
//	    		"MOEADDRAnew",
//	    		"GrEAnew",
////	    		"GrEAnewDoublePopSize",
//	    		"eMOABCnew",
//	    		"NSGAIIInew","MOEADnew",// add new means they are the newest ones   
	    		
//	    		"MOABCDArchive", 	    		
//	    		"MOABCDWithoutOnlooker",
//	    		"MOABCDWithoutScout",
//	    		"MOABCDWithoutOnlookerScout",    
//				"VaEABest",
//	    		"MOEADD",
//	    		"NSGAIII", 
//	    		"MOEAD",
//	    		"MOMBI2", 
		};
		
		String [] indicators = new String [] {
//				"IGD+",	
//				"GSPREAD",	
//				"HypE Hypervolume",
				"HV",
//				"DCI",
//				"GD",
//				"runtime",
				"IGD",
//				"PD",
		};
		
		
		(new CollectionDataForTest(objectives,runs, expPath, problems,
				algorithms,indicators)).execute();
	}
	
}
