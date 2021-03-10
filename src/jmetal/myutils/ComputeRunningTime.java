package jmetal.myutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.Vector;

public class ComputeRunningTime {
	public int numberOfRuns_ ;
	public String [] problemList_;
	public String besePath_;
	
	public ComputeRunningTime() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Write data in corresponding path
	 * @param path
	 * @param data
	 * @throws IOException 
	 */
	public void recordData(String path,String data) throws IOException{		  	  
			 File f = new File(path);	
			 FileWriter os = new FileWriter(path, true);
			 os.write(data);
			 os.write("\n"); 	
			 os.close();
	}
	
	public Vector loadData(String fileName) throws IOException{
		Vector data = new Vector(); 
		FileInputStream fis = new FileInputStream(fileName);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		//System.out.println(directory);
		String aux = br.readLine();
		while (aux != null) {
			data.add(aux);
			//System.out.println(Double.parseDouble(aux));
			aux = br.readLine();
		} // while
	      return  data;
	}
	
	
	public static void main(String[] args) throws IOException {
		
		ComputeRunningTime crt = new ComputeRunningTime();
		
		crt.besePath_ = "E:/JMetalTool/jmetal/jmetalExperiment/metric/"+ "DMCDMOABC/";
		crt.numberOfRuns_ = 20;		
		crt.problemList_ = new String[]{
	     /**
	     * QAP
	     */
//	    "QAPbur26a", "QAPbur26b",
//	    "QAPbur26c", "QAPbur26d",
//	    "QAPbur26e", "QAPbur26f",
//	    "QAPbur26g",  "QAPbur26h",    		
//	    "QAPchr12a",  "QAPchr12b",
//	    "QAPchr12c",  "QAPchr15a",
//	    "QAPchr15b",  "QAPchr15c",
//	    "QAPchr18a",  "QAPchr18b",
//	    "QAPchr20a",  "QAPchr20b",
//	    "QAPchr20c",  "QAPchr22a",
//	    "QAPchr22b",  "QAPchr25a",  	    
//	    "QAPesc32a",  "QAPesc32b", 
//	    "QAPesc32c",  "QAPesc32d", 
//	    "QAPesc32g",  "QAPesc32h", 
//	    "QAPesc64a",  "QAPesc128", 
//		"QAPkra30a", 
//		"QAPkra30b", 
//		"QAPnug24", 
//		"QAPnug25", 
//		"QAPnug30", 
//		"QAPsko42", 
//		"QAPsko49",
//		"QAPsko56",
//		"QAPsko64",
//		"QAPsko72",
//		"QAPsko81",
//		"QAPsko90",
//		"QAPsko100a",
//		"QAPsko100b",
//		"QAPsko100c",
//		"QAPsko100d",
//		"QAPsko100e",
//		"QAPsko100f",
//		"QAPste36a",
//		"QAPste36b",
//		"QAPste36c",
		
//		"QAPtai20b",
//		"QAPtai25a",
//		"QAPtai25b",
//		"QAPtai30a",
//		"QAPtai30b",
//		"QAPtai35a",
//		"QAPtai35b",    		
//		"QAPtai40a",
//		"QAPtai40b",
//		"QAPtai50a",
//		"QAPtai50b",
//		"QAPtai60a",
//		"QAPtai60b",
//		"QAPtai64c",
//		"QAPtai80a",
//		"QAPtai80b",
//		"QAPtai100a",
//		"QAPtai100b",
//		"QAPtai150b",
//	    "QAPtho30",
//	    "QAPtho40",
//	    "QAPtho150",
//	    "QAPwil50",
//	    "QAPwil100",
				
				"mQAPKC10-2fl-1uni", 
				 "mQAPKC10-2fl-2uni", 
				 "mQAPKC10-2fl-3uni", 
				 "mQAPKC10-2fl-1rl", 
				 "mQAPKC10-2fl-2rl", 
				 "mQAPKC10-2fl-3rl", 
				 "mQAPKC10-2fl-4rl", 
				 "mQAPKC10-2fl-5rl", 
				 
				 "mQAPKC20-2fl-1uni", 
				 "mQAPKC20-2fl-2uni", 
				 "mQAPKC20-2fl-3uni", 
				 "mQAPKC20-2fl-1rl", 
				 "mQAPKC20-2fl-2rl", 
				 "mQAPKC20-2fl-3rl", 
				 "mQAPKC20-2fl-4rl", 
				 "mQAPKC20-2fl-5rl", 
				 
				 "mQAPKC30-3fl-1uni", 
				 "mQAPKC30-3fl-2uni", 
				 "mQAPKC30-3fl-3uni", 
				 "mQAPKC30-3fl-1rl", 
				 "mQAPKC30-3fl-2rl", 
				 "mQAPKC30-3fl-3rl", 			
		};
     /**
      * Write FEs in the file
      */
//		int FEs = 1000000;
//		String dataType = ".FEs";
//		
//		for(int i=0;i< crt.problemList_.length;i++) {
//			String path = crt.besePath_+ crt.problemList_[i] + dataType;
//			
//			for(int j=0;j<crt.numberOfRuns_;j++) {
//				crt.recordData(path, Integer.toString(FEs));			
//			}
//			
//		}
	
		
		for(int i=0;i< crt.problemList_.length;i++) {
			String FEsPath = crt.besePath_ + crt.problemList_[i]+ ".FEs";
			String FEsCutPath = crt.besePath_ + crt.problemList_[i]+ ".FEsCut";
			String timePath = crt.besePath_ + crt.problemList_[i]+ ".time";
			
			Vector FEsVec = new Vector();
			FEsVec = crt.loadData(FEsPath);
			
			Vector FEsCutVec = new Vector();
			FEsCutVec = crt.loadData(FEsCutPath);
			
			Vector timeVec = new Vector();
			timeVec = crt.loadData(timePath);
			
			Vector runningTimeVec = new Vector();
			
			String runningTimePath =  crt.besePath_ + crt.problemList_[i]+ ".runningTime";
			
			for(int j=0;j<crt.numberOfRuns_;j++) {
//				double runningTime = Double.parseDouble(timeVec.get(j))/ *Integer.parseInt(FEsVec.get(j));
				double runningTime = Double.parseDouble((String) timeVec.get(j))/ Integer.parseInt((String) FEsVec.get(j))
						*Integer.parseInt((String) FEsCutVec.get(j))/1000;// time in seconds
				crt.recordData(runningTimePath, Double.toString(runningTime));
			}			
		}
			
	}//main

}
