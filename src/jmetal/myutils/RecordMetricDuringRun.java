/**
 * Yi Xiang gzhuxiang_yi@163.com
 * This class is used to record performance metrics of an algorithm during the search process
 * 
 */
package jmetal.myutils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.experiments.Experiment;

public class RecordMetricDuringRun {
   public String basePath_; // The base path of the metrics to be stored
   public String problemName_;  
   public String algName_;  
   public String expName_;    
    
   // Below are performance metrics
   String problemType = "Type"; // The estimated type of a problem
   
         
   public RecordMetricDuringRun(String experimentName, String problem, String algName,int objective) {
	    // Can be changed accordingly
	    expName_ = experimentName; 
		// Default parameters	
		basePath_= "./jmetalExperiment/";
		problemName_ = problem;
		algName_ = algName;
		
		basePath_ = basePath_ + expName_  + "/M=" + objective + "/data/" + algName + "/" + problem + "/";
		checkExperimentDirectory();		
	}
   
   public void resetFiles() {
		resetFile(basePath_ + problemType); // Reset problemType file
   }
	
   /**
    * 
    * @param 
    */
   public void writeMetric(String metric, int data) {
	   
	   String path = basePath_ + metric + ".csv";	   

	   FileWriter os;
       try {
           os = new FileWriter(path, true);   
           os.write("" + data + "\n");    
           os.close();                     
           
       } catch (IOException ex) {
           Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
       }    
       	   
   }
   	
   /**
    * 
    * @param 
    */
   public void writeMetric(String metric, long data) {
	   
	   String path = basePath_ + metric;
		   
	   FileWriter os;
       try {
           os = new FileWriter(path, true);
           os.write("" + data + "\n");
           os.close();
       } catch (IOException ex) {
           Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
       }    
       	   
   }
	
   /**
    * 
    * @param 
    */
   public void writeMetric(String metric, double data) {
	   
	   String path = basePath_ + metric;
	   
	   FileWriter os;
       try {
           os = new FileWriter(path, true);
           os.write("" + data + "\n");
           os.close();
       } catch (IOException ex) {
           Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
       }    
       	   
   }
   
   /**
    * 
    * @param 
    */
   public void writeMetric(String metric, String data) {
	   
	   String path = basePath_ + metric;
	   	   
	   FileWriter os;
       try {
           os = new FileWriter(path, true);
           os.write("" + data + "\n");
           os.close();
       } catch (IOException ex) {
           Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
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
