package jmetal.myutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.StringTokenizer;
import java.util.Vector;

public class ReadAndWrite {
	
	int startingIndex_;	
	    
	public ReadAndWrite(int startingIndex) {
		startingIndex_  = startingIndex;// TODO Auto-generated constructor stub
	};
	
   public void excute(String readPath,String writePath) throws IOException {
	 
	 File archivo = new File(readPath);
	 FileReader fr = null;
	 BufferedReader br = null;        
	 fr = new FileReader (archivo);
	 br = new BufferedReader(fr);
	
	 
	 FileOutputStream fos   = new FileOutputStream(writePath)     ;
     OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
     BufferedWriter bw      = new BufferedWriter(osw)        ;
     
     
	 // File reading
	 String line;
	
	while ((line=br.readLine())!=null) {
		 StringTokenizer st = new StringTokenizer(line);
		 String newLine = "";
		 
		 int i = 0;
		 while (st.hasMoreTokens()) {
			 String aux = st.nextToken();
			 if( i < startingIndex_) {
				i++;
				continue;
			 }
			 else {	
				 newLine = newLine + aux +" ";
//				 System.out.println(newLine);
			 }
		 }//
		 bw.write(newLine);
		 bw.newLine();
	} // while     
	
	  bw.close();
	  br.close();
	  
   }//excute
   
   public static void exchangeName(String readPath,String writePath) throws IOException {
		 
		 File archivo = new File(readPath);
		 FileReader fr = null;
		 BufferedReader br = null;        
		 fr = new FileReader (archivo);
		 br = new BufferedReader(fr);
		
		 
		 FileOutputStream fos   = new FileOutputStream(writePath)     ;
	     OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	     BufferedWriter bw      = new BufferedWriter(osw)        ;
	     
	     
		 // File reading
		 String line;
		
		while ((line=br.readLine())!=null) {
			 StringTokenizer st = new StringTokenizer(line);
			 String newLine = "";

			 while (st.hasMoreTokens()) {
				String aux = st.nextToken();	
				newLine = newLine + aux +" ";
				
			 }//
			 bw.write(newLine);
			 bw.newLine();
		} // while     
		
		  bw.close();
		  br.close();
		  
	   }//excute
   
   public static void main(String args[]) throws IOException {
	   String basePath = "E:/JMetalTool/jmetal/paretoFronts/";
	   int startingIndex = 10;
	   ReadAndWrite rw = new ReadAndWrite(startingIndex);
	   String [] fileNames = {
			   "KC10-2fl-1uni" ,
			   "KC10-2fl-2uni" ,
			   "KC10-2fl-3uni" ,
			   "KC10-2fl-1rl" ,
			   "KC10-2fl-2rl" ,
			   "KC10-2fl-3rl" ,
			   "KC10-2fl-4rl" ,
			   "KC10-2fl-5rl" ,
	   };
	   
	   for(int i=0;i<fileNames.length;i++) {
		   String readPath = basePath + fileNames[i] + ".PO";
		   String writePath = basePath + fileNames[i] + ".pf";
		   rw.excute(readPath, writePath);
	   }
	   
	   System.out.println("OK");
   }
}
