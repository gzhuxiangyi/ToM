/** CMOEAStudy.java
 * Created 2019.6.20
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


public class CMOEAStudyDASR1Parameter extends Experiment {

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
      
//      double[][] dif_level = { {0.0, 0.5, 0.0}};
      
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
      levelIdx = Integer.parseInt(problemName.substring(pos + 1, problemName.length())) - 5; //-5 is  because the start index is 5 in our case
           
           
	  Object[] problemParams ;
	  
	  if (this.noOfObjectives_ <= 3) {
		  problemParams =  new Object[] {"Real", dif_level[levelIdx]}; // 
	  } else {
		  problemParams =  new Object[] {"Real", noOfObjectives_, dif_level[levelIdx]}; // 
	  }
	  	  
//	  parameters[0].put("alpha_", 2.0);
//      parameters[1].put("alpha_", 4.0);	  	  	  	  
//      parameters[2].put("alpha_", 6.0);	 
//      parameters[3].put("alpha_", 8.0);	  
//      parameters[4].put("alpha_", 10.0);	 
//      parameters[5].put("alpha_", 12.0);	 
//      parameters[6].put("alpha_", 14.0);	 
//      parameters[7].put("alpha_", 16.0);	 
//      parameters[8].put("alpha_", 18.0);	 
//      parameters[9].put("alpha_", 20.0);	 
      
 
	    parameters[0].put("alpha_", 1.0);	 
	    parameters[1].put("alpha_", 5.0);
	    parameters[2].put("alpha_", 10.0);
	    parameters[3].put("alpha_", 15.0);
	    parameters[4].put("alpha_", 20.0);
	    parameters[5].put("alpha_", 25.0);
	    parameters[6].put("alpha_", 30.0);    
	    parameters[7].put("alpha_", 35.0);
	    parameters[8].put("alpha_", 40.0);
	    parameters[9].put("alpha_", 45.0);
	    parameters[10].put("alpha_", 50.0);
      
	    for (int i = 0; i < numberOfAlgorithms; i++) { 
	    	algorithm[i] = new MOEAD_ToM_DAS_Settings(problemName,problemParams).configure(parameters[i]);	
//	    	algorithm[i] = new NSGAII_ToM_DAS_Settings(problemName,problemParams).configure(parameters[i]);	
	     } // for 
	      
	  
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(CMOEAStudyDASR1Parameter.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(CMOEAStudyDASR1Parameter.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(CMOEAStudyDASR1Parameter.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    CMOEAStudyDASR1Parameter exp = new CMOEAStudyDASR1Parameter();

    exp.experimentName_ = "CMOEAStudyDASParameter"; 
    
	exp.noOfObjectives_ = 3;  
    exp.algorithmNameList_ = new String[]{	    	

//    		"MOEADToMAlpha2.0",
//    		"MOEADToMAlpha4.0",
//    		"MOEADToMAlpha6.0",
//    		"MOEADToMAlpha8.0",
//    		"MOEADToMAlpha10.0",
//    		"MOEADToMAlpha12.0",
//    		"MOEADToMAlpha14.0",
//    		"MOEADToMAlpha16.0",
//      		"MOEADToMAlpha18.0",
//    		"MOEADToMAlpha20.0",
    		
    		"MOEADToMAlpha1.0",
    		"MOEADToMAlpha5.0",
    		"MOEADToMAlpha10.0",
    		"MOEADToMAlpha15.0",
    		"MOEADToMAlpha20.0",    		
    		"MOEADToMAlpha25.0",    
    		"MOEADToMAlpha30.0",
    		"MOEADToMAlpha35.0",
    		"MOEADToMAlpha40.0",
    		"MOEADToMAlpha45.0",
    		"MOEADToMAlpha50.0",
 		
    		
//    		"NSGAIIToMAlpha2.0",
//    		"NSGAIIToMAlpha4.0",
//    		"NSGAIIToMAlpha6.0",
//    		"NSGAIIToMAlpha8.0",
//    		"NSGAIIToMAlpha10.0",
//    		"NSGAIIToMAlpha12.0",
//    		"NSGAIIToMAlpha14.0",
//    		"NSGAIIToMAlpha16.0",
//      		"NSGAIIToMAlpha18.0",
//    		"NSGAIIToMAlpha20.0",
    		
//    		"NSGAIIToMAlpha1.0",
//    		"NSGAIIToMAlpha5.0",
//    		"NSGAIIToMAlpha10.0",
//    		"NSGAIIToMAlpha15.0",
//    		"NSGAIIToMAlpha20.0",
//    		"NSGAIIToMAlpha25.0",
//    		"NSGAIIToMAlpha30.0",
//    		"NSGAIIToMAlpha35.0",
//    		"NSGAIIToMAlpha40.0",
//    		"NSGAIIToMAlpha45.0",
//    		"NSGAIIToMAlpha50.0",
    		};
    
    String[] problemStrings = {
//    		"DASCMOP1", 
//    		"DASCMOP2", 
//    		"DASCMOP3",
//    		"DASCMOP4", 
//    		"DASCMOP5",
//    		"DASCMOP6",
    		"DASCMOP7", //m=3
    		"DASCMOP8", //m=3
//    		"DASCMOP9"  //m=3
////    		
    		// many-objective
////    		"DASCMaOP1", 
//    		"DASCMaOP2",
////    		"DASCMaOP3",
//    		 "DASCMaOP4",//
////    		 "DASCMaOP5", 
//    		"DASCMaOP6", 
////    		"DASCMaOP7", 
//    		"DASCMaOP8",
////    		 "DASCMaOP9", 
    		};

    int numberOfDif_level = 16;
    
    if (exp.noOfObjectives_ > 3)
    	numberOfDif_level = 12;
    
    numberOfDif_level = 3;    
    
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

    exp.independentRuns_ = 30;    

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    
//    ClearMetricDuringRun cmdr = new ClearMetricDuringRun(exp.experimentName_,exp.algorithmNameList_,exp.problemList_,exp.noOfObjectives_);      
//    exp.runExperiment(numberOfThreads = 1) ;
//    exp.generateQualityIndicators() ;

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
} // AdMOEAStudy


