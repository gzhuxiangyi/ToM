/** CMOEAStudy.java
 * Created 2019.6.20
 */

package jmetal.problems.CEC20;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;


public class CEC2020Study extends Experiment {

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
	  
       algorithm[0] = new CTsMOEAD_Settings(problemName,problemParams).configure(parameters[0]);	
	  
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(CEC2020Study.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(CEC2020Study.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(CEC2020Study.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    CEC2020Study exp = new CEC2020Study();

    exp.experimentName_ = "CEC2020Study"; 
    
	exp.noOfObjectives_ = 2;  
    exp.algorithmNameList_ = new String[]{	
    		"CTsMOEAR1Release", // CTsMOEADR1Release.java with updateArchive,write into paper	
//    		"CTsMOEAR1ReleaseCDP", // Consider always constraints, handled by CDP 

    };
    
    exp.problemList_ = new String[] {
//    		"CMOP1", 
//    		"CMOP2",  
//    		"CMOP3", 
//    		"CMOP4", 
//    		"CMOP5",  
//    		"CMOP6", 
    		"CMOP7", 
    		"CMOP8",  
    		"CMOP9", 
    		"CMOP10", 
    };

    exp.paretoFrontFile_ = new String[] {
//    		"PF1.dat", 
//    		"PF2.dat", 
//    		"PF3.dat", 
//      		"PF4.dat", 
//    		"PF5.dat", 
//    		"PF6.dat", 
    		"PF7.dat",
     		"PF8.dat",
    		"PF9.dat",
    		"PF10.dat",
    };
    
   
    exp.indicatorList_ = new String[]{
//    		"HV",
//    		"HV2",
//    		"GSPREAD", 
//    		"SPREAD", 
//    		"DCI",
//    		"EPSILON",
    	 	"IGD",
//    	 	"IGDNS",
//    		"PD",
//    		"GD",
//    		"IGD+",
//    		"RUNTIME",

    		};
    
    int numberOfAlgorithms = exp.algorithmNameList_.length;    

    exp.experimentBaseDirectory_ = "./jmetalExperiment/" + exp.experimentName_;
   
    exp.paretoFrontDirectory_ = "./paretoFronts/CEC20";

    exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

    exp.independentRuns_ = 30;    

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    
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
} // AdMOEAStudy


