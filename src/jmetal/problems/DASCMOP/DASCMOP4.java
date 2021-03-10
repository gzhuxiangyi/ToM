//  DASCMOP4.java
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
 * Class representing problem DASCMOP4
 */
public class DASCMOP4 extends Problem {

	private double[] DifficultyFactors;
	/**
	 * Creates an DASCMOP4 problem (30 variables and 2 objectives)
	 *
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public DASCMOP4(String solutionType, double[] difficultyFactors) throws ClassNotFoundException {
		this(solutionType, 30, 2, difficultyFactors);
	} // DASCMOP4

	/**
	 * Creates an DASCMOP4 problem instance
	 *
	 * @param numberOfVariables
	 *            Number of variables
	 * @param numberOfObjectives
	 *            Number of objective functions
	 * @param solutionType
	 *            The solution type must "Real" or "BinaryReal".
	 */
	public DASCMOP4(String solutionType, Integer numberOfVariables,
                    Integer numberOfObjectives, double[] difficultyFactors) throws ClassNotFoundException {
		numberOfVariables_ = numberOfVariables.intValue();
		numberOfObjectives_ = numberOfObjectives.intValue();
		numberOfConstraints_ = 11;
		problemName_ = "DASCMOP4";
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

		double sum1, sum2, yj;
		int j;
		sum1 = sum2 = 0.0;
		int nx = numberOfVariables_;
		int temp_k = numberOfVariables_ - numberOfObjectives_ + 1;
		double[] x = new double[numberOfVariables_];
		double[] f = new double[numberOfObjectives_];
		Variable[] gen = solution.getDecisionVariables();

		for (int i = 0; i < numberOfVariables_; i++)
			x[i] = gen[i].getValue();

		for (int i = numberOfVariables_ - temp_k; i < numberOfVariables_; i++)
			sum1 += (x[i] - 0.5)*(x[i] - 0.5) - Math.cos(20.0 * Math.PI * ( x[i] - 0.5));

		sum1 = (temp_k + sum1);


		f[0] = x[0] + sum1 ;
		f[1] = 1.0 - Math.pow(x[0],2) + sum1;

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

		//Type-II constraints
		constraints[1] = (e - sum1) * (sum1 - d);

		if(DifficultyFactors[1] == 1.0){
			constraints[1] = 1e-4 - Math.abs(sum1 - e);
		}

		//Type-III constraints
		double[] p_k = {0.0, 1.0, 0.0, 1.0, 2.0, 0.0, 1.0, 2.0, 3.0};
		double[] q_k = {1.5, 0.5, 2.5, 1.5, 0.5, 3.5, 2.5, 1.5, 0.5};
		double a_k = 0.3;
		double b_k = 1.2;
		double theta_k = -0.25 * Math.PI;

		for(int k = 0; k < p_k.length; k++){
			constraints[2+k] = Math.pow(((f[0] - p_k[k]) * Math.cos(theta_k) - (f[1] - q_k[k]) * Math.sin(theta_k)) ,2) / a_k
					+ Math.pow(((f[0] - p_k[k]) * Math.sin(theta_k) + (f[1] - q_k[k]) * Math.cos(theta_k)),2) / b_k
					- r;
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
