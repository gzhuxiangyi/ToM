package jmetal.problems.DASCMaOP;

//  CMaOP.java
/**
 * generate constrained many-objective optimization problems
 * author: wenji li email: wenji_li@126.com
 * data: 17th June, 2016
 */

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;





public class ConstraintSetting extends Problem {

    public double eta;
    public double zeta;
    public double gamma;
    public int[] sRange;


    public ConstraintSetting(String solutionType, int objectiveNumber, int variableNumber,int[] sRange, double[] difficultyTriple) {

        numberOfConstraints_ = objectiveNumber * 2 + 1;
        numberOfObjectives_  = objectiveNumber;
        numberOfVariables_   = variableNumber;

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];
        for (int var = 0; var < numberOfVariables_; var++){
            lowerLimit_[var] = 0.0;
            upperLimit_[var] = 1.0;
        } //for

        eta = difficultyTriple[0];
        zeta = difficultyTriple[1];
        gamma = difficultyTriple[2];
        this.sRange = sRange;


        if (solutionType.compareTo("BinaryReal") == 0)
            solutionType_ = new BinaryRealSolutionType(this) ;
        else if (solutionType.compareTo("Real") == 0)
            solutionType_ = new RealSolutionType(this) ;
        else {
            System.out.println("Error: solution type " + solutionType + " invalid") ;
            System.exit(-1) ;
        }
    } // CMaOP

    public double[] evaluate(float[] X, float[] F) throws JMException {

        double a = 20;
        double b = eta;
        double c = 0.5;
        double d = c + Math.pow(10, -2 * zeta);
        double r = 0.5 * gamma;

        double [] con = new double[numberOfConstraints_];

        double [] f = new double[numberOfObjectives_];

        for (int i = 0; i < numberOfObjectives_; i++){
            f[i] = F[i] / sRange[i];
        }

        // evaluate the constraints

        // type-I constraints
        for(int i = 0; i < numberOfObjectives_ - 1;i++){
            if(eta < 1e-6){
                break;
            }

            if(i % 2 == 0){
                con[i] = (Math.sin(a * Math.PI * X[i]) - b);
            }else {
                con[i] = (Math.cos(a * Math.PI * X[i]) - b);
            }
        }

        // type-II constraints
        if(zeta > 1e-6){
            con[numberOfObjectives_-1] = ((d - X[numberOfObjectives_ - 1])*(X[numberOfObjectives_ - 1] - c));
        }

        // type-III constraints
        double sum_c ;
        for (int i = 0; i < numberOfObjectives_; i++){
            sum_c = 0;
            for(int j = 0; j < numberOfObjectives_; j++){
                sum_c = sum_c + f[j] * f[j];
            }
            sum_c = sum_c - f[i] * f[i] + (f[i] - 1) * (f[i] - 1) - r * r;
            con[i + numberOfObjectives_] =  (sum_c);
        }

        sum_c = 0;
        for(int i = 0; i < numberOfObjectives_; i++){
            sum_c = sum_c + Math.pow(f[i] - Math.sqrt(1.0/numberOfObjectives_),2) - r * r;
        }
        con[2*numberOfObjectives_] =  sum_c;

        return con;
    }

    public final void evaluate(Solution solution) throws JMException {

    }

}

