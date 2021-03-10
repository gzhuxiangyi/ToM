package jmetal.problems.Realworld;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

public class MEMS extends Problem {

	/**
	 * Constructor
	 * Creates a default instance of the MEMS problem
	 * @param solutionType The solution type must "Real" or "BinaryReal"
	 */
	public MEMS(String solutionType) throws ClassNotFoundException{
		this(solutionType,14,2);
	}

	public MEMS(String solutionType, Integer numberOfVariables,
				 Integer numberOfObjectives){
		numberOfVariables_   = numberOfVariables.intValue();
		numberOfObjectives_  = numberOfObjectives.intValue();
		numberOfConstraints_ = 25;
		problemName_         = "MEMS";

		lowerLimit_ = new double[]{2,2,2,2,2,10,2,10,10,8,2,4,0,3};
		upperLimit_ = new double[]{400,20,400,20,400,400,400,400,400,400,20,400,50,50};

		if (solutionType.compareTo("BinaryReal") == 0)
			solutionType_ = new BinaryRealSolutionType(this) ;
		else if (solutionType.compareTo("Real") == 0)
			solutionType_ = new RealSolutionType(this) ;
		else {
			System.out.println("Error: solution type " + solutionType + " invalid") ;
			System.exit(-1) ;
		}
	}

	public void evaluate(Solution solution) throws JMException{
		XReal vars = new  XReal(solution);
		double[] fx = new double[2];//function values
		double[] x = new double[numberOfVariables_];

		for(int i = 0; i < numberOfVariables_; i++)
			x[i] = vars.getValue(i);

		x[13] = Math.round(x[13]);

		double g,wba=11,wca=14,wa;//

		//x=x_var;
		g= x[10];


		double L1,L2,As,At,Ab,Ac;

		L1=(x[13]-1)*(x[10]+2*g)+x[13]*x[10];
		L2=L1+2*(g+x[10]);

		wa=x[6]+x[5]-2*x[2]+x[1];

		As=x[7]*x[6]+2*x[5]*x[4];
		At=2*(wca*L2+(x[13]+1)*x[10]*x[9]);
		Ab=8*x[0]*x[1]+2*x[3]*(2*x[2]+wa)+2*wa*wba;
		Ac=2*(L1*x[8]+x[13]*x[10]*x[9]);


		//2 objections
		fx[0] = x[12];
		fx[1] = 1e-3*(As+At+Ab+Ac);

		solution.setObjective(0, fx[0]);
		solution.setObjective(1, fx[1]);

	}



	public void evaluateConstraints(Solution solution) throws JMException {

		XReal vars = new  XReal(solution);
		double[] fx = new double[2];//function values
		double[] x = new double[numberOfVariables_];

		for(int i = 0; i < numberOfVariables_; i++)
			x[i] = vars.getValue(i);

		double [] g_const = new double[this.getNumberOfConstraints()];

		x[13] = Math.round(x[13]);

		double permitivity_air=8.854e-6;       //
		double viscosity_air=1.8508e-11;        //
		double e=1.7e5;                    //
		double lou=2.33e-15;                 //

		double g,d,wba=11,wca=14,r=4,t=2,wa;//wa=11,;

		//x=x_var;
		g= x[10];
		d= x[10];

		double L1,L2,xdisp,Q,fex,alpha,beta_x,kx,ky,key,As,At,Ab,Ac,mx,ms,mt,mb;

		L1=(x[13]-1)*(x[10]+2*g)+x[13]*x[10];
		L2=L1+2*(g+x[10]);

		alpha=(x[3]*x[3]*x[3])/(x[1]*x[1]*x[1]);
		fex=1.12*permitivity_air*x[13]*(t/g)*x[12]*x[12];
		kx=((2*e*t*x[1]*x[1]*x[1])/(x[0]*x[0]*x[0]))*((x[2]*x[2]+14*alpha*x[2]*x[0]+36*alpha*alpha*x[0]*x[0])/(4*x[2]*x[2]+41*alpha*x[2]*x[0]+36*alpha*alpha*x[0]*x[0]));
		ky=((2*e*t*x[3]*x[3]*x[3])/(x[2]*x[2]*x[2]))*((8*x[2]*x[2]+8*alpha*x[2]*x[0]+alpha*alpha*x[0]*x[0])/(4*x[2]*x[2]+10*alpha*x[2]*x[0]+5*alpha*alpha*x[0]*x[0]));
		key=2*permitivity_air*x[13]*x[12]*x[12]*x[11]*t/(g*g*g) ;

		wa=x[6]+x[5]-2*x[2]+x[1];

		As=x[7]*x[6]+2*x[5]*x[4];
		At=2*(wca*L2+(x[13]+1)*x[10]*x[9]);
		Ab=8*x[0]*x[1]+2*x[3]*(2*x[2]+wa)+2*wa*wba;
		Ac=2*(L1*x[8]+x[13]*x[10]*x[9]);

		ms=lou*As*t;
		mt=lou*At*t;
		mb=lou*Ab*t;
		mx=ms+0.25*mt+12/35*mb;

		beta_x=viscosity_air * ((As + 0.5 * At +0.5*Ab)*(1.0/d+1.0/r)+Ac/g);
		Q=Math.sqrt(mx*kx/(beta_x*beta_x));
		xdisp=Q*fex/kx;



		//2 objections
		fx[0] = x[12];
		fx[1] = 1e-3*(As+At+Ab+Ac);

		//25 constraints ,g(x)>=0
		g_const[0] = L2;
		g_const[1] = 700-L2;
		g_const[2] = (x[4]+2*x[0]+2*x[3]);
		g_const[3] = 700-(x[4]+2*x[0]+2*x[3]);
		g_const[4] = 2*(wca+2*x[9]-x[11]+x[8])+x[6]+2*x[5];
		g_const[5] = 700-(2*(wca+2*x[9]-x[11]+x[8])+x[6]+2*x[5]);
		g_const[6] = (x[9]-x[11]- xdisp)-4;
		g_const[7] = 200-(x[9]-x[11]-xdisp);
		g_const[8] = (x[11]-xdisp)-4;
		g_const[9] = 200-(x[11] - xdisp);
		g_const[10] = (x[6]/2.0+x[5]+xdisp)-(x[2]+wa/2.0);
		g_const[11] = (x[2]+wa/2.0-x[1])-(x[6]/2.0+xdisp);
		g_const[12] = (x[4]/2.0-wba-x[7]/2.0)-2;
		g_const[13] = 200-(x[4]/2.0-wba-x[7]/2.0);
		g_const[14] = xdisp-2;
		g_const[15] = 100-xdisp;
		g_const[16] = Q-5;
		g_const[17] = 1.0e5-Q;
		g_const[18] = xdisp/x[0];
		g_const[19] = 0.1-xdisp/x[0];
		g_const[20] = key/ky;
		g_const[21] = (1.0/3.0)-(key/ky);
		g_const[22] = (x[6]/2.0-xdisp-wa/2.0)-4;
		g_const[23] = 200-(x[6]/2.0-xdisp-wa/2.0);

		g_const[24] = wa-1e-8;
//		g_const[24] = x[6]+x[5]-2*x[2]-x[1];


		//
		double total = 0.0;
		int number = 0;
		for(int i = 0 ; i < this.getNumberOfConstraints(); i++){
			if (g_const[i] < 0.0){
				total+=g_const[i] ;
				number++;
				solution.setConstraint(i,g_const[i]);
			}
		}
		solution.setOverallConstraintViolation(total);
		solution.setNumberOfViolatedConstraint(number);
	}
}
