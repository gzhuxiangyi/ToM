//  CF2.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems.cec2009Competition;

import jmetal.core.*;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem CF2
 */
public class CF2 extends Problem {
	/**
	 * Creates an CF2 problem (10 variables and 2 objectives)
	 * 
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF2(String solutionType) throws ClassNotFoundException {
		this(solutionType, 10, 2);
	} // CF2

	/**
	 * Creates an CF2 problem instance
	 * 
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public CF2(String solutionType, Integer numberOfVariables,
			Integer numberOfObjectives) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 1;
		problemName_ = "CF2";

		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		for (int var = 0; var < numberOfVariables; var++) {
			lowerLimit_[var] = -1.0;
			upperLimit_[var] = 1.0;
		} // for
		lowerLimit_[0] = 0;
		

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
		int j, count1, count2;
        sum1 = sum2 = 0.0;
		count1 = count2 = 0;
        int nx = numberOfVariables_;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		for (j = 2; j <= nx; j++) {
			
			if (j % 2 == 1) {
				yj = x[j-1] - Math.sin(6.0 * Math.PI * x[0] + j * Math.PI / nx);
				sum1 += yj * yj;
				count1++;
			} else {
				yj = x[j-1] - Math.cos(6.0 * Math.PI * x[0] + j * Math.PI / nx);
				sum2 += yj * yj;
				count2++;
			}
		}
		f[0] = x[0] + 2.0 * sum1 / (double) count1;
		f[1] = 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / (double) count2;

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
		
		double f0 = solution.getObjective(0);
		double f1 = solution.getObjective(1);
		double N = 2.0,a = 1.0;
		
		double t = f1 + Math.sqrt(f0) - a * Math.sin(N * Math.PI * (Math.sqrt(f0) - f1 + 1)) - 1;
		double constraint = t / (1 + Math.exp(4.0 * Math.abs(t)));
		
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
