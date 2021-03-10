/**
 * This class 实现数据的自动整理，便于采用WFG方法计算HV值（linuex系统下进行计算）
 * By Yi Xiang
 * 1. 运行该程序，汇总计算HV的数据，根目录为HVResults
 * 2. 将汇总的数据复制到虚拟机的HVComputation目录下
 * 3. 编译make march=native，运行命令./wfg，即可产生HV
 * 4. 将文件从虚拟机复制回jmetal即可生成latex表格
 */
package jmetal.myutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import jmetal.myutils.FileUtils;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.JMException;

/**
 * @author Administrator
 *
 */
public class WFG_HV_DataCollect {
	private String experimentName_ ;
	private String[] algName_;
	private String[] problemNames_;
	private String[] paretoFrontFile_;
	private int numberofRuns_ ;
	private int objectives_;
	/**
	 * 
	 */
	public WFG_HV_DataCollect(String experimentName,String[] problemNames, String[] paretoFrontFile, int numberofRuns,
			String[] algName,int objectives) {
		
		experimentName_ = experimentName;
		problemNames_ = problemNames;
		paretoFrontFile_ =  paretoFrontFile;
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
		int objectives = 15 ;		// 
		
		String [] algNames = new String[]{
				/**
				 * Used in AdMOEAStudy
				 */
//	    		"AdMOEAMAFR1",// 第一轮修改，再次运行原算法    		
//	     		"AdMOEAMAF+MAF", // 只采用MAF
	    		"AdMOEAMAF+eliminate", // 只采用eliminate
//	    		"AdMOEAMAFNoRank",//
//				"AdMOEAMAF", // 最好的还是这个，最终写入论文。在WFG测试函数上，MAF效果好
				"NSGAIII", 
	    		"VaEA",
				"MOEAD",
				"ThetaDEA",
				"1by1EA",
	    		"GWASFGA", 
	    		"MOEADIPBI"
	
	    		
		};
		
		String [] problemNames = new String[]{
	    		"DTLZ1",   
				"DTLZ2",   
				"DTLZ3",  
				"DTLZ4",  
				"DTLZ5",
				"DTLZ6",
				"DTLZ7",			
				"ConvexDTLZ2",
				"ConvexDTLZ4",	
				"MinusDTLZ1",//"MinusDTLZ2",
				"MinusDTLZ3",//"MinusDTLZ4",
//				
				"WFG1",	"WFG2",	"WFG3",	"WFG4", "WFG5",	"WFG6",	"WFG7",	"WFG8",	"WFG9",				
//				    		
	    		"MinusWFG1","MinusWFG2","MinusWFG3", "MinusWFG4","MinusWFG5","MinusWFG6",
	    		"MinusWFG7",
	    		"MinusWFG8", "MinusWFG9",
//				"DTLZ3Pdot2","DTLZ3Pdot4","DTLZ3Pdot8","DTLZ3P2","DTLZ3P4","DTLZ3P8",
		};
				
		 String [] paretoFrontFile = new String[]{  
		    		
		    		"RandDTLZ1" +".M" + objectives+".pf",    		
		    		"RandDTLZ234" +".M" + objectives+".pf",
		    		"RandDTLZ234" +".M" + objectives+".pf",
		    		"RandDTLZ234" +".M" +objectives+".pf",   
		    		
		    		"RandDTLZ5" +".M" +objectives+".rf",
		    		"RandDTLZ6" +".M" +objectives+".rf",
		    		"RandDTLZ7" +".M" +objectives+".rf",    	
		    		
		    		"RandConvexDTLZ234" +".M" +objectives+".pf",
					"RandConvexDTLZ234" +".M" +objectives+".pf",

					"RandMinusDTLZ1" +".M" +objectives+".pf",
//		    		"RandMinusDTLZ24" +".M" +objectives+".pf",
		    		"RandMinusDTLZ3" +".M" +objectives+".pf",
//		    		"RandMinusDTLZ24" +".M" +objectives+".pf",
					
//					"RandWFG1" +".M" +objectives+".pf",
//		    		"RandWFG2" +".M" +objectives+".pf",
//		    		"RandWFG3" +".M" +objectives+".rf",
		    		"","","",
		    		"RandWFG4To9" +".M" +objectives+".pf",
		    		"RandWFG4To9" +".M" +objectives+".pf",
		    		"RandWFG4To9" +".M" +objectives+".pf",
		    		"RandWFG4To9" +".M" +objectives+".pf",
		    		"RandWFG4To9" +".M" +objectives+".pf",
		    		"RandWFG4To9" +".M" +objectives+".pf", 
////		    		"","","","","","","","","","","","","","","",    	
		    		
//		    		"RandMinusWFG1" +".M" +objectives+".rf",
//		    		"RandMinusWFG2" +".M" +objectives+".rf",
		    		"","",
		    		
		    		"RandMinusWFG3" +".M" +objectives+".pf",
		    		"RandMinusWFG4To9" +".M" +objectives+".pf",
		    		"RandMinusWFG4To9" +".M" +objectives+".pf",
		    		"RandMinusWFG4To9" +".M" +objectives+".pf",
		    		"RandMinusWFG4To9" +".M" +objectives+".pf",
		    		"RandMinusWFG4To9" +".M" +objectives+".pf",
		    		"RandMinusWFG4To9" +".M" +objectives+".pf",

		    		};
		 
		/*
		 * The following should be tuned manually
		 */

		String experimentName = "./jmetalExperiment/AdMOEAStudy/M=" + objectives;
//		 String experimentName = "./jmetalExperiment/PaRPEAStudyConvergence/M=" + objectives;
		int numberofRuns = 10;			// WFG 112, DTLZ1:56, DTLZ2:42;   DTLZ3:84; 
		
		/*
		 * End
		 */
		
		WFG_HV_DataCollect hvData = new WFG_HV_DataCollect(experimentName,problemNames,paretoFrontFile,numberofRuns,
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
    			
    			String writePath = "./jmetalExperiment//HVResults/AdMOEAStudy/M=" + objectives_
    				    			+ "/data/" + algName_[k] + "/" + problemNames_[i];    		// 
    			FileUtils.CheckDir(writePath);
    			
    			writePath = writePath  +"/allbest.txt";
    			
    			FileUtils.resetFile(writePath);
    			    			
    			for (int j = 0; j < numberofRuns_;j++) { // for each run
    				String readPath = experimentName_ + "/data/" + algName_[k] + "/" + problemNames_[i]+"/FUN."+ j;
//    				System.out.println(readPath);
    				double [][] data = FileUtils.readMatrix(readPath);    				
    				    
    				if  (problemNames_[i].equalsIgnoreCase("DTLZ1")){ 
    					
 		    			for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = data[r][o]/0.5;
 		    					
 	    	    			}
 		    			} // for 
 		    	 		    			
    				} else if (problemNames_[i].equalsIgnoreCase("DTLZ2") || problemNames_[i].equalsIgnoreCase("DTLZ3")
    						 || problemNames_[i].equalsIgnoreCase("DTLZ4") ||problemNames_[i].equalsIgnoreCase("ConvexDTLZ2")
    						 || problemNames_[i].equalsIgnoreCase("ConvexDTLZ4") || problemNames_[i].contains("DTLZ3P") ){
	    				// Do nothing. Objective values are in [0,1]
    				
	    			} else if (problemNames_[i].equalsIgnoreCase("DTLZ5") || problemNames_[i].equalsIgnoreCase("DTLZ6")
   						 || problemNames_[i].equalsIgnoreCase("DTLZ7")) {
	    				
	    			   Hypervolume indicators = new Hypervolume();
	    			   MetricsUtil utils_ = new jmetal.qualityIndicator.util.MetricsUtil();
	    			   
	    			   double[][] trueFront =  indicators.utils_.readFront("./paretoFronts/DTLZ&WFG/" + paretoFrontFile_[i]);
	    			   
	    			   double[] maximumValue = utils_.getMaximumValues(trueFront, objectives_);
	    			   double[] minimumValue = utils_.getMinimumValues(trueFront, objectives_);
	    			   
	    			   data  = utils_.getNormalizedFront(data, maximumValue, minimumValue);

	    			} else if (problemNames_[i].equalsIgnoreCase("MinusDTLZ1") ) {
	    				
	    				for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = (data[r][o] - (-551.15066)) / (0 - (-551.15066));
 	    	    			}
 		    			} // for 
	    	
	    			} else if (problemNames_[i].equalsIgnoreCase("MinusDTLZ2") || problemNames_[i].equalsIgnoreCase("MinusDTLZ4")) {
	    				
	    				for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = (data[r][o] - (-3.5))/ (0 - (-3.5));
 	    	    			}
 		    			} // for 
	    		
	    			} else if (problemNames_[i].equalsIgnoreCase("MinusDTLZ3") ) {
	    				
	    				for (int r = 0; r < data.length; r ++) {
 		    				for(int o = 0; o < objectives_;o++) {
 		    					data[r][o] = (data[r][o] - (-2203.603))/ (0 - (-2203.603));
 	    	    			}
 		    			} // for 
	    	
	    			} else if (problemNames_[i].length() >= 4 && problemNames_[i].substring(0,3).equalsIgnoreCase("WFG")){ 
    	    			
	    				for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					data[r][o] = data[r][o]/(2*(o+1));
        	    			}
    	    			}    
    	    	    			
	    			} else if (problemNames_[i].length() >= 9 && problemNames_[i].substring(0,8).equalsIgnoreCase("MinusWFG")){ 
    	    			
	    				for (int r = 0; r < data.length; r ++) {
    	    				for(int o = 0; o < objectives_;o++) {
    	    					data[r][o] = (data[r][o] - (-2*(o+1) - 1)) / (-1 - (-2*(o+1) - 1) );
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
    	    			
        	    	} else { 
    	    			System.out.println("Problem Undefined!!");
    	    		} // if 	
    	    		
    				// 个数输出数据
    				
    				if (j < numberofRuns_ - 1) 
    					FileUtils.writeMatrix(writePath, data);
    				else 
    					FileUtils.writeMatrixEnd(writePath, data);
    				
    			} // For 
    			
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
