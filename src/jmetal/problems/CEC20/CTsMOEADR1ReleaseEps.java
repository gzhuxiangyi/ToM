/**
 * C-TaMOEAD.java
 * A Two-stage and Adaptive Decomposition-based Multi-objective Evolutionary Algorithm for Constrained and 
 * Unconstrained Problems with Irregular Fronts
 */

package jmetal.problems.CEC20;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.myutils.CaptureConvergence;
import jmetal.myutils.WeightSet;
import jmetal.myutils.Weights;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Ranking;
import jmetal.util.Utils;
import jmetal.util.comparators.DominanceComparator;
import jmetal.util.comparators.FitnessComparator;
import jmetal.util.comparators.OverallConstraintViolationComparator;
import jmetal.util.comparators.PureDominanceComparator;
import Jama.Matrix;

public class CTsMOEADR1ReleaseEps extends Algorithm {

	private static final long serialVersionUID = 1L;
	private SolutionSet population_;    // Population repository
	private SolutionSet feasiblePopulation_;  // feasible population
	private SolutionSet offspringPopulation_;    // Offspring population repository
	private SolutionSet union_;                 // Offspring and population 
	private SolutionSet archive_;       // Archive repository
    private SolutionSet bestPopulation_;
	
	private WeightSet newWeights_;      // Weights generated based on current population

	private int    populationSize_;     // Population size
	private int    archiveSize_;        // Archive size
	private int    T_;     				// Neighborhood size
	private int    nr_;    				// Maximal number of solutions replaced by each child solution
	private int    objectives_;         // No of objective
	
	private double delta_; 				// Probability that parent solutions are selected from neighborhood
	private double theta_;              // Used in PBI function 
	private double eps_  = 0.05;        // A small positive number, used in defining ideal or nadir points
	private double max_eps_  ;          // Max eps
	private double min_eps_  ;          // Min eps
	
	private int[][]    neighborhood_; 	// Neighborhood matrix
	private double[][] lambda_; 		// Lambda vectors

	private double []   zp_;	       // Reference point (ideal or nadir points), set dynamically
	private double []   z_ideal;       // estimated ideal point 
	private double []   z_nadir;       // estimated nadir point 	
	private double []   zmax;          // the max point in the current population, used in normalization
	
	private double [] feasiableZideal; 
			
	double[][] AngleMatrix;
	
	//-----------Begin:The following four variables are used in normalization---------	 
	double[][] extremePoints;
	int []     extremeSolutionIndex;
	double[] intercepts;
	//-----------------------------------End------------------------------------	 
		
	private String functionType_;             // The type of scalarization  function  
	private String rpType_;                   // The type of reference point  
	private String dataDirectory_;
	
	private Operator crossover_;             // Crossover 
	private Operator mutation_; 			 // mutation 
	private Operator crossoverDE_;             // Crossover 
	
	private static final Comparator constraint_ = new OverallConstraintViolationComparator();
	private static final Comparator dominance_ = new PureDominanceComparator();
	  
	private CaptureConvergence captureConvergence_; // our class 
	private boolean convergenceFlag = false;
    private boolean rmdrFlag = true;
	private int    evaluations_;   // Counter for the number of function evaluations
	private int    maxEvaluations_;
	private boolean stage2_ = false; // 
	private int unsuccessfulTrailsForFeasible_ = 0; // Feasible ideal point 
	private int unsuccessfulTrails_ = 0;  // ideal point 
    double tao_ = 0.1 ;
    double alpha_ = 0.95;
    private double epsilon_k_;
	/**
	 * Constructor
	 * 
	 * @param Problem to solve
	 */
	public CTsMOEADR1ReleaseEps(Problem problem) {
		super(problem);		
	} // CTaMOEAD

	public SolutionSet execute() throws JMException, ClassNotFoundException {
		// ------------鐠囪褰噁unction閸滃p閻ㄥ嫮琚崹瀣剁礉閻㈢喐鍨氶惄绋跨安閻ㄥ嫭鏋冩禒锟�---------------------
		functionType_ = "PBI";
		rpType_       = "ideal";
//		if (convergenceFlag== true) 
//			  captureConvergence_ = new CaptureConvergence(problem_.getName(), "CTaMOEAD" + functionType_ + rpType_,
//					  problem_.getNumberOfObjectives());	
		// ------------鐠囪褰噁unction閸滃p閻ㄥ嫮琚崹瀣剁礉閻㈢喐鍨氶惄绋跨安閻ㄥ嫭鏋冩禒璁圭礄end閿涳拷---------------------
				
		
		System.out.println("=============Running CTs-MOEA R1 eps=============");
		long elapsed = 0, start = System.currentTimeMillis(); // start
		
		evaluations_ = 0;	
		int generations_ = 0;
		
		// 鐠囪褰囩紒鍫燁剾閺夆�叉
		maxEvaluations_ = ((Integer) this.getInputParameter("maxEvaluations")).intValue();		
		//Long maxRunTimeMS = ((Long) this.getInputParameter("maxRunTimeMS")).longValue();			
				
		System.out.println(maxEvaluations_);
		populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
		archiveSize_    = populationSize_;
		// ----------------------------------------------------------
		// Used only for random weight vectors, should be modified
//		while (populationSize_ % 4 > 0)
//			populationSize_ += 1;	
		// ----------------------------------------------------------
		
		System.out.println("CTs-MOEA Release eps maxEvaluations = " +  maxEvaluations_);		
		System.out.println("CTs-MOEA Release eps popsize = " +  populationSize_ );		
		
		
		dataDirectory_ = this.getInputParameter("dataDirectory").toString();		
	    T_ = ((Integer) this.getInputParameter("T")).intValue();
	    nr_ = ((Integer) this.getInputParameter("nr")).intValue();
	    delta_ = ((Double) this.getInputParameter("delta")).doubleValue();
	    theta_ = ((Double) this.getInputParameter("theta")).doubleValue();
	    objectives_ = problem_.getNumberOfObjectives();
	    
		population_ = new SolutionSet(populationSize_);
		feasiblePopulation_ = new SolutionSet(populationSize_);
		bestPopulation_   = new SolutionSet(populationSize_);
		
		// Add null
		for(int i = 0; i < populationSize_; i++) {
			feasiblePopulation_.add(null);
		}
		
		offspringPopulation_ = new SolutionSet(populationSize_);
		union_               = new SolutionSet(2 * populationSize_);
		
		archive_    = new SolutionSet(archiveSize_);
	
		neighborhood_ = new int[populationSize_][T_];		
		lambda_ = new double[populationSize_][objectives_];
			
		zp_     = new double[objectives_];
		z_ideal = new double [objectives_];
		z_nadir = new double [objectives_];
		zmax    = new double [objectives_]; // Used in normalization
		feasiableZideal = new double [objectives_]; 
				
		for (int j=0; j < objectives_; j++) {
			z_ideal[j] = 1e+30; 
			z_nadir[j] = -1e+30;
			feasiableZideal[j] =  1e+30; 
		} // Initialize z_ideal and z_nadir
								
		newWeights_= new WeightSet(populationSize_); // Store historical and new weights populationSize_
		
		crossover_ = operators_.get("crossover"); 	// default: SBX crossover
		mutation_ = operators_.get("mutation"); 	// default: polynomial mutation
		crossoverDE_ = operators_.get("crossoverDE");
		
		long initTime = System.currentTimeMillis();	
		
		// STEP 1. Initialization		
		// STEP 1.1. Compute Euclidean distances between weight vectors and find T
		initUniformWeight();		
//		initRandomWeight();		
		initNeighborhood();

		// STEP 1.2. Initialize population
		initPopulation(); // Initialize N random solutions					
		generations_ = 1;			
		
		if (convergenceFlag== true) 
			captureConvergence_.runCaptureConvergence(0, population_);		
		
		boolean reset = false;
		int gap = 2;
			
		int changeTimes = 0;
		int fesCut = 0;
		
		double epsilon_0_ = 0;
		epsilon_k_ = epsilon_0_;
        int tc_ = (int) (0.8 * maxEvaluations_ / populationSize_);
        double r_k_ = population_.GetFeasible_Ratio();
        double cp_ = 2.0;
        
		// STEP 2. Update
		do {		
			
	        if(generations_ < tc_) {    
                if (stage2_) {
                    if (r_k_ < alpha_) {
                        epsilon_k_ = (1 - tao_) * epsilon_k_;
                    } else {
                        epsilon_k_ = epsilon_0_ * Math.pow((1 - 1.0 * generations_ / tc_), cp_);
                    }
                }

            } else {
                    epsilon_k_ = 0;
            }	        

//	        System.out.println(epsilon_k_);
	        
			// ---------------Stage 1: Evolve (considering convergence and diversity)--------------------------
			if (!stage2_ ) { // The first stage
				generateOffspring(); 		// Generate N offspring
				union_ = population_.union(offspringPopulation_);
				normalize(union_);          // Normalize parent and offspring population	
				select(union_);
				updateFeasiblePopulation(); // Update feasible solutions
			}
				
			if (stage2_) { // the second stage
				normalize_pop(population_);			
				initialize_RP();
				evolution();	
			}
			
		
			if (!stage2_ && (unsuccessfulTrailsForFeasible_ > gap * populationSize_||evaluations_ >= 0.5 * this.maxEvaluations_)) {	// || 
				stage2_ = true;			
				unsuccessfulTrailsForFeasible_ = 0;
				reconstructPopulation();
				changeTimes++;
				System.out.println("---Stage 2---");
				fesCut = evaluations_;
				
				// Find max OverallConViolation
				epsilon_0_ = Math.abs(population_.MaxOverallConViolationNew());
							
                epsilon_k_ = epsilon_0_;
                System.out.println(epsilon_k_);
			}

//			if (changeTimes <= 2 && !stage2_ && (unsuccessfulTrails_ > gap * populationSize_ && evaluations_ > 0.5 * this.maxEvaluations_)) {	//
//				stage2_ = true;
//				unsuccessfulTrails_ = 0;
//				reconstructPopulation();
//				changeTimes++;
//				System.out.println("Stage 2");
//			}
//			
//			
//			if (changeTimes <= 2 && stage2_ && (unsuccessfulTrailsForFeasible_ > gap * populationSize_ && evaluations_ > 0.5 * this.maxEvaluations_)) {	//
//				stage2_ = false;		
//				unsuccessfulTrailsForFeasible_ = 0;		
//				reconstructPopulation1();				
////				union_ = population_.union(bestPopulation_);
////				normalize(union_);         // Normalize parent and offspring population	
////				select(union_);					
//				changeTimes++;
//				System.out.println("Stage 1 ");
//			}
				
			// ----------------------------Update archive--------------------------
			if (generations_ % gap == 0 && stage2_) {
				updateArchive(population_);		//better than feasiblePopulation_
			}
			
//			// ---------------------------Update weights-----------------------
//			if (generations_ % gap == 0 && evaluations_ > 0.5 * maxEvaluations_ && evaluations_ < 0.9 * maxEvaluations_) {//
//				computeDistance2RP(population_); 
//				initializeAngleMatrix(population_);				
//				
//				calculateCrowdingDistanceDirect(population_); 
//				updateWeightsBasedOnCrowdingDistanceR1();						
//				initNeighborhood();	
//			}

			// --------------------Evolve archived solutions----------------
			if (evaluations_ >= 0.8 * maxEvaluations_ && reset == false){		
				if (archive_.size() > 0) {				
					union_ = population_.union((archive_));
					normalize(union_);         // Normalize parent and offspring population	
					select(union_);				
					reset = true;
				}		
			}		
			
			r_k_ = population_.GetFeasible_Ratio();
	
			generations_++;
			elapsed = System.currentTimeMillis() - start;						
//			System.out.println(evaluations_);
		} while (evaluations_ < maxEvaluations_);
		
				
	    System.out.println("RunTimeMS: " + elapsed);
        System.out.println("evaluations: " + evaluations_);
        updateArchive(population_);
//        updateArchive(bestPopulation_);
//        updateArchive(removeNull(feasiblePopulation_));
        
        if(archive_.size() == 0) 
        	archive_ = population_;
        
        // At last remove identical solutions 
        SolutionSet finalSet = removeSame(this.archive_);    
//        SqlUtils.InsertSolutionSet(problemName,population_);
        Ranking ranking = new Ranking(finalSet);
        System.out.println("Final size = " + ranking.getSubfront(0).size());
        return ranking.getSubfront(0);        
//         return population_ ;//閻ㄥ嫬甯崶鐘虫Ц閸︹墽unExperiment缁鑵戦張澶庣箻鐞涘苯鎮撻弽椋庢畱婢跺嫮鎮婇妴鍌欑瑐鏉╂澘顦╅悶鍡曠矌娑撹桨绨℃笟澶哥艾鐟欏倸鐧傞幀褑鍏�
	}

	/**
	 * 
	 */
	public void reconstructPopulation() {
//		bestPopulation_.clear();
		
		// Update best solution population
		for (int i = 0; i < populationSize_;i++) {			
			
			int flag = 0; 
			flag = dominance_.compare(population_.get(i), bestPopulation_.get(i))  ;
			
			if (flag ==-1) 
				bestPopulation_.replace(i, population_.get(i));
			else if (flag == 0)
				if (fitnessFunction(population_.get(i), lambda_[i], i) < fitnessFunction(bestPopulation_.get(i), lambda_[i], i))
					bestPopulation_.replace(i,population_.get(i));		
		}
		
		//-----------------------------------------------------------------
		for (int i = 0; i < populationSize_;i++) {
			
			if (!isfeasible(population_.get(i)) && feasiblePopulation_.get(i)!=null) {
				population_.replace(i, feasiblePopulation_.get(i));	
			}
		}
	}
	
	public void reconstructPopulation1() {
	
		for (int i = 0; i < populationSize_;i++) {		
			
			int flag = 0; 
			flag = dominance_.compare(bestPopulation_.get(i), population_.get(i))  ;
			
			if (flag ==-1) 
				population_.replace(i, bestPopulation_.get(i));		
			else if (flag == 0)
				if (fitnessFunction(population_.get(i), lambda_[i], i) > fitnessFunction(bestPopulation_.get(i), lambda_[i], i))
					population_.replace(i, bestPopulation_.get(i));	
						
		}
	}

	
	public SolutionSet removeNull(SolutionSet feasiblePopulation){
		SolutionSet result = new SolutionSet(feasiblePopulation.size());
		
		for (int i = 0; i < feasiblePopulation.size();i++) {
			if (feasiblePopulation.get(i) != null)
				result.add(feasiblePopulation.get(i));
		}
		return result;
	}
	
	
	
	public void updateFeasiblePopulation() {
		// Update the best feasible population
		for (int k = 0; k < populationSize_;k++) {
			
			Solution indiv = population_.get(k);
			
			if (isfeasible(indiv)) { // indiv is feasible 
				
				int flagBetter = 0;
				
				if (feasiblePopulation_.get(k) == null)  
					flagBetter = -1;
				else {
					flagBetter = dominance_.compare(indiv, feasiblePopulation_.get(k));
					
					if (flagBetter == 0) {
						double f1, f2;	
						f1 = fitnessFunction(feasiblePopulation_.get(k), lambda_[k],k);
						f2 = fitnessFunction(indiv, lambda_[k],k);
						
						if (f2 < f1) {
							flagBetter = -1;
						}
					}
					
				}
				
				if (flagBetter == -1) {// indiv is better 
					feasiblePopulation_.replace(k, indiv);			
					// Update feasiable zideal
					
					if (updateFeasibleZideal(indiv)) {
						unsuccessfulTrailsForFeasible_ = 0;
					} else {
						unsuccessfulTrailsForFeasible_++;
					}
				
				}  	
			}
		}
		
		
		
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
	
	
	public double [][] projectSolution2Plant(SolutionSet population) {
		double [][] projectedPoints = new double [population.size()][objectives_];
		
		for (int i = 0; i < population.size(); i++) {
			   Solution sol = population.get(i);
			   double [] normalizedObj = new double[objectives_];
			   normalizedObj = sol.getNormalizedObjectives();
			   double [] tempVector = new double[objectives_];
			   
			   double sumDiff = 0.0;
			   
			   for (int j = 0; j < objectives_; j++) {
				   sumDiff = sumDiff + (normalizedObj[j] - zp_[j]);
			   } // for j	   
			   		
			   // Intersection method
			   for (int j = 0; j < objectives_; j++) {					   
				   tempVector[j] = (1 - objectives_ * zp_[j]) * ((normalizedObj[j] - zp_[j]) / sumDiff) + zp_[j];
				   
//				   if (tempVector[j] > -1e-6 && tempVector[j] < 0.0) 
//					   tempVector[j] = 0.0;
			   } // for j	
			   
			   projectedPoints[i] = tempVector;
			   
		} // for i
		
		return projectedPoints;
	}
	
	/**
	 * Calculate crowding distance using angle, based on neighbor
	 * @param population
	 */
	public void calculateCrowdingDistance(SolutionSet population) {
		
		for (int i = 0; i < populationSize_; i++) {
			Solution sol = population_.get(i);
			double sumAngle = 0.0;
			
			for (int j = 0; j < T_; j++) { // for each neighbor
				sumAngle = sumAngle + (AngleMatrix[i][neighborhood_[i][j]]);
			}
		    
			sol.setCrowdingDistance(sumAngle/T_);
//			System.out.println("i = " + i + "CD = " + sumAngle/T_ );
		}
	}
	
	/**
	 * Calculate crowding distance using angle, based on neighbor
	 * @param population
	 */
	public double [] calculateSelectProb(SolutionSet population) {
		double [] prob = new double[population.size()];
		double maxCD = -1e30;
		double minCD = 1e30;
		
		for (int i = 0; i < populationSize_; i++) {
			Solution sol = population_.get(i);
			double cd = sol.getCrowdingDistance();
			
			if (cd > maxCD) {
				maxCD = cd;
			}
			
			if (cd < minCD) {
				minCD = cd;
			}
		}
		
		for (int i = 0; i < populationSize_; i++) {
			prob[i] = 1 - ( population_.get(i).getCrowdingDistance() - minCD)/(maxCD - minCD);
//			System.out.println(prob[i] );
			if (maxCD == minCD) {
				System.out.println("maxCD == minCD in calculateSelectProb");
				prob[i] = 1.0;
			}
		}
		return prob;
	}
	
	/**
	 * Calculate crowding distance using angle, based on solutions directly
	 * @param population
	 */
	public void calculateCrowdingDistanceDirect(SolutionSet population) {
		
		for (int i = 0; i < populationSize_; i++) {
			Solution sol = population_.get(i);
			double sumAngle  = 0.0;
			double [] angles = new double [populationSize_];
			int    [] idx    = new int [populationSize_];
			
			System.arraycopy(AngleMatrix[i], 0, angles, 0, populationSize_);
			Utils.minFastSort(angles, idx, populationSize_, T_);	
			
			for (int j = 0; j < T_; j++) { // for each neighbor
				sumAngle = sumAngle + (AngleMatrix[i][idx[j]]);
			}
		    
			sol.setCrowdingDistance(sumAngle/T_);

		}
	}
	
	
	public void replaceWeightBasedOnCrowdingDistance() {
		double minCD = 1E30;
		int minCDIndex = -1;
		
		for (int i = 0; i < populationSize_; i++) {
			if (population_.get(i).getCrowdingDistance() < minCD) {
				minCD = population_.get(i).getCrowdingDistance();
				minCDIndex = i;
			}
		}

		System.out.println(minCDIndex);
		
		 double [] normalizedObj = new double[objectives_];
		 normalizedObj = population_.get(minCDIndex).getNormalizedObjectives();
		 double [] tempVector = new double[objectives_];
		   
		 double [] rp = new double[objectives_];
		 for (int j = 0; j < objectives_; j++) {
			   rp[j] = 0.0;
		 } // for j	  
		   
		 double sumDiff = 0.0;
		   
		 for (int j = 0; j < objectives_; j++) {
			   sumDiff = sumDiff + (normalizedObj[j] - rp[j]);
		 } // for j	   
		   		 
		 boolean validWeight = true;
		   
		   // Intersection method
		   for (int j = 0; j < objectives_; j++) {					   
			   tempVector[j] = (1 - objectives_ * rp[j]) * ((normalizedObj[j] - rp[j]) / sumDiff) + rp[j];
			   
			   if (tempVector[j] < -1e-6) {		
				   validWeight = false;
			   }
		   } // for j		
		   
		   if (validWeight == false) {
			   tempVector = getRandomWeights(objectives_);	
	       }
		   
		   this.lambda_[minCDIndex] = tempVector; 

	}
		
	
	public void evolution() throws JMException {
		//--------------------------------------------------------
		int[] permutation = new int[populationSize_];
		Utils.randomPermutation(permutation, populationSize_);
	
		for (int i = 0; i < populationSize_; i++) {
			int n = permutation[i];
			evolve(n);	
		} // for		
		//---------------------------------
	}
	
	public void evolve(int n) throws JMException {
		int type;
		double rnd = PseudoRandom.randDouble();

		// STEP 2.1. Mating selection based on probability
		if (rnd < delta_) // if (rnd < realb)
		{
			type = 1; // neighborhood
		} else {
			type = 2; // whole population
		}
		
		Vector<Integer> p = new Vector<Integer>();
		matingSelection(p, n, 2, type);
			      
		/**
		 * --------------------Use crossover and mutation (or update)--------------------
		 */
//		Solution[] parents = new Solution[2];
//		parents[0] = population_.get(p.get(0));
//		parents[1] = population_.get(p.get(1));
//		
//		Solution[] childs = (Solution[]) crossover_.execute(parents);
//
//		Solution child = null;
//		
//		double rndSel =  PseudoRandom.randDouble();				
//		if (rndSel < 0.5) {
//			child = childs[0];
//        } else 
//        	child = childs[1];
		 
		
		Solution child = null;
		 // Apply Crossover for Real codification
       if (crossover_.getClass().getSimpleName().equalsIgnoreCase("SBXCrossover")) {
           Solution[] parents = new Solution[2];
           parents[0] = population_.get(p.get(0));
           parents[1] = population_.get(n);
           child = ((Solution[]) crossover_.execute(parents))[0];
       }
       // Apply DE crossover
       else if (crossover_.getClass().getSimpleName().equalsIgnoreCase("DifferentialEvolutionCrossover")) {
           Solution[] parents = new Solution[3];
           parents[0] = population_.get(p.get(0));
           parents[1] = population_.get(p.get(1));
           parents[2] = population_.get(n);  
           child = (Solution) crossover_.execute(new Object[]{population_.get(n), parents});
        
//		   Operator selectionOperator = operators_.get("selection");   		
//           Solution[]  parent = (Solution [])selectionOperator.execute(new Object[]{population_, n});
//           // Crossover. Two parameters are required: the current individual and the  array of parents
//           child = (Solution)crossoverDE_.execute(new Object[]{population_.get(n), parent}) ;

       } else {
           System.out.println("unknown crossover");
       }
		
		// Apply mutation
		mutation_.execute(child); 
		 // --------------------Use crossover and mutation (end)--------------------
						
		// STEP 2.3. Repair. 
		
		// Evaluation
		problem_.evaluate(child);
		problem_.evaluateConstraints(child);
		evaluations_++;		
		
		if (convergenceFlag== true) 
			captureConvergence_.runCaptureConvergence(evaluations_, population_);
		
		normalize_ind(child);				
		update_RP(child);
		
		// STEP 2.5. Update of solutions
		updateProblem(child, n, type);
	}
		
	
	public void evolve2(int n) throws JMException {
		int type;
		double rnd = PseudoRandom.randDouble();

		// STEP 2.1. Mating selection based on probability
		if (rnd < delta_) // if (rnd < realb)
		{
			type = 1; // neighborhood
		} else {
			type = 2; // whole population
		}		
		
		// STEP 2.5. Update of solutions
		updateProblem(offspringPopulation_.get(n), n, type);
	}
	/**
	 * Find the index of the min value in an array
	 * 
	 * @param array
	 * @return index
	 */
	public int findMin(double[] array) {
		int index;
		double minValue;

		index = 0;
		minValue = array[0];

		for (int j = 1; j < array.length; j++) {
			if (array[j] < minValue) {
				minValue = array[j];
				index = j;
			} //
		} // for
		return index;
	} //
	
	/**
	 * Update weights
	 * @throws ClassNotFoundException 
	 * @throws JMException 
	 */
	public void updateWeightsBasedOnCrowdingDistance() throws ClassNotFoundException, JMException {

		int replaceNumber           = 0; // To be replaced number	
		double [] crowdingDistances = new double [populationSize_];
		int[]       idx             = new int[populationSize_];
		int[]       max_idx         = new int[populationSize_];
				
		for (int i = 0; i < populationSize_; i++) {
			crowdingDistances[i] = population_.get(i).getCrowdingDistance() ;	
			idx[i]               = i;
			max_idx[i]           = i;			
		}
		
		double [] tempArray = new double [populationSize_];

		/* find the first replaceNumber minimum crowding distances*/
		System.arraycopy(crowdingDistances, 0, tempArray, 0, populationSize_);
		Utils.minFastSort(tempArray, idx, populationSize_, T_);	
				
		System.arraycopy(crowdingDistances, 0, tempArray, 0, populationSize_);
		Utils.maxFastSort(tempArray, max_idx, populationSize_,  T_);

//		computeExtremePoints(population_);		
		
		int[] permMax = new int[T_];
		Utils.randomPermutation(permMax, T_);
				
		for (int i = 0; i < T_; i++) {
			if (crowdingDistances[idx[i]] < 1e-3) { //&& PseudoRandom.randDouble() < 0.5	
				replaceNumber ++;
			}
		}	
		if (replaceNumber == 0)  replaceNumber = 1;
		
//		replaceNumber = (int)((double)T_/(this.objectives_ - 1));
//		if (this.objectives_ <5) 
//			replaceNumber = (int)(T_/2.0);
		
		boolean [] inActiveFlag = new boolean [populationSize_];			
					
		for (int k = 0; k < T_; k++) {
			
			int maxCDIndex            = max_idx[permMax[k]];
			inActiveFlag [maxCDIndex] = true; // 鏉╂瑤閲滃В鏃囩窛闁插秷顩�

			double [] currentWeight = new double [objectives_];	
			currentWeight = lambda_[maxCDIndex]; 
			
			// Generate T new weights around maxCDIndex		
			for (int i = T_ - 1; i < T_; i++) { // Can't start from 0, but 1
					
					double [] newWeight     = new double [objectives_];
					double [] neighorWeight = new double [objectives_];
					
					neighorWeight = lambda_[neighborhood_[maxCDIndex][i]];
					          
					for (int j = 0; j < objectives_; j++) {
						newWeight[j] = 0.5 * (currentWeight[j] + neighorWeight[j]);
					}
					
					
					Weights temp = new Weights(objectives_);
					temp.setWeight(newWeight);
					temp.setWeightType("unevaluated");
														
					if (newWeights_.size() == populationSize_) {
						newWeights_.remove(0);				
					}			
					
					newWeights_.add(temp);	
			}
				
		} // for i
		
		double  [] closestDistance = new double [newWeights_.size()];
		int     [] closestIndex    = new int    [newWeights_.size()];
		boolean [] selected        = new boolean[newWeights_.size()];
		
		for (int i = 0; i < newWeights_.size();i++) {
			double minDistance = 1e30;
			int minDistanceIndex = -1;
			
			for (int j = 0; j < populationSize_; j++) {
				if (inActiveFlag[j] == false) {
					double dist = Utils.distVector(newWeights_.get(i).getWeight(), lambda_[j]);		    
				    if (dist  < minDistance) {
				    	minDistance = dist;
				    	minDistanceIndex = j;
				    } // if
				}
			} // for j
			
			if (minDistanceIndex == -1) return;
	
			closestDistance[i] = minDistance;
			closestIndex[i]    = minDistanceIndex;
		} // for i
					
		for (int cycle = 0; cycle < replaceNumber; cycle++) { // Repeat replaceNumber times
	
			double maxDistance = -1e30;
			int maxDistanceIndex = -1;
			
			for (int i = 0; i < newWeights_.size(); i++) {			
				if (selected[i] == false && inActiveFlag[closestIndex[i]] == false) {
					if (closestDistance[i] >  maxDistance) {
						maxDistance = closestDistance[i];
						maxDistanceIndex = i;
					}
				}// if
			} // for i
			
			if (maxDistanceIndex == -1) return;
			
			selected[maxDistanceIndex] = true;						
	
			int position = idx[cycle];	
			
			lambda_[position] = newWeights_.get(maxDistanceIndex).getWeight();
					
			if (newWeights_.get(maxDistanceIndex).getWeightType().equalsIgnoreCase("unevaluated")) {
				Solution sol = new Solution(problem_);	
				problem_.evaluate(sol);
				problem_.evaluateConstraints(sol);
				evaluations_++;				
				normalize_ind(sol);				
				update_RP(sol);
				newWeights_.get(maxDistanceIndex).setWeightType("evaluated");
				newWeights_.get(maxDistanceIndex).setSolution(sol);
			}			
			
			population_.replace(position, newWeights_.get(maxDistanceIndex).getSolution());	
				
			// Update distances			
			for (int i = 0; i < newWeights_.size(); i++) {			
				if (selected[i] == false ) {
					double dist = Utils.distVector(newWeights_.get(i).getWeight(), lambda_[position]);
					if (dist < closestDistance[i]) {
						closestDistance[i] = dist;
						closestIndex[i]    = position;
					}
				}// if
			} // for i
		
		} // for cycle		
			
		//------------------------------------------------------------------------
		WeightSet deselected = new WeightSet(newWeights_.size());		
		for (int i = 0; i < newWeights_.size(); i++) {		
		
			if (selected[i] == false) {
				deselected.add(newWeights_.get(i));
			}
		}
				
		newWeights_.clear();		
		for (int i = 0; i < deselected.size(); i++) {
			newWeights_.add(deselected.get(i));
		}
		//------------------------------------------------------------------------	
	}
	

/**
	 * Update weights
	 * @throws ClassNotFoundException 
	 * @throws JMException 
	 */
	public void updateWeightsBasedOnCrowdingDistanceR1() throws ClassNotFoundException, JMException {

		int replaceNumber           = 0; // To be replaced number	
		double [] crowdingDistances = new double [populationSize_];
		int[]       idx             = new int[populationSize_];
		int[]       max_idx         = new int[populationSize_];
				
		for (int i = 0; i < populationSize_; i++) {
			crowdingDistances[i] = population_.get(i).getCrowdingDistance() ;	
			idx[i]               = i;
			max_idx[i]           = i;			
		}
		
		double [] tempArray = new double [populationSize_];

		/* find the first replaceNumber minimum crowding distances*/
		System.arraycopy(crowdingDistances, 0, tempArray, 0, populationSize_);
		Utils.minFastSort(tempArray, idx, populationSize_,  T_);	
				
		System.arraycopy(crowdingDistances, 0, tempArray, 0, populationSize_);
		Utils.maxFastSort(tempArray, max_idx, populationSize_, 2*T_);

//		computeExtremePoints(population_);		
		
//		int[] permMin = new int[ 2*T_];
//		Utils.randomPermutation(permMin,  2*T_);
		
//		int[] permMax = new int[ 2*T_];
//		Utils.randomPermutation(permMax,  2*T_);
		
		boolean [] inActiveFlag = new boolean [populationSize_];			
		
		for (int i = 0; i <  T_; i++) {
			inActiveFlag [idx[i]] = true; // 杩欎釜姣旇緝閲嶈
			
			if (crowdingDistances[idx[i]] < 1e-3) { //&& PseudoRandom.randDouble() < 0.5	
				replaceNumber ++;
			}
		}	
		
		if (replaceNumber == 0)  replaceNumber = 1;
		
//		System.out.println(replaceNumber);
//		replaceNumber = (int)((double)T_/(this.objectives_ - 1));
//		if (this.objectives_ <=5) 
//			replaceNumber = (int)(T_/2.0);
//		

					
		for (int k = 0; k <  2 * T_; k++) { // Generate T weightS
			
			int maxCDIndex            = max_idx[k];
			
//			inActiveFlag [maxCDIndex] = true; // 杩欎釜姣旇緝閲嶈

			double [] currentWeight = new double [objectives_];	
			currentWeight = lambda_[maxCDIndex]; 
			
			// Generate T new weights around maxCDIndex		
			for (int i = T_ - 1; i < T_; i++) { // Can't start from 0, but 1
					
					double [] newWeight     = new double [objectives_];
					double [] neighorWeight = new double [objectives_];
					
					neighorWeight = lambda_[neighborhood_[maxCDIndex][i]];
					          
					for (int j = 0; j < objectives_; j++) {
						newWeight[j] = 0.5 * (currentWeight[j] + neighorWeight[j]);
					}
					
					
					Weights temp = new Weights(objectives_);
					temp.setWeight(newWeight);
//					temp.setWeightType("unevaluated");
														
					if (newWeights_.size() == populationSize_) {
						newWeights_.remove(0);				
					}			
					
					newWeights_.add(temp);	
			}
				
		} // for i
		
		double  [] closestDistance = new double [newWeights_.size()];
		int     [] closestIndex    = new int    [newWeights_.size()];
		boolean [] selected        = new boolean[newWeights_.size()];
		
		for (int i = 0; i < newWeights_.size();i++) {
			double minDistance = 1e30;
			int minDistanceIndex = -1;
			
			for (int j = 0; j < populationSize_; j++) {
				if (inActiveFlag[j] == false) {
					double dist = Utils.distVector(newWeights_.get(i).getWeight(), lambda_[j]);		    
				    if (dist  < minDistance) {
				    	minDistance = dist;
				    	minDistanceIndex = j;
				    } // if
				}
			} // for j
			
			if (minDistanceIndex == -1) return;
	
			closestDistance[i] = minDistance;
			closestIndex[i]    = minDistanceIndex;
		} // for i
					
		for (int cycle = 0; cycle < replaceNumber; cycle++) { // Repeat replaceNumber times
	
			double maxDistance = -1e30;
			int maxDistanceIndex = -1;
			
			for (int i = 0; i < newWeights_.size(); i++) {			
				if (selected[i] == false && inActiveFlag[closestIndex[i]] == false) {
					if (closestDistance[i] >  maxDistance) {
						maxDistance = closestDistance[i];
						maxDistanceIndex = i;
					}
				}// if
			} // for i
			
			if (maxDistanceIndex == -1) return;
			
			selected[maxDistanceIndex] = true;						
	
			int position = idx[cycle];	
			
			lambda_[position] = newWeights_.get(maxDistanceIndex).getWeight();
					
//			if (newWeights_.get(maxDistanceIndex).getWeightType().equalsIgnoreCase("unevaluated")) {
//				Solution sol = new Solution(problem_);	
//				problem_.evaluate(sol);
//				problem_.evaluateConstraints(sol);
//				evaluations_++;				
//				normalize_ind(sol);				
//				update_RP(sol);
//				newWeights_.get(maxDistanceIndex).setWeightType("evaluated");
//				newWeights_.get(maxDistanceIndex).setSolution(sol);
//			}			
//			
//			population_.replace(position, newWeights_.get(maxDistanceIndex).getSolution());	
				
			// Update distances			
			for (int i = 0; i < newWeights_.size(); i++) {			
				if (selected[i] == false ) {
					double dist = Utils.distVector(newWeights_.get(i).getWeight(), lambda_[position]);
					if (dist < closestDistance[i]) {
						closestDistance[i] = dist;
						closestIndex[i]    = position;
					}
				}// if
			} // for i
		
		} // for cycle		
			
		//------------------------------------------------------------------------
		WeightSet deselected = new WeightSet(newWeights_.size());		
		for (int i = 0; i < newWeights_.size(); i++) {		
		
			if (selected[i] == false) {
				deselected.add(newWeights_.get(i));
			}
		}
				
		newWeights_.clear();		
		for (int i = 0; i < deselected.size(); i++) {
			newWeights_.add(deselected.get(i));
		}
		//------------------------------------------------------------------------	
	}
	  // Adaptive adaptiveAlpha 
	  private double adaptiveTheta(int fes, int mfes, double max, double min) {	  
	    return min + (((max-min)*(double)fes)/(double)mfes);
	  } // adaptiveTheta
	  
	  // Adaptive eps 
	  private void adaptiveEps(int fes, int mfes, double max, double min) {	  
	    eps_ =  max + (((min - max)*(double)fes)/(double)mfes);
	  } // adaptiveTheta
	  
		/**
		 * Initialize angle matrix
		 */
		public void initializeAngleMatrix(SolutionSet solutionSet) {
			// solutionSet.sort(new FitnessComparator());
			AngleMatrix = new double[solutionSet.size()][solutionSet.size()];
			for (int i = 0; i < solutionSet.size(); i++) {
				solutionSet.get(i).setID(i);// Give a solution a unique ID
			}

			for (int i = 0; i < solutionSet.size(); i++) {

				Solution soli = solutionSet.get(i);

				for (int j = i; j < solutionSet.size(); j++) {

					if (i == j) {
						AngleMatrix[i][i] = 0.0;
					} else {
						Solution solj = solutionSet.get(j);
						AngleMatrix[i][j] = calAngle(soli, solj);
						AngleMatrix[j][i] = AngleMatrix[i][j];	
					}

				} // for j

			} // for i

		} // initializeAngleMatrix
						
	
	public SolutionSet removeRepetedSolutions(SolutionSet population) {
		 // At last remove identical solutions 
	    SolutionSet finalSet = new SolutionSet(population.size());        
	    finalSet.add(population.get(0));        
	  
	    for (int i = 1; i < population.size(); i++) {      
			      
			Solution sol = population.get(i);
			boolean existEqual = false;
			
			for (int j = 0; j < finalSet.size();j++) {
				if (equalSolution(sol, finalSet.get(j))) {
					existEqual = true;
					break;	    		
				}
			}
			
			if (existEqual == true) continue;
		
			finalSet.add(population.get(i));	 
			
		} // for	
	    return finalSet;
	}
       
    
	
	/**
	 * Update archive using the current population
	 * @param population
	 * @throws ClassNotFoundException 
	 */
	public void updateArchive(SolutionSet population) throws ClassNotFoundException {
		// Step 1: Get the merged population
		SolutionSet union_ = archive_.union(population);
		/** 
		 * ---------------Normalization part-------------------
		 */
		// Use solutions in union_ set to find z_ideal and zmax
		computeIdealPoint(union_); // Compute z_ideal, the estimated ideal point 
		computeMaxPoint(union_);  // Compute zmax, the max point of the current population
				
		computeExtremePoints(union_); // Compute extreme solutions
		computeIntercepts(); // Compute current intercepts
		updateNadir();  // Update z_nadir based current intercepts and previous nadir
				
		normalizePopulation(union_);
		//---------------Normalization part end -------------------
		 
		// Add solutions 
		archive_.clear();
		SolutionSet candidates = null; // Candidates to be selected from	
		
		// Obtain feasible and infeasible solution sets 
		SolutionSet feasible, infeasible;
		feasible = getFeasibleSet(union_);			
		infeasible = getInfeasibleSet(union_);
		
		SolutionSet better;  
		SolutionSet worse = null;
		
		if (getNumberOfFeasibleSolutions(union_) <= archiveSize_) {				
			
			for (int i = 0; i < feasible.size();i++) {
				archive_.add(feasible.get(i));
			}
			
//			infeasible.sort(new OverallConstraintViolationComparator());
//			
//			int remain = archiveSize_ - archive_.size();
//			
//			for (int i = 0; i < remain;i++) {
//				archive_.add(infeasible.get(i));
//			}
			
		} else {
		
			SolutionSet[] betterAndWorse = splitBetterWorse(feasible);
			better = betterAndWorse[0];
			worse = betterAndWorse[1];	
				
			if (better.size() <= archiveSize_) {					
				
				for (int i = 0; i < better.size(); i++) {
					archive_.add(better.get(i));
				}	
					
//				worse.sort(new FitnessComparator());						
//				int t = 0;
//				while (archive_.size() < archiveSize_) {
//					archive_.add(worse.get(t));
//					t++;
//				}
	//			candidates = new SolutionSet(worse.size());
	//			candidates = worse; // union_ points to worse						
				
			} else {
				
				SolutionSet[] splitResults = splitDominatedNondominated(better); // 
			    
				SolutionSet nondominated = splitResults[0];
				SolutionSet dominated = splitResults[1];
//				
				// Estimate shapes 
//				estimateShapes(nondominated);
//				initialize_RP(); // Set zp_		
			
			    if (nondominated.size() <= archiveSize_) { 			
				   // Add all non-dominated solutions into archive_
					for (int i = 0; i < nondominated.size(); i++) {
						archive_.add(nondominated.get(i));
					}		
					
//					candidates = new SolutionSet(dominated.size());
//					candidates = dominated; // union_ points to dominated
					
//					dominated.sort(new FitnessComparator());						
//					int t = 0;
//					while (archive_.size() < archiveSize_) {
//						archive_.add(dominated.get(t));
//						t++;
//					}
							
			   }  else {
				    candidates = new SolutionSet(nondominated.size());
				    candidates = nondominated; // union_ points to nondominated					
			   }
			}	
		} // if else
	    
		if (candidates!= null) {
			computeDistance2RP(candidates); 
	
//			if (this.evaluations_ < 0.5 * this.maxEvaluations_ || worse.size() > 0) {	//
				initializeAngleMatrix(candidates);
				eliminate(candidates, archive_);
//			} else {				
//				MAF(archive_, candidates);	
//			}
		}		 
	} // update Archive
			
	
	/**
	 * Update archive using the current population
	 * @param population
	 * @throws ClassNotFoundException 
	 */
	public void updateArchiveR1(SolutionSet population) throws ClassNotFoundException {
		// Step 1: Get the merged population
		SolutionSet union_ = archive_.union(population);
		/** 
		 * ---------------Normalization part-------------------
		 */
		// Use solutions in union_ set to find z_ideal and zmax
		computeIdealPoint(union_); // Compute z_ideal, the estimated ideal point 
		computeMaxPoint(union_);  // Compute zmax, the max point of the current population
				
		computeExtremePoints(union_); // Compute extreme solutions
		computeIntercepts(); // Compute current intercepts
		updateNadir();  // Update z_nadir based current intercepts and previous nadir
				
		normalizePopulation(union_);
		//---------------Normalization part end -------------------
		
		// Add solutions 
		archive_.clear();
		SolutionSet candidates = null; // Candidates to be selected from	
				
		SolutionSet better;  
		SolutionSet worse = null;
		
		SolutionSet[] betterAndWorse = splitBetterWorse(union_);		
		better = betterAndWorse[0];
		worse  = betterAndWorse[1];
		
		if (better.size() <= archiveSize_) {					
			
			for (int i = 0; i < better.size(); i++) {
				archive_.add(better.get(i));
			}				
			return;				
		}
							
		// Obtain feasible and infeasible solution sets 
		SolutionSet feasible, infeasible;
		feasible = getFeasibleSet(better);			
		infeasible = getInfeasibleSet(better);
				
		if (getNumberOfFeasibleSolutions(better) <= archiveSize_) {				
			
			for (int i = 0; i < feasible.size();i++) {
				archive_.add(feasible.get(i));
			}
			return;
		}
							
		SolutionSet[] splitResults = splitDominatedNondominated(feasible); // 
	    
		SolutionSet nondominated = splitResults[0];
		SolutionSet dominated = splitResults[1];
	   
		if (nondominated.size() <= archiveSize_) { 			
		   // Add all non-dominated solutions into archive_
			for (int i = 0; i < nondominated.size(); i++) {
				archive_.add(nondominated.get(i));
			}	
			return;
		}			

//		estimateShapes(nondominated);
//		initialize_RP(); // Set zp_		
		
	    candidates = new SolutionSet(nondominated.size());
	    candidates = nondominated; // union_ points to nondominated					
				    
		if (candidates!= null) {
			computeDistance2RP(candidates); 
	
//			if ( worse.size() == 0) {	//
				initializeAngleMatrix(candidates);
				eliminate(candidates, archive_);
//			} else {				
//				MAF(archive_, candidates);	
//			}
		}		 
	} // update Archive
	
	/**
	 * Delete solutions
	 * 
	 * @param union
	 * @param pop
	 */
	public void eliminate(SolutionSet union, SolutionSet pop) {
//		System.out.println("eliminate");
//		union.sort(new FitnessComparator());
		// Step: 1 Initialize angle matrix
		boolean [] removed = new boolean[union.size()];
		double [] minAngleArray = new double[union.size()];
		boolean[] isExtreme = new boolean[union.size()];
        boolean [] considered = new boolean[union.size()];
	    int [] removedSolutions = new int[archiveSize_];
	    int noOfRemoved = 0;
	    
		// Add extreme solutions
//	    int [] perm = (new jmetal.util.PermutationUtility()).intPermutation(objectives_);		
		for (int k = 0; k < objectives_; k++) {
			double[] vector = new double[objectives_];
			vector[k] = 1.0;
			double minAngle = 1e+30;
			int minAngleID = -1;

			for (int i = 0; i < union.size(); i++) {

				if (removed[i] == false) {
					Solution soli = union.get(i);
//					double angle = calAngle(soli, vector);
					double angle = this.calAngleFromIdealPoint(soli, vector);
					if (angle < minAngle) {
						minAngle = angle;
						minAngleID = i;
					}
				}
			} // for i
			
			// Find the cloest solution to minAngleID
			double minAng =  1.0e+30;
			int closedAngID = -1;		
				
			for (int j = 0; j < union.size();j++){				
				if (j == minAngleID) continue;
				if ( removed[j]== false && AngleMatrix[minAngleID][j] < minAng){
					minAng = AngleMatrix[minAngleID][j];
					closedAngID = j;
				} 
				
		    } // for j	
			
			if (union.get(closedAngID).getDistanceToIdealPoint() <= 0.5 *union.get(minAngleID).getDistanceToIdealPoint()) {
				minAngleID = closedAngID;
				System.out.println("closedAngID is better than minAngleID");
			}
			
			if (pop.size() >= archiveSize_) return;
			
			pop.add(new Solution(union.get(minAngleID)));
			removed[minAngleID] = true;
			isExtreme[minAngleID] = true;
			considered[minAngleID] = true;			
		} // for k
		
		boolean removedFlag = false;
		
		 // Find the minimum angle for each unadded solution
		for (int i = 0; i < union.size(); i++) {
			if (removed[i] == false) {
				double minAng = 1.0e+30;
				int minAngID = -1;

				for (int j = 0; j < union.size(); j++) {

					if (j == i)
						continue;

					if (removed[j] == false
							&& AngleMatrix[union.get(i).getID()][union.get(j).getID()] < minAng) {
						minAng = AngleMatrix[union.get(i).getID()][union.get(j).getID()];
						minAngID = j;
					}										

				} // for j		
			
				union.get(i).setClusterID(minAngID);
				union.get(i).setAssociateDist(minAng);
				minAngleArray[i] = minAng;
			} // if
		} // for i

		int remain = archiveSize_ - pop.size();
//		System.out.println("remain" + remain);
		// Remove solutions
		int numberOfLoops = union.size() - pop.size() - remain;
//		System.out.println("numberOfLoops" + numberOfLoops);
		
		if (removedFlag == true)
			numberOfLoops = (union.size() - this.objectives_) - pop.size() - remain;
		
		for (int r = 0; r < numberOfLoops; r++) {

			double minValue = 1.0e+30;
			int minValueID = -1;

			for (int i = 0; i < union.size(); i++) {
				if (removed[i] == true)
					continue;
				if (minAngleArray[i] <= minValue) { // 鍙栧皬閿熻妭鐚存嫹
					minValue = minAngleArray[i];
					minValueID = i;
				}
			} // for i

			int associatedId = union.get(minValueID).getClusterID();
			int removedID = -1;
			
			if (union.get(minValueID).getFitness() < union.get(associatedId).getFitness()) {

				removedID = associatedId;

			} else if (union.get(minValueID).getFitness() > union.get(associatedId).getFitness()) {

				removedID = minValueID;

			} else {
		
//				if (union.get(minValueID).getDistanceToIdealPoint() < union.get(associatedId).getDistanceToIdealPoint()){
//					removedID = associatedId;
//				} else if (union.get(minValueID).getDistanceToIdealPoint() > union.get(associatedId).getDistanceToIdealPoint()){
//					removedID = minValueID;					
//				} else {
					
					if (PseudoRandom.randDouble() < 0.5) {
						removedID = associatedId;
					} else {
						removedID = minValueID;
					}
					
//				}// IF 

			} // if
			
			removed[removedID] = true;
			
			removedSolutions[noOfRemoved] = removedID;
			noOfRemoved ++;
			
			considered[minValueID] = true;
			considered[associatedId] = true;
			
			// Update angles
			for (int i = 0; i < union.size(); i++) {
				if (removed[i] == true)
					continue;

				if (AngleMatrix[union.get(i).getID()][union.get(removedID).getID()] == union.get(i).getAssociateDist()) { // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓闃舵棦鍑ゆ嫹閿燂拷
					double minAng = 1.0e+30;
					int minAngID = -1;

					for (int j = 0; j < union.size(); j++) {

						if (j == i)
							continue;                        
											
						if (removed[j] == false	&& AngleMatrix[union.get(i).getID()][union.get(j).getID()] < minAng) {
							minAng = AngleMatrix[union.get(i).getID()][union.get(j).getID()];
							minAngID = j;
						}

					} // for j
					
//					if (minAngID == -1) {
//						System.out.println("union.size = " + union.size());
//						System.out.println("r = " + r);
//						System.out.println("numberOfLoops = " + numberOfLoops);
//						System.out.println(minAng);
//						System.out.println("here minAngID == -1");
//					}
					
					union.get(i).setClusterID(minAngID);
					union.get(i).setAssociateDist(minAng);
					minAngleArray[i] = minAng;
				}
			}

		} // for r							
		
		// Add remain solutions into pop
		for (int i = 0; i < union.size(); i++) {
			if (removed[i] == false && isExtreme[i] == false && pop.size() < archiveSize_) { //
				pop.add(union.get(i));
			}
		} // for i

	} // niching
	
	/**
	 * Get Feasible Set
	 * @return
	 */
	public SolutionSet getFeasibleSet(SolutionSet union) {
		SolutionSet feasible = new SolutionSet(getNumberOfFeasibleSolutions(union));
		
		for (int i = 0;i < union.size();i++) {
        	if (Math.abs(union.get(i).getOverallConstraintViolation()) <= 1e-4) {
        		feasible.add(union.get(i));
        	}
        }
		
		return feasible;
		
	}
	
	/**
	 * Get the number of feasible solutions
	 * @return
	 */
	public int getNumberOfFeasibleSolutions(SolutionSet union) {
		 
        int feasibleNo = 0;
        
        for (int i = 0;i < union.size(); i++) {
        	if (Math.abs(union.get(i).getOverallConstraintViolation()) <= 1e-4) {
        		feasibleNo ++;
        	}
        }
        
        return feasibleNo;
		
	} // getNumberOfFeasibleSolutions
	/**
	 * Get infeasible Set
	 * @return
	 */
	public SolutionSet getInfeasibleSet(SolutionSet union) {
		SolutionSet infeasible = new SolutionSet(2* populationSize_ - getNumberOfFeasibleSolutions(union));
		
		for (int i = 0;i < union.size();i++) {
        	if (Math.abs(union.get(i).getOverallConstraintViolation()) > 1e-4) {
        		infeasible.add(union.get(i));
        	}
        }
		
		return infeasible;
		
	}
	/**
	 * Select N solutions from union, including a boundary solutions handling method.  
	 * 
	 * @param union
	 * @param pop
	 */
	public void  environmentalSelection(SolutionSet union, SolutionSet pop) {		
		/*
		 * Bugs:
		 * 1. 閿熸枻鎷峰墠閿熸枻鎷穟nion_閿熸枻鎷烽敓鏂ゆ嫹閿熻妭闈╂嫹涓簎nion閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂竴閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
		 */

		union.sort(new FitnessComparator());

		// Step: 1
		boolean [] removed = new boolean[union.size()];
		double [] minAngleArray = new double[union.size()];
		boolean[] isExtreme = new boolean[union.size()];	
		int initalSize = pop.size();
		
		// Step 2: ***************** Directly add extreme solutions**************** start 
		for (int k = 0; k < objectives_; k++) {
			double[] vector = new double[objectives_];
			vector[k] = 1.0;
			double minAngle = 1e+30;
			int minAngleID = -1;			
			
			for (int i = 0; i < union.size(); i++) {

				if (removed[i] == false) {
					Solution soli = union.get(i);				
					double angle = calAngleFromIdealPoint(soli, vector); 

					if (angle < minAngle) {						
						minAngle = angle;
						minAngleID = i;
					} 
					
				} // if 
				
			} // for i					
	
			if (pop.size() >= populationSize_) {
				return;
			}
			
			pop.add(union.get(minAngleID));	

			removed[minAngleID] = true;
			isExtreme[minAngleID] = true;	// Indicate the extreme solutions 			

		} // for k			
		// ***************** Add extreme solutions**************** end 	
		
//		// Add extreme solutions 		 
//		 for (int k = 0; k < objectives_;k++) {
//			 double [] vector = new double [objectives_];
//			 vector [k] = 1.0;	
//			 double minAngle = 1e+30;
//			 int minAngleID = -1;
//			 
//			 for (int i = 0; i < union.size();i++) {			 
//				 
//				 if (removed[i] == false) {
//					 Solution soli = union.get(i);
//					 double angle = calAngle(soli,vector);
//					 
//					 if (angle < minAngle) {
//						 minAngle = angle;
//						 minAngleID = i;
//					 }
//				 }
//			 } // for i
//			 
//			double minAng =  1.0e+30;
//			int closedAngID = -1;		
//				
//			for (int j = 0; j < union.size();j++){				
//				if (j == minAngleID) continue;
//				if ( removed[j]== false && AngleMatrix[union.get(minAngleID).getID()][union.get(j).getID()] < minAng){
//					minAng = AngleMatrix[union.get(minAngleID).getID()][union.get(j).getID()];
//					closedAngID = j;
//				} 
//				
//		    } // for j		
//				
//			if (pop.size() >= populationSize_) {
//				return;
//			}
//			
//			if (union.get(minAngleID).getDistanceToIdealPoint() <= union.get(closedAngID).getDistanceToIdealPoint()) {
//				 pop.add(new Solution(union.get(minAngleID))) ;				 
//				 removed[minAngleID] = true;
//				 isExtreme[minAngleID] = true;		
//				 
//			} else if (union.get(minAngleID).getDistanceToIdealPoint() - union.get(closedAngID).getDistanceToIdealPoint() 
//					   < 0.5 * union.get(closedAngID).getDistanceToIdealPoint()) {
//				
//				 pop.add(new Solution(union.get(minAngleID))) ;				 
//				 removed[minAngleID] = true;
//				 isExtreme[minAngleID] = true;	
//				 
//			}  else {
//				 pop.add(new Solution(union.get(closedAngID))) ;				 
//				 removed[closedAngID] = true;
//				 isExtreme[closedAngID] = true;		
//			}
//		 } // for k	 
		 
		 
	    /*------------------------------------------------------------------------------------------------*/		
		// Step 2. Add better solutions in terms of the fitness value
//		if (this.nondominatedAdded == false) { // 閿熸枻鎷风洿閿熸帴纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鐨嗗ソ鐨勯潻鎷烽敓钘夛紝閿熸枻鎷烽敓鏂ゆ嫹閿熼樁纰閿熸枻鎷烽敓鏂ゆ嫹
//			int n = 0;
//			int t = 0;					
//			
//			double theta = adaptiveTheta(iteration_, maxIteration_, 0.5, 0.0);
//	//		theta = 1.0;
////			System.out.println(theta);
//			while ( n <  (int)(theta * populationSize_) && t < union.size()) { // 
//	            
//				if (removed[t] == false && isExtreme[t] == false && pop.size() < populationSize_) { // To avoid identical solutions 
//					
//					if (pop.size() >= populationSize_) {
//						return;
//					}
//					
//					pop.add(union.get(t));	
//					
//					removed[t] = true;
//					n++;				
//				}
//						
//				t ++;
//			} 
//		} 
//		
//		if (pop.size() >= populationSize_) {
//			return;
//		}
		
		// Step 3: Find the minimum angle for each solution
		for (int i = 0; i < union.size(); i++) {
			if ((removed[i] == false) ) {
				double minAng = 1.0e+30;
				int minAngID = -1;
								
				for (int j = 0; j < union.size(); j++) {

					if (j == i)
						continue;

					if (removed[j] == false && AngleMatrix[union.get(i).getID()][union.get(j).getID()] <= minAng) {

						minAng = AngleMatrix[union.get(i).getID()][union.get(j).getID()];
						minAngID = j;
					}										

				} // for j

				union.get(i).setClusterID(minAngID);
				union.get(i).setAssociateDist(minAng);
				minAngleArray[i] = minAng;				
				
			} // if
		} // for i

		// Step 4: Remove union.size() - remain solutions
		int remain = populationSize_ - pop.size();

		int cycles;		
				
		cycles = union.size() -  pop.size() - remain  + initalSize ; //
		
		for (int r = 0; r < cycles; r++) {

			double minValue = 1.0e+30;
			int minValueID = -1;

			for (int i = 0; i < union.size(); i++) {
				
				if (removed[i] == true)
					continue;
				if (minAngleArray[i] <= minValue) {	
					minValue = minAngleArray[i];
					minValueID = i;
				}
			} // for i

			int associatedId = union.get(minValueID).getClusterID();
			int removedID = -1;					
						
			
//			// ----------------------Use distance-----------------------
//			if (union.get(minValueID).getDistanceToReferencePoint() < union.get(
//					associatedId).getDistanceToReferencePoint()) {				
//				if (q_ > 0.9) 
//					removedID = associatedId;
//				else 
//					removedID = minValueID;
//
//			} else if (union.get(minValueID).getDistanceToReferencePoint() > union
//					.get(associatedId).getDistanceToReferencePoint()) {
//		
//				if (q_ > 0.9) 
//					removedID = minValueID;
//				else 
//					removedID = associatedId;
//				
//			} else {
//
//				if (PseudoRandom.randDouble() < 0.5) {
//					removedID = associatedId;
//				} else {
//					removedID = minValueID;
//				}
//
//			} // if	
			// ----------------------Use distance-----------------------
			
			// ----------------------Use fitness-----------------------
//			System.out.println(minValueID);
			if (union.get(minValueID).getFitness() < union.get(associatedId).getFitness()) {

				removedID = associatedId;

			} else if (union.get(minValueID).getFitness() > union.get(associatedId).getFitness()) {

				removedID = minValueID;

			} else {		
					if (PseudoRandom.randDouble() < 0.5) {
						removedID = associatedId;
					} else {
						removedID = minValueID;
					}
			} // if
			// ----------------------Use fitness-----------------------
						
			removed[removedID] = true;
		
			// Update angles
			for (int i = 0; i < union.size(); i++) {
				if (removed[i] == true)
					continue;

				if (union.get(i).getClusterID() == removedID) {
//				if (AngleMatrix[union.get(i).getID()][union.get(removedID).getID()] <= union.get(i).getAssociateDist()) {
					double minAng = 1.0e+30;
					int minAngID = -1;

					for (int j = 0; j < union.size(); j++) {

						if (j == i)
							continue;

						if (removed[j] == false && AngleMatrix[union.get(i).getID()][union.get(j).getID()] <= minAng) {

							minAng = AngleMatrix[union.get(i).getID()][union.get(j).getID()];
							minAngID = j;
						}

					} // for j

					union.get(i).setClusterID(minAngID);
					union.get(i).setAssociateDist(minAng);
					minAngleArray[i] = minAng;
				}
			}

		} // for r

		// Add remain solutions into pop
		for (int i = 0; i < union.size(); i++) {
			if (removed[i] == false && isExtreme[i] == false && pop.size() < populationSize_) { // 
				pop.add(union.get(i));
			}
		} // for i

	} // environmentalSelection
	/**
	 * Calculate the angle between a solution and a vector
	 * 
	 * @param s1
	 * @param vector
	 * @return
	 */
	public double calAngleFromIdealPoint(Solution s1, double[] vector) {
		double angle = 0.0;
		double norm1 = 0.0;
		double norm2 = computeNormFromIdealPoint(vector);

		double innerProduct = 0.0;

		for (int i = 0; i < objectives_; i++) {
			innerProduct += (s1.getNormalizedObjective(i)) * (vector[i]);
		}

		norm1 = s1.getDistanceToIdealPoint();
  
		double value = Math.abs(innerProduct / (norm1 * norm2));
		
		if (value > 1.0) {
			value = 1.0;
		}
		
		angle = Math.acos(value);
		
		Float f = new Float (angle);
		
		if (f.isNaN()) {
			System.out.println("Error in calAngleFromIdealPoint: angle is not a number");
		}

		return angle;
	}
	
	/**
	 * Compute the norm of a vector
	 * 
	 * @param sol
	 */
	public double computeNormFromIdealPoint(double[] v) {
		double norm = 0.0;

		for (int j = 0; j < objectives_; j++) {
			norm += Math.pow(v[j], 2);
		} // for

		norm = Math.sqrt(norm);
		return norm;
	}// computeNormFromIdealPoint
	
	
	/**
	 * Generate weights
	 * @throws ClassNotFoundException 
	 */
	public void GenerateWeights (SolutionSet pop) throws ClassNotFoundException {
	   // Generate weights		
	   newWeights_ = new WeightSet(2 * population_.size());
	   
	  // Generate weights from ideal point	 
	   double [] rp = new double[objectives_];
	   for (int j = 0; j < objectives_; j++) {
		   rp[j] = 0.0;
	   } // for j	   
	   	
	   getWeights(pop,"ideal", rp);
	   
	   // Generate weights from nadir point	   	  
	   for (int j = 0; j < objectives_; j++) {
		   rp[j] = 1.0;
	   } // for j	  
	   
	   getWeights(pop,"nadir", rp);
	  
	}
	
	public double [] generateWeightFromSolution(Solution sol) {
		   double [] normalizedObj = new double[objectives_];
		   normalizedObj = sol.getNormalizedObjectives();
		   double [] tempVector = new double[objectives_];
		   double [] rp = new double[objectives_];
		   
		   double sumDiff = 0.0;
		   
		   for (int j = 0; j < objectives_; j++) {
			   rp[j] = 0.0;
			   sumDiff = sumDiff + (normalizedObj[j] - rp[j]);
		   } // for j	   
		   		 
		   boolean validWeight = true;
		   
		   // Intersection method
		   for (int j = 0; j < objectives_; j++) {					   
			   tempVector[j] = (1 - objectives_ * rp[j]) * ((normalizedObj[j] - rp[j]) / sumDiff) + rp[j];
			   
//			   if (tempVector[j] > -1e-6 && tempVector[j] < 0.0) 
//				   tempVector[j] = 0.0;
		   } // for j	
		   
		   return tempVector;
	}
	
	
	public void getWeights(String weightType, double [] rp) throws ClassNotFoundException {
		
		for (int i = 0; i < archive_.size();i++) {
			   Solution sol = archive_.get(i);
			   Weights temp = new Weights(objectives_);
			   			 			   
			   double [] normalizedObj = new double[objectives_];
			   normalizedObj = sol.getNormalizedObjectives();
			   double [] tempVector = new double[objectives_];
			   
			   double sumDiff = 0.0;
			   
			   for (int j = 0; j < objectives_; j++) {
				   sumDiff = sumDiff + (normalizedObj[j] - rp[j]);
			   } // for j	   
			   		 
			   boolean validWeight = true;
			   
			   // Intersection method
			   for (int j = 0; j < objectives_; j++) {					   
				   tempVector[j] = (1 - objectives_ * rp[j]) * ((normalizedObj[j] - rp[j]) / sumDiff) + rp[j];
				   
//				   if (tempVector[j] > -1e-6 && tempVector[j] < 0.0) 
//					   tempVector[j] = 0.0;
				   
				   if (tempVector[j] < -1e-6) {		
					   validWeight = false;
				   }
			   } // for j		
			   
//			   if (validWeight == false) {
////				   temp.setSolution(new Solution(problem_)); // Set to a random solution
//				   temp.setSolution(sol); // Set solution
//				   temp.setWeightType("random"); 
//				   tempVector = getRandomWeights(objectives_);
//				   temp.setWeight(tempVector);		  
//				   newWeights_.add(temp);
//				   }
			   
//			   } else {
//				   temp.setSolution(sol); // Set solution
//				   temp.setWeightType(weightType); 
//			   }			   
			 
			   if (validWeight == true) {
				   temp.setSolution(sol); // Set solution
				   temp.setWeightType(weightType); 
				   temp.setWeight(tempVector);		  
				   newWeights_.add(temp);
			   }
			  
		   } //for i
		
	} // getWeights
	
public void getWeights(SolutionSet pop, String weightType, double [] rp) throws ClassNotFoundException {
		
		for (int i = 0; i < pop.size();i++) {
			   Solution sol = pop.get(i);
			   Weights temp = new Weights(objectives_);
			   			 			   
			   double [] normalizedObj = new double[objectives_];
			   normalizedObj = sol.getNormalizedObjectives();
			   double [] tempVector = new double[objectives_];
			   
			   double sumDiff = 0.0;
			   
			   for (int j = 0; j < objectives_; j++) {
				   sumDiff = sumDiff + (normalizedObj[j] - rp[j]);
			   } // for j	   
			   		 
			   boolean validWeight = true;
			   
			   // Intersection method
			   for (int j = 0; j < objectives_; j++) {					   
				   tempVector[j] = (1 - objectives_ * rp[j]) * ((normalizedObj[j] - rp[j]) / sumDiff) + rp[j];
				   
//				   if (tempVector[j] > -1e-6 && tempVector[j] < 0.0) 
//					   tempVector[j] = 0.0;
				   
				   if (tempVector[j] < -1e-6) {		
					   validWeight = false;
				   }
			   } // for j		
			   
			   if (validWeight == false) {
//				   temp.setSolution(new Solution(problem_)); // Set to a random solution
				   temp.setSolution(sol); // Set solution
				   temp.setWeightType("random"); 
				   tempVector = getRandomWeights(objectives_);
				   temp.setWeight(tempVector);		  
				   newWeights_.add(temp);
				   }
			   
//			   } else {
//				   temp.setSolution(sol); // Set solution
//				   temp.setWeightType(weightType); 
//			   }			   
			 
			   if (validWeight == true) {
				   temp.setSolution(sol); // Set solution
				   temp.setWeightType(weightType); 
				   temp.setWeight(tempVector);		  
				   newWeights_.add(temp);
			   }
			  
		   } //for i
		
	} // getWeights
	/**
	 * Get a really random weight 
	 * @param objectives
	 * @return
	 */
	 public double [] getRandomWeights(int objectives) {
		 double [] weight = new double [objectives];
		 
		 for (int j = 0; j < objectives_; j++) {	
			 weight[j] = PseudoRandom.randDouble();
		 }
		 
		 weight[0] = 1 - Math.pow(weight[0], 1.0/(objectives_ - 1));
		 
		 for (int j = 1; j < objectives_ - 1; j++) {
			 double sum = 0.0;		 
			 for (int i = 0; i < j ; i++) {
				 sum = sum + weight[i] ;
			 }
			 weight[j] = (1 - sum) * (1 - Math.pow(weight[j], 1.0/(objectives_ - (j+1))));
		 }
		 
		 // For the last weight
		 double sum = 0.0;		 
		 for (int i = 0; i < objectives_ - 1; i++) {
			 sum = sum + weight[i] ;
		 }
		 
		 weight[objectives - 1] = 1 - sum;
		 
		 // Print weights 
//		 sum = 0.0;
//		 for (int j = 0; j < objectives_; j++) {	
//			  sum = sum + weight[j] ;
////			 System.out.print(weight[j] + ",");
//		 }	 
//		 System.out.println(sum);
		 
		 return weight;
	 }
	
	/**
	 * Calculate the angle between each pair of solutions in a SolutionSet named union
	 * @param union
	 */
	void calculateAngles(SolutionSet union) {		

//		union.sort(new FitnessComparator());

		// Set the ID, for referencing 
		for (int i = 0; i < union.size(); i++) {
			union.get(i).setID(i); 
		}
				
				
		AngleMatrix = new double[union.size()][union.size()];
		
		for (int i = 0; i < union.size(); i++) {

			Solution soli = union.get(i);

			for (int j = i; j < union.size(); j++) {

				if (i == j) {
					AngleMatrix[i][i] = 0.0;
				} else {
					Solution solj = union.get(j);
					AngleMatrix[i][j] = calAngle(soli, solj);		
					AngleMatrix[i][j] = Math.round(AngleMatrix[i][j]*10000)/10000.0000;
					
					AngleMatrix[j][i] = AngleMatrix[i][j];	
					AngleMatrix[j][i] = Math.round(AngleMatrix[j][i]*10000)/10000.0000;
				
				}	

			} // for j

		} // for i
		
	} // calculateAngles	
	
	/**
	 * Maximum angle first principle
	 * @param lastFront
	 */
	public void MAF(SolutionSet archive, SolutionSet lastFront) {		

//		System.out.println("MAF");
		int remain = archiveSize_ - archive.size();		
		if (remain == 0) return; // if the archive is full, return 
		
//		lastFront.sort(new FitnessComparator());	
		int n = lastFront.size();   
		double [] angles = new double[n];
		int [] index = new int[n];
		boolean [] removed = new boolean[n];
		
		if (archive.size() == 0) { // Add extreme solutions
			
			 for (int j = 0; j < objectives_; j++) {
				    double minAngle2Axis = 1.0e+30;
					int minAngle2AxisID = -1;
					
					for (int i = 0;i < n;i++) {										
						if (removed[i]== false ) {
							Solution solLastFront = lastFront.get(i);
							double angle = Math.acos(Math.abs(solLastFront.getNormalizedObjective(j)/solLastFront.getDistanceToIdealPoint()));
						
							if (angle < minAngle2Axis) {
								minAngle2Axis = angle;
								minAngle2AxisID = i;
							}
						}	
					}// for 
					 if (minAngle2AxisID == -1) break; 
					 removed[minAngle2AxisID] = true;
					 archive.add(new Solution(lastFront.get(minAngle2AxisID)));
					 remain--;
			 }
			
		}
			
		/**
		 * Associate each solution in the last front with a solution in the population
		 */
		for (int i = 0;i < n;i++) {
			Solution solLastFront = lastFront.get(i);
			double minAng =  1.0e+30;
			int minAngID = -1;
			
			for (int j = 0;j < archive.size();j++){
				Solution solPop = archive.get(j);
				double angle = calAngle(solLastFront,solPop);
				if (angle < minAng){
					minAng = angle;
					minAngID = j;						
				}					
			} // for j		
			angles[i] = minAng;	
			index[i] = minAngID;
		} // for i		
		
		/**
		 * Niching procedure
		 */
		for(int r = 0;r < remain;r++ ) {
			/**
			 * Step 1: Find max angles 
			 */
			 int maxAngleID = -1;
			 double maxAngle = -1.0e+30;			 
			 
			 for(int j = 0;j < n;j++){
				// Find max angle 
				 if (removed[j]== false && angles[j] > maxAngle){
					 maxAngle = angles[j];
					 maxAngleID = j;
				 }

			 } // for 
			 
			 
			 // Step 2: Maximum-angle-first principle 				 
			 if (maxAngleID != -1 ) {	// Not all solutions in the last front have been added
				 removed[maxAngleID] = true;
				 archive.add(new Solution(lastFront.get(maxAngleID)));
				 
				 // Update angles			 
				 for (int i = 0; i < n; i++){ // For each solution in the last front
					 if (removed[i]== false) {
						 double angle = calAngle(lastFront.get(i),lastFront.get(maxAngleID));
						 if (angle < angles[i]){
							 angles[i] = angle;
							 index[i] = archive.size() - 1;
						 }
					 }//if				 
				 }//for i	
				 
			 } else {
				 break;
			 }				 

		}// for r 
	}	
	
	/**
	 * Normalize mixed population
	 */
	public void normalize(SolutionSet union_) {	
		computeIdealPoint(union_); // Compute z_ideal, the estimated ideal point 
		computeMaxPoint(union_);   // Compute zmax, the max point of the current population
//		computeExtremePoints(union_); // Compute extreme solutions

		// Normalize 
		for (int i = 0; i < union_.size(); i++) {
			Solution sol = union_.get(i);
			double fitness = 0.0;
			double dist = 0.0;
			
			for (int j = 0; j < objectives_; j++) {

				double val = 0.5;
				if (zmax[j] != z_ideal[j]) {
					val  = (sol.getObjective(j) - z_ideal[j]) / (zmax[j] - z_ideal[j]);
				}
				
				fitness = fitness + val;
				dist = dist + val * val;
				
				sol.setNormalizedObjective(j, val);
			}// for            
			
			sol.setDistanceToIdealPoint(Math.sqrt(dist));
			sol.setFitness(fitness);
			
		} // for i

	} // normalize
	
	
	/**
	 * Normalize mixed population
	 */
	public void calculateIdealNadirPoints(SolutionSet union_) {	

		computeIdealPoint(union_); // Compute z_ideal, the estimated ideal point 
		computeMaxPoint(union_);   // Compute zmax, the max point of the current population
		computeExtremePoints(union_); // Compute extreme solutions
		computeIntercepts(); // Compute current intercepts
	
		for (int j = 0; j < objectives_; j++) {			
				z_nadir[j] = intercepts[j];			
		}
	} // normalize
	
	
	public double calVDistance(Solution sol, double[] ref){
		return calNormlizedVDistance(sol, ref);
		
	}
	
	
	public double calNormlizedVDistance(Solution sol, double[] ref) {

		double ip = 0;
		double refLenSQ = 0;

		for (int j = 0; j < this.objectives_; j++) {

			ip += sol.getNormalizedObjective(j) * ref[j];
			refLenSQ += (ref[j] * ref[j]);
		}
		refLenSQ = Math.sqrt(refLenSQ);

		double d1 = Math.abs(ip) / refLenSQ;

		double d2 = 0;
		for (int i = 0; i < sol.numberOfObjectives(); i++) {
			d2 += (sol.getNormalizedObjective(i) - d1 * (ref[i] / refLenSQ))
					* (sol.getNormalizedObjective(i) - d1 * (ref[i] / refLenSQ));
		}
		
		d2 = Math.sqrt(d2);

		return d2;
	}
	
	
	double calUnNormalizedVDistance(Solution sol, double[] ref){
		
		double d1, d2, nl;

		d1 = d2 = nl = 0.0;

		for (int i = 0; i < sol.numberOfObjectives(); i++) {
			d1 += (sol.getObjective(i) - z_ideal[i]) * ref[i];
			nl += (ref[i] * ref[i]);
		}
		nl = Math.sqrt(nl);
		d1 = Math.abs(d1) / nl;
		
	
		d2 =0;
		for (int i = 0; i < sol.numberOfObjectives(); i++) {
			
			d2 += ((sol.getObjective(i) - z_ideal[i]) - d1
					* (ref[i] / nl)) * ((sol.getObjective(i) - z_ideal[i]) - d1
							* (ref[i] / nl));
		}
		d2 = Math.sqrt(d2);
		

		
		return d2;
	}
	
	/**
	 * Select solution for each weight
	 */
	public void select(SolutionSet union_) {
		//The distance between each solution and each weight
//		union_.sort((new FitnessComparator()));
		
		double [][] distanceMatrix = new double [union_.size()][lambda_.length]; 
		
		for (int i = 0 ; i < union_.size();i++) { // for each solution
			Solution sol = union_.get(i);
			
			for(int j = 0; j < lambda_.length;j++) {
//				distanceMatrix[i][j] = calAngleFromIdealPoint(sol, lambda_[j]);
				distanceMatrix[i][j] = calNormlizedVDistance(sol, lambda_[j]);
			} // for j
			
		} // for i
		
		// Associate each solution with a weight
		boolean [] selected = new boolean[union_.size()]; // indicate a solution is selected or not
		boolean [] occupied = new boolean[lambda_.length]; // indicate a weight is occupied or not
				
		ArrayList<Integer> [] closestList = new ArrayList[lambda_.length]; // The list to store solution index for each weight
		
		for (int i = 0; i < lambda_.length;i ++) {
			closestList[i] = new ArrayList();
		}
		
		int machedNumber = 0;
		int rank = 0;
		
		while (machedNumber < lambda_.length) {
									
			// Step 1: Association
			for (int i = 0; i < union_.size();i++) {
				
				if (selected[i] == false) { // The ith solution is not selected
					double  minDistance = 1e30;
					int minDistanceIdx = -1;
					
					for(int j = 0; j < lambda_.length;j++) {
						
						if(occupied[j] == false) { // The j-th weight is not occupied
							double dis = distanceMatrix[i][j];
							if (dis < minDistance) {
								minDistance = dis;
								minDistanceIdx = j;
							} // if 
						}
						
					} // for j
					
					if (minDistanceIdx == -1) { // All weights have been occupied
						break;
					}	
					
					closestList[minDistanceIdx].add(i) ;
				}
			} // for i
						
			int maxNumber = 0;
			
			for (int k = 0 ; k < lambda_.length; k++) {
				if (closestList[k].size() > maxNumber)
					maxNumber = closestList[k].size();
			}				

			if (maxNumber == 0) {
				System.out.println("illegal case");
				return;
			}
			
			int numberOfConsidered = 2;
			
			if (maxNumber >= 3) 
				numberOfConsidered = 3;
			
			// Step 2	
			for (int k = 0 ; k < lambda_.length; k++) {
				if (occupied[k] == true) continue; 
				
//				if (closestList[k].size() == 1 && maxNumber == 1) {
//					System.out.println("ideal case");
//					machedNumber++;
//					occupied[k] = true;
//					selected[closestList[k].get(0)] = true;
//					union_.get(closestList[k].get(0)).setRank(rank);					
//				} else 
				if (closestList[k].size() < numberOfConsidered) {
					closestList[k].clear();
				} else
				if (closestList[k].size() >= numberOfConsidered) {
			
					// Multiple solutions associated with a single weight
					double [] fitness = new double[closestList[k].size()];
					
					for (int i = 0; i < closestList[k].size();i++) {
						fitness[i] = fitnessFunction(union_.get(closestList[k].get(i)), lambda_[k], k);
					}
					
					// Find the best
					int minIndex = findMin(fitness);
										
					machedNumber++;
					occupied[k] = true;
					
					int solutionId = closestList[k].get(minIndex);					
					selected[solutionId] = true;
					union_.get(solutionId).setRank(rank);
					
					// Reset closest solutions
					closestList[k].clear();
					closestList[k].add(solutionId);
				} // if 
				
			} // for k			
			rank++;
		}// while	
		
//		System.out.println(rank);
		// Step: 3
		population_.clear();					
		for (int i = 0; i < populationSize_;i++) {
			population_.add(union_.get(closestList[i].get(0)));
			
//			// Update best solution population	
//			int flag = 0; 
//			flag = dominance_.compare(population_.get(i), bestPopulation_.get(i)) ;
//			
//			if (flag == -1)
//				bestPopulation_.replace(i, population_.get(i));
//			else if (flag == 0) 
//				if (fitnessFunction(population_.get(i), lambda_[i], i) < fitnessFunction(bestPopulation_.get(i), lambda_[i], i)) {
//					bestPopulation_.replace(i, population_.get(i));
//			}
			
		}
		
	}
	
	
	/**
	 * Compute Curvature based on nondominated solutions
	 */
	public void normalization(SolutionSet nondominated, SolutionSet dominated) {

		// Use solutions in nondominated set to find z_ideal and zmax
		computeIdealPoint(nondominated); // Compute z_ideal, the estimated ideal point 
		computeMaxPoint(nondominated);  // Compute zmax, the max point of the current population
				
		computeExtremePoints(nondominated); // Compute extreme solutions
		computeIntercepts(); // Compute current intercepts
		updateNadir();  // Update z_nadir based current intercepts and previous nadir
		
		normalizePopulation(nondominated); // normalize non-dominated
		normalizePopulationSimple(dominated) ; // 

	} // computeCurvature
	
	/**
	 *  Update z_nadir based current intercepts and previous nadir, whichever is smaller
	 */
	public void updateNadir() {	
		
		for (int j = 0; j < objectives_; j++) {
//			if (intercepts[j] < z_nadir[j]) {
				z_nadir[j] = intercepts[j];
//			}
		}
		
	}
	
	/**
	 * Calculate the angle between two solutions
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public double calAngle(Solution s1, Solution s2) {
		double angle = 0.0;
		double norm1 = 0.0;
		double norm2 = 0.0;

		double innerProduct = 0.0;

		for (int i = 0; i < objectives_; i++) {
			innerProduct += (s1.getNormalizedObjective(i) - zp_[i]) * (s2.getNormalizedObjective(i) - zp_[i]);
		}

		norm1 = s1.getDistanceToReferencePoint();
		norm2 = s2.getDistanceToReferencePoint();

		double value = Math.abs(innerProduct / (norm1 * norm2));
		
		if (value > 1.0) {
			value = 1.0;
		}
		
		angle = Math.acos(value);

		Float f = new Float (angle);
		if (f.isNaN()) {
			System.out.println("Error in calAngle: angle is not a number");
		}
		
		return angle;
	}

	
	/**
	 * Split out better and worse solutions from the union set
	 * 
	 * @param union
	 * @return
	 */
	public SolutionSet[] splitBetterWorse(SolutionSet union) {
		SolutionSet[] results = new SolutionSet[2];
		
		boolean [] flagWorse = new boolean[union.size()];
		int noOfWorse= 0;
		
		for (int i = 0; i < union.size(); i++) {
			Solution solution = union.get(i);

			for (int j = 0; j < objectives_; j++) {
				double val = 1.0 + eps_;	
				if (solution.getNormalizedObjective(j) > val)	{
					flagWorse[i] = true;
					noOfWorse ++; 
					break;
				}
			}
		}

		results[0] = new SolutionSet(union.size()- noOfWorse); // results[0] is better ones 
		results[1] = new SolutionSet(noOfWorse); // results[1] is worse ones 
		
		for (int i = 0; i < union.size(); i++) {
			if (flagWorse[i] == false) {
				results[0].add(union.get(i));
			} else {
				results[1].add(union.get(i));
			}
		}
		
		return results;
	}
	
	void computeIdealPoint(SolutionSet solutionSet) {
			
//		for (int j = 0; j < objectives_; j++) {
//
//			for (int i = 0; i < solutionSet.size(); i++) {
//				if (solutionSet.get(i).getObjective(j) < z_ideal[j]) {
//					z_ideal[j] = solutionSet.get(i).getObjective(j);
//					
//				}
//			}
//
//		} // for 
		
//		System.out.println("solutionSet size" + solutionSet.size());
		
		// New implementation, considering trails		
		for (int i = 0; i < solutionSet.size(); i++) {
			boolean updated = false;
						
			for (int j = 0 ; j < objectives_; j++) {

				if (solutionSet.get(i) != null) {
					if (solutionSet.get(i).getObjective(j) < z_ideal[j]) {
						z_ideal[j] = solutionSet.get(i).getObjective(j);
						updated = true;
					}
				}
			}
			
			// Count the number of unsuccessful trails
			if (updated) 
				unsuccessfulTrails_ = 0;
			else
				unsuccessfulTrails_ ++;
			
		}	// for	

	}
	
	void computeMaxPoint(SolutionSet solutionSet) {

		for (int j = 0; j < objectives_; j++) {
			zmax[j] = -1.0e+30;

			for (int i = 0; i < solutionSet.size(); i++) {
				if (solutionSet.get(i).getObjective(j) > zmax[j])
					zmax[j] = solutionSet.get(i).getObjective(j);
			}

		}
	}
	
	void computeExtremePoints(SolutionSet solutionSet) {
		extremePoints = new double[objectives_][objectives_];
		extremeSolutionIndex = new int[objectives_]; // Store the index of extreme solutions
		
		for (int j = 0; j < objectives_; j++) {
			int index = -1;
			double min = Double.MAX_VALUE;

			for (int i = 0; i < solutionSet.size(); i++) {
				double asfValue = asfFunction(solutionSet.get(i), j);
				if (asfValue < min) {
					min = asfValue;
					index = i;
				}
			}
			
			extremeSolutionIndex[j] = index;
			
			for (int k = 0; k < objectives_; k++)
				extremePoints[j][k] = solutionSet.get(index).getObjective(k);
		}
	}
	
	double asfFunction(Solution sol, int j) {
		double max = Double.MIN_VALUE;
		double epsilon = 1.0E-6;

		for (int i = 0; i < objectives_; i++) {

			double val = Math.abs(sol.getObjective(i) - z_ideal[i]);

			if (j != i)
				val = val / epsilon;

			if (val > max)
				max = val;
		}

		return max;
	}
	
	void computeIntercepts() {

		intercepts = new double[objectives_];

		double[][] temp = new double[objectives_][objectives_];

		for (int i = 0; i < objectives_; i++) {
			for (int j = 0; j < objectives_; j++) {
				double val = extremePoints[i][j] - z_ideal[j];
				temp[i][j] = val;
			}
		}

		Matrix EX = new Matrix(temp);

		if (EX.rank() == EX.getRowDimension()) {
			double[] u = new double[objectives_];
			for (int j = 0; j < objectives_; j++)
				u[j] = 1;

			Matrix UM = new Matrix(u, objectives_);

			Matrix AL = EX.inverse().times(UM);

			int j = 0;
			for (j = 0; j < objectives_; j++) {

				double aj = 1.0 / AL.get(j, 0) + z_ideal[j];

				if ((aj > z_ideal[j]) && (!Double.isInfinite(aj))
						&& (!Double.isNaN(aj)))
					intercepts[j] = aj;
				else
					break;
			}
			if (j != objectives_) {
				for (int k = 0; k < objectives_; k++)
					intercepts[k] = zmax[k];
			}

		} else {
			for (int k = 0; k < objectives_; k++)
				intercepts[k] = zmax[k];
		}

	}

	void normalizePopulation(SolutionSet union_) {
		
		for (int i = 0; i < union_.size(); i++) {
			Solution sol = union_.get(i);
			double fitness = 0.0;
			double dist = 0.0;

			for (int j = 0; j < objectives_; j++) {
				
				double val = 0.0;
				if (z_nadir[j] != z_ideal[j]) {
					val  = (sol.getObjective(j) - z_ideal[j]) / (z_nadir[j] - z_ideal[j]);
				} else {
					val  = 0.5;
				}
				
//				if (z_nadir[j] == z_ideal[j]) {
//					System.out.println("z_nadir[j] == z_ideal[j]");
//				}
				
				fitness = fitness + val;
				dist = dist + val * val;
				
				sol.setNormalizedObjective(j, val);
			}// for            
			
			sol.setFitness(fitness);
			sol.setDistanceToIdealPoint(Math.sqrt(dist));
			
		}
		
	}
	
	void estimateShapes(SolutionSet union_) {
		int noOfSolBeyondPlane = 0;
		
		for (int i = 0; i < union_.size(); i++) {
			Solution sol = union_.get(i);
			double fitness = 0.0;

			for (int j = 0; j < objectives_; j++) {				
				double val = sol.getNormalizedObjective(j);								
				fitness = fitness + val;
			}// for            
			
			if (fitness < 1.0) noOfSolBeyondPlane++;
		}

		
		if (noOfSolBeyondPlane > 0.95 * union_.size())  {
			rpType_ = "Nadir";
		}
		else {
			rpType_ = "ideal";
		}
	
//		System.out.println(rpType_);
		
	}
	
	void normalizePopulationSimple(SolutionSet union_) {
		
		for (int i = 0; i < union_.size(); i++) {
			Solution sol = union_.get(i);
			double fitness = 0.0;
			double dist = 0.0;
			
			for (int j = 0; j < objectives_; j++) {

				double val = 0.5;
				if (z_nadir[j] != z_ideal[j]) {
					val  = (sol.getObjective(j) - z_ideal[j]) / (z_nadir[j] - z_ideal[j]);
				}
				
				fitness = fitness + val;
				dist = dist + val * val;
				
				sol.setNormalizedObjective(j, val);
			}// for            
			
			sol.setDistanceToIdealPoint(Math.sqrt(dist));
			sol.setFitness(fitness);
			
		} // for i
		
	}
	
	public void computeDistance2RP(SolutionSet front) {
		// Calculate norm of each solution in front
		for (int i = 0; i < front.size(); i++) {
			Solution sol = front.get(i);

			double norm = 0.0;
			for (int j = 0; j < objectives_; j++) {
				norm += (sol.getNormalizedObjective(j) - zp_[j]) * (sol.getNormalizedObjective(j) - zp_[j]);	
			}
			
			norm = Math.sqrt(norm);
			sol.setDistanceToReferencePoint(norm); // 
			
			if (rpType_.equalsIgnoreCase("ideal")) {
				sol.setFitness(norm);
				sol.setDistanceToIdealPoint(norm);
			} else {
				sol.setFitness(1.0/norm);
				if (norm == 0) {	
					System.out.println("Warning in function: computeDistance2RP: norm == 0");
				}
			}
		}
	}
	
	public boolean equalSolution (Solution sol1, Solution sol2) {
		
		if (sol1.getNumberOfViolatedConstraint() !=sol2.getNumberOfViolatedConstraint()) { // 绾﹂敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
			return false; // 閿熸枻鎷烽敓鎴鎷烽敓鏂ゆ嫹
		}
		
		for (int i = 0; i < sol1.getNumberOfObjectives();i++) {
			if (sol1.getObjective(i) != sol2.getObjective(i))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Initialize the weight vectors, this function only can read from the 
	 * existing data file, instead of generating itself.
	 * 
	 */
	public void initUniformWeight() {
		String dataFileName;
		dataFileName = "W" + problem_.getNumberOfObjectives() + "D_"
				+ populationSize_ + ".dat";
		
		if ((problem_.getNumberOfObjectives() == 2) && (populationSize_ <= 300)) {
		      for (int n = 0; n < populationSize_; n++) {
		        double a = 1.0 * n / (populationSize_ - 1);
		        lambda_[n][0] = a;
		        lambda_[n][1] = 1 - a;
		      } // for
		} else {// if
		
			try {
				// Open the file
				FileInputStream fis = new FileInputStream(dataDirectory_ + "/"
						+ dataFileName);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);
	
				int i = 0;
				int j = 0;
				String aux = br.readLine();
				while (aux != null) {
					StringTokenizer st = new StringTokenizer(aux);
					j = 0;
					while (st.hasMoreTokens()) {
						double value = (new Double(st.nextToken())).doubleValue();
						lambda_[i][j] = value;					
						j++;
						
					}
					aux = br.readLine();
					i++;
				}
				br.close();
			} catch (Exception e) {
				System.out.println("initUniformWeight: failed when reading for file: "
								+ dataDirectory_ + "/" + dataFileName);
				e.printStackTrace();
			}
		}
		
//		for (int i = 0; i < lambda_.length;i++) {
//			System.arraycopy(lambda_[i], 0, initialLambda_[i], 0, objectives_);
//		}
		
	} // initUniformWeight

	/**
	   * initRandomWeight
	 * @throws IOException 
	   */
	  public void initRandomWeight() {	

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
		        populationSize_ + ".rw";
		      String filePath = dataDirectory_ + "/" + dataFileName;
		      
		      File f = new File(filePath);
		      if(f.exists()) {
		    	  System.out.println("Weight " + dataFileName + " exists, loading weights");
		    	  loadWeights(filePath);
		      } else{
		    	  System.out.println("Weight " + dataFileName + " does not exist, generating weights");
		    	  try {
		    		  generateWeights(filePath);
		    	  } catch (Exception e) {
		  			System.err.println("generate Weights fiailed");
			        e.printStackTrace();
		           }
		      }//else
		      
		     } // else 		      
	
	  } // initUniformWeight  
	  
	  
	  public void loadWeights(String path) {
			try {
				// Open the file
				FileInputStream fis = new FileInputStream(path);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);

				int numberOfObjectives = 0;
				int i = 0;
				int j = 0;
				String aux = br.readLine();
				while (aux != null) {
					StringTokenizer st = new StringTokenizer(aux);
					j = 0;
					numberOfObjectives = st.countTokens();
					while (st.hasMoreTokens()) {
						double value = (new Double(st.nextToken())).doubleValue();
						lambda_[i][j] = value;
//						 System.out.println("lambda["+i+","+j+"] = " + value) ;
						j++;
					}
					aux = br.readLine();
					i++;
				}
				br.close();
			} catch (Exception e) {
				System.err
						.println("initUniformWeight: failed when reading for file: "
								+ path);
				e.printStackTrace();
			}
		}// loadWeights
	  
	  /**
	   * Generate weight vectors
	   * @param path
	   * @throws IOException
	   */
	  public void generateWeights(String path) throws IOException{

			int numberOfObjectives = problem_.getNumberOfObjectives();
			
			List<double[]> weights = new ArrayList<double[]>(5000);

			// create 5000 random weights
			for (int i = 0; i < 5000; i++) {
				double[] weight = new double[numberOfObjectives];
				double sum = 0.0; 
				
				for (int j = 0; j < numberOfObjectives; j++) {
					weight[j] =  PseudoRandom.randDouble();
					sum = sum + weight[j];
				}		
			
				
				for (int j = 0; j < numberOfObjectives; j++) {
					weight[j] /= sum;
				}
				
				weights.add(weight);
			}

			List<double[]> W = new ArrayList<double[]>(populationSize_);
			
			// initialize W with weights (1,0,...,0), (0,1,...,0), ...,
			// (0,...,0,1)
			for (int i = 0; i < numberOfObjectives; i++) {
				double[] weight = new double[numberOfObjectives];
				weight[i] = 1.0;
				W.add(weight);
			}
			
			// fill in remaining weights with the weight vector with the largest
			// distance from the assigned weights
			while (W.size() < populationSize_) {
				double[] weight = null;
				double distance = Double.NEGATIVE_INFINITY;

				for (int i = 0; i < weights.size(); i++) {
					double d = Double.POSITIVE_INFINITY;

					for (int j = 0; j < W.size(); j++) {		
						d = Math.min(d, Utils.distVector(weights.get(i),W.get(j)));
					}
					
					if (d > distance) {
						weight = weights.get(i);
						distance = d;
					}
				}		
				
				W.add(weight);
				weights.remove(weight);
			}

			/**
			 * Write weight into a file 
			 */
			try {
				 FileOutputStream fos   = new FileOutputStream(path)     ;
			     OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
			     BufferedWriter bw      = new BufferedWriter(osw)        ;
			     
			     
				for(int i=0;i< populationSize_;i++) {
					lambda_[i] = W.get(i);
					String aux = "";
					for(int j=0; j< numberOfObjectives;j++){
						aux = aux + Double.toString(lambda_[i][j]) + " ";				
					}		
					bw.write(aux);
					bw.newLine();
				}
				
				bw.close();
			} catch (Exception e) {
				System.err.println("Write weights : failed when reading for file: "
								+ path);
				e.printStackTrace();
			}
			
		}
	/**
	 * Initialize the neighborhood matrix of subproblems, based on the Euclidean
	 * distances between different weight vectors
	 * 
	 */
	public void initNeighborhood() {
		int[] idx  = new int[populationSize_];
		double[] x = new double[populationSize_];
		
		for (int i = 0; i < populationSize_; i++) {
			/* calculate the distances based on weight vectors */
			for (int j = 0; j < populationSize_; j++) {
				x[j] = Utils.distVector(lambda_[i], lambda_[j]);
				idx[j] = j;
			}
			/* find 'niche' nearest neighboring subproblems */
			Utils.minFastSort(x, idx, populationSize_, T_);
			
			for (int k = 0; k < T_; k++) {
				neighborhood_[i][k] = idx[k];

			} // for k
			
		}
	
	} // initNeighborhood

	/**
	 * Initialize the population, random sampling from the search space
	 * 
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	public void initPopulation() throws JMException, ClassNotFoundException {
		
		for (int i = 0; i < populationSize_; i++) {
			Solution newSolution = new Solution(problem_);		
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations_++;		
			population_.add(newSolution);

			if (isfeasible(newSolution)) {				
				feasiblePopulation_.replace(i, newSolution);
			}
			
			bestPopulation_.add(newSolution);
		}
	} // initPopulation

	
	// Judge a solution is feasible or not
	public boolean isfeasible(Solution sol) {
		if (sol.getOverallConstraintViolation() == 0.0 && sol.getNumberOfViolatedConstraint() == 0)
			return true;
		else 
			return false;
	}
	
	/**
	 * To generate offspring population
	 * @throws JMException 
	 */
	public void generateOffspring() throws JMException {
		offspringPopulation_.clear();
		
		int[] permutation = new int[populationSize_];
		Utils.randomPermutation(permutation, populationSize_);

		for (int i = 0; i < populationSize_; i++) {
			int n = permutation[i];
			int type;
			double rnd = PseudoRandom.randDouble();

			// STEP 2.1. Mating selection based on probability
			if (rnd < delta_) // if (rnd < realb)
			{
				type = 1; // neighborhood
			} else {
				type = 2; // whole population
			}
			
			Vector<Integer> p = new Vector<Integer>();
			
//			if (this.objectives_ < 5) {
				matingSelectionStage1(p, n, 2, type);
//			} else 
//				matingSelectionStage1Ma(p, n, 2, type);
			
				      
			/**
			 * --------------------Use crossover and mutation (or update)--------------------
			 */
//			Solution[] parents = new Solution[2];
//			parents[0] = population_.get(p.get(0));
//			parents[1] = population_.get(p.get(1));
//			
//			Solution[] childs = (Solution[]) crossover_.execute(parents);
//
//			
//			Solution child = null;
//			
//			double rndSel =  PseudoRandom.randDouble();				
//			if (rndSel < 0.5) {
//				child = childs[0];
//	        } else 
//	        	child = childs[1];
			 
				Solution child = null;
				 // Apply Crossover for Real codification
                if (crossover_.getClass().getSimpleName().equalsIgnoreCase("SBXCrossover")) {
                    Solution[] parents = new Solution[2];
                    parents[0] = population_.get(p.get(0));
                    parents[1] = population_.get(n);
                    child = ((Solution[]) crossover_.execute(parents))[0];
                }
//                // Apply DE crossover
                else  {
                    Solution[] parents = new Solution[3];
                    parents[0] = population_.get(p.get(0));
                    parents[1] = population_.get(p.get(1));
                    parents[2] = population_.get(n);
                    child = (Solution) crossover_.execute(new Object[]{population_.get(n), parents});
//				   Operator selectionOperator = operators_.get("selection");   		
//		           Solution[]  parent = (Solution [])selectionOperator.execute(new Object[]{population_, n});
//		           // Crossover. Two parameters are required: the current individual and the  array of parents
//		           child = (Solution)crossover_.execute(new Object[]{population_.get(n), parent}) ;
                } 
                
			// Apply mutation
			mutation_.execute(child); 
			 // --------------------Use crossover and mutation (end)--------------------
							
			// STEP 2.3. Repair. 
			
			// Evaluation
			problem_.evaluate(child);
			problem_.evaluateConstraints(child);
			evaluations_++;		
			
			 // Dominance test
//	        int result  ;
//	        result = dominance_.compare(population_.get(n), child) ;
//	        if (result == -1) { // Solution i dominates child
//	        	offspringPopulation_.add(population_.get(n)) ;
//	        } // if
//	        else if (result == 1) { // child dominates
//	        	offspringPopulation_.add(child) ;
//	        } // else if
//	        else { // the two solutions are non-dominated
//	        	offspringPopulation_.add(child) ;
//	        	offspringPopulation_.add(population_.get(n)) ;
//	        } // else
	        
			offspringPopulation_.add(child);
			
			if (convergenceFlag== true) 
				captureConvergence_.runCaptureConvergence(evaluations_, population_);

		} // for		
	}
	
	/**
	 * Initialize the archive, random sampling from the search space
	 * 
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	public void initArchive() throws JMException, ClassNotFoundException {
		
		for (int i = 0; i < archiveSize_; i++) {
			Solution newSolution = new Solution(problem_);		
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations_++;
			archive_.add(newSolution);
		}
	} // initArchive
	
	/**
	 * Select the mating parents, depending on the selection 'type'
	 * 
	 * @param list : the set of the indexes of selected mating parents
	 * @param cid  : the id of current subproblem
	 * @param size : the number of selected mating parents
	 * @param type : 1 - neighborhood; otherwise - whole population
	 */
	public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
		int ss;
		int r;
		int p;

		ss = neighborhood_[cid].length;
		while (list.size() < size) {
			if (type == 1) {	
				r = PseudoRandom.randInt(0, ss - 1);
				p = neighborhood_[cid][r];
			} else {				
				p = PseudoRandom.randInt(0, populationSize_ - 1);			
			}
			boolean flag = true;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == p) // p is in the list
				{
					flag = false;
					break;
				}
			}

			if (flag) {
				list.addElement(p);
			}
		}
	} // matingSelection

	/**
	 * Select the mating parents, depending on the selection 'type'
	 * 
	 * @param list : the set of the indexes of selected mating parents
	 * @param cid  : the id of current subproblem
	 * @param size : the number of selected mating parents
	 * @param type : 1 - neighborhood; otherwise - whole population
	 */
	public void matingSelectionStage1(Vector<Integer> list, int cid, int size, int type) {
		int ss;
		int r;
		int p;
		
		ss = neighborhood_[cid].length;
		while (list.size() < size) {
			if (type == 1) {
				
				int r1 = PseudoRandom.randInt(0, ss - 1);
				int r2 = PseudoRandom.randInt(0, ss - 1);
				while (r2 == r1)
					r2 =  PseudoRandom.randInt(0, ss - 1);
				
				if (population_.get(r1).getRank() < population_.get(r2).getRank()) 
					r = r1;
				else 
					r = r2;
				
				p = neighborhood_[cid][r];
				
			} else {
				
				int r1 = PseudoRandom.randInt(0, populationSize_ - 1);
				int r2 = PseudoRandom.randInt(0, populationSize_ - 1);
				while (r2 == r1)
					r2 =  PseudoRandom.randInt(0, populationSize_ - 1);
				
				if (population_.get(r1).getRank() < population_.get(r2).getRank()) 
					r = r1;
				else 
					r = r2;
				
				p = r;
			}
			boolean flag = true;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == p) // p is in the list
				{
					flag = false;
					break;
				}
			}

			if (flag) {
				list.addElement(p);
			}
		}
	} // matingSelection

	/**
	 * Select the mating parents for many-objective
	 * 
	 * @param list : the set of the indexes of selected mating parents
	 * @param cid  : the id of current subproblem
	 * @param size : the number of selected mating parents
	 * @param type : 1 - neighborhood; otherwise - whole population
	 */
	public void matingSelectionStage1Ma(Vector<Integer> list, int cid, int size, int type) {
		int ss;
		int r;
		int p;
	
		ss = neighborhood_[cid].length;
		while (list.size() < size) {		
				
			int r1 = PseudoRandom.randInt(0, populationSize_ - 1);
			int r2 = PseudoRandom.randInt(0, populationSize_ - 1);
			while (r2 == r1)
				r2 =  PseudoRandom.randInt(0, populationSize_ - 1);
			
			if (population_.get(r1).getRank() < population_.get(r2).getRank()) 
				r = r1;
			else 
				r = r2;

			p = r;
	
			boolean flag = true;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == p) // p is in the list
				{
					flag = false;
					break;
				}
			}

			if (flag) {
				list.addElement(p);
			}
		}
	} // matingSelection
	
	// initialise the reference point
	void initialize_RP() {
//		adaptiveEps(evaluations_,this.maxEvaluations_,0.1,0.01);
		int i;
		for(i = 0; i < problem_.getNumberOfObjectives(); i++)  {
			if(rpType_.equalsIgnoreCase("Nadir")) {
				zp_[i] = 1.0 + eps_;
			} else if(rpType_.equalsIgnoreCase("Ideal")){
				zp_[i] = 0 - eps_; // This is very important for improving the performance
			} else {				
				System.out.println("MOEDA.initialize_RP: unknown type " + rpType_);
				System.exit(-1);
			}
			
		}
	}
	/**
	 * Update the population by the current offspring
	 * 
	 * @param indiv: current offspring
	 * @param id:    index of current subproblem
	 * @param type:  update solutions in - neighborhood (1) or whole population (otherwise)
	 */
	void updateProblem(Solution indiv, int id, int type) {
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
				k = perm[i];
			}
				           
			//----------------------------------------old-----------------------------------------
			int flagDominate = 0;
			
			// The second stage
			if (stage2_) {
//				if (((OverallConstraintViolationComparator) constraint_).needToCompare(indiv, population_.get(k)))
//					 flagDominate =  constraint_.compare(indiv, population_.get(k)) ;
				 
				 double  con1, con2;	
				 con1 = Math.abs(population_.get(k).getOverallConstraintViolation());
		         con2 = Math.abs(indiv.getOverallConstraintViolation());
		         		        				
		         if (con1 <= epsilon_k_ && con2 <= epsilon_k_) {
		        	 
		        	 flagDominate = 0;   	 
	                	  
		         } else if (con2 == con1) {
		        	 
		        	 flagDominate = 0;   
		        	 
		         } else if (con2 < con1) {
		        	 flagDominate = -1;
		         } else if (con2 > con1) {
		        	 flagDominate = 1;
		         }
		         
			} // if stage2
			
			if (flagDominate == 0) { // The same number of Violated Constraints
				flagDominate = dominance_.compare(indiv, population_.get(k));
			}
			
			if (flagDominate == 0) { // Non-dominated 
				double f1, f2;					
				//濮ｆ棁绶濋柅鍌氱安閸婏拷
				f1 = fitnessFunction(population_.get(k), lambda_[k],k);
				f2 = fitnessFunction(indiv, lambda_[k],k);
				
				if (f2 < f1) {
					flagDominate = -1;
				}

			}
			
			if (flagDominate == -1) {// indiv is better 
				population_.replace(k, indiv);
				time++;					
			}  			
				
			// Update best solution population		
			int flag = 0; 
			flag = dominance_.compare(indiv, bestPopulation_.get(k))  ;
			
			if (flag ==-1) 
				bestPopulation_.replace(k, indiv);
			else if (flag == 0)
				if (fitnessFunction(indiv, lambda_[k], k) < fitnessFunction(bestPopulation_.get(k), lambda_[k], k))
					bestPopulation_.replace(k, indiv);

			
			
			// Update the best feasible population
			if (isfeasible(indiv)) { // indiv is feasible 
				
				int flagBetter = 0;
				
				if (feasiblePopulation_.get(k) == null)  
					flagBetter = -1;
				else {
					flagBetter = dominance_.compare(indiv, feasiblePopulation_.get(k));
					
					if (flagBetter == 0) {
						double f1, f2;	
						f1 = fitnessFunction(feasiblePopulation_.get(k), lambda_[k],k);
						f2 = fitnessFunction(indiv, lambda_[k],k);
						
						if (f2 < f1) {
							flagBetter = -1;
						}
					}
					
				}
				
				if (flagBetter == -1) {// indiv is better 
					feasiblePopulation_.replace(k, indiv);			
					// Update feasiable zideal
					
					if (updateFeasibleZideal(indiv)) {
						unsuccessfulTrailsForFeasible_ = 0;
					} else {
						unsuccessfulTrailsForFeasible_++;
					}
				
				}  	
								
			}
			
			/**
			 * ------------------------Code before------------------------------
			 */
//			int nvc_indiv = indiv.getNumberOfViolatedConstraint();
//			int nvc_k = population_.get(k).getNumberOfViolatedConstraint();
//			
//			if (nvc_indiv < nvc_k) {
//				
//				population_.replace(k, new Solution(indiv));
//				time++;
//				
//			} else if (nvc_indiv == nvc_k) {
//				
//				double f1, f2;
//
//				//濮ｆ棁绶濋柅鍌氱安閸婏拷
//				f1 = fitnessFunction(population_.get(k), lambda_[k]);
//				f2 = fitnessFunction(indiv, lambda_[k]);
//
//				if (problem_.isMaxmized() == true && functionType_.equals("WS")) {
//					if (f2 > f1) {
//						population_.replace(k, new Solution(indiv));
//						time++;
//					}
//				}
//				
//				else {
//					if (f2 < f1) {
//						population_.replace(k, new Solution(indiv));
//						time++;
//					}
//				}
//			}		
			//-----------------------Code before (end)------------------------------			 
			
			// the maximal number of solutions updated is not allowed to exceed 'limit'
			if (time > nr_) {
				return;
			}
		}
	} // updateProblem

	boolean updateFeasibleZideal(Solution indiv) {
		boolean updated = false;
		
		for (int i = 0 ; i < this.objectives_;i++) {
			if (indiv.getObjective(i) < feasiableZideal[i]) {
				feasiableZideal[i] = indiv.getObjective(i);
				updated  = true;
			}
		}
		
		return updated;
	}
	
	
	double innerproduct(double[] vec1, double[] vec2) {
		double sum = 0;
		for (int i = 0; i < vec1.length; i++)
			sum += vec1[i] * vec2[i];
		return sum;
	}

	double norm_vector(Vector<Double> x) {
		double sum = 0.0;
		for (int i = 0; i < (int) x.size(); i++)
			sum = sum + x.get(i) * x.get(i);
		return Math.sqrt(sum);
	}

	double norm_vector(double[] z) {
		double sum = 0.0;
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			sum = sum + z[i] * z[i];
		return Math.sqrt(sum);
	}

	double fitnessFunction(Solution individual, double[] lambda,int k) {
		double fitness;
		fitness = 0.0;
			    
		if (functionType_.equals("WS")) { //Weighted Sum Approach 
			
			for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
				fitness = fitness + lambda[n] * individual.getNormalizedObjective(n); // Objectives after normalization
			}
			
		} 
		else if (functionType_.equals("TCHE1")) {
		      double maxFun = -1.0e+30;

		      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
		        double diff = individual.getNormalizedObjective(n) - zp_[n]; //Note: No abs 
		       		        
		        double feval;
		        if (lambda[n] == 0) {
		          feval = 0.0001 * diff;
		        } else {
		          feval = lambda[n] * diff ;
		        }
		       
		        if (feval > maxFun) {
		          maxFun = feval;
		        }
		        
		      } // for

		      fitness = maxFun;
		} // if
		else if (functionType_.equals("TCHE2")) {
		      double maxFun = -1.0e+30;

		      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
		        double diff = individual.getNormalizedObjective(n) - zp_[n];
		        	       
		        
		        double feval;
		        if (lambda[n] == 0) {
		          feval = diff/0.0001;
		        } else {
		          feval = diff/lambda[n];
		        }
		        if (feval > maxFun) {
		          maxFun = feval;
		        }
		        
		      } // for

	         fitness = maxFun;
	         
		} else if (functionType_.equals("PBI")) {
			    int nobj = problem_.getNumberOfObjectives();
	            
	    		double nd = norm_vector(lambda); 
	    		
	    		for(int i=0; i<nobj; i++) 
	    			lambda[i] = lambda[i]/nd; //A unit vector
	     
	    	    // penalty method  
	    	    // temporary vectors NBI method 
	    		 double[] realA = new double[nobj]; 
	    		 double[] realB = new double[nobj]; 
	     
	    		// difference between current point and reference point 
	    		for(int n=0; n<nobj; n++) {
	    			realA[n] = (individual.getNormalizedObjective(n) - zp_[n]); // 	    				    			
	    		}
	     
	    		// distance along the search direction norm 
//	    		double d1 = Math.abs(innerproduct(realA,lambda)); 
	    		
	    		// 濞夈劍鍓伴敍锟� 瑜版徆1娑撳秴褰囩紒婵嗩嚠閸婂吋妞傞敍瀛瑽I閸滃瓥PBI閺勵垰褰叉禒銉х埠娑擄拷閻ㄥ嫨锟藉倸宓咺PBI閻╃缍嬫禍宸閸欐牕銇夋惔鏇犲仯
	    		double d1 = innerproduct(realA,lambda); // 娑撳秴褰囩紒婵嗩嚠閸婏拷
	
	    		// distance to the search direction norm 
	    		for(int n=0; n<nobj; n++) {
	    			realB[n] = (individual.getNormalizedObjective(n) - (zp_[n] + d1*lambda[n])); 
	    				    			
	    		}
	    		
	    		double d2 = norm_vector(realB); 	     
	    		fitness =  (d1 + theta_ *d2);   
	    		
		} else if (functionType_.equals("ASF1")) {
				
		      double maxFun = -1.0e+30;
		      double sumDiff = 0.0;
		      
		      // ------------------------Normalize weights------------------------
//		      double sum = 0.0;
//		      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
//		    	  if (lambda[n] == 0) {
//			          sum =  sum + 1 / 0.0001;
//			        } else {
//			          sum =  sum + 1 / lambda[n] ;
//			        }
//		      }
//			    
//		      
//		      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
//		    	  lambda[n] = (1.0/lambda[n]) / sum; 
//		    	  if (lambda[n] == 0) {
//		    		  lambda[n] = (1.0/0.0001) / sum; 
//		    	  }
//		    	  
//		    	  if (sum == 0) 
//		    		  lambda[n] = (1.0/lambda[n]) / 0.0001; 
////		    	  System.out.print(lambda[n] + ",");
//		      }
//		      System.out.print("\n");
		      //----------------------------------------------------------
		      
		      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
		        double diff = individual.getNormalizedObjective(n) - zp_[n]; 
		       		        
		        double feval;	
		        
		        if (lambda[n] == 0) {		
		           feval =  diff * 0.000001;
		        } else {
		           feval =  diff * lambda[n] ;
		        }    
		        
		        sumDiff = sumDiff + feval;
		        
		        if (feval > maxFun) {
		          maxFun = feval;
		        }
		        
		      } // for

		      fitness = maxFun + 0.001 * sumDiff;
		} // if
	 else if (functionType_.equals("ASF2")) {
		
	      double maxFun = -1.0e+30;
	      double sumDiff = 0.0;
	     	      
	      for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
	        double diff = individual.getNormalizedObjective(n) - zp_[n]; 
	       		        
	        double feval;	
	        
	        if (lambda[n] == 0) {		
	           feval =  diff / 0.000001;
	        } else {
	           feval =  diff / lambda[n] ;
	        }    
	        
	        sumDiff = sumDiff + feval;
	        
	        if (feval > maxFun) {
	          maxFun = feval;
	        }
	        
	      } // for

	      fitness = maxFun + 0.001 * sumDiff;
	} // if
	   else {
			System.out.println("MOEDA.fitnessFunction: unknown type " + functionType_);
			System.exit(-1);
		}
		return fitness;
	} // fitnessEvaluation
	
	
	public void normalize_pop (SolutionSet pop)	{
		int i, j;
		
		computeIdealPoint(pop);
		
		for (j = 0; j < problem_.getNumberOfObjectives(); j++) {
			
//			if (this.evaluations_ < 0.5 * this.maxEvaluations_) 
			z_nadir[j] = -1e+30;
			
			for (i = 0; i < populationSize_; i++)	{
				if (pop.get(i).getObjective(j) < z_ideal[j]) {
					z_ideal[j] = pop.get(i).getObjective(j);
				}
				if (pop.get(i).getObjective(j) > z_nadir[j])	{
					z_nadir[j] = pop.get(i).getObjective(j);
				}				
			}
		 
			if (z_nadir[j] == z_ideal[j])	{
				for (i = 0; i < populationSize_; i++){
					pop.get(i).setNormalizedObjective(j, 0.5);
				}
			} else {
				for (i = 0; i < populationSize_; i++)	{
					pop.get(i).setNormalizedObjective(j, (pop.get(i).getObjective(j) - z_ideal[j]) / (z_nadir[j] - z_ideal[j]));
				}
			} //if	 
			
		}	// for j		
	
	} //normalize_pop

	public void normalize_ind (Solution ind)	{
		int j;
		for (j=0; j< problem_.getNumberOfObjectives(); j++)	{
			if (z_nadir[j] == z_ideal[j]) {
				ind.setNormalizedObjective(j, 0.5);
			}
			else {
				ind.setNormalizedObjective(j, (ind.getObjective(j) - z_ideal[j]) / (z_nadir[j] - z_ideal[j]));
			}
		}
		return;
	}
	
	// update the reference point
	void update_RP(Solution ind){
		int i;
		
		for(i = 0; i < problem_.getNumberOfObjectives(); i++)   {
			
			if(rpType_.equalsIgnoreCase("Nadir")) {
				
				if(ind.getNormalizedObjective(i) > zp_[i])	{
					zp_[i] = ind.getNormalizedObjective(i);
				}
				
			} else {
				
				if(ind.getNormalizedObjective(i) < zp_[i])	{
					zp_[i] = ind.getNormalizedObjective(i);
				}
				
			}
			
		}
	}
	
	/**
	 * Split out nondominated and dominated solutions from the union set	 * 
	 * @param union
	 * @return
	 */
	public SolutionSet[] splitDominatedNondominated(SolutionSet union) {
		SolutionSet[] results = new SolutionSet[2];

		boolean[] dominated = flagsDominateNondominate(union);
		int noOfNondominated = 0;

		for (int i = 0; i < union.size(); i++) {
			if (dominated[i] == false) {
				noOfNondominated++;
			}
		}

		results[0] = new SolutionSet(noOfNondominated); // results[0] is the set of nondominated

		for (int i = 0; i < union.size(); i++) {
			if (dominated[i] == false) {
				results[0].add(union.get(i));
			}
		}

		int noOfdominated = 0;

		for (int i = 0; i < union.size(); i++) {
			if (dominated[i] == true) {
				noOfdominated++;
			}
		}

		results[1] = new SolutionSet(noOfdominated); // results[1] is the set of dominated

		for (int i = 0; i < union.size(); i++) {
			if (dominated[i] == true) {
				results[1].add(union.get(i));
			}
		}

		return results;
	}
	
	public boolean[] flagsDominateNondominate(SolutionSet solutionSet) {
		// Find dominated members in population_
		boolean[] dominated = new boolean[solutionSet.size()];
		Comparator dominance = new DominanceComparator();
		// Remove dominated solutions in the population
		for (int i = 0; i < solutionSet.size(); i++) {

			if (dominated[i] == false) { //
				Solution sol_i = solutionSet.get(i);

				for (int j = i + 1; j < solutionSet.size(); j++) {
					if (dominated[j] == true) {
						continue;
					}

					Solution sol_j = solutionSet.get(j);
					int flagDominate = dominance.compare(sol_i, sol_j);
					if (flagDominate == -1) { // sol_i dominates sol_j
						dominated[j] = true;
					} else if (flagDominate == 1) { // sol_i is dominated by
													// sol_j
						dominated[i] = true;
						break;
					}
				}
			}
		}
		return dominated;
	}

} // MOEDA
