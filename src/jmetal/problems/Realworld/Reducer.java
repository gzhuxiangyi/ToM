//  ReducerDesign.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems.Realworld;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem ReducerDesign
 */
public class Reducer extends Problem {
	/**
	 * Creates an ReducerDesign problem (30 variables and 2 objectives)
	 *
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public Reducer(String solutionType) throws ClassNotFoundException {
		this(solutionType, 7, 2);
	} // ReducerDesign

	/**
	 * Creates an ReducerDesign problem instance
	 *
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public Reducer(String solutionType, Integer numberOfVariables,
                   Integer numberOfObjectives) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 11;
		problemName_ = "ReducerDesign";

		lowerLimit_ = new double[]{2.6,0.7,17.0,7.3,7.3,2.9,5.0};
		upperLimit_ = new double[]{3.6,0.8,28.0,8.3,8.3,3.9,5.5};

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

		double sum1, sum2, sum3;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		x[2] = Math.round(x[2]);

		sum1 = 0.7854 * x[0] * x[1] * x[1] * (10.0 * x[2] * x[2] / 3 + 14.933 * x[2] - 43.0934);

		sum2 = 1.508 * x[0] * (x[5] * x[5] + x[6] * x[6]) - 7.477 * (Math.pow(x[5],3) + Math.pow(x[6],3));

		sum3 = 0.7854 * (x[3] * x[5] * x[5] + x[4] * x[6] * x[6]);


		f[0] = sum1 - sum2 + sum3;
		f[1] = Math.sqrt(Math.pow((745 * x[3] / (x[1] * x[2])),2) + 1.69 * 1e7) / (0.1 * Math.pow(x[5],3));

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);

		double[] constraint = new double[numberOfConstraints_];
		constraint[0] = 1.0 / 27 - 1.0 / (x[0] * x[1] * x[1] * x[2]);
		constraint[1] = 1.0 / 397.5 - 1.0 / (x[0] * x[1] * x[1] * x[2] * x[2]);
		constraint[2] = 1 / 1.93 - Math.pow(x[3],3) / (x[1] * x[2] * Math.pow(x[5],4));
		constraint[3] = 1 / 1.93 - Math.pow(x[4],3) / (x[1] * x[2] * Math.pow(x[6],4));
		constraint[4] = 40 - x[1] * x[2];
		constraint[5] = 12 - x[0] / x[1];
		constraint[6] = x[0] / x[1] - 5;
		constraint[7] = x[3] - 1.5 * x[5] - 1.9;
		constraint[8] = x[4] - 1.1 * x[6] - 1.9;
		constraint[9] = 1300 - f[1];
		constraint[10] = 1100 - Math.sqrt(Math.pow((745 * x[4] / (x[1] * x[2])),2) + 1.575 * 1e8) / (0.1 * Math.pow(x[6],3));

		double total = 0.0;
		int number = 0;
		for(int i = 0 ; i < numberOfConstraints_; i++){
			if (constraint[i] < 0.0){
				total+=constraint[i] ;
				number++;
				solution.setConstraint(i,constraint[i]);
			}
		}

		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);

	} // evaluate

	/**
	 * Evaluates the constraint overhead of a solution
	 * 
	 * @param solution
	 *            The solution
	 * @throws JMException
	 */
	public void evaluateConstraints(Solution solution) throws JMException {
		
//		double constraint;
//		double x_1 = solution.getDecisionVariables()[0].getValue();
//		constraint = Math.sin(20 * Math.PI * x_1) - 0.5;
//
////        double y_1 = 10 * (x_1 - 0.5);
////
////        constraint = 2.5 - (-0.9 * y_1 * y_1 + Math.pow((5 * Math.pow(Math.abs(y_1),0.001) / Math.pow(5,0.001) ),2));
//
//		double total = 0.0;
//		int number = 0;
//		if (constraint < 0.0) {
//			total += constraint;
//			number++;
//			solution.setConstraint(0,constraint);
//		}
//		solution.setOverallConstraintViolation(total);
//		solution.setNumberOfViolatedConstraint(number);
	} // evaluateConstraints

}
