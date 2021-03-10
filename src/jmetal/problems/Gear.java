package jmetal.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

/**
 * Created by Administrator on 2015/4/12 0012.
 */
public class Gear extends Problem {
    public Gear(String solutionType) throws ClassNotFoundException{
        this(solutionType,10,2);
    }

    public Gear(String solutionType,
                  Integer numberOfVariables,
                  Integer numberOfObjectives) throws ClassNotFoundException {
        numberOfVariables_  = numberOfVariables.intValue();
        numberOfObjectives_ = numberOfObjectives.intValue();
        numberOfConstraints_= 11;
        problemName_        = "Gear";

        lowerLimit_ = new double[]{0,0,2,2,14,0,14,14,0,14};
        upperLimit_ = new double[]{100,180,10,10,30,160,50,30,160,60};

        if (solutionType.compareTo("BinaryReal") == 0)
            solutionType_ = new BinaryRealSolutionType(this) ;
        else if (solutionType.compareTo("Real") == 0)
            solutionType_ = new RealSolutionType(this) ;
        else {
            System.out.println("Error: solution type " + solutionType + " invalid") ;
            System.exit(-1) ;
        }
    }

    public void evaluate(Solution solution) throws JMException {
        double [] y = new double[2];
        Variable[] gen  = solution.getDecisionVariables();
        double [] x = new double[gen.length];
        int[] n = new int[]{3,3};
        for(int i = 0; i <gen.length; i++){
            x[i] = gen[i].getValue();
        }
        y[0] = 1e-7*(0.7854* (x[0]* x[2]*x[2]* (x[4]*x[4]+n[0]* x[6]*x[6]+9* x[5]-30.2)+x[1]* x[3]*x[3]* (x[7]*x[7]+n[1]* x[9]*x[9]+9* x[8]-30.2)));
        y[1] = -(1-0.1178* x[5]* (1.0/x[4]+2.0/x[6]-1.0/x[5]) /(x[4]+x[5]))* (1-0.1178* x[8]* (1.0/x[7]+2.0/x[9]-1.0/x[8]) /(x[7]+x[8]));
        solution.setObjective(0,y[0]);
        solution.setObjective(1,y[1]);
    }

    public void evaluateConstraints(Solution solution) throws JMException {
        Variable[] gen  = solution.getDecisionVariables();
        double [] x = new double[gen.length];
        int[] n = new int[]{3,3};
        for(int i = 0; i <gen.length; i++){
            x[i] = gen[i].getValue();
        }
        double[] g_const = new double[numberOfConstraints_];
        g_const[0] = -(Math.abs(0.0357 * (x[4] + x[5]) * (x[7] + x[8]) / (x[4] * x[7]) - 1)-0.04);  //传动比条�?
        g_const[1] = -(2+x[6]-(x[4]+x[6])* Math.sin(3.14159265 / n[0]));  //邻接条件;
        g_const[2] = -(2+x[9]-(x[7]+x[9])* Math.sin(3.14159265 / n[1]));
        g_const[3] = -(659130* Math.sqrt((x[5] - x[4]) / (x[5] * x[2] * x[2] * x[4] * x[4] * x[0] * n[0]))-1100);  //接触强度条件
        g_const[4] = -(659130* Math.sqrt((x[8] - x[7]) * (x[5] + x[4]) / (x[8] * x[3] * x[3] * x[7] * x[7] * x[1] * x[4] * n[1])) -1100);
        g_const[5] = -(7455600 /(x[2]*x[2]* x[4]* x[0]* n[0])-525);  //弯曲强度条件
        g_const[6] = -(7455600* (x[5]+x[4]) /(x[3] *x[3]* x[4]* x[7]* x[1]*n[1])-525);
        g_const[7] = -(0.8-x[2]* x[5] /(x[3]* x[8]));    //外径尺寸条件
        g_const[8] = -(x[2]* x[5] /(x[3]* x[8])-1.2);
        g_const[9] = -(x[4]+2* x[6]-x[5]-1e-5);         //同心条件;
        g_const[10] = -(x[7]+2* x[9]-x[8]-1e-5);

        double total = 0.0;
        int number = 0;
        for(int i = 0 ; i < numberOfConstraints_; i++){
            if (g_const[i] < 0.0){
                total+=g_const[i] ;
                number++;
            }
        }
        solution.setOverallConstraintViolation(total);
        solution.setNumberOfViolatedConstraint(number);

    }
}
