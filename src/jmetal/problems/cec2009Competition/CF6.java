//  CF6.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems.cec2009Competition;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;


/**
 * Class representing problem CF6
 */
public class CF6 extends Problem {
	/**
	 * Creates an CF6 problem (10 variables and 2 objectives)
	 * 
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF6(String solutionType) throws ClassNotFoundException {
		this(solutionType, 10, 2);
	} // CF6

	/**
	 * Creates an CF6 problem instance
	 * 
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF6(String solutionType, Integer numberOfVariables,
			Integer numberOfObjectives) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 2;
		problemName_ = "CF6";

		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		for (int var = 0; var < numberOfVariables; var++) {
			lowerLimit_[var] = -2.0;
			upperLimit_[var] = 2.0;
		} // for
		lowerLimit_[0] = 0.0;
		upperLimit_[0] = 1.0;

		if (solutionType.compareTo("BinaryReal") == 0)
			solutionType_ = new BinaryRealSolutionType(this);
		else if (solutionType.compareTo("Real") == 0)
			solutionType_ = new RealSolutionType(this);
		else {
			System.out.println("Error: solution type " + solutionType
					+ " invalid");
			System.exit(-1);
		}
	}

	/**
	 * Evaluates a solution
	 * 
	 * @param solution
	 *            The solution to evaluate
	 * @throws JMException
	 */

	public void evaluate(Solution solution) throws JMException {

		double sum1, sum2, yj;
		int j;
        sum1 = sum2 = 0.0;
        int nx = numberOfVariables_;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		for (j = 2; j <= nx; j++) {
			if (j % 2 == 1) {
				yj    = x[j-1] - 0.8*x[0]*Math.cos(6.0*Math.PI*x[0] + j*Math.PI/nx);
				sum1 += 2.0*yj*yj - Math.cos(4.0*Math.PI*yj) + 1.0;
				
			} 
			else {
				yj    = x[j-1] - 0.8*x[0]*Math.sin(6.0*Math.PI*x[0] + j*Math.PI/nx);
				sum2  += yj*yj;
			}
		}
		f[0] = x[0]		  + sum1;
		f[1] = (1.0 - x[0])*(1.0 - x[0]) + sum2;

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);
	} // evaluate

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		
		Variable[] gen = solution.getDecisionVariables();
		double x0 = gen[0].getValue();
		double x1 = gen[1].getValue();
		double x3 = gen[3].getValue();
        int nx = numberOfVariables_;
        double [] cons = new double[2]; 
		cons[0] = x1 - 0.8*x0*Math.sin(6.0*x0*Math.PI+2.0*Math.PI/nx) 
				- Math.signum((x0-0.5)*(1.0-x0))*Math.sqrt(Math.abs((x0-0.5)*(1.0-x0)));
		cons[1] = x3 - 0.8*x0*Math.sin(6.0*x0*Math.PI+4.0*Math.PI/nx)
				- Math.signum(0.25*Math.sqrt(1-x0)-0.5*(1.0-x0))*Math.sqrt(Math.abs(0.25*Math.sqrt(1-x0)-0.5*(1.0-x0)));

		double total = 0.0;
	     int number = 0;
	     for(int i = 0 ; i < 2; i++){
	         if (cons[i] < 0.0){
	             total+=cons[i] ;
	             number++;
	           }
	     }

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);
	} // evaluateConstraints

}
