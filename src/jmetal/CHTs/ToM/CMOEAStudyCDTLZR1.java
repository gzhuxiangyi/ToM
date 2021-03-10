/* CMOEAStudyCDTLZR1.java
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

/**
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class CMOEAStudyCDTLZR1 extends Experiment {

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
	  	  
	  // Within MOEA/D framework
	  algorithm[0] = new MOEAD_ToM_CDTLZ_Settings(problemName,problemParams).configure(parameters[0]);		  
	  algorithm[1] = new MOEAD_CDP_CDTLZ_Settings(problemName,problemParams).configure(parameters[1]);	
	  algorithm[2] = new MOEAD_SP_CDTLZ_Settings(problemName,problemParams).configure(parameters[2]);	
	  algorithm[3] = new MOEAD_SR_CDTLZ_Settings(problemName,problemParams).configure(parameters[3]);	
	  algorithm[4] = new MOEAD_Epsilon_CDTLZ_Settings(problemName,problemParams).configure(parameters[4]);	
	  
	  // Within NSGA-II framework
//	  algorithm[0] = new NSGAII_ToM_CDTLZ_Settings(problemName,problemParams).configure(parameters[0]);	
//	  algorithm[1] = new NSGAII_CDP_CDTLZ_Settings(problemName,problemParams).configure(parameters[1]);
//	  algorithm[2] = new NSGAII_SP_CDTLZ_Settings(problemName,problemParams).configure(parameters[2]);   
//	  algorithm[3] = new NSGAII_SR_CDTLZ_Settings(problemName,problemParams).configure(parameters[3]);
//	  algorithm[4] = new NSGAII_Epsilon_CDTLZ_Settings(problemName,problemParams).configure(parameters[4]);	
	  
	// Comprehensive comparisons
//    algorithm[1] = new CNSGAIII_CDTLZ_Settings(problemName,problemParams).configure(parameters[1]); 
//    algorithm[2] = new CMOEADD_CDTLZ_Settings(problemName,problemParams).configure(parameters[2]);      
//	  algorithm[3] = new PPS_MOEAD_CDTLZ_Settings(problemName,problemParams).configure(parameters[3]); 	  
//	  algorithm[0] = new MOEAD_CDP_CDTLZ_Settings(problemName,problemParams).configure(parameters[0]);	  
//	  algorithm[0] = new NSGAII_CDP_CDTLZ_Settings(problemName,problemParams).configure(parameters[0]);   
            
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(CMOEAStudyCDTLZR1.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(CMOEAStudyCDTLZR1.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(CMOEAStudyCDTLZR1.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    CMOEAStudyCDTLZR1 exp = new CMOEAStudyCDTLZR1();

    exp.experimentName_ = "CMOEAStudyCDTLZ";
//  exp.experimentName_ = "CMOEAStudyCDTLZConvergence";
    exp.noOfObjectives_ = 2; 
    
    exp.algorithmNameList_ = new String[]{	    		
     		"MOEADToM",  
    		"MOEADCDP",
    		"MOEADSP",
    		"MOEADSR",
    		"MOEADEpsilon",   	

    };
    
    exp.problemList_ = new String[]{
           "DC1DTLZ1", 
           "DC1DTLZ3",
           "DC2DTLZ1", 
           "DC2DTLZ3",
           "DC3DTLZ1", 
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
            
    exp.independentRuns_ = 2;

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    
//    ClearMetricDuringRun cmdr = new ClearMetricDuringRun(exp.experimentName_,exp.algorithmNameList_
//    													,exp.problemList_,exp.noOfObjectives_);  
    exp.runExperiment(numberOfThreads = 1) ;
    exp.generateQualityIndicators() ;

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
} 


