//  C1DTLZ3.java
//
//  Author:
//       Yi Xiang


package jmetal.problems.CDTLZ;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem C1DTLZ3
 */
public class C1DTLZ3 extends Problem{
  
 /**
  * Creates a default C1DTLZ3 problem (12 variables and 3 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C1DTLZ3(String solutionType) throws ClassNotFoundException {
//    this(solutionType, 12, 3);//3 objectives 
//  this(solutionType, 13, 4);//4 objectives 
    this(solutionType, 14, 5);//5 objectives 
//  this(solutionType, 15, 6);//6 objectives 
//  this(solutionType, 17, 8);//8 objectives 
//  this(solutionType, 19, 10);//10 objectives 
//  this(solutionType, 24, 15);//15 objectives  
  } // C1DTLZ3
    
  /**
  * Creates a new C1DTLZ3 problem instance
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C1DTLZ3(String  solutionType,
               Integer numberOfVariables,
               Integer numberOfObjectives) {
    numberOfVariables_   = numberOfVariables;
    numberOfObjectives_  = numberOfObjectives;
    numberOfConstraints_ = 1;
    problemName_         = "C1DTLZ3";
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    for (int var = 0; var < numberOfVariables_; var++){
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.0;
    }
        
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
      g += (x[i] - 0.5)*(x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
        
    g = 100.0 * (k + g);
    for (int i = 0; i < numberOfObjectives_; i++)
      f[i] = 1.0 + g;
        
    for (int i = 0; i < numberOfObjectives_; i++){
      for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)            
        f[i] *= java.lang.Math.cos(x[j]*0.5*java.lang.Math.PI);                
        if (i != 0){
          int aux = numberOfObjectives_ - (i + 1);
          f[i] *= java.lang.Math.sin(x[aux]*0.5*java.lang.Math.PI);
        } // if
    } //for
        
    for (int i = 0; i < numberOfObjectives_; i++)
      solution.setObjective(i,f[i]);                
  } //evaluate
  
  /** 
   * Evaluates the constraint overhead of a solution 
   * @param solution The solution
   * @throws JMException 
   */  
  public void evaluateConstraints(Solution solution) throws JMException {
    double [] constraint = new double[numberOfConstraints_]; // 1 constraint
    double [] f          = new double[numberOfObjectives_]; // M objectives
    
    for (int i=0;i<numberOfObjectives_;i++){
    	f[i] =  solution.getObjective(i);
    }
    double r;
    
    switch(numberOfObjectives_) {
	    case 3: r = 9; break;
	    case 5: r = 12.5;break;
	    case 8: r = 12.5;break;
	    case 10: r = 15;break;
	    case 15: r = 15;break;
	    default : r = 9;  break;
    }
    
    double sum;
    sum = 0.0;
    
    for (int i=0;i < numberOfObjectives_ - 1; i++){
    	sum = sum + Math.pow(f[i], 2);
    }    
   
    
    constraint[0] =  (sum - 16) * (sum - Math.pow(r, 2))     ;
    
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
  
}// C1DTLZ3

