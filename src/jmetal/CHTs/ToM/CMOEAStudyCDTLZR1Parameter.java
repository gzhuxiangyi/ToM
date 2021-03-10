//  CMOEAStudyCDTLZ.java
//  
//  Author:
//       Yi Xiang <gzhuxiang_yi@163.com>


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

/**
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class CMOEAStudyCDTLZR1Parameter extends Experiment {

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
        for (int i = 0; i < numberOfAlgorithms; i++)
          parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
        } // if
   
	  Object[] problemParams  =  new Object[] {"Real"};		
	 
	  if (problemName.equalsIgnoreCase("C1DTLZ1") || problemName.equalsIgnoreCase("C3DTLZ1") 
		  || problemName.equalsIgnoreCase("C3DTLZ4") || problemName.equalsIgnoreCase("DC1DTLZ1")
		  || problemName.equalsIgnoreCase("DC2DTLZ1")|| problemName.equalsIgnoreCase("DC3DTLZ1")) {
		  
		  problemParams =  new Object[] {"Real", 4 + noOfObjectives_, noOfObjectives_}; // 
		  
	  } else if (problemName.equalsIgnoreCase("C1DTLZ3") || problemName.equalsIgnoreCase("C2DTLZ2") 
			     || problemName.equalsIgnoreCase("C2DTLZ2Convex") || problemName.equalsIgnoreCase("DC1DTLZ3") 
			     || problemName.equalsIgnoreCase("DC2DTLZ3") || problemName.equalsIgnoreCase("DC3DTLZ3")) {
		  
		  problemParams =  new Object[] {"Real", 9 + noOfObjectives_, noOfObjectives_}; // 
		  
	  } else {
		  problemParams =  new Object[] {"Real"}; // 
	  }
	  
//      parameters[0].put("alpha_", 2.0);
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
//	    	algorithm[i] = new MOEAD_ToM_CDTLZ_Settings(problemName,problemParams).configure(parameters[i]);	
	    	algorithm[i] = new NSGAII_ToM_CDTLZ_Settings(problemName,problemParams).configure(parameters[i]);	
	     } // for 
	    
      
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(CMOEAStudyCDTLZR1Parameter.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(CMOEAStudyCDTLZR1Parameter.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(CMOEAStudyCDTLZR1Parameter.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    CMOEAStudyCDTLZR1Parameter exp = new CMOEAStudyCDTLZR1Parameter();

    exp.experimentName_ = "CMOEAStudyCDTLZParameter";
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
    		
    };// The third three algorithms
    
    exp.problemList_ = new String[]{
//           "C1DTLZ1", 
//           "C1DTLZ3", 
//           "C2DTLZ2", 
//           "C3DTLZ1",
//           "C3DTLZ4",
////           "C2DTLZ2Convex",
//           "DC1DTLZ1", 
//           "DC1DTLZ3",
//           "DC2DTLZ1", 
           "DC2DTLZ3",
//           "DC3DTLZ1", 
           "DC3DTLZ3",
    };
    
    // PF files
    exp.paretoFrontFile_ = new String[exp.problemList_.length]; 
    
    for (int i = 0; i < exp.problemList_.length; i++) {      
        exp.paretoFrontFile_[i] = exp.problemList_[i] + ".M" + exp.noOfObjectives_ +".pf";                   
    } // for i
    
   
    exp.indicatorList_ = new String[]{
//    		"GD",
//    		"HV",
//    		"GSPREAD",
//    		"EPSILON",
    		"IGD+",
//    		"IGD",
//    		"RUNTIME",
//    		"FS",
    		};
    
    exp.experimentBaseDirectory_ = "./jmetalExperiment/" + exp.experimentName_ + "/M=" +  exp.noOfObjectives_ ;    
        
    exp.paretoFrontDirectory_ = "./paretoFronts/Constraint/";

    exp.algorithmSettings_ = new Settings[exp.algorithmNameList_.length];
            
    exp.independentRuns_ = 30;

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
//    ClearMetricDuringRun cmdr = new ClearMetricDuringRun(exp.experimentName_,exp.algorithmNameList_
//    													,exp.problemList_,exp.noOfObjectives_);  
//    exp.runExperiment(numberOfThreads = 1) ;
//    exp.generateQualityIndicators() ;

    // Generate latex tables
    exp.generateLatexTables(false) ;  
//    exp.generateLatexTables(true) ;  

    // Applying Friedman test
    Friedman test = new Friedman(exp);
//    test.executeTest("EPSILON");
//    test.executeTest("HV");
//    test.executeTest("GSPREAD");
//    test.executeTest("GD");
//    test.executeTest("IGD");
//    test.executeTest("RUNTIME");
  } // main
} // CVaEAStudy


