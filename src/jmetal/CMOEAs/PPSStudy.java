/** PPSStudy.java
 * Created 2019.6.20
 */

package jmetal.CMOEAs;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;


public class PPSStudy extends Experiment {

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
	  
      problemParams =  new Object[] {"Real"}; // 
		  	  
//	  algorithm[0] = new MOEAD_CDP_Settings(problemName,problemParams).configure(parameters[0]);
//	  algorithm[1] = new NSGAII_CDP_Settings(problemName,problemParams).configure(parameters[1]);  
//	  algorithm[2] = new AdMOEAD_Settings(problemName,problemParams).configure(parameters[2]);
	  
	  algorithm[0] = new PPS_MOEAD_Settings(problemName,problemParams).configure(parameters[0]);
//	  algorithm[1] = new CTsMOEAD_Settings(problemName,problemParams).configure(parameters[1]);
	  
	  //algorithm[0] = new CTsMOEAD_Settings(problemName,problemParams).configure(parameters[0]);

	  
	  
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(PPSStudy.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(PPSStudy.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(PPSStudy.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    PPSStudy exp = new PPSStudy();

    exp.experimentName_ = "PPSStudy"; 
    
	exp.noOfObjectives_ = 2;  
    exp.algorithmNameList_ = new String[]{	
    		"PPSMOEAD",    		
 
//    		"CTsMOEADRelease", 
//    		"CTsMOEADReleaseNewarchive", 
    		"CTsMOEADReleaseEps", 
//    		"PPSMOEADDE",    		
//    		"AdMOEADPBIDE", 
//    		"AdMOEADTCHE2DE", 
//    		"AdMOEADTCHE1DE", 
//    		"AdMOEADPBIDE", 
//    		"AdMOEADPBIDELargeNeig", 
    		};
    
    exp.problemList_ =  new String[]  {
    		"LIRCMOP1","LIRCMOP2","LIRCMOP3","LIRCMOP4",
    		"LIRCMOP5","LIRCMOP6","LIRCMOP7",
    		"LIRCMOP8","LIRCMOP9","LIRCMOP10",
    		"LIRCMOP11","LIRCMOP12",
    		// 3-objective
//    		"LIRCMOP13","LIRCMOP14",
    		};
     

    exp.paretoFrontFile_ = new String[exp.problemList_.length]; 
    
    for (int i = 0; i < exp.problemList_.length; i++) {      
    	
        exp.paretoFrontFile_[i] = exp.problemList_[i]  + ".pf";         
        
    } // for i
      
   
    exp.indicatorList_ = new String[]{
    		"HV",
//    		"HV2",
//    		"GSPREAD", 
//    		"DCI",
//    		"EPSILON",
    	 	"IGD",
//    	 	"IGDNS",
//    		"PD",
    		"GD",
    		"IGD+",
    		"RUNTIME",

    		};
    
    int numberOfAlgorithms = exp.algorithmNameList_.length;    
      
    exp.experimentBaseDirectory_ = "./jmetalExperiment/" + exp.experimentName_ + "/M=" +  exp.noOfObjectives_ ;
    exp.paretoFrontDirectory_ = "./paretoFronts/";
//    exp.paretoFrontDirectory_ = "./paretoFronts";
    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 2;    

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    
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
    test.executeTest("IGD+");
//    test.executeTest("RUNTIME");
  } // main
} // AdMOEAStudy


