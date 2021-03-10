//  CF4.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems.cec2009Competition;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;


/**
 * Class representing problem CF4
 */
public class CF4 extends Problem {
	/**
	 * Creates an CF4 problem (10 variables and 2 objectives)
	 * 
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF4(String solutionType) throws ClassNotFoundException {
		this(solutionType, 10, 2);
	} // CF4

	/**
	 * Creates an CF4 problem instance
	 * 
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF4(String solutionType, Integer numberOfVariables,
			Integer numberOfObjectives) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 1;
		problemName_ = "CF4";

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
			yj = x[j-1] - Math.sin(6.0*Math.PI*x[0] + j*Math.PI/nx);
			if (j % 2 == 1) {
				sum1 += yj * yj;
				
			} 
			else {
				if (j==2)
					sum2 += yj < 1.5-0.75*Math.sqrt(2.0) ? Math.abs(yj) : (0.125+(yj-1)*(yj-1));
				else
					sum2  += yj*yj;
			}
		}
		f[0] = x[0]		  + sum1;
		f[1] = 1.0 - x[0] + sum2;

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
        int nx = numberOfVariables_;
    	double t = x1 - Math.sin(6.0*x0*Math.PI+2.0*Math.PI/nx) - 0.5*x0 + 0.25;
		double constraint = t / (1+Math.exp(4.0*Math.abs(t)));
        double total = 0.0;
		int number = 0;
		if (constraint < 0.0) {
			total += constraint;
			number++;
		}

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);
	} // evaluateConstraints

}
