//  DASCMOP8.java
//
//  Author:
//  wenji li Email: wenji_li@126.com

package jmetal.problems.DASCMOP;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Class representing problem DASCMOP8
 */
public class DASCMOP8 extends Problem {

	private double[] DifficultyFactors;
	/**
	 * Creates an DASCMOP8 problem (30 variables and 2 objectives)
	 *
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public DASCMOP8(String solutionType, double[] difficultyFactors) throws ClassNotFoundException {
		this(solutionType, 30, 3, difficultyFactors);
	} // DASCMOP8

	/**
	 * Creates an DASCMOP8 problem instance
	 *
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public DASCMOP8(String solutionType, Integer numberOfVariables,
                    Integer numberOfObjectives, double[] difficultyFactors) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 7;
		problemName_ = "DASCMOP8";
		DifficultyFactors = difficultyFactors;

		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		for (int var = 0; var < numberOfVariables; var++) {
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 1.0;
		} // for

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

		double gx = 0.0;
		int temp_k = numberOfVariables_ - numberOfObjectives_ + 1;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		for (int i = numberOfVariables_ - temp_k; i < numberOfVariables_; i++)
			gx += (x[i] - 0.5)*(x[i] - 0.5) - Math.cos(20.0 * Math.PI * ( x[i] - 0.5));

		gx = (temp_k + gx);

		double pi = Math.PI;

		f[0] = Math.cos(0.5 * pi * x[0]) * Math.cos(0.5 * pi * x[1]) + gx ;
		f[1] = Math.cos(0.5 * pi * x[0]) * Math.sin(0.5 * pi * x[1]) + gx;
		f[2] = Math.sin(0.5 * pi * x[0]) + gx;

		for (int i = 0; i < numberOfObjectives_; i++)
			solution.setObjective(i, f[i]);

		// set the parameters of constraints
		//Type-I parameters
		double a = 20;
		double b = 2 * DifficultyFactors[0] - 1;

		//Type-II parameters
		double d = 0.5;
		if(DifficultyFactors[1] == 0.0){
			d = 0.0;
		}

		double e = d - Math.log(DifficultyFactors[1]);
		if(Double.isInfinite(e)){
			e = 1e+30;
		}

		//Type-III parameters
		double r = 0.5 * DifficultyFactors[2];

		double[] constraints = new double[numberOfConstraints_];

		//Type-I constraints
		constraints[0] = Math.sin(a * Math.PI * x[0]) - b;
		constraints[1] = Math.cos(a * Math.PI * x[1]) - b;

		//Type-II constraints
		constraints[2] = (e - gx) * (gx - d);

		if(DifficultyFactors[1] == 1.0){
			constraints[2] = 1e-4 - Math.abs(gx - e);
		}

		//Type-III constraints
		double[] x_k = {1.0, 0.0, 0.0, 1.0 / Math.sqrt(3.0)};
		double[] y_k = {0.0, 1.0, 0.0, 1.0 / Math.sqrt(3.0)};
		double[] z_k = {0.0, 0.0, 1.0, 1.0 / Math.sqrt(3.0)};

		for(int k = 0; k < x_k.length; k++){
			constraints[3+k] = Math.pow((f[0] - x_k[k]),2) + Math.pow((f[1] - y_k[k]),2) + Math.pow((f[2] - z_k[k]),2) - r * r;
		}

		double total = 0.0;
		int number = 0;
		for(int i = 0 ; i < numberOfConstraints_; i++){
			if (constraints[i] < 0.0){
				total+=constraints[i] ;
				number++;
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

	} // evaluateConstraints

}
