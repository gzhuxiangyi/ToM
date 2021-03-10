/**
 * This class implements the calculation of HV as in Hype.
 * By Yi Xiang
 */
package jmetal.myutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class HypE_HV {
	private String experimentName_ ;
	private String[] algName_;
	private String[] problemNames_;
	private int numberofRuns_ ;
	private int objectives_;
	/**
	 * 
	 */
	public HypE_HV(String experimentName,String[] problemNames, int numberofRuns,
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
				 * Used in NewTestProblemsStudy
				 */
//	    		"MaEABest",
//	    		"ThetaDEA", 
//	    		"NSGAIII",  
//	    		"MOEAD",
//	    		"NSGAII",   
	    		"SPEA2", 
				/**
				 * Used in MaPSO study
				 */		
//	    		"MaPSOR2", // 求解复杂PF的问题    	    		
//	    		"MaPSOR3RandRP",// 不采用曲率选择RP，而是随机选择    
//	    		"MaPSOR3Ideal",// 只采用理想点
//	    		"MaPSOR3Nadir",// 只采用天底点
	    		
//	    		"MaPSObest",  // write into pater 
//	    		"MaPSOpaper", // write into pater 
//	    		"NSGAIII", 
//	    		"VaEABest", 
//	    		"KnEA",	
//	    		"MOEAD",
//	    		"dMOPSO",
//	    		"hmopso",
//				"IBEA",
//				"NSGAIIDE"
//	    		"theta0.0",  
//	    		"theta0.1",    	
//	    		"theta0.2",
//	    		"theta0.3",
//	    		"theta0.4",
//	    		"theta0.5", 
//	    		"theta0.6",
//	    		"theta0.7",  
//	    		"theta0.8",  
//	    		"theta0.9",  
//	    		"theta1.0",  
				
//	    		"alpha0.0",  "alpha0.1",   
//	    		"alpha0.2",  "alpha0.3",
//	    		"alpha0.4",  "alpha0.5",    	
//	    		"alpha0.6",  "alpha0.7",    	
//	    		"alpha0.8",  "alpha0.9",    	
//	    		"alpha1.0",
	    		
//	    		"w0.1",
//	    		"w0.2",
//	    		"w0.3",
//	    		"w0.4",
//	    		"w0.5",    		
//	    		"w0.6",
//	    		"w0.7",
//	    		"w0.8",
//	    		"w0.9",
//	    		"w1.0"
				
//				"c0.5",
//	    		"c1.0",
//	    		"c1.5",
//	    		"c2.0",
//	    		"c2.5",    		
//	    		"c3.0",
//	    		"c3.5"
				/**
				 * Used in PAEAStudy
				 */
//    		    "MOEAD",
//				"MOEADrw"
//				"MOEADDrw",
//				"NSGAIIIrw",
//				"GrEA",
//				"MOMBI2",
//				"MOMBI2rw",
//				"PAEAbest",
//				"PAEANoV1V2"
//				"PAEA", "MOEADD","NSGAIII","MOEAD","GrEA",
//				"VaEABest",
				
//				"theta0.0",    	
//	    		"theta5.0",
//	    		"theta10.0",
//	    		"theta15.0",
//	    		"theta20.0",   	
//				"theta25.0",
//	    		"theta30.0",  
//	    		"theta35.0",  
	    		
//	    		"alpha0.0",    	
//	    		"alpha0.1",
//	    		"alpha0.3",
//	    		"alpha0.5",
//	    		"alpha0.7",
//	    		"alpha0.9",
//	    		"alpha1.0",
//				"alpha1.5",
//	    		"alpha2.0",
//	    		"alpha2.5",
//	    		"alpha3.0",
//				"alphaInf",
				
				/**
				 * Used in MOABCDStudyRevise study
				 */
//				"MOABCDnoDE",
//				"MOABCDArchive", 				
//	    		"MOABCDWithoutOnlooker",
//	    		"MOABCDWithoutScout",
//	    		"MOABCDWithoutOnlookerScout",    
//	    		"MOEADDRAnew",
//	    		"GrEAnew",
//	    		"GrEAnewDoublePopSize",
//	    		"eMOABCnew",
//	    		"NSGAIIInew","MOEADnew",// add new means they are the newest ones 
//	    		"GrEAnewDoublePopSize",
//				"MOABCD", "MOEADDRA","eMOABC","NSGAIII","MOEAD",
//				"GrEA"// For convergence plot
				/**
				 * Used in VaEAStudy study
				 */			
//				"PAEAbest",
//				"PAEAnormal",
//	    		"LSMOEAMinimumAngle",
//	    		"LSMOEA",
//				"VaEABest",
//				"MOEADD",
//	    		"NSGAIII", 
////	    		"MOEAD",
//	    		"GrEA",
//	    		"MOMBI2",   
				/**
				 * Used in MOABCDParameterStudyRevised
				 */
				
//				"CR=0.0","CR=0.1","CR=0.2","CR=0.3","CR=0.4","CR=0.5","CR=0.6","CR=0.7","CR=0.8","CR=0.9","CR=1.0",
//				"F=0.0","F=0.1","F=0.2","F=0.3","F=0.4","F=0.5","F=0.6","F=0.7","F=0.8","F=0.9","F=1.0",
		};
		
		String [] problemNames = new String[]{

//	    		"F1","F2", "F9","F10", // 四个测试问题，n=m
	    		
	    		"F1","F2","F3","F4",// Griewank + without Penalty
	    		"F5","F6","F7","F8", // Rosenbrock + without Penalty
	    		"F9","F10","F11","F12", // Griewank + with Penalty
	    		"F13","F14","F15","F16", // Rosenbrock + with Penalty
	    		
//				"LDTLZ4", 
//	    		"LDTLZ5",
//	    		"JYF4",
//	    		"JYmF4",
//	    		"JYF5",
//	    		"JYF6",
//	    		"DTLZ5","DTLZ6","DTLZ7",
//	    		"MinusWFG1","MinusWFG2","MinusWFG3",
	    		
//				"WFG1",
//				"WFG2","WFG3",
//				"WFG4",
//				"WFG5",
//				"WFG6",
//				"WFG7",
//				"WFG8",
//				"WFG9",
//				
//				"DTLZ1",
//				"DTLZ2",
//				"DTLZ3",
//				"DTLZ4",
//        
//				"ConvexDTLZ2",
////				"ConvexDTLZ3","ConvexDTLZ4",
//	    		"ScaledDTLZ1",
//	    		"ScaledDTLZ2"
				
//				"NormalWFG4", 
//				"NormalWFG5",
//				"NormalWFG6",
//				"NormalWFG7",
//				"NormalWFG8",
//				"NormalWFG9",
				
//				"DTLZ1",   
////				"DTLZ2",   
//				"DTLZ3",  
////				"DTLZ4",
//				"ConvexDTLZ2",//"ConvexDTLZ3","ConvexDTLZ4",
//				"ScaledDTLZ1",//"ScaledDTLZ2",   
//				
//				"WFG1",
//				"WFG2",
////				"WFG3",
//				"WFG4", 
////				"WFG5",
//				"WFG6",
////				"WFG7",
//				"WFG8",
//				"WFG9",
		};
				
		/*
		 * The following should be tuned manually
		 */
		int objectives = 10;		// 
//		String experimentName = "./jmetalExperiment/MaPSOStudy/M=" + objectives;
//		String experimentName = "E:/eclipse/workspace/jMetal-master/jmetal-exec/jmetalExperiments/MOABCDStudyRevise/M=" + objectives;
		String experimentName =  "./jmetalExperiment/NewTestProblemsStudy/D=20FEs=300000/M="+ objectives;
		
		int numberofRuns = 2;			// WFG 112, DTLZ1:56, DTLZ2:42;   DTLZ3:84; 
		
		/*
		 * End
		 */
		
		HypE_HV hvData = new HypE_HV(experimentName,problemNames,numberofRuns,
				                    algNames,objectives);				
		hvData.execute();			
					

	}
	
	/**
	 * Execute 
	 * @throws ClassNotFoundException
	 * @throws InterruptedException 
	 */
	
	 public void execute() throws ClassNotFoundException, InterruptedException{
		

    	for (int k = 0; k < algName_.length;k++){ // for each algorithm
    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
    			
    			String writePath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/allbest.txt";    		
    			FileUtils.resetFile(writePath);
    			
    			for (int j = 0; j < numberofRuns_;j++) { // for each run
    				String readPath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/FUN."+ j;
//    				System.out.println(readPath);
    				double [][] data = FileUtils.readMatrix(readPath);    				
    				    
    		
    	    		if (problemNames_[i].length() >= 4 &&problemNames_[i].substring(0,3).equalsIgnoreCase("WFG")){ 
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
//    	    		} else if (problemNames_[i].substring(0,9).equalsIgnoreCase("NormalWFG")){ 
//    	    			//System.out.println("NormalWFG");
//    	    		} // if 
    	    		}
    	    		
    	    		
    				FileUtils.writeMatrix(writePath, data);
    				
    			} // For 
    			
    		} // for i
    	}// for k   
    	
    	/**
    	 * Copy files 
    	 */
    	File OrigianlF1 = new File ("E:\\SourceCodes\\MonteCarlo HV\\HypE_Hypervolume.exe");
    	
    	File OrigianlF2 = new File ("E:\\SourceCodes\\MonteCarlo HV\\msvcp100.dll");
    	
    	File OrigianlF3 = new File ("E:\\SourceCodes\\MonteCarlo HV\\msvcr100.dll");
    	
    	File OrigianlF4 = new File ("E:\\SourceCodes\\MonteCarlo HV\\ReferencePoint.txt");
    	
    	File OrigianlF5 = new File ("E:\\SourceCodes\\MonteCarlo HV\\Samples.txt");
    	
    	for (int k = 0; k < algName_.length;k++){ // for each algorithm
    		
    		for (int i = 0;i < problemNames_.length;i++) { // for each problem
    			String path = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i];
    			
    			File copyF1 = new File (path + "/HypE_Hypervolume.exe");
    	    	
    	    	File copyF2 = new File (path + "/msvcp100.dll");
    	    	
    	    	File copyF3 = new File (path + "/msvcr100.dll");
    	    	
    	    	File copyF4 = new File (path + "/ReferencePoint.txt");
    	    	
    	    	File copyF5 = new File (path + "/Samples.txt");
    	    	
    	    	this.fileChannelCopy(OrigianlF1, copyF1);
    	    	this.fileChannelCopy(OrigianlF2, copyF2);
    	    	this.fileChannelCopy(OrigianlF3, copyF3);
    	    	this.fileChannelCopy(OrigianlF4, copyF4);
    	    	this.fileChannelCopy(OrigianlF5, copyF5);
    	    	System.out.println("copy file to " + path);  
    	    	
    	       	String readPath =  experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i] + "/HypE Hypervolume.txt";
    	       	FileUtils.resetFile(readPath);
    	       	
    	    	String exePath = path + "/HypE_Hypervolume.exe"; 
    	    	try {
	    	    	Runtime runtime = Runtime.getRuntime();
	    	    	Process process = runtime.exec(exePath);
	    	    	process.waitFor();
	    	    	
	    	    	// Rename    
	    	   
	    	    	String writePath =  experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i] + "/HV";
	    	    	FileUtils.resetFile(writePath);
	    	    	
	    	    	try {
						ReadAndWrite.exchangeName(readPath, writePath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	    	} catch (IOException e) {
    	    	e.printStackTrace();
    	    	}
    	    	
    	    	
    		}// for i
    		
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
	
	
	  /**
	    * 使用文件通道的方式复制文件
	    * 
	    * @param s
	    *            源文件
	    * @param t
	    *            复制到的新文件
	    */
	    public void fileChannelCopy(File s, File t) {
	        FileInputStream fi = null;
	        FileOutputStream fo = null;
	        FileChannel in = null;
	        FileChannel out = null;
	        try {
	            fi = new FileInputStream(s);

	            fo = new FileOutputStream(t);
	            in = fi.getChannel();//得到对应的文件通道
	            out = fo.getChannel();//得到对应的文件通道
	            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {

	            try {
	                fi.close();
	                in.close();
	                fo.close();
	                out.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	 }
