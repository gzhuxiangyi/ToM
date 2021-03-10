//  DC1DTLZ1.java
//
//  Author:
//       Yi Xiang
//       


package jmetal.problems.CDTLZ;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/** 
 * Class representing problem DTLZ1 
 */
public class DC1DTLZ1 extends Problem {   
 /** 
  * Creates a default DC1DTLZ1 problem (7 variables and 3 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public DC1DTLZ1(String solutionType) throws ClassNotFoundException {
    this(solutionType, 7, 3);//3 objectives 
//    this(solutionType, 8, 4);//4 objectives 
//	  this(solutionType, 9, 5);//5 objectives 
//    this(solutionType, 10, 6);//6 objectives 
//    this(solutionType, 12, 8);//8 objectives 
//    this(solutionType, 14, 10);//10 objectives 
//    this(solutionType, 19, 15);//15 objectives 
  } // DC1DTLZ1   
    
  /** 
  * Creates a DC1DTLZ1 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public DC1DTLZ1(String solutionType, 
               Integer numberOfVariables, 
  		         Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 1;
    problemName_        = "DC1DTLZ1";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    for (int var = 0; var < numberOfVariables; var++){
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.0;
    } //for
        
    if (solutionType.compareTo("BinaryReal") == 0)
    	solutionType_ = new BinaryRealSolutionType(this) ;
    else if (solutionType.compareTo("Real") == 0)
    	solutionType_ = new RealSolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }            
  }            
 
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */    
  public void evaluate(Solution solution) throws JMException {
    Variable[] gen  = solution.getDecisionVariables();
                
    double [] x = new double[numberOfVariables_];
    double [] f = new double[numberOfObjectives_];
    int k = numberOfVariables_ - numberOfObjectives_ + 1;
        
    for (int i = 0; i < numberOfVariables_; i++)
      x[i] = gen[i].getValue();
        
    double g = 0.0;
    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
      g += (x[i] - 0.5)*(x[i] - 0.5) - Math.cos(20.0 * Math.PI * ( x[i] - 0.5));   
    
    g = 100.0 * (k + g);        
    for (int i = 0; i < numberOfObjectives_; i++)
      f[i] = (1.0 + g) * 0.5;
        
    for (int i = 0; i < numberOfObjectives_; i++){
      for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)            
        f[i] *= x[j];                
        if (i != 0){
          int aux = numberOfObjectives_ - (i + 1);
          f[i] *= (1 - x[aux]);
        } //if
    }//for
        
    for (int i = 0; i < numberOfObjectives_; i++){
      solution.setObjective(i,f[i]); 
    }
  } // evaluate   

  /** 
   * Evaluates the constraint overhead of a solution 
   * @param solution The solution
   * @throws JMException 
   */  
  public void evaluateConstraints(Solution solution) throws JMException {
    double [] constraint = new double[numberOfConstraints_]; // 1 constraint   
    double [] x = new double[numberOfVariables_];
    Variable[] gen  = solution.getDecisionVariables();
    
    for (int i = 0; i < numberOfVariables_; i++)
        x[i] = gen[i].getValue();
       
    double a  = 5;
    double b  = 0.95;
    double re = Math.cos (a * Math.PI * x[0]) - b;
        
    constraint[0] =  re ;
    
    solution.setNumberOfConstraints(numberOfConstraints_);   
    solution.setConstraint(0, constraint[0]);
            
    double CV = 0.0;
    int number = 0;
    
    // The smaller the CV, the better the solution
    for (int i = 0; i < numberOfConstraints_; i++) {
      if (constraint[i] < 0.0){
        CV += (constraint[i]);
        number ++;
      } // 
    } // for
        
    solution.setOverallConstraintViolation(CV);    
    solution.setNumberOfViolatedConstraint(number);        
  } // evaluateConstraints   
}//DC1DTLZ1

