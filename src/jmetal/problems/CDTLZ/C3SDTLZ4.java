//  C3SDTLZ4.java
//
//  Author:
//      Yi Xiang <gzhuxiang_yi@163.com>


package jmetal.problems.CDTLZ;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem C3SDTLZ4
 */
public class C3SDTLZ4 extends Problem{
   
  
 /**
  * Creates a default C3SDTLZ4 problem (12 variables and 3 objectives) 
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C3SDTLZ4(String solutionType) throws ClassNotFoundException {
    this(solutionType, 12, 3);//3 objectives 
//  this(solutionType, 13, 4);//4 objectives 
//  this(solutionType, 14, 5);//5 objectives 
//  this(solutionType, 15, 6);//6 objectives 
//  this(solutionType, 17, 8);//8 objectives 
//  this(solutionType, 19, 10);//10 objectives 
//  this(solutionType, 24, 15);//15 objectives  
  }
    
 /**
  * Creates a C3SDTLZ4 problem problem instance 
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C3SDTLZ4(String  solutionType,
               Integer numberOfVariables,
               Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= numberOfObjectives;
    problemName_        = "C3SDTLZ4";
        
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
  } //C3SDTLZ4         

  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */      
  public void evaluate(Solution solution) throws JMException {
    Variable[] gen  = solution.getDecisionVariables();
  
    double [] x = new double[numberOfVariables_];
    double [] f = new double[numberOfObjectives_];
    double alpha = 100.0;
    int k = numberOfVariables_ - numberOfObjectives_ + 1;
  
    for (int i = 0; i < numberOfVariables_; i++)
      x[i] = gen[i].getValue();

    double g = 0.0;
    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
      g += (x[i] - 0.5)*(x[i] - 0.5);                
        
    for (int i = 0; i < numberOfObjectives_; i++)
      f[i] = 1.0 + g;
        
    for (int i = 0; i < numberOfObjectives_; i++) {
      for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)            
        f[i] *= java.lang.Math.cos(java.lang.Math.pow(x[j],alpha)*(java.lang.Math.PI/2.0));                
        if (i != 0){
          int aux = numberOfObjectives_ - (i + 1);
          f[i] *= java.lang.Math.sin(java.lang.Math.pow(x[aux],alpha)*(java.lang.Math.PI/2.0));
        } //if
    } // for
        
    double factor = getFactor(numberOfObjectives_);
	for (int i = 0; i < numberOfObjectives_; i++)
		solution.setObjective(i, f[i] * Math.pow(factor, i));           
  } // evaluate 
  
  /**
   * Get scalar factor
   * @param obj
   * @return
   */
	double getFactor(int obj){
		if (obj == 3)
			return 10;
		else if (obj == 5)
			return 10;
		else if (obj == 8)
			return 3;
		else if (obj == 10)
			return 3;
		else if (obj == 15)
			return 2;
		else 
			return 1;
		
	}
  /** 
   * Evaluates the constraint overhead of a solution 
   * @param solution The solution
   * @throws JMException 
   */  
  public void evaluateConstraints(Solution solution) throws JMException {
    double [] constraint = new double[numberOfConstraints_]; // M constraint
    double [] f          = new double[numberOfObjectives_]; // M objectives    
    double factor = getFactor(numberOfObjectives_);  
    
    for (int i=0;i<numberOfObjectives_;i++){
    	f[i] =  solution.getObjective(i);
    	f[i] = f[i]/Math.pow(factor, i);
    }
    
    double sum;
   
    for (int j = 0;j< numberOfConstraints_; j++){ // For each constraint
    	 
    	 sum = 0.0;
    	 
    	 for (int i = 0; i< numberOfObjectives_;i++){
    		 
    		 if(i!=j){
    			 sum = sum + f[i] * f[i];
    		 }// if    		 
    		 
    	 }// for i
    	 
    	 constraint[j] =  f[j]*f[j]/4 + sum  - 1 ;
    }// for j
    
    
    solution.setNumberOfConstraints(numberOfConstraints_);       
    
    for (int j = 0;j< numberOfConstraints_; j++){ // For each constraint
    	solution.setConstraint(j, constraint[j]);
    }
    
    
    double CV = 0.0;
    int number = 0;
    
    // The smaller the CV, the better the solution
    for (int i = 0; i < numberOfConstraints_; i++) {
      if (constraint[i] < 0.0){
        CV += (-constraint[i]);
        number ++;
      } // 
    } // for
        
    solution.setOverallConstraintViolation(CV);    
    solution.setNumberOfViolatedConstraint(number);        
  } // evaluateConstraints   
}//C3SDTLZ4

