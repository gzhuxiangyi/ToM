/**
 * Yi Xiang gzhuxiang_yi@163.com
 * This class is used to record performance metrics of an algorithm during the search process
 * 
 */
package jmetal.myutils;

import java.io.File;

public class ClearMetricDuringRun {
   public String basePath_; // The base path of the metrics to be stored
   public String expName_;    

   
   
   public ClearMetricDuringRun(String expName,  String [] algNames, String [] problems,int objectives) {
	    // Can be changed accordingly
		expName_ = expName;		
		
		// Default parameters	
		basePath_= "./jmetalExperiment/" ;
		
		for (int i = 0; i < algNames.length;i++) {
			String algName = algNames[i];
			
			for (int j = 0; j < problems.length;j++) {
				String problemName = problems[j];
				String path = basePath_ + expName_ +  "/M=" + objectives + "/data/" + algName + "/" + problemName + "/Type.csv";
				resetFile(path);
			}			
			
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
