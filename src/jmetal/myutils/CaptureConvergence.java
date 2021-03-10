/**
 * This class is used to capture the convergence of an algorithm during the search process
 * It records the solution set every $gap$ FEs
 */
package jmetal.myutils;

import java.io.File;

import jmetal.core.SolutionSet;

public class CaptureConvergence {
   public int gap_;
   public int objectives_;
   public String basePath_;
   public String problemName_;  
   public String algName_;  
   public String expName_; 
   int times_ = 0;
		
//   public CaptureConvergence(String problem, String algName, int objectives) {
//		/**
//		 * 	gap_ and expName_ can be modified by the users
//		 * m = 3, gap_ = 92*10
//		 * m =  15, gap_ = 136*100
//		 */
////		expName_ ="PaRPEAStudyConvergence";		
//	   expName_ ="NewTestProblemsStudyConvergence";	
//	   objectives_ = objectives;	
//				
//		if (objectives_ ==3) {
//			gap_ = 92*10;		
//		} else if (objectives_ == 8){
//			gap_ = 156*50; // 
//		} else if (objectives_ == 10){
//			gap_ = 276*50; // 
//		} else if (objectives_ == 15){
//			gap_ = 136*100; // MaPSO study 	�ڵ�3���޸��У�����136*50
//		} else {
//			System.out.println("Objective number is undefined!!!");
//		}
//			
//		// Default parameters	
//		basePath_= "./jmetalExperiment/" ;
//		basePath_ = basePath_ + expName_ + "/M=" + objectives_ + "/data/";
//		checkExperimentDirectory();
//		
//		this.problemName_ = problem;
//		this.algName_ = algName;
//	}
   
   public CaptureConvergence(String problem, String algName, int objectives, int D, int FEs) {
		/**
		 * 	gap_ and expName_ can be modified by the users	
		 */
	   expName_ ="ConstrainedProblemsStudyConvergence";	
	   objectives_ = objectives;	
		
	    if (objectives_ == 2) {
			gap_ = 80*10;	// For each 10 generations, record once
	    } else if (objectives_ ==3) {
			gap_ = 92*10;		
	    } else if (objectives_ == 5){
			gap_ = 212*10; // 
		} else if (objectives_ == 8){
			gap_ = 156*10; // 
		} else if (objectives_ == 10){
			gap_ = 276*10; // 
		} else if (objectives_ == 15){
			gap_ = 136*100; // 
		} else {
			System.out.println("Objective number is undefined!!!");
		}
			
		// Default parameters	
		basePath_= "./jmetalExperiment/" ;
		basePath_ = basePath_ + expName_ + "/D=" + D  + "FEs=" + FEs + "/M=" + objectives_ + "/data/";
		checkExperimentDirectory();
		
		this.problemName_ = problem;
		this.algName_ = algName;
	}
   
	public void runCaptureConvergence(int FEs, SolutionSet solutionSet){		
		if(FEs == 0 || FEs % gap_ == 0) {
			System.out.println("FEs = " + FEs );
						
			File experimentDirectory;
			String directory;
			
			directory = basePath_ +  this.algName_ + "/" + this.problemName_;

			experimentDirectory = new File(directory);
			
			if (!experimentDirectory.exists()) {
				boolean result = new File(directory).mkdirs();
				System.out.println("Creating " + directory);
			}
			
			String path = basePath_ + this.algName_ + "/" + this.problemName_ +  "/FUN." + times_;
			resetFile(path);
			solutionSet.printObjectivesToFile(path);
			times_ ++;
		}
	}
		
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

		experimentDirectory = new File(basePath_);
		if (experimentDirectory.exists()) {
			System.out.println("Experiment directory exists");
			if (experimentDirectory.isDirectory()) {
				System.out.println("Experiment directory is a directory");
			} else {
				System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
			}
			experimentDirectory.delete();
			new File(basePath_).mkdirs();
		} // if
		else {
			System.out.println("Experiment directory does NOT exist. Creating");
			new File(basePath_).mkdirs();
		} // else
	} // checkDirectories
}
