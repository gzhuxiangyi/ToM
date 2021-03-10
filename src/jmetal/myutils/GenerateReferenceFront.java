/** GenerateReferenceFront.java
 * This is the class for generating a set of reference front for given problems and algorithms
 * Coded by Yi Xiang gzhuxiang_yi@163.com
 */

package jmetal.myutils;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.MOEADD_Settings;
import jmetal.experiments.settings.MOEAD_Settings;
import jmetal.experiments.settings.NSGAIII_Settings;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;


public class GenerateReferenceFront extends Experiment {
 

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    GenerateReferenceFront exp = new GenerateReferenceFront();

    exp.experimentName_ = "MaPSOStudy";
    
	exp.noOfObjectives_ = 15;  
	exp.independentRuns_ = 20;
	
    exp.algorithmNameList_ = new String[]{	 
  
    		"MaPSOpaper", // write into pater 
    		"NSGAIII", 
    		"VaEABest", 
    		"KnEA",	
    		"MOEAD",
    		"dMOPSO",
    		"hmopso",
    		
    		/**
    		 * Used in PAEAStudy
    		 */
//    		"PAEAFinal", 
////    		"PAEA",
//    		
//    		"MOEADD",
//    		"MOEADDrw", // Use random weight vectors
//    		"NSGAIII", 
//    		"NSGAIIIrw", 
//    		"MOEAD",
//    		"MOEADrw",
//    		"MOMBI2",   
//    		"GrEA",
    };// 
    exp.problemList_ = new String[]{
			"WFG1",
			"WFG2",
			"WFG3",					
    		};
    
    int numberOfAlgorithms = exp.algorithmNameList_.length;    
      
    exp.experimentBaseDirectory_ = "./jmetalExperiment/" +
                                   exp.experimentName_ + "/M=" +  exp.noOfObjectives_ ;
    
    exp.initExperiment();
    
    exp.generateReferenceFronts();
    
    System.out.println("Done!");

  } // main

	@Override
	public void algorithmSettings(String problemName, int problemId,
			Algorithm[] algorithm) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}
} // PAEAStudy


