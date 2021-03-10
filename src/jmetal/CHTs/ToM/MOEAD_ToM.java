/* MOEAD_ToM.java
 * 
 * Author:  Yi Xiang <xiangyi@scut.edu.cn> or <gzhuxiang_yi@163.com>  
 * 
 * Data: 10/03/2021
 * Copyright (c) 2021 Yi Xiang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package jmetal.CHTs.ToM;

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
import jmetal.core.Variable;
import jmetal.myutils.RecordMetricDuringRun;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Ranking;
import jmetal.util.Utils;
import jmetal.util.comparators.EqualSolutions;
import jmetal.util.comparators.OverallConstraintViolationComparator;
import jmetal.util.comparators.PureDominanceComparator;

public class MOEAD_ToM extends Algorithm {

	private static final long serialVersionUID = 1L;
	private SolutionSet population_;    // Population repository
	private SolutionSet feasiblePopulation_;  // feasible population
	private SolutionSet offspringPopulation_;    // Offspring population repository
	private SolutionSet union_;                 // Offspring and population 
    //private SolutionSet bestPopulation_;
	

	private int    populationSize_;     // Population size
	private int    T_;     				// Neighborhood size
	private int    nr_;    				// Maximal number of solutions replaced by each child solution
	private int    objectives_;         // No of objective
	
	private double delta_; 				// Probability that parent solutions are selected from neighborhood
	private double theta_;              // Used in PBI function 
	private double eps_  = 0.05;        // A small positive number, used in defining ideal or nadir points

	public double alpha_ = 10.0;
	
	private int[][]    neighborhood_; 	// Neighborhood matrix
	private double[][] lambda_; 		// Lambda vectors

	private double []   zp_;	       // Reference point (ideal or nadir points), set dynamically
	private double []   z_ideal;       // estimated ideal point 
	private double []   z_nadir;       // estimated nadir point 	
		
	private String functionType_;             // The type of scalarization  function  
	private String rpType_;                   // The type of reference point  
	private String dataDirectory_;
	
	private Operator crossover_;             // Crossover 
	private Operator mutation_; 			 // mutation 

	private static final Comparator constraint_ = new OverallConstraintViolationComparator();
	private static final Comparator dominance_ = new PureDominanceComparator();
	  
	private CaptureConvergence captureConvergence_; // our class 
	private boolean convergenceFlag = false;
	
	RecordMetricDuringRun rmdr; // our class 
    boolean rmdrFlag = false;    

	private boolean normalize_ = false; // normlize the population or not

	private int    evaluations_;   // Counter for the number of function evaluations
	private int    maxEvaluations_;
	private boolean stage2_ = false; // 

	//public static int runTimes = 0;
	/**
	 * Constructor
	 * 
	 * @param Problem to solve
	 */
	public MOEAD_ToM(Problem problem) {
		super(problem);					
		
	} // MOEADToM

	public SolutionSet execute() throws JMException, ClassNotFoundException {
		
//		runTimes++; // Record runtimes, only used in convergence study.
				
		// ------------鐠囪褰噁unction閸滃p閻ㄥ嫮琚崹瀣剁礉閻㈢喐鍨氶惄绋跨安閻ㄥ嫭鏋冩禒锟�---------------------
		functionType_ = "PBI";
		rpType_       = "ideal";
		
		alpha_ = ((Double) this.getInputParameter("alpha")).doubleValue();	
		
		if (convergenceFlag == true) 
			  captureConvergence_ = new CaptureConvergence(problem_.getName(), "MOEADToM", problem_.getNumberOfObjectives());	
		
		// ------------鐠囪褰噁unction閸滃p閻ㄥ嫮琚崹瀣剁礉閻㈢喐鍨氶惄绋跨安閻ㄥ嫭鏋冩禒璁圭礄end閿涳拷---------------------
				
		if (rmdrFlag== true) {
//			rmdr = new RecordMetricDuringRun("CMOEAStudyCDTLZ",problem_.getName(), 
//											"MOEADToMOnlyStage1",problem_.getNumberOfObjectives()); // For CMOEAStudyCDTLZ 
			
//			rmdr = new RecordMetricDuringRun("CMOEAStudyMW",problem_.getName(), 
//					"MOEADToMRelease", problem_.getNumberOfObjectives()); // For CMOEAStudyMW 
			
//			rmdr = new RecordMetricDuringRun("CMOEAStudyMW",problem_.getName(), 
//					"MOEADToMOnlyStage2",problem_.getNumberOfObjectives()); // For CMOEAStudyMW 
//			
//			rmdr = new RecordMetricDuringRun("CMOEAStudyDAS",problem_.getFullProblemName(), 
//					"MOEADToMOnlyStage2",problem_.getNumberOfObjectives());  // For CMOEAStudyDAS 
		}
		
		
		System.out.println("=============Running MOEA/D-ToM=============");
		long elapsed = 0, start = System.currentTimeMillis(); // start
		
		evaluations_ = 0;	
		int generations_ = 0;
		
		maxEvaluations_ = ((Integer) this.getInputParameter("maxEvaluations")).intValue();		
		//Long maxRunTimeMS = ((Long) this.getInputParameter("maxRunTimeMS")).longValue();
		populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
		
		int maxGen = maxEvaluations_/populationSize_;
				
		alpha_ = alpha_/100;
		
//		normalize_ = ((boolean) this.getInputParameter("normalize"));		

		// ----------------------------------------------------------
		// Used only for random weight vectors, should be modified
//		while (populationSize_ % 4 > 0)
//			populationSize_ += 1;	
		// ----------------------------------------------------------
		
		System.out.println("MOEA/D-ToM maxEvaluations = " +  maxEvaluations_);		
		System.out.println("MOEA/D-ToM popsize = " +  populationSize_ );				
		System.out.println("MOEA/D-ToM alpha_ = " +  alpha_ );	
		
		
		dataDirectory_ = this.getInputParameter("dataDirectory").toString();		
	    T_ = ((Integer) this.getInputParameter("T")).intValue();
	    nr_ = ((Integer) this.getInputParameter("nr")).intValue();
	    delta_ = ((Double) this.getInputParameter("delta")).doubleValue();
	    theta_ = ((Double) this.getInputParameter("theta")).doubleValue();
	    objectives_ = problem_.getNumberOfObjectives();
	    
		population_ = new SolutionSet(populationSize_);
		feasiblePopulation_ = new SolutionSet(populationSize_);
		//bestPopulation_   = new SolutionSet(populationSize_);
		
		// Add null
		for(int i = 0; i < populationSize_; i++) {
			feasiblePopulation_.add(null);
		}
		
		offspringPopulation_ = new SolutionSet(populationSize_);
		union_               = new SolutionSet(2 * populationSize_);		
		
		neighborhood_ = new int[populationSize_][T_];		
		lambda_ = new double[populationSize_][objectives_];
			
		zp_     = new double[objectives_];
		z_ideal = new double [objectives_];
		z_nadir = new double [objectives_];		
		
		for (int j=0; j < objectives_; j++) {
			z_ideal[j] = 1e+30; 
			z_nadir[j] = -1e+30;	
		} // Initialize z_ideal and z_nadir
								
		//newWeights_= new WeightSet(populationSize_); // Store historical and new weights populationSize_		
		crossover_ = operators_.get("crossover"); 	// default: SBX crossover
		mutation_ = operators_.get("mutation"); 	// default: polynomial mutation
			
		// STEP 1. Initialization		
		// STEP 1.1. Compute Euclidean distances between weight vectors and find T
		initUniformWeight();		
//		initRandomWeight();		
		initNeighborhood();

		// STEP 1.2. Initialize population
		initPopulation(); // Initialize N random solutions	
//		initPopulationFixed();//for convergence 
		
		
		generations_ = 1;			
		
		initIdealPoint();
		
		if (convergenceFlag== true) 
			captureConvergence_.runCaptureConvergence(0, population_);				
			    
		int infeasibleGen = 0;

		
		// STEP 2. Update
		do {		
//			normalize_pop(population_);	// Find max and min values in current population
//			initialize_RP();
						
			if (this.population_.GetFeasible_Ratio() == 0.0) {
				infeasibleGen ++;
			}
			else {
				infeasibleGen = 0;
			}
			
			evolution();				
			
			///*
			if ((!stage2_  && (evaluations_ >= 0.5 * this.maxEvaluations_ )) ||  (!stage2_ && infeasibleGen >= (alpha_ * maxGen))) { // 
				
				if (infeasibleGen >= (alpha_ * maxGen)) {
					
					System.out.println("-----------------Terminated in advance--------------------");
					if (rmdrFlag== true) { 	
						rmdr.writeMetric("Terminated", 1);	
					} // if 
					
				} else {
					
					if (rmdrFlag== true) { 	
						rmdr.writeMetric("Terminated", 0);	
					} // if 
					
				}							
				
				int interSize = intersectionSize(population_,feasiblePopulation_);
//				System.out.println("feasibleSize = " + population_.GetFeasible_Ratio());
//				System.out.println("" + "interSize = " + interSize);
				
				int problemType = 0;						
				
				if (interSize >= 0.95 * populationSize_) {
					problemType = 1;
					System.out.println("Type I");
				} else if (interSize == 0) {
					problemType = 3;
					System.out.println("Type III");
				} else {
					problemType = 2;
					System.out.println("Type II");
				}
							
				// ----------------------Construct new population based on different types----------------------------
				if (problemType == 2) {
					reconstructPopulationTypeII(); 
//					reconstructPopulation();
				} else if (problemType == 3 ) { //
					reconstructPopulationTypeIII();
//					reconstructPopulation();
				} else {
					// type I, do nothing
				}
				//------------------------------------------------------------------------------------------------------
				
				// Construct population without considering types
//				reconstructPopulation();
				
				System.out.println("---Stage 2---");
				stage2_ = true;	
				
				//---------------record type ------------------				
				// record time
				if (rmdrFlag== true) { 	
					rmdr.writeMetric("Type", problemType);	
				} // if 
			}		
			// */			
		
			generations_++;
			elapsed = System.currentTimeMillis() - start;		
		

		} while (evaluations_ < maxEvaluations_);
						        
        reconstructPopulation();
        Ranking ranking = new Ranking(removeSame(population_));
        
	    System.out.println("RunTimeMS: " + elapsed);
        System.out.println("evaluations: " + evaluations_);
        System.out.println("Final size = " + ranking.getSubfront(0).size());
        
        return ranking.getSubfront(0);        
//         return population_ ;
	}

	  /*
	   */
	  void initIdealPoint() throws JMException, ClassNotFoundException {
	    for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
	      zp_[i] = 1.0e+30;
	    } // for

	    for (int i = 0; i < populationSize_; i++) {
	      updateReference(population_.get(i));
	    } // for
	  } // initIdealPoint
	   
	  void updateReference(Solution individual) {
		    for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
		      if (individual.getObjective(n) < zp_[n]) {
		        zp_[n] = individual.getObjective(n);
		      }
		    }
		  } // updateReference
	  
	 public int intersectionSize(SolutionSet population,SolutionSet feasiblePopulation) {
		 int size = 0;
		 
		 Comparator   equals  = new EqualSolutions();
		    
		 for (int i = 0; i< population.size(); i++) {
			 if (equals.compare(population.get(i),feasiblePopulation.get(i)) == 0) 
				 size++;
		 }
		 
		 return size;
	 }
	 
	 
	/**
	 * 
	 */
	public void reconstructPopulation() {
//		bestPopulation_.clear();
		
		// Update best solution population
//		for (int i = 0; i < populationSize_;i++) {			
//			
//			int flag = 0; 
//			flag = dominance_.compare(population_.get(i), bestPopulation_.get(i))  ;
//			
//			if (flag ==-1) 
//				bestPopulation_.replace(i, population_.get(i));
//			else if (flag == 0)
//				if (fitnessFunction(population_.get(i), lambda_[i], i) < fitnessFunction(bestPopulation_.get(i), lambda_[i], i))
//					bestPopulation_.replace(i,population_.get(i));		
//		}
		
		//-----------------------------------------------------------------
		for (int i = 0; i < populationSize_;i++) {
			
			if (!isfeasible(population_.get(i)) && feasiblePopulation_.get(i)!=null) {
				population_.replace(i, feasiblePopulation_.get(i));	
			}
		}
	}
	
	
	/**
	 * 
	 */
	public void reconstructPopulationTypeII() {		
		//-----------------------------------------------------------------
		for (int i = 0; i < populationSize_;i++) {
			
			if (!isfeasible(population_.get(i)) && feasiblePopulation_.get(i)!=null) {
				// Do it randomly
				if (PseudoRandom.randDouble() <= 0.5) 
					population_.replace(i, feasiblePopulation_.get(i));	
			}
		}
	}
	
	
	
	public void reconstructPopulationTypeIII() {
		
		for (int i = 0; i < populationSize_;i++) {
			
			if (feasiblePopulation_.get(i)!=null) {
				population_.replace(i, feasiblePopulation_.get(i));	
			}
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
									
				}  	
			}
		}
		
		
		
	}
	
	  public SolutionSet removeSame(SolutionSet pop) {
		 // if (pop.size() == 0) return pop;
		  
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

		Solution child = null;
		//----------------------------old----------------------------------------------------- 
		
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
		 // --------------------Use crossover and mutation (end)--------------------
						
		// STEP 2.3. Repair. 
		
		// Evaluation
		problem_.evaluate(child);
		problem_.evaluateConstraints(child);
		evaluations_++;		
		
		if (convergenceFlag== true) {
//			SolutionSet notNull = new SolutionSet(feasiblePopulation_.size());
//			for (int i=0;i<feasiblePopulation_.size();i++) {
//				if (feasiblePopulation_.get(i) != null)
//					notNull.add(feasiblePopulation_.get(i));
//			}
			captureConvergence_.runCaptureConvergence(evaluations_, removeSame(population_));
		}
			
		
//		normalize_ind(child);				
//		update_RP(child);
		
		updateReference(child);
		// STEP 2.5. Update of solutions
		updateProblem(child, n, type);
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
	 * Get Feasible Set
	 * @return
	 */
	public SolutionSet getFeasibleSet(SolutionSet union) {
		SolutionSet feasible = new SolutionSet(getNumberOfFeasibleSolutions(union));
		
		for (int i = 0;i < union.size();i++) {
        	if (union.get(i).getOverallConstraintViolation() == 0.0) {
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
        	if (union.get(i).getOverallConstraintViolation() == 0.0) {
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
        	if (union.get(i).getOverallConstraintViolation() < 0.0) {
        		infeasible.add(union.get(i));
        	}
        }
		
		return infeasible;
		
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
			
//			bestPopulation_.add(newSolution);						
		}
	} // initPopulation

	public void initPopulationFixed() throws JMException, ClassNotFoundException {

		/**
		 * Method 1: Random generate N solutions
		 */
		population_ = new SolutionSet(populationSize_);

		String path = "./jmetalExperiment/ConvergenceInitialPopulation/M=" + problem_.getNumberOfObjectives() 
				      + "/InitialPopulation/" + problem_.getName() +".VAR";
		System.out.println(" problem_.getName() "  + problem_.getName());
		
		
//		if (! (new File(path).exists()))  {
			path = "./jmetalExperiment/ConvergenceInitialPopulation/M=" + problem_.getNumberOfObjectives()  + "/InitialPopulation/";
			checkExperimentDirectory(path);				
			path = path +  problem_.getName() +  ".VAR";
			resetFile(path);	
			
			
			for (int i = 0; i < populationSize_; i++) {
				Solution newSolution = new Solution(problem_);		
				problem_.evaluate(newSolution);
				problem_.evaluateConstraints(newSolution);
				evaluations_++;		
				population_.add(newSolution);

				if (isfeasible(newSolution)) {				
					feasiblePopulation_.replace(i, newSolution);
				}
									
			}
						
			population_.printVariablesToFile(path);	
			
//			return;
//		} //if 
		
//		// Write to file as initial population, optional
//		 try {
//		      // Open the file
//		      FileInputStream fis   = new FileInputStream(path)     ;
//		      InputStreamReader isr = new InputStreamReader(fis)    ;
//		      BufferedReader br      = new BufferedReader(isr)      ;
//		      
//		      String aux = br.readLine();
//		      while (aux!= null && !"".equalsIgnoreCase(aux)) {
//		    	  
//		    	Solution newSolution = new Solution(problem_);
//		    	Variable [] var = newSolution.getDecisionVariables();
//		    	
//		        StringTokenizer st = new StringTokenizer(aux);
//		        int i = 0;
//		  
//		        while (st.hasMoreTokens()) {
//		          double value = new Double(st.nextToken());
//		          (var [i]).setValue(value); 
//		          i++;
//		        }		        
//		        
//		        problem_.evaluate(newSolution);
//				problem_.evaluateConstraints(newSolution);
//				evaluations_++;
//				population_.add(newSolution);				
//		       
//				if (isfeasible(newSolution)) {				
//					feasiblePopulation_.replace(i, newSolution);
//				}
//				
////				System.out.print(newSolution.toString() + " \n");
//				
//		        aux = br.readLine();
//		      }
//		            
//		      br.close();
//		      
//		  } catch (Exception e) {
//		      System.out.println("InputFacilities crashed reading for file: "+path);
//		      e.printStackTrace();
//		 }
		
	} // initPopulationRandom
	
	private void checkExperimentDirectory(String path) {
		File experimentDirectory;

		experimentDirectory = new File(path);
		if (experimentDirectory.exists()) {
			System.out.println("Experiment directory exists");
			if (experimentDirectory.isDirectory()) {
				System.out.println("Experiment directory is a directory");
			} else {
				System.out.println("Experiment directory is not a directory. Deleting file and creating directory");
			}
			experimentDirectory.delete();
			new File(path).mkdirs();
		} // if
		else {
			System.out.println("Experiment directory does NOT exist. Creating");
			new File(path).mkdirs();
		} // else
	} // checkDirectories
	
	public void resetFile(String file) {
		File f = new File(file);
		if(f.exists()){			
			System.out.println("File " + file + " exist.");

			if(f.isDirectory()){
				System.out.println("File " + file + " is a directory. Deleting directory.");
				if(f.delete()){
					System.out.println("Directory successfully deleted.");
				}else{
					System.out.println("Error deleting directory.");
				}
			}else{
				System.out.println("File " + file + " is a file. Deleting file.");
				if(f.delete()){
					System.out.println("File succesfully deleted.");
				}else{
					System.out.println("Error deleting file.");
				}
			}			 
		}else{
			System.out.println("File " + file + " does NOT exist.");
		}
	} // resetFile
	
	
	// Judge a solution is feasible or not
	public boolean isfeasible(Solution sol) {
		if (sol.getOverallConstraintViolation() == 0.0 && sol.getNumberOfViolatedConstraint() == 0) //
			return true;
		else 
			return false;
	}
			
	
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

		
	
	// initialise the reference point
	void initialize_RP() {
		int i;
		for(i = 0; i < problem_.getNumberOfObjectives(); i++)  {
							
			if(rpType_.equalsIgnoreCase("Ideal")){
			
				if (normalize_) 
					zp_[i] = 0 - eps_; // This is very important for improving the performance
				else
					zp_[i] = z_ideal[i];
				
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
				
			int flagDominate = 0;
			
			if (stage2_) {
				if (((OverallConstraintViolationComparator) constraint_).needToCompare(indiv, population_.get(k)))
					 flagDominate =  constraint_.compare(indiv, population_.get(k)) ;				
			} 
			
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
//			int flag = 0; 
//			flag = dominance_.compare(indiv, bestPopulation_.get(k))  ;
//			
//			if (flag ==-1) 
//				bestPopulation_.replace(k, indiv);
//			else if (flag == 0)
//				if (fitnessFunction(indiv, lambda_[k], k) < fitnessFunction(bestPopulation_.get(k), lambda_[k], k))
//					bestPopulation_.replace(k, indiv);
						
			// --------------------Update the best feasible population------------------
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
					feasiblePopulation_.replace(k, new Solution(indiv));	
				}  	
								
			} //--------------------------------------------------------------------------------------------
			
			// the maximal number of solutions updated is not allowed to exceed 'limit'
			if (time > nr_) {
				return;
			}
		}
	} // updateProblem
	
	
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
		        if (!normalize_)    
		        	diff = individual.getObjective(n) - zp_[n];
		        
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
		       
		        if (!normalize_)    
		        	diff = individual.getObjective(n) - zp_[n];
		        
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
	    			if (normalize_) 
	    				realA[n] = (individual.getNormalizedObjective(n) - zp_[n]); 	   
	    			else
	    				realA[n] = (individual.getObjective(n) - zp_[n]); 	  
	    		}
	     
	    		// distance along the search direction norm 
//	    		double d1 = Math.abs(innerproduct(realA,lambda)); 
	    		
	    		// 濞夈劍鍓伴敍锟� 瑜版徆1娑撳秴褰囩紒婵嗩嚠閸婂吋妞傞敍瀛瑽I閸滃瓥PBI閺勵垰褰叉禒銉х埠娑擄拷閻ㄥ嫨锟藉倸宓咺PBI閻╃缍嬫禍宸閸欐牕銇夋惔鏇犲仯
	    		double d1 = innerproduct(realA,lambda); // 娑撳秴褰囩紒婵嗩嚠閸婏拷
	
	    		// distance to the search direction norm 
	    		for(int n=0; n<nobj; n++) {
	    			if (normalize_) 
	    				realB[n] = (individual.getNormalizedObjective(n) - (zp_[n] + d1*lambda[n])); 	 
	    			else
	    				realB[n] = (individual.getObjective(n) - (zp_[n] + d1*lambda[n])); 
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
	
	
	public void normalize_pop (SolutionSet pop )	{
		int i, j;
		
		for (j = 0; j < problem_.getNumberOfObjectives(); j++) {
			
			z_nadir[j] = -1e+30;
			
			for (i = 0; i < populationSize_; i++)	{
						
				if (pop.get(i).getObjective(j) < z_ideal[j]) {
					z_ideal[j] = pop.get(i).getObjective(j);			
				} 											
				
				if (pop.get(i).getObjective(j) > z_nadir[j])	{
					z_nadir[j] = pop.get(i).getObjective(j);
				}				
			}
		 
			if (normalize_) {
				
				if (z_nadir[j] == z_ideal[j])	{
					for (i = 0; i < populationSize_; i++){
						pop.get(i).setNormalizedObjective(j, 0.5);
					}
				} else {
					for (i = 0; i < populationSize_; i++)	{
						pop.get(i).setNormalizedObjective(j, (pop.get(i).getObjective(j) - z_ideal[j]) / (z_nadir[j] - z_ideal[j]));
					}
				} //if	 
				
			} // if
			
		}	// for j		
	
	} //normalize_pop

	public void normalize_ind (Solution ind)	{
		int j;
		
		if (!normalize_) {
			return;
		}
		
		for (j=0; j < problem_.getNumberOfObjectives(); j++)	{
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
			
			if(rpType_.equalsIgnoreCase("Ideal")) {
				
				if (normalize_) {
					if(ind.getNormalizedObjective(i) < zp_[i])	
						zp_[i] = ind.getNormalizedObjective(i);
				} else {
					if(ind.getObjective(i) < zp_[i])	
						zp_[i] = ind.getObjective(i);
				}
				
			} else {			
				
				System.out.println("Unknown reference point type");
				
			}			
		} // for
	} // update_RP
	

} // MOEDA
