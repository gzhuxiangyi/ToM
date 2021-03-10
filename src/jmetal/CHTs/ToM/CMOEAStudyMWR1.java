/* CMOEAStudyMWR1.java
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


public class CMOEAStudyMWR1 extends Experiment {

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
	  	 
	  // Within MOEA/D framework
	  algorithm[0] = new MOEAD_ToM_MW_Settings(problemName,problemParams).configure(parameters[0]);		  
	  algorithm[1] = new MOEAD_CDP_MW_Settings(problemName,problemParams).configure(parameters[1]);	
	  algorithm[2] = new MOEAD_SP_MW_Settings(problemName,problemParams).configure(parameters[2]);	
	  algorithm[3] = new MOEAD_SR_MW_Settings(problemName,problemParams).configure(parameters[3]);	
	  algorithm[4] = new MOEAD_Epsilon_MW_Settings(problemName,problemParams).configure(parameters[4]);	
	  
	// Within NSGA-II framework
//	  algorithm[0] = new NSGAII_ToM_MW_Settings(problemName,problemParams).configure(parameters[0]);		  
//	  algorithm[1] = new NSGAII_CDP_MW_Settings(problemName,problemParams).configure(parameters[1]);	
//	  algorithm[2] = new NSGAII_SP_MW_Settings(problemName,problemParams).configure(parameters[2]);	
//	  algorithm[3] = new NSGAII_SR_MW_Settings(problemName,problemParams).configure(parameters[3]);	
//	  algorithm[4] = new NSGAII_Epsilon_MW_Settings(problemName,problemParams).configure(parameters[4]);	
	  
	  // Comprehensive comparisons
//	  algorithm[0] = new MOEAD_CDP_Settings(problemName,problemParams).configure(parameters[0]);
//	  algorithm[1] = new NSGAII_CDP_Settings(problemName,problemParams).configure(parameters[1]);   
//	  algorithm[2] = new PPS_MOEAD_Settings(problemName,problemParams).configure(parameters[2]); 
//	  algorithm[3] = new CNSGAIII_Settings(problemName,problemParams).configure(parameters[3]); 
//	  algorithm[0] = new CMOEADD_Settings(problemName,problemParams).configure(parameters[0]);	  
 
	  
      } catch (IllegalArgumentException ex) {
      Logger.getLogger(CMOEAStudyMWR1.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(CMOEAStudyMWR1.class.getName()).log(Level.SEVERE, null, ex);
    } catch  (JMException ex) {
      Logger.getLogger(CMOEAStudyMWR1.class.getName()).log(Level.SEVERE, null, ex);
    }
  } // algorithmSettings

  /**
   * Main method
   * @param args
   * @throws JMException
   * @throws IOException
   */
  public static void main(String[] args) throws JMException, IOException {
    CMOEAStudyMWR1 exp = new CMOEAStudyMWR1();

    exp.experimentName_ = "CMOEAStudyMW"; 
    
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
//    		"CTAEA",  

    };
    
    exp.problemList_ = new String[] {
    		"MW1", 
    		"MW2", 
    		"MW3",
    		"MW5",
    		"MW6",
    		"MW7",
    		"MW9",
    		"MW10",
    		"MW11",
    		"MW12",
    		"MW13",
//    		
//    		// Many-objective optimization
//    		"MW4",
//    		"MW8",
//    		"MW14",
    };

    exp.paretoFrontFile_ = new String[] {
    		"MW1.dat", 
    		"MW2.dat", 
    		"MW3.dat", 
      		"MW5.dat", 
    		"MW6.dat", 
    		"MW7.dat",
    		"MW9.dat",
    		"MW10.dat",
    		"MW11.dat",
    		"MW12.dat",
    		"MW13.dat",
    		
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

    exp.independentRuns_ = 2;    

    exp.initExperiment();

    // Run the experiments
    int numberOfThreads ;
    
//    ClearMetricDuringRun cmdr = new ClearMetricDuringRun(exp.experimentName_,exp.algorithmNameList_
//			,exp.problemList_,exp.noOfObjectives_);      
    exp.runExperiment(numberOfThreads = 1) ;
    exp.generateQualityIndicators() ;
    
    // Different from generateQualityIndicators because multiple runs should be handled 
    // The parameter specifies how many indenpendent runs     
//    exp.generateQualityIndicatorsForConvergence(1); 
    

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
} // 


