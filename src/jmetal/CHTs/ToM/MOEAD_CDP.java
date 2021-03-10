//  MOEAD_CDP.java
//
//  Author:
//       wenji li  <wenji_li@126.com>
// This is actually C-MOEA/D in NSGA-III paper
package jmetal.CHTs.ToM;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Ranking;
import jmetal.util.comparators.IConstraintViolationComparator;
import jmetal.util.comparators.OverallConstraintViolationComparator;


// This class implements a constrained version of the MOEAD algorithm based on
// the CDP method.
public class MOEAD_CDP extends Algorithm {

  private SolutionSet allPop;

  private int populationSize_;
  /**
   * Stores the population
   */
  private SolutionSet population_;
  /**
   * Z vector (ideal point)
   */
  double[] z_;
  /**
   * Lambda vectors
   */
  //Vector<Vector<Double>> lambda_ ;
  double[][] lambda_;
  /**
   * T: neighbour size
   */
  int T_;
  /**
   * Neighborhood
   */
  int[][] neighborhood_;
  /**
   * delta: probability that parent solutions are selected from neighbourhood
   */
  double delta_;
  /**
   * nr: maximal number of solutions replaced by each child solution
   */
  int nr_;
  String functionType_;
  int evaluations_;
  /**
   * Operators
   */
  Operator crossover_;
  Operator mutation_;

  String dataDirectory_;

  /**
   * Use this encodings.variable as comparator for the constraints
   */
  IConstraintViolationComparator comparator = new OverallConstraintViolationComparator();
  boolean isDisplay_;
  String constraintFile_ ;
  String methodName_;
  int maxEvaluations_;

 private CaptureConvergence captureConvergence_; // our class 
 private boolean convergenceFlag = false;
 //public static int runTimes = 0;
 
  /**
   * Constructor
   * @param problem Problem to solve
   */
  public MOEAD_CDP(Problem problem) {
    super (problem) ;
//    functionType_ = "_TCHE1";
    functionType_ = "_PBI";
  } // MOEAD_CDP

  public SolutionSet execute() throws JMException, ClassNotFoundException {
//	this.runTimes++;
	
    int runningTime;
    int dif_index;
    evaluations_     = 0;
    maxEvaluations_  = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
    populationSize_  = ((Integer) this.getInputParameter("populationSize")).intValue();
    
    System.out.println("MOEAD-CDP maxEvaluations = " +  maxEvaluations_);		
	System.out.println("MOEAD-CDP popsize = " +  populationSize_ );	
	
	if (convergenceFlag == true) 
		  captureConvergence_ = new CaptureConvergence(problem_.getName(), "MOEADCDP", problem_.getNumberOfObjectives());	
	
    dataDirectory_   = this.getInputParameter("dataDirectory").toString();
//    constraintFile_  = this.getInputParameter("constraintInfo").toString();
//    methodName_      = this.getInputParameter("AlgorithmName").toString();
    isDisplay_       = (Boolean)this.getInputParameter("isDisplay");
//    runningTime      = ((Integer)this.getInputParameter("runningTime")).intValue() + 1; // start from 1
//    dif_index      = ((Integer)this.getInputParameter("difcultyIndex")).intValue() + 1; // start from 1
    population_      = new SolutionSet(populationSize_);
    T_               = ((Integer) this.getInputParameter("T")).intValue();
    nr_              = ((Integer) this.getInputParameter("nr")).intValue();
    delta_           = ((Double) this.getInputParameter("delta")).doubleValue();
    neighborhood_    = new int[populationSize_][T_];
    isDisplay_       = (Boolean)this.getInputParameter("isDisplay");
    z_               = new double[problem_.getNumberOfObjectives()];
    lambda_          = new double[populationSize_][problem_.getNumberOfObjectives()];
    crossover_       = operators_.get("crossover"); // default: DE crossover
    mutation_        = operators_.get("mutation");  // default: polynomial mutation

    //creat database
//    String problemName = problem_.getName() +"_" + Integer.toString(dif_index) + "_" + Integer.toString(runningTime);
//    SqlUtils.CreateTable(problemName,methodName_);

    // STEP 1. Initialization
    // STEP 1.1. Compute euclidean distances between weight vectors and find T
    initUniformWeight();

    initNeighborhood();

    // STEP 1.2. Initialize population
    initPopulation();    
//    initPopulationFixed(); // for convergence 
    
    // STEP 1.3. Initialize z_
    initIdealPoint();

	if (convergenceFlag== true) 
		captureConvergence_.runCaptureConvergence(0, population_);		
	
    int gen = 0;

    // STEP 2. Update
    do {
      int[] permutation = new int[populationSize_];
      Utils.randomPermutation(permutation, populationSize_);

      for (int i = 0; i < populationSize_; i++) {
        int n = permutation[i]; // or int n = i;
        //int n = i ; // or int n = i;
        int type;
        double rnd = PseudoRandom.randDouble();

        // STEP 2.1. Mating selection based on probability
        if (rnd < delta_) // if (rnd < realb)    
        {
          type = 1;   // neighborhood
        } else {
          type = 2;   // whole population
        }
        Vector<Integer> p = new Vector<Integer>();
        matingSelection(p, n, 2, type);

        // STEP 2.2. Reproduction
        Solution child = null;

        // Apply Crossover for Real codification
        if(crossover_.getClass().getSimpleName().equalsIgnoreCase("SBXCrossover")){
          Solution[] parents = new Solution[2];
          parents[0] = population_.get(p.get(0));
          parents[1] = population_.get(n);
          child = ((Solution[])crossover_.execute(parents))[0];
        }
        // Apply DE crossover
        else if(crossover_.getClass().getSimpleName().equalsIgnoreCase("DifferentialEvolutionCrossover")){
          Solution[] parents = new Solution[3];
          parents[0] = population_.get(p.get(0));
          parents[1] = population_.get(p.get(1));
          parents[2] = population_.get(n);
          child = (Solution) crossover_.execute(new Object[]{population_.get(n), parents});
        }
        else {
          System.out.println("unknown crossover" );
        }
        // Apply mutation
        mutation_.execute(child);

        // Evaluation
        problem_.evaluate(child);
        problem_.evaluateConstraints(child);
        evaluations_++;
		
        // STEP 2.3. Repair. Not necessary

        // STEP 2.4. Update z_
        updateReference(child);

        // STEP 2.5. Update of solutions
        updateProblem(child, n, type);
        
		if (convergenceFlag== true) 
			captureConvergence_.runCaptureConvergence(evaluations_, removeSame(population_));
      } // for

      gen = gen + 1;
    } while (evaluations_ < maxEvaluations_);

    // At last remove identical solutions 
    SolutionSet finalSet = removeSame(population_);    
//    SqlUtils.InsertSolutionSet(problemName,population_);
    Ranking ranking = new Ranking(finalSet);
    
    System.out.println("evaluations: " + evaluations_);
    System.out.println("Final size = " + ranking.getSubfront(0).size());
    
    return ranking.getSubfront(0);
//    return population_;
  }

  /**
   * initUniformWeight
   */
  public void initUniformWeight() {
    if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ <= 300)) {
      for (int n = 0; n < populationSize_; n++) {
        double a = 1.0 * n / (populationSize_ - 1);
        lambda_[n][0] = a;
        lambda_[n][1] = 1 - a;
      } // for
    } // if
    else {
      String dataFileName;
      dataFileName = "W" + problem_.getNumberOfObjectives() + "D_" +
              populationSize_ + ".dat";

      try {
        // Open the file
        FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        int i = 0;
        int j;
        String aux = br.readLine();
        while (aux != null) {
          StringTokenizer st = new StringTokenizer(aux);
          j = 0;
          while (st.hasMoreTokens()) {
            double value = (new Double(st.nextToken())).doubleValue();
            lambda_[i][j] = value;
            //System.out.println("lambda["+i+","+j+"] = " + value) ;
            j++;
          }
          aux = br.readLine();
          i++;
        }
        br.close();
      } catch (Exception e) {
        System.out.println("initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName);
        e.printStackTrace();
      }
    }

    //System.exit(0) ;
  } // initUniformWeight
  /**
   *
   */
  public void initNeighborhood() {
    double[] x = new double[populationSize_];
    int[] idx = new int[populationSize_];

    for (int i = 0; i < populationSize_; i++) {
      // calculate the distances based on weight vectors
      for (int j = 0; j < populationSize_; j++) {
        x[j] = Utils.distVector(lambda_[i], lambda_[j]);
        idx[j] = j;
      } // for

      // find 'niche' nearest neighboring subproblems
      Utils.minFastSort(x, idx, populationSize_, T_);
      System.arraycopy(idx, 0, neighborhood_[i], 0, T_);
    } // for
  } // initNeighborhood

  /**
   *
   */
  public void initPopulation() throws JMException, ClassNotFoundException {
    for (int i = 0; i < populationSize_; i++) {
      Solution newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations_++;
      population_.add(newSolution) ;
    } // for
  } // initPopulation

  public void initPopulationFixed() throws JMException, ClassNotFoundException {

		/**
		 * Method 1: Random generate N solutions
		 */
		population_ = new SolutionSet(populationSize_);

		String path = "./jmetalExperiment/ConvergenceInitialPopulation/M=" + problem_.getNumberOfObjectives() 
				      + "/InitialPopulation/" + problem_.getName() +  ".VAR";
		
		System.out.println(" problem_.getName() "  + problem_.getName());
		
		 try {
		      // Open the file
		      FileInputStream fis   = new FileInputStream(path)     ;
		      InputStreamReader isr = new InputStreamReader(fis)    ;
		      BufferedReader br      = new BufferedReader(isr)      ;
		      
		      String aux = br.readLine();
		      while (aux!= null && !"".equalsIgnoreCase(aux)) {
		    	  
		    	Solution newSolution = new Solution(problem_);
		    	Variable [] var = newSolution.getDecisionVariables();
		    	
		        StringTokenizer st = new StringTokenizer(aux);
		        int i = 0;
		  
		        while (st.hasMoreTokens()) {
		          double value = new Double(st.nextToken());
		          (var [i]).setValue(value); 
		          i++;
		        }		        
		        
		        problem_.evaluate(newSolution);
				problem_.evaluateConstraints(newSolution);
				evaluations_++;
				population_.add(newSolution);		
				
//				System.out.print(newSolution.toString() + " \n");
				
		        aux = br.readLine();
		      }
		            
		      br.close();
		      
		  } catch (Exception e) {
		      System.out.println("InputFacilities crashed reading for file: "+path);
		      e.printStackTrace();
		 }
		
	} // initPopulationRandom
  /**
   *
   */
  void initIdealPoint() throws JMException, ClassNotFoundException {
    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
      z_[i] = 1.0e+30;
    } // for

    for (int i = 0; i < populationSize_; i++) {
      updateReference(population_.get(i));
    } // for
  } // initIdealPoint

  /**
   *
   */
  public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
    // list : the set of the indexes of selected mating parents
    // cid  : the id of current subproblem
    // size : the number of selected mating parents
    // type : 1 - neighborhood; otherwise - whole population
    int ss;
    int r;
    int p;

    ss = neighborhood_[cid].length;
    while (list.size() < size) {
      if (type == 1) {
        r = PseudoRandom.randInt(0, ss - 1);
        p = neighborhood_[cid][r];
        //p = population[cid].table[r];
      } else {
        p = PseudoRandom.randInt(0, populationSize_ - 1);
      }
      boolean flag = true;
      for (Integer aList : list) {
        if (aList == p) // p is in the list
        {
          flag = false;
          break;
        }
      }

      //if (flag) list.push_back(p);
      if (flag) {
        list.addElement(p);
      }
    }
  } // matingSelection

  /**
   *
   * @param individual
   */
  void updateReference(Solution individual) {
    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
      if (individual.getObjective(n) < z_[n]) {
        z_[n] = individual.getObjective(n);
      }
    }
  } // updateReference

  /**
   * @param indiv
   * @param id
   * @param type
   */
  void updateProblem(Solution indiv, int id, int type) {
    // indiv: child solution
    // id:   the id of current subproblem
    // type: update solutions in - neighborhood (1) or whole population (otherwise)
    int size;
    int time;

    time = 0;

    if (type == 1) {
      size = neighborhood_[id].length;
    } else {
      size = population_.size();
    }
    int[] perm = new int[size];

    Utils.randomPermutation(perm, size);

    for (int i = 0; i < size; i++) {
      int k;
      if (type == 1) {
        k = neighborhood_[id][perm[i]];
      } else {
        k = perm[i];      // calculate the values of objective function regarding the current subproblem
      }
      double f1, f2;

      f1 = fitnessFunction(population_.get(k), lambda_[k]);
      f2 = fitnessFunction(indiv, lambda_[k]);

      /***** This part is new according to the violation of constraints *****/
      if (comparator.needToCompare(population_.get(k), indiv)) {
        int flag = comparator.compare(population_.get(k), indiv);
        if (flag == 1)
          population_.replace(k,new Solution(indiv));
        else if (flag == 0)
          if (f2 < f1) {
            population_.replace(k, new Solution(indiv));
            time++;
          }
      } else {
        if (f2 < f1) {
          population_.replace(k, new Solution(indiv));
          time++;
        }
      }
      // the maximal number of solutions updated is not allowed to exceed 'limit'
      if (time >= nr_) {
        return;
      }
    }
  } // updateProblem

//  double fitnessFunction(Solution individual, double[] lambda) {
//    double fitness;
//    fitness = 0.0;
//    if (functionType_.equals("_TCHE1")) {
//      double maxFun = -1.0e+30;
//
//      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
//        double diff = Math.abs(individual.getObjective(n) - z_[n]);
//        double feval;
//        if (lambda[n] == 0) {
//          feval = 0.0001 * diff;
//        } else {
//          feval = diff * lambda[n];
//        }
//        if (feval > maxFun) {
//          maxFun = feval;
//        }
//      } // for
//
//      fitness = maxFun;
//    } // if
//    else {
//      System.out.println(methodName_ + ".fitnessFunction: unknown type " + functionType_);
//      System.exit(-1);
//    }
//    return fitness;
//  } // fitnessEvaluation    

	double fitnessFunction(Solution indiv, double[] lambda) {
		double fitness;
		fitness = 0.0;

		if (functionType_.equals("_TCHE1")) {
			double maxFun = -1.0e+30;

			for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
				double diff = Math.abs(indiv.getObjective(n) - z_[n]);

				double feval;
				if (lambda[n] == 0) {
					feval = 0.0001 * diff;
				} else {
					feval = diff * lambda[n];
				}
				if (feval > maxFun) {
					maxFun = feval;
				}
			} // for

			fitness = maxFun;
		} else if (functionType_.equals("_TCHE2")) {
			double maxFun = -1.0e+30;

			for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
				double diff = Math.abs(indiv.getObjective(i) - z_[i]);

				double feval;
				if (lambda[i] == 0) {
					feval = diff / 0.000001;
				} else {
					feval = diff / lambda[i];
				}
				if (feval > maxFun) {
					maxFun = feval;
				}
			} // for
			fitness = maxFun;
		} else if (functionType_.equals("_PBI")) {
			double theta; // penalty parameter
			theta = 5.0;

			// normalize the weight vector (line segment)
			double nd = norm_vector(lambda);
			for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
				lambda[i] = lambda[i] / nd;

			double[] realA = new double[problem_.getNumberOfObjectives()];
			double[] realB = new double[problem_.getNumberOfObjectives()];

			// difference between current point and reference point
			for (int n = 0; n < problem_.getNumberOfObjectives(); n++)
				realA[n] = (indiv.getObjective(n) - z_[n]);

			// distance along the line segment
			double d1 = Math.abs(innerproduct(realA, lambda));

			// distance to the line segment
			for (int n = 0; n < problem_.getNumberOfObjectives(); n++)
				realB[n] = (indiv.getObjective(n) - (z_[n] + d1 * lambda[n]));
			double d2 = norm_vector(realB);

			fitness = d1 + theta * d2;
//			fitness = d2;
		} else {
			System.out.println("MOEAD.fitnessFunction: unknown type "
					+ functionType_);
			System.exit(-1);
		}
		return fitness;
	} // fitnessEvaluation
	
	/**
	 * Calculate the dot product of two vectors
	 * 
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	public double innerproduct(double[] vec1, double[] vec2) {
		double sum = 0;

		for (int i = 0; i < vec1.length; i++)
			sum += vec1[i] * vec2[i];

		return sum;
	}
	
  public SolutionSet removeSame(SolutionSet pop) {
	  // At last remove identical solutions 
	    SolutionSet finalSet = new SolutionSet(pop.size());        
	    finalSet.add(pop.get(0));        
	  
	    for (int i = 1; i < pop.size(); i++) {      
			      
			Solution sol = pop.get(i);
			boolean existEqual = false;
			
			for (int j = 0; j < finalSet.size();j++) {
				if (equalSolution(sol, finalSet.get(j))) {
					existEqual = true;
					break;	    		
				}
			}
			
			if (existEqual == true) continue;
		
			finalSet.add(pop.get(i));	
			
		} // for	
	    return finalSet;
  }
  
  /**
	 * Calculate the norm of the vector
	 * 
	 * @param z
	 * @return
	 */
	public double norm_vector(double[] z) {
		double sum = 0;

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			sum += z[i] * z[i];

		return Math.sqrt(sum);
	}
  
  public boolean equalSolution (Solution sol1, Solution sol2) {
		
		if (sol1.getNumberOfViolatedConstraint() !=sol2.getNumberOfViolatedConstraint()) { // Լ������
			return false; // ���ز���
		}
		
		for (int i = 0; i < sol1.getNumberOfObjectives();i++) {
			if (sol1.getObjective(i) != sol2.getObjective(i))
				return false;
		}
		
		return true;
	}
}
