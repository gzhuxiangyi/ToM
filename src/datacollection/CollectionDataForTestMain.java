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
//	    		"F1","F2", "F9","F10", // �ĸ��������⣬n=m
				
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
//	    		"MaPSOpaper", // ���DTLZ,WFG ���õ��㷨������	 
//	    		"MaPSOR3EA", //����EA�����Ӵ�
//	    		"MaPSOR3MAF", //����PSO�����Ӵ�,���ǲ���MAF��Ŀ��ռ�ά��������   
				
//				"MaPSOR2", // ��⸴��PF������    
//	    		"NSGAIII", "VaEA", 
//	    		"MOEAD", 
//	    		"dMOPSO",
//	    		"MaPSOR3Ideal",// �Ȳ���ideal �ڲ���nadir��
//	    		"MaPSOR3Nadir",// ֻ������׵�
				
//	    		"MaPSOR2", // ��⸴��PF������ 
//	    		"MaPSOR3Ideal",// ֻ���������
//	    		"MaPSOR3Nadir",// ֻ������׵�
//	    		"MaPSOR3RandRP",// ����������ѡ��RP���������ѡ��    
	    		
				//-------------R3------------------
				
				//-------------R2------------------
//	    		"MaPSOR2", // theta_ = 0.5 theta_���㷨����Ӱ�첻�Ǻܴ����������ѡ��İ汾   	
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
//	    		"PAEAIIRevised", // Modified version of PAEAII�������˼нǾ���ķ���,��ʱ����Ч���ã���Ϊһ�ڽ��бȽ�
//	    		"PAEARevisedWZF", // ���WZF��������Ӧ�㷨PAEA4ZWF
//	    		/**
//	    		 * ��Ⱥ��ʼ�������Ƚ�
//	    		 */
//	    		"PAEARevisedInitDominanceAngle", // ��ʼ���׶Σ�����֧���ϵ�ͼн�ѡ�����
//	    		"PAEARevisedInitAngle",// ��ʼ���׶Σ��������н�����ѡ�����
//	    		// �޳������ıȽ�
//	    		"PAEARevisedMaxAngleFirst",
//	    		"MOEADD",
//				"NSGAIII", 
//				"MOEAD",
//				"1by1EA",
//	    		"GWASFGA",
	    		
//	    		"MOEADAWA", // Added algorithm after the second review��TC
//	    		"PAEARevisedII", 	    
//	    		"MOEADTPN", // Added algorithm after the second review��TC
	    		
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
