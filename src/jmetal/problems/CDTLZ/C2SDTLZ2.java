//  C2SDTLZ2.java
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
 * Class representing problem C2SDTLZ2
 */
public class C2SDTLZ2 extends Problem{
       
 /** 
  * Creates a default C2SDTLZ2 problem (12 variables and 3 objectives)
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C2SDTLZ2(String solutionType) throws ClassNotFoundException {
    this(solutionType, 12, 3);//3 objectives 
//    this(solutionType, 13, 4);//4 objectives 
//    this(solutionType, 14, 5);//5 objectives 
//    this(solutionType, 15, 6);//6 objectives 
//    this(solutionType, 17, 8);//8 objectives 
//    this(solutionType, 19, 10);//10 objectives 
//    this(solutionType, 24, 15);//15 objectives  
  
  } // C2SDTLZ2

 /**
  * Creates a new instance of C2SDTLZ2 
  * @param numberOfVariables Number of variables
  * @param numberOfObjectives Number of objective functions
  * @param solutionType The solution type must "Real" or "BinaryReal". 
  */
  public C2SDTLZ2(String  solutionType,
               Integer numberOfVariables,
               Integer numberOfObjectives) {
    numberOfVariables_  = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_= 1;
    problemName_        = "C2SDTLZ2";
        
    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];        
    for (int var = 0; var < numberOfVariables_; var++){
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
  } //C2DTLZ2
        
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
      g += (x[i] - 0.5)*(x[i] - 0.5);
        
    for (int i = 0; i < numberOfObjectives_; i++)
      f[i] = 1.0 + g;
        
    for (int i = 0; i < numberOfObjectives_; i++){
      for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)            
        f[i] *= Math.cos(x[j]*0.5*Math.PI);                
        if (i != 0){
          int aux = numberOfObjectives_ - (i + 1);
          f[i] *= Math.sin(x[aux]*0.5*Math.PI);
        } //if 
    } // for
        
    double factor = getFactor(numberOfObjectives_);
	for (int i = 0; i < numberOfObjectives_; i++)
		solution.setObjective(i, f[i] * Math.pow(factor, i));
  }    //evaluate
  
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
    double [] constraint = new double[numberOfConstraints_]; // 1 constraint
    double [] f          = new double[numberOfObjectives_]; // M objectives    
    double factor = getFactor(numberOfObjectives_);  
    
    for (int i=0;i<numberOfObjectives_;i++){
    	f[i] =  solution.getObjective(i);
    	f[i] = f[i]/Math.pow(factor, i);
    }
    
    double r;
    
    switch(numberOfObjectives_) {
	    case 3:   r = 0.4;	   
	    default : r = 0.5;  
    }
    
    double sum,min1;
    
    min1 = Double.MAX_VALUE;
    
    
    for (int i=0;i < numberOfObjectives_ ; i++){
    	
    	sum = 0.0;
    	
    	for (int j=0;j < numberOfObjectives_;j++ ) {
    		
	    	if (j != i) {
	    		sum = sum + Math.pow(f[j], 2);
	    	}//if
	    	
    	}//for
    	
    	double t;
    	
    	t = (f[i] - 1)*(f[i] - 1) + sum - r * r;
    	
    	min1 = Math.min(min1, t);
    	
    }     
    
    double v  = 0.0;
    
    for (int i=0;i < numberOfObjectives_ ; i++){
    	v = v + Math.pow(f[i] - 1/Math.sqrt(numberOfObjectives_),2);
    }
    
    v = v - r * r;        
    
    constraint[0] =  - Math.min(min1, v)     ;
    
    solution.setNumberOfConstraints(numberOfConstraints_);   
    solution.setConstraint(0, constraint[0]);    
    
    
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
  
} //C2SDTLZ2
