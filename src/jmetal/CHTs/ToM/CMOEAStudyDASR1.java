/* CMOEAStudyDASR1.java
 * 
 * Author:  Yi Xiang <xiangyi@scut.edu.cn> or <gzhuxiang_yi@163.com>  
 * 
 * Data: 10/03/2021
 * Copyright (c) 2021 Yi Xiang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package jmetal.CHTs.ToM;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.util.Friedman;
import jmetal.myutils.ClearMetricDuringRun;
import jmetal.util.JMException;


public class CMOEAStudyDASR1 extends Experiment {

  /**
   * Configures the algorithms in each independent run
   * @param problemName The problem to solve
   * @param problemIndex
   * @throws ClassNotFoundException 
   */
  public void algorithmSettings(String problemName, 
  		                          int problemIndex, 
  		                          Algorithm[] algorithm) throws ClassNotFoundException {
    try {
    	
      int numberOfAlgorithms = algorithmNameList_.length;

      HashMap[] parameters = new HashMap[numberOfAlgorithms];

      for (int i = 0; i < numberOfAlgorithms; i++) {
        parameters[i] = new HashMap();
      } // for

      if (!paretoFrontFile_[problemIndex].equals("")) {
	        for (int i = 0; i < numberOfAlgorithms; i++){
	          parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);	        
	        }
       } // if
      
      double[][] dif_level = { {0.5, 0.0, 0.0},{0.0, 0.5, 0.0}, {0.0, 0.0, 0.5}};
            
//      double[][] dif_level = {{0.25, 0.0, 0.0}, {0.0, 0.25, 0.0}, {0.0, 0.0, 0.25}, {0.25, 0.25, 0.25},
//              {0.5, 0.0, 0.0},{0.0, 0.5, 0.0}, {0.0, 0.0, 0.5}, {0.5, 0.5, 0.5},
//              {0.75, 0.0, 0.0}, {0.0, 0.75, 0.0}, {0.0, 0.0, 0.75},{0.75, 0.75, 0.75},
//              {0.0, 1.0, 0.0}, {0.5, 1.0, 0.0}, {0.0, 1.0, 0.5},{0.5, 1.0, 0.5}};
      
      //Set maxEvaluations_
//       for (int i = 0; i < numberOfAlgorithms; i++){ 
//          parameters[i].put("maxEvaluations_", noOfFEs_); // Using this comment can uniform the maxEvaluations_
//       }
      
      String realProbName;
      int levelIdx;
      
      int pos = problemName.indexOf("_");
      realProbName = problemName.substring(0, pos);        
      levelIdx = Integer.parseInt(problemName.substring(pos + 1, problemName.length())) - 5; //-5  because the start index is 5 in our case
           
           
	  Object[] problemParams ;
	  
	  if (this.noOfObjectives_ <= 3) {
		  problemParams =  new Object[] {"Real", dif_level[levelIdx]}; // 
	  } else {
		  problemParams =  new Object[] {"Real", noOfObjectives_, dif_level[levelIdx]}; // 
	  }
	  	        
	  algorithm[0] = new MOEAD_ToM_DAS_Settings(problemName,problemParams).configure(parameters[0]);		  
	  algorithm[1] = new MOEAD_CDP_DAS_Settings(problemName,problemParams).configure(parameters[1]);	
	  algorithm[2] = new MOEAD_SP_DAS_Settings(problemName,problemParams).configure(parameters[2]);	
	  algorithm[3] = new MOEAD_SR_DAS_Settings(problemName,problemParams).configure(parameters[3]);	
	  algorithm[4] = new MOEAD_Epsilon_DAS_Settings(problemName,problemParams).configure(parameters[4]);	
	  
//	  algorithm[1] = new NSGAII_ToM_DAS_Settings(problemName,problemParams).configure(parameters[1]);		  
//	  algorithm[1] = new NSGAII_CDP_DAS_Settings(problemName,problemParams).configure(parameters[1]);	
//	  algorithm[2] = new NSGAII_SP_DAS_Settings(problemName,problemParams).configure(parameters[2]);	
//	  algorithm[3] = new NSGAII_SR_DAS_Settings(problemName,problemParams).configure(parameters[3]);	
//	  algorithm[4] = new NSGAII_Epsilon_DAS_Settings(problemName,problemParams).configure(parameters[4]);	
	  
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(CMOEAStudyDASR1.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(CMOEAStudyDASR1.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(CMOEAStudyDASR1.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    CMOEAStudyDASR1 exp = new CMOEAStudyDASR1();

    exp.experimentName_ = "CMOEAStudyDAS"; 
    
	exp.noOfObjectives_ =  2;  
    exp.algorithmNameList_ = new String[]{	    	
     		"MOEADToM",  
    		"MOEADCDP",
    		"MOEADSP",
    		"MOEADSR",
    		"MOEADEpsilon",   
    		
//    		"PPSMOEAD",
//    		"CNSGAIII",    		
//    		"C-MOEAD", 
//    		"CMOEADD",    		
    		//"CTAEA",  

    		};
    
    String[] problemStrings = {
    		"DASCMOP1", 
    		"DASCMOP2", 
    		"DASCMOP3",
    		"DASCMOP4", 
    		"DASCMOP5",
    		"DASCMOP6",
//    		"DASCMOP7", //m=3
//    		"DASCMOP8", //m=3
//    		"DASCMOP9"  //m=3
//    		
    		// many-objective
//    		"DASCMaOP1", "DASCMaOP2","DASCMaOP3",
//    		 "DASCMaOP4",//
//    		 "DASCMaOP5", "DASCMaOP6", 
//    		"DASCMaOP7", "DASCMaOP8",
//    		 "DASCMaOP9", 
    		};

    int numberOfDif_level = 3;
    
//    if (exp.noOfObjectives_ > 3)
//    	numberOfDif_level = 12;
//    
//    numberOfDif_level = 3;    
    
   //-------------do automatically -------------    
    exp.problemList_     = new String[problemStrings.length * numberOfDif_level];  	
    exp.paretoFrontFile_ = new String[problemStrings.length * numberOfDif_level]; 
           
    for (int i = 0; i < problemStrings.length; i++) {

        for (int k = 0; k < numberOfDif_level; k++) {
        	exp.problemList_[i * numberOfDif_level + k] = problemStrings[i] + "_" + (k + 5);
        	exp.paretoFrontFile_[i * numberOfDif_level + k] = problemStrings[i] + "_" + (k + 5)+".pf";
        } // for k        
        
    } // for i
    //-------------------------
   
    exp.indicatorList_ = new String[]{
//    		"HV",
//    		"HV2",
//    		"GSPREAD", 
//    		"DCI",
//    		"EPSILON",
//    	 	"IGD",
//    	 	"IGDNS",
//    		"PD",
//    		"GD",
    		"IGD+",
//    		"RUNTIME",
//    		"FS",
    		};
    
    int numberOfAlgorithms = exp.algorithmNameList_.length;    
      
    exp.experimentBaseDirectory_ = "./jmetalExperiment/" + exp.experimentName_ + "/M=" +  exp.noOfObjectives_ ;
    exp.paretoFrontDirectory_ = "./paretoFronts/";
//    exp.paretoFrontDirectory_ = "./paretoFronts";
    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 1;    

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    
//    ClearMetricDuringRun cmdr = new ClearMetricDuringRun(exp.experimentName_,exp.algorithmNameList_,exp.problemList_,exp.noOfObjectives_);      
    exp.runExperiment(numberOfThreads = 1) ;
    exp.generateQualityIndicators() ;

    // Generate latex tables
    exp.generateLatexTables(false) ;    // generate tables without test symbols
//    exp.generateLatexTables(true) ;    // generate tables with test symbols
  
    // Applying Friedman test
    Friedman test = new Friedman(exp);
//    test.executeTest("EPSILON");
//    test.executeTest("HV");
//    test.executeTest("GSPREAD");
//    test.executeTest("IGD+");
//    test.executeTest("RUNTIME");
  } // main
} 

