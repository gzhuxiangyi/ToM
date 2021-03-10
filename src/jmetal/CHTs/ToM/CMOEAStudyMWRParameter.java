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


public class CMOEAStudyMWRParameter extends Experiment {

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

      
      //Set maxEvaluations_
//       for (int i = 0; i < numberOfAlgorithms; i++){ 
//          parameters[i].put("maxEvaluations_", noOfFEs_); // Using this comment can uniform the maxEvaluations_
//       }
      
                
	  Object[] problemParams ;	
	  
	  if (this.noOfObjectives_ <= 3 ) { // 
		  problemParams =  new Object[] {"Real"}; // 
	  } else { 							//for many
		  problemParams =  new Object[] {"Real", 12 + noOfObjectives_, noOfObjectives_}; // 
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
	    parameters[2].put("alpha_", 15.0);
	    parameters[3].put("alpha_", 25.0);    
	    parameters[4].put("alpha_", 35.0);
	    parameters[5].put("alpha_", 45.0);
	    
	    
	    for (int i = 0; i < numberOfAlgorithms; i++) { 
//	    	algorithm[i] = new MOEAD_ToM_MW_Settings(problemName,problemParams).configure(parameters[i]);	
	    	algorithm[i] = new NSGAII_ToM_MW_Settings(problemName,problemParams).configure(parameters[i]);	
	     } // for 
	      
	  
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(CMOEAStudyMWRParameter.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(CMOEAStudyMWRParameter.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(CMOEAStudyMWRParameter.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    CMOEAStudyMWRParameter exp = new CMOEAStudyMWRParameter();

    exp.experimentName_ = "CMOEAStudyMWParameter"; 
    
	exp.noOfObjectives_ = 2;  
    exp.algorithmNameList_ = new String[]{	
    		// ---------------TC RR-------------------
//    		"MOEADToMAlpha2.0",
////    		"MOEADToMAlpha4.0",
////    		"MOEADToMAlpha6.0",
////    		"MOEADToMAlpha8.0",
//    		"MOEADToMAlpha10.0",
////    		"MOEADToMAlpha12.0",
////    		"MOEADToMAlpha14.0",
////    		"MOEADToMAlpha16.0",
////      		"MOEADToMAlpha18.0",
//    		"MOEADToMAlpha20.0",
    		
//    		"MOEADToMAlpha1.0",
//    		"MOEADToMAlpha5.0",
//    		"MOEADToMAlpha10.0",
//    		"MOEADToMAlpha15.0",
//    		"MOEADToMAlpha20.0",    		
//    		"MOEADToMAlpha25.0",    
//    		"MOEADToMAlpha30.0",
//    		"MOEADToMAlpha35.0",
//    		"MOEADToMAlpha40.0",
//    		"MOEADToMAlpha45.0",
//    		"MOEADToMAlpha50.0",

//    		"NSGAIIToMAlpha2.0",
//    		"NSGAIIToMAlpha4.0",
//    		"NSGAIIToMAlpha6.0",
//    		"NSGAIIToMAlpha8.0",

//    		"NSGAIIToMAlpha12.0",
//    		"NSGAIIToMAlpha14.0",
//    		"NSGAIIToMAlpha16.0",
//      		"NSGAIIToMAlpha18.0",
//    		"NSGAIIToMAlpha20.0",
    		
    		"NSGAIIToMAlpha1.0",
    		"NSGAIIToMAlpha5.0",
    		"NSGAIIToMAlpha10.0",
    		"NSGAIIToMAlpha15.0",
    		"NSGAIIToMAlpha20.0",
    		"NSGAIIToMAlpha25.0",
    		"NSGAIIToMAlpha30.0",
    		"NSGAIIToMAlpha35.0",
    		"NSGAIIToMAlpha40.0",
    		"NSGAIIToMAlpha45.0",
    		"NSGAIIToMAlpha50.0",
    		
    };
    
    exp.problemList_ = new String[] {
//    		"MW1", 
//    		"MW2", 
//    		"MW3",
//    		"MW5",
//    		"MW6",
//    		"MW7",
//    		"MW9",
//    		"MW10",
//    		"MW11",
    		"MW12",
//    		"MW13",
//    		
//    		// Many-objective optimization
//    		"MW4",
//    		"MW8",
//    		"MW14",
    };

    exp.paretoFrontFile_ = new String[] {
//    		"MW1.dat", 
//    		"MW2.dat", 
//    		"MW3.dat", 
//      		"MW5.dat", 
//    		"MW6.dat", 
//    		"MW7.dat",
//    		"MW9.dat",
//    		"MW10.dat",
//    		"MW11.dat",
    		"MW12.dat",
//    		"MW13.dat",
    		
    		// M=3
//    		"MW4.dat", 
//    		"MW8.dat", 
//    		"MW14.dat",
    		// For many-objective
//    		"MW4.M"  + exp.noOfObjectives_ + ".pf", 
//    		"MW8.M"  + exp.noOfObjectives_ + ".pf", 
//    		"MW14.M"  + exp.noOfObjectives_ + ".pf", 
    };
    
   
    exp.indicatorList_ = new String[]{
//    		"HV",
//    		"HV2",
//    		"GSPREAD", 
//    		"SPREAD", 
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

    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 30;    

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    
//    ClearMetricDuringRun cmdr = new ClearMetricDuringRun(exp.experimentName_,exp.algorithmNameList_
//			,exp.problemList_,exp.noOfObjectives_);  
//    
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


