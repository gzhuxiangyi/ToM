package jmetal.myutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.Configuration;
import jmetal.util.JMException;

public class CaptureConvergencPlot {	
	String expName_;
	
	String [] probNames_;
    String [] algNames_;
    
    String basePath_; 
    String storePath_ ;
   public String indicatorName_;
    
    int numberOfpoints_;
    int gap_ ;
    
    double [][] indicators_;
    MetricsUtil utils_ ;
    		
	public CaptureConvergencPlot(String expName,  String basePath, int numberOfpoints, int gap,
			                     String [] probNames, String [] algNames, String indicatorName) {
		this.expName_ = expName;
		this.basePath_ = basePath;
		this.numberOfpoints_ = numberOfpoints;
		this.gap_ = gap;
		this.probNames_ = probNames;
		this.algNames_ = algNames;
		this.indicatorName_ = indicatorName;
		
		utils_ = new MetricsUtil();
		
		storePath_ = basePath_ + expName_ +"convergencePlots/" + indicatorName_+"/";
		basePath_ = basePath_ + expName_ +"data/";		
		this.checkExperimentDirectory();
	}
	
   public void execute () {	 
	   for(int j=0;j< probNames_.length;j++ ) { // for each problem 
		   String problem = probNames_[j];
		   indicators_ = new double[numberOfpoints_][algNames_.length];
		   
		   for (int i=0;i<algNames_.length;i++){ // for each algorithm
			   String algorithm = algNames_[i];
//			   String path = basePath_ + algorithm + "/" + problem + "/IGD"; // Plot IGD
//			   String path = basePath_ + algorithm + "/" + problem + "/HV";// Plot HV
			   String path = basePath_ + algorithm + "/" + problem + "/" + this.indicatorName_;
			   double [][] values = utils_.readFront(path);	 
			   
			   for(int k=0;k < numberOfpoints_;k++) 
				   indicators_[k][i] = values [k][0];
		   }// for i
		   
		  String storePath = storePath_ + problem  + ".m"; 		  
		  
		  writeToFile(storePath,indicators_, problem);
	   }// for j
	   
	      
   }//execute   
	
	public void writeToFile(String path, double [][] values, String problem){
		int rows = values.length;
		int colns = values[0].length;
		String strToWrite ;	
		resetFile(path);
		int [] xlabels = new int[rows];
		
		for(int i=0;i<rows;i++)
			xlabels[i] = (i)*gap_;
		
		try {
		      /* Open the file */
		      FileOutputStream fos   = new FileOutputStream(path)     ;
		      OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
		      BufferedWriter bw      = new BufferedWriter(osw)        ;
		      /**
		       * Plot figure of the true front
		       */
		      bw.write("clear ");
		      bw.newLine();
		      bw.write("clc ");
		      bw.newLine();
		      bw.write("xlabels = [ ");
		      bw.newLine();
		      
		      for(int i=0;i<rows;i++){
		    	  strToWrite ="";		    	 
		    	  strToWrite = strToWrite + String.valueOf(xlabels[i]) + " ";		    	 
		    	  bw.write(strToWrite);
		    	  bw.newLine();
		      }
		      bw.write("];");
		      bw.newLine();
		      
		      
		      bw.write("indicatorValues = [ ");
		      
		      for(int i=0;i<rows;i++){
		    	  strToWrite ="";
		    	  for(int j=0;j<colns;j++){
		    		  strToWrite = strToWrite + String.valueOf(values[i][j]) + " ";
		    	  }
		    	  bw.write(strToWrite);
		    	  bw.newLine();
		      }
		        //if (this.vector[i].getFitness()<1.0) {
		        bw.write("];");
		        bw.newLine();
		        bw.write("set(0,'units','centimeters')");
		        bw.newLine();
		        bw.write("position=[0 0 17 6.5];");
		        bw.newLine();		        
		     
		        String [] property  =new String[]{
						"-ro","-b>","-kd","-m^","-cs","-gh"
				};
		        
		        for(int k = 0; k < colns; k++){
		        	bw.write("plot(xlabels" + " ,indicatorValues(:," + (k+1) + "),'" + property[k] 
		        			+ "','MarkerFaceColor','" + property[k].substring(1,2)+  "','LineWidth',1.5,'MarkerSize',6)");
//		        	bw.write("plot(xlabels" + " ,indicatorValues(:," + (k+1) + "),'" + property[k] 
//		        			+  "','LineWidth',1.6,'MarkerSize',6)");
		        	bw.newLine();	
		        	bw.write("hold on");
		        	bw.newLine();		   
		        }
		        
		        bw.newLine();
		        bw.write(" xl = xlabel('FEs');");
		        bw.newLine();
		        bw.write("set(xl,'fontsize',20)");
		        bw.newLine();
//		        bw.write(" yl = ylabel(' Better \\leftarrow IGD \\rightarrow Worse');");
//		        bw.write(" yl = ylabel(' Worse \\leftarrow HV \\rightarrow Better');");
		        if (indicatorName_.equalsIgnoreCase("HV") || indicatorName_.equalsIgnoreCase("DCI")) {
		        	bw.write(" yl = ylabel(' Worse \\leftarrow " + indicatorName_ + "\\rightarrow Better');");
		        } else {
		        	bw.write(" yl = ylabel(' Better \\leftarrow " + indicatorName_ + "\\rightarrow Worse');");
		        }
		        
		        bw.newLine();
		        bw.write("set(yl,'fontsize',20)");
		        bw.newLine();		        
//		        bw.write(" tit = title('Evolutionary trajectories of IGD on "+ problem + "');");
//		        bw.newLine();	
//		        bw.write("set(tit,'fontsize',15)");
//		        bw.newLine();
		        bw.write("set(gca,'fontsize',13)");
		        bw.newLine();
		        
		        // legend 
		        
		        String lgd = "lgd = legend(";
		        
		        for(int algID = 0; algID < algNames_.length-1;algID++) {
		        	lgd = lgd + "'" + algNames_[algID] + "',";
		        }
		        lgd = lgd + "'" + algNames_[algNames_.length-1] + "');";
		        
		        bw.write(lgd);
		        bw.newLine();
		        bw.write("set(lgd,'fontsize',13)");
		        bw.newLine();
		        bw.write("set(gca,'XLim',[0 " + rows * gap_ + "]);");
		        
		  /* Close the file */
	      bw.close();
	    } catch (IOException e) {
	      Configuration.logger_.severe("Error acceding to the file");
	      e.printStackTrace();
	    }//catch	
		
	}// 
	
	public void resetFile(String file) {
		File f = new File(file);
		if(f.exists()){			
			System.out.println("File " + file + " exist.");

			if(f.isDirectory()){
				System.out.println("File " + file + " is a directory. Deleting directory.");
				if(f.delete()){
					System.out.println("Directory successfully deleted.");
				}else{
					System.out.println("Error deleting directory.");
				}
			}else{
				System.out.println("File " + file + " is a file. Deleting file.");
				if(f.delete()){
					System.out.println("File succesfully deleted.");
				}else{
					System.out.println("Error deleting file.");
				}
			}			 
		}else{
			System.out.println("File " + file + " does NOT exist.");
		}
	} // resetFile
	
	private void checkExperimentDirectory() {
		File experimentDirectory;

		experimentDirectory = new File(storePath_);
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
	} // checkDirectories
	
	public static void main(String[] args) throws JMException, IOException {
		String basePath = "./jmetalExperiment/";
		String expName = "MaPSOStudyConvergence/M=15/";
		int numberOfpoints = 98;  // PAEA: (WFG 112, DTLZ1:56, DTLZ2:42;   DTLZ3:84) 
//		int gap =  276*9*2 ; // Used in PAEA study, M = 10
//		int gap =  400*10 ;// Used in MOABC/D study;
		int gap =  136*1 ;// Used in VaEA study;
//		int gap =  92*10 ;// Used in VaEA study;
		String indicatorName = "IGD";
		
		String [] probNames =  new String [1];
		String [] algNames = new String [2];
		
		probNames[0] = "DTLZ3";
		
		//VaEA
//		algNames [0] = "MaPSO";
//		algNames [1] = "MaPSOR3EA";
		algNames [0] = "MaPSOFirst100";
		algNames [1] = "MaPSOR3EAFirst100";//采用EA产生子代
		
//		algNames [2] = "VaEA";
//		algNames [3] = "MOEAD";
//		algNames [4] = "dMOPSO";
//		algNames [4] = "MOEAD";
		
		// PAEA
//		algNames [0] = "PAEA";
//		algNames [1] = "MOEADD";
//		algNames [2] = "NSGAIII";			
//		algNames [3] = "MOEAD";
//		algNames [4] = "GrEA";
//		algNames [5] = "MOMBI2";			
		
		CaptureConvergencPlot plot = new CaptureConvergencPlot(expName, basePath, numberOfpoints, gap,
               probNames, algNames,indicatorName) ;
		
		plot.execute();
		
		
	}
}
