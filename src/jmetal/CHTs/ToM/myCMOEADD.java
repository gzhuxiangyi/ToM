/**
 * CMOEADD.java
 * 
 * This is main implementation of C-MOEA/DD for handling many-objective constrained optimization problems.
 * 
 * Author:
 * 		Ke Li <keli.genius@gmail.com>
 * 
 * Reference:
 * 		Ke Li, Kalyanmoy Deb, Qingfu Zhang, Sam Kwong, 
 * 		"An Evolutionary Many-Objective Optimization Algorithm Based on Dominance and Decomposition"
 * 		IEEE Transactions on Evolutionary Computation, 19(5): 694-716, 2015.
 * 
 * Homepage:
 * 		http://www.cs.cityu.edu.hk/~51888309/
 * 
 * Copyright (c) 2015 Ke Li
 * 1. To handle the case where negative CV is used, we reset CV values after evaluating
 * 
 */

package jmetal.CHTs.ToM;

import java.io.BufferedReader;
import java.io.File;
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
import jmetal.myutils.CaptureConvergence;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.Ranking;
import jmetal.util.ranking.ConstraintNondominatedRanking;
import jmetal.util.ranking.NondominatedRanking;

public class myCMOEADD extends Algorithm {

	private int populationSize_;

	private SolutionSet population_; // Pareto-based population

	double[][] lambda_; 	// Lambda vectors

	int numRanks;			// ��ǰ�ķ�֧�����
	int T_; 				// neighborhood size
	int evaluations_; 		// counter of evaluation times
	double delta_; 			// probability that parent solutions are selected from neighborhood
	int[][] neighborhood_;  // neighborhood structure
	
	int[][] rankIdx;			// index matrix for the nondomination levels
	int[][] subregionIdx;		// index matrix for subregion record
	double[][] subregionDist;	// distance matrix for perpendicular distance

	String functionType_;

	Operator crossover_;
	Operator mutation_;
	Operator selectionOperator_;

	String dataDirectory_;
	
	CaptureConvergence captureConvergence_; // our class 
    boolean convergenceFlag = false;
    
	/***********************************************************************************/
	double[] zp_; 	// ideal point for Pareto-based population
	double[] nzp_; 	// nadir point for Pareto-based population

	/***********************************************************************************/
	/**
	 * Constructor
	 * 
	 * @param problem
	 */
	public myCMOEADD(Problem problem) {
		super(problem);

		// functionType_ = "_TCHE2";
		functionType_ = "_PBI";
		
	} // ssNSGAIII

	public SolutionSet execute() throws JMException, ClassNotFoundException {

		int maxEvaluations;

		String str1 = "Conv";
		String str2 = "Div";
		String str3, str4;

		evaluations_ = 0;

		crossover_ = operators_.get("crossover");
		mutation_  = operators_.get("mutation");
		selectionOperator_ = operators_.get("selection");

		dataDirectory_  = this.getInputParameter("dataDirectory").toString();
		maxEvaluations  = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
		System.out.println("CMOEA/DD maxEvaluations = " + maxEvaluations);
		
		if (convergenceFlag== true) 
			  captureConvergence_ = new CaptureConvergence(problem_.getName(), "CMOEADD", 
					  problem_.getNumberOfObjectives(), problem_.getNumberOfVariables(), maxEvaluations);
		
		populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
		System.out.println("CMOEA/DD popSize = " + populationSize_);
		 
		T_               = ((Integer) this.getInputParameter("T")).intValue();
		
//		T_     = 20;
		delta_ = 0.9;

		System.out.println("CMOEA/DD T = " + T_);
		
		population_ = new SolutionSet(populationSize_);

		neighborhood_ = new int[populationSize_][T_];
		lambda_ 	  = new double[populationSize_][problem_.getNumberOfObjectives()];

		/*****************************************************************************/
		zp_  = new double[problem_.getNumberOfObjectives()]; // ideal point for Pareto-based population
		nzp_ = new double[problem_.getNumberOfObjectives()]; // nadir point for Pareto-based population

		rankIdx		  = new int[populationSize_][populationSize_];
		subregionIdx  = new int[populationSize_][populationSize_];
		subregionDist = new double[populationSize_][populationSize_];

		/**************************************************************************************/
		
		// STEP 1. Initialization
		initUniformWeight();
		initNeighborhood();
		
		if (convergenceFlag == true) 
			initPopulationFixed();
		else 
			initPopulation(); // 初始化种群

		if (convergenceFlag== true) 
			captureConvergence_.runCaptureConvergence(0, returnPopulation(population_));
		
		initIdealPoint();
		initNadirPoint();

		// initialize the distance
		for (int i = 0; i < populationSize_; i++) {
			double distance = calculateDistance2(population_.get(i), lambda_[i], zp_, nzp_);
			subregionDist[i][i] = distance;
		}
		
		// ��ȡ��feasible��
		int num_feasible = 0;
		SolutionSet feasiblePop = new SolutionSet(populationSize_);
		Vector<Integer> feasibleList = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			if (population_.get(i).getOverallConstraintViolation() == 0.0) {
				num_feasible++;
				feasibleList.addElement(i);
				feasiblePop.add(population_.get(i));
			}
		}
		
		// ��feasible����з�֧������
		Ranking ranking = new Ranking(feasiblePop);
		int curRank;
		for (int i = 0; i < num_feasible; i++) {
			curRank = population_.get(feasibleList.get(i)).getRank();
			rankIdx[curRank][feasibleList.get(i)] = 1;
		}

		int gen = 0;
		// main procedure
		do {
			int[] permutation = new int[populationSize_];
			Utils.randomPermutation(permutation, populationSize_);

			for (int i = 0; i < populationSize_; i++) {
				int cid = permutation[i];

				int type;
				double rnd = PseudoRandom.randDouble();

				// mating selection style
				if (rnd < delta_)
					type = 1; // neighborhood
				else
					type = 2; // whole population

				Solution[] parents   = new Solution[2];
				Solution[] offSpring = new Solution[2];
				//parents = matingSelection(cid, type);
				parents = matingSelection_constraint(cid, type);

				// SBX crossover
				offSpring = (Solution[]) crossover_.execute(parents);

				// polynomial mutation
				mutation_.execute(offSpring[0]);
				mutation_.execute(offSpring[1]);

				// evaluation
				problem_.evaluate(offSpring[0]);
				problem_.evaluateConstraints(offSpring[0]);
				
				offSpring[0].setOverallConstraintViolation(-offSpring[0].getOverallConstraintViolation());
				
				problem_.evaluate(offSpring[1]);
				problem_.evaluateConstraints(offSpring[1]);		
				
				offSpring[1].setOverallConstraintViolation(-offSpring[1].getOverallConstraintViolation());
				
				evaluations_ += 1;

				if (convergenceFlag== true) {
					captureConvergence_.runCaptureConvergence(evaluations_, returnPopulation(population_));
				}
					
				evaluations_ += 1;
				
				if (convergenceFlag== true) {
					captureConvergence_.runCaptureConvergence(evaluations_, returnPopulation(population_));
				}
				
				if (evaluations_ >= maxEvaluations) {
					break;
				}
				// update ideal points
				updateReference(offSpring[0], zp_);
				updateReference(offSpring[1], zp_);

				// update nadir points
				updateNadirPoint(offSpring[0], nzp_);
				updateNadirPoint(offSpring[1], nzp_);

				updateArchive(offSpring[0]);
				updateArchive(offSpring[1]);
			} // for
			++gen;					
			
//			System.out.println(gen);
		} while (evaluations_ < maxEvaluations);
		
		 // Return only feasible solutions
        int feasibleNo = 0;
        
        for (int i=0;i<population_.size();i++) {
        	if (population_.get(i).getOverallConstraintViolation() == 0.0) {
        		feasibleNo ++;
        	}
        }
        
        SolutionSet finalPop = new SolutionSet(feasibleNo);
        
        for (int i=0;i<population_.size();i++) {
        	if (population_.get(i).getOverallConstraintViolation() == 0.0) {
        		finalPop.add(population_.get(i));
        	}
        }
        
//        System.out.println("maxFEs = " + evaluations_);
//        return finalPop;	
        
          System.out.println("CMOEA/DD maxFEs = " + evaluations_);
		  System.out.println("CMOEA/DD No. of feasible solutions  = " + finalPop.size());
		 
		  if (finalPop.size() != 0) {
				// Return feasible non-dominated solutions 
		        NondominatedRanking nondominanceRanking = new NondominatedRanking(finalPop);
				return nondominanceRanking.getSubfront(0);
			} else {
				ConstraintNondominatedRanking ConstraintRanking = new ConstraintNondominatedRanking(population_); // Return non-dominated solutions
				return ConstraintRanking.getSubfront(0);
//				return population_;
			}     		
	}

	/**
	 * Initialize the weight vectors for subproblems (We only use the data that
	 * are already available)
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
			dataFileName = "W" + problem_.getNumberOfObjectives() + "D_"
					+ populationSize_ + ".dat";
	
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
				System.out
						.println("initUniformWeight: failed when reading for file: "
								+ dataDirectory_ + "/" + dataFileName);
				e.printStackTrace();
			}
		}
	} // initUniformWeight

	public void initPopulationFixed() throws JMException, ClassNotFoundException {

		/**
		 * Method 1: Random generate N solutions
		 */
		population_ = new SolutionSet(populationSize_);

		String path = "./jmetalExperiment/ConstrainedProblemsStudyConvergence/D=" + problem_.getNumberOfVariables()
				      + "FEs=" + ((Integer) this.getInputParameter("maxEvaluations")).intValue()
				      + "/M=" + problem_.getNumberOfObjectives() 
				      + "/InitialPopulation/";
		
		File f = new File(path);
		
		if (!f.exists()) {
			f.mkdirs();
		}
		
		path = path + problem_.getName() +".VAR";
		
		f = new File(path);			
		
		if(!f.exists()){ // File does not exist.		
			System.out.println("File does not exist.");
			
			for (int i = 0; i < populationSize_; i++) {
				Solution newSolution = new Solution(problem_);

				problem_.evaluate(newSolution);
				problem_.evaluateConstraints(newSolution);

				newSolution.setOverallConstraintViolation(-newSolution.getOverallConstraintViolation());
				
				population_.add(newSolution);
			} // for
			
			population_.printVariablesToFile(path); // Write into file
			
		} else{
			System.out.println("File exists.");
			// Read from file
			 try {
			      // Open the file
			      FileInputStream fis   = new FileInputStream(path)     ;
			      InputStreamReader isr = new InputStreamReader(fis)    ;
			      BufferedReader br      = new BufferedReader(isr)      ;
			      
			      String aux = br.readLine();
			      
			      int j = 0;
			      
			      while (aux!= null && !"".equalsIgnoreCase(aux) && j < populationSize_) {   	  
			    	
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
					newSolution.setOverallConstraintViolation(-newSolution.getOverallConstraintViolation());
					
					evaluations_++;
					population_.add(newSolution);	
					subregionIdx[j][j] = 1;
				    aux = br.readLine();
				    j++;
			      }
			            
			      br.close();
			      
			  } catch (Exception e) {
			      System.out.println("InputFacilities crashed reading for file: "+path);
			      e.printStackTrace();
			 }
		}		
		
	} // initPopulationFixed
	
	/**
	 * Initialize the neighborhood structure
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
			for (int k = 0; k < T_; k++) {
				neighborhood_[i][k] = idx[k];
			}
		} // for
	} // initNeighborhood

	/**
	 * Initialize the population
	 * 
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	public void initPopulation() throws JMException, ClassNotFoundException {
		for (int i = 0; i < populationSize_; i++) {
			Solution newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			
			newSolution.setOverallConstraintViolation(-newSolution.getOverallConstraintViolation());
			evaluations_++;
			population_.add(newSolution);
			subregionIdx[i][i] = 1;
		}
	} // initPopulation

	/**
	 * Initialize the ideal objective vector
	 * 
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	void initIdealPoint() throws JMException, ClassNotFoundException {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			zp_[i] = 1.0e+30;

		for (int i = 0; i < populationSize_; i++)
			updateReference(population_.get(i), zp_);
	} // initIdealPoint

	/**
	 * Initialize the nadir point
	 * 
	 * @throws JMException
	 * @throws ClassNotFoundException
	 */
	void initNadirPoint() throws JMException, ClassNotFoundException {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			nzp_[i] = -1.0e+30;

		for (int i = 0; i < populationSize_; i++)
			updateNadirPoint(population_.get(i), nzp_);
	} // initNadirPoint

	/**
	 * Update the ideal objective vector
	 * 
	 * @param indiv
	 */
	void updateReference(Solution indiv, double[] z_) {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			if (indiv.getObjective(i) < z_[i]) {
				z_[i] = indiv.getObjective(i);
			}
		}
	} // updateReference

	/**
	 * Update the nadir point
	 * 
	 * @param indiv
	 */
	void updateNadirPoint(Solution indiv, double[] nz_) {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			if (indiv.getObjective(i) > nz_[i])
				nz_[i] = indiv.getObjective(i);
		}
	} // updateNadirPoint
	
	void RefreshNadirPoint() throws JMException, ClassNotFoundException {
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			nzp_[i] = -1.0e+30;
			
		for (int i = 0; i < populationSize_; i++)			
			updateNadirPoint(population_.get(i), nzp_);
	} // initNadirPoint

	/**
	 * Select two parents for reproduction
	 * 
	 * @param cid
	 * @param type
	 * @return
	 */
	public Solution[] matingSelection(int cid, int type) {

		int rnd1, rnd2;

		Solution[] parents = new Solution[2];

		int nLength = neighborhood_[cid].length;
		
		Vector<Integer> activeList = new Vector<Integer>();
		if (type == 1) {
			for (int i = 0; i < nLength; i++) {
				int idx = neighborhood_[cid][i];
				for (int j = 0; j < populationSize_; j++) {
					if (subregionIdx[idx][j] == 1) {
						activeList.addElement(idx);
						break;
					}
				}
			}
			if (activeList.size() < 2) {
				activeList.clear();
				for (int i = 0; i < populationSize_; i++) {
					for (int j = 0; j < populationSize_; j++) {
						if (subregionIdx[i][j] == 1) {
							activeList.addElement(i);
							break;
						}
					}
				}
			}
			int activeSize = activeList.size();
			rnd1 = PseudoRandom.randInt(0, activeSize - 1);
			do {
				rnd2 = PseudoRandom.randInt(0, activeSize - 1);
			} while (rnd1 == rnd2);	// in a very extreme case, this will be a dead loop
			Vector<Integer> list1 = new Vector<Integer>();
			Vector<Integer> list2 = new Vector<Integer>();
			int id1 = activeList.get(rnd1);
			int id2 = activeList.get(rnd2);
			for (int i = 0; i < populationSize_; i++) {
				if (subregionIdx[id1][i] == 1)
					list1.addElement(i);
				if (subregionIdx[id2][i] == 1)
					list2.addElement(i);
			}
			int p1 = PseudoRandom.randInt(0, list1.size() - 1);
			int p2 = PseudoRandom.randInt(0, list2.size() - 1);
			parents[0] = population_.get(list1.get(p1));
			parents[1] = population_.get(list2.get(p2));
		} else {
			for (int i = 0; i < populationSize_; i++) {
				for (int j = 0; j < populationSize_; j++) {
					if (subregionIdx[i][j] == 1) {
						activeList.addElement(i);
						break;
					}
				}
			}
			int activeSize = activeList.size();
			rnd1 = PseudoRandom.randInt(0, activeSize - 1);
			do {
				rnd2 = PseudoRandom.randInt(0, activeSize - 1);
			} while (rnd1 == rnd2);	// in a very extreme case, this will be a dead loop
			Vector<Integer> list1 = new Vector<Integer>();
			Vector<Integer> list2 = new Vector<Integer>();
			int id1 = activeList.get(rnd1);
			int id2 = activeList.get(rnd2);
			for (int i = 0; i < populationSize_; i++) {
				if (subregionIdx[id1][i] == 1)
					list1.addElement(i);
				if (subregionIdx[id2][i] == 1)
					list2.addElement(i);
			}
			int p1 = PseudoRandom.randInt(0, list1.size() - 1);
			int p2 = PseudoRandom.randInt(0, list2.size() - 1);
			parents[0] = population_.get(list1.get(p1));
			parents[1] = population_.get(list2.get(p2));
		}

		return parents;
	} // matingSelection
	
	/**
	 * ����Լ���������mating selection
	 * 
	 * @param cid
	 * @param type
	 * @return
	 */
	public Solution[] matingSelection_constraint(int cid, int type) {

		int rnd1, rnd2, rnd3, rnd4;

		Solution[] parents 	  = new Solution[2];
		Solution[] candidates = new Solution[2];
		
		rnd1 = PseudoRandom.randInt(0, populationSize_ - 1);
		do {
			rnd2 = PseudoRandom.randInt(0, populationSize_ - 1);
			rnd3 = PseudoRandom.randInt(0, populationSize_ - 1);
			rnd4 = PseudoRandom.randInt(0, populationSize_ - 1);
		} while (rnd1 == rnd2 && rnd1 == rnd3 && rnd1 == rnd4 && rnd2 == rnd3 && rnd2 == rnd4 && rnd3 == rnd4);
		
		candidates[0] = population_.get(rnd1);
		candidates[1] = population_.get(rnd2);
		parents[0]    = binarySelection(candidates);
		
		candidates[0] = population_.get(rnd3);
		candidates[1] = population_.get(rnd4);
		parents[1]    = binarySelection(candidates);

		return parents;
	} // matingSelection
	
	/**
	 * ���Լ��Υ�����ѡ��һ��candidate��Ϊparent
	 * 
	 * @param candidates
	 * @return
	 */
	public Solution binarySelection(Solution[] candidates) {
		
		Solution parent = new Solution();
		
		if (candidates[0].getOverallConstraintViolation() == 0 && candidates[1].getOverallConstraintViolation() == 0) {
			switch (checkDominance(candidates[0], candidates[1])) {
				case 1:
					parent = candidates[0];
				case -1:
					parent = candidates[1];
				case 0: {
					double rnd = PseudoRandom.randDouble();
					if (rnd < 0.5)
						parent = candidates[0];
					else
						parent = candidates[1];
				}
			}
		} else if (candidates[0].getOverallConstraintViolation() == 0 && candidates[1].getOverallConstraintViolation() > 0) 
			parent = candidates[0];
		else if (candidates[0].getOverallConstraintViolation() > 0 && candidates[1].getOverallConstraintViolation() == 0)
			parent = candidates[1];
		else {
			if (candidates[0].getOverallConstraintViolation() < candidates[1].getOverallConstraintViolation())
				parent = candidates[0];
			else if (candidates[0].getOverallConstraintViolation() > candidates[1].getOverallConstraintViolation())
				parent = candidates[1];
			else {
				double rnd = PseudoRandom.randDouble();
				if (rnd < 0.5)
					parent = candidates[0];
				else
					parent = candidates[1];
			}
		}
		
		return parent;
	}
	
	/**
	 * ������Ⱥ,�������ر���feasible�⣬�������CV���Ľ���isolated subregion����ȥɾ��CV�δ�ģ��Դ�����
	 * 
	 * @param indiv
	 */
	public void updateArchive(Solution indiv) {

		// �ҵ�indiv��λ��
		setLocation(indiv, zp_, nzp_);
		int location = indiv.readRegion();
		
		// �ҳ���Ⱥ�е�infeasible��
		int num_infeasible = 0;
		Vector<Integer> infeasibleList = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			if (population_.get(i).getOverallConstraintViolation() > 0.0) {
				infeasibleList.addElement(i);
				num_infeasible++;
			}
		}
		
		if (indiv.getOverallConstraintViolation() == 0.0) {	// indiv��feasible��
			if (num_infeasible == 0) { // ���н��Ϊfeasible
				originalUpdate(indiv, location);
			} else {
				// �õ�indiv�ķ�֧����
				nondominated_sorting_add(indiv);
				
				int singleTargetIdx   = infeasibleList.get(0);
				int multipleTargetIdx = singleTargetIdx;
				int targetRegion 	  = findRegion(singleTargetIdx);

				int flag = 0;
				if (countOnes(targetRegion) > 1)
					flag = 1;	
				
				double multipleMax = population_.get(multipleTargetIdx).getOverallConstraintViolation();
				double singleMax   = multipleMax;
				for (int i = 1; i < num_infeasible; i++) {
					int curIdx    = infeasibleList.get(i);
					int curRegion = findRegion(curIdx);
					double curCV;
					if (countOnes(curRegion) > 1) {
						flag = 1;
						curCV = population_.get(curIdx).getOverallConstraintViolation();
						if (curCV > multipleMax) {
							multipleMax       = curCV;
							multipleTargetIdx = curIdx;
						}
					} else {
						curCV = population_.get(curIdx).getOverallConstraintViolation();
						if (curCV > singleMax) {
							singleMax       = curCV;
							singleTargetIdx = curIdx;
						}
					}
				}
				if (flag == 1) {
					targetRegion = findRegion(multipleTargetIdx);
					population_.replace(multipleTargetIdx, indiv);
					subregionIdx[targetRegion][multipleTargetIdx] = 0;
					subregionIdx[location][multipleTargetIdx]  	  = 1;
				} else {
					targetRegion = findRegion(singleTargetIdx);
					population_.replace(singleTargetIdx, indiv);
					subregionIdx[targetRegion][singleTargetIdx] = 0;
					subregionIdx[location][singleTargetIdx]  	= 1;
				}
			}
		} else {	// indiv��infeasible
			if (num_infeasible == 0)
				return;
			else {
				double singleMax, multipleMax;
				int singleTargetIdx   = infeasibleList.get(0);
				int multipleTargetIdx = singleTargetIdx;
				int targetRegion 	  = findRegion(singleTargetIdx);

				int curNC = countOnes(targetRegion);
				if (targetRegion == location)
					curNC++;
				
				int flag = 0;
				if (curNC > 1)
					flag = 1;	
				
				multipleMax = population_.get(multipleTargetIdx).getOverallConstraintViolation();
				singleMax   = multipleMax;
				for (int i = 1; i < num_infeasible; i++) {
					int curIdx    = infeasibleList.get(i);
					int curRegion = findRegion(curIdx);
					curNC 	 	  = countOnes(curRegion);
					if (curRegion == location)
						curNC++;
					double curCV;
					if (curNC > 1) {
						flag = 1;
						curCV = population_.get(curIdx).getOverallConstraintViolation();
						if (curCV > multipleMax) {
							multipleMax       = curCV;
							multipleTargetIdx = curIdx;
						}
					} else {
						curCV = population_.get(curIdx).getOverallConstraintViolation();
						if (curCV > singleMax) {
							singleMax       = curCV;
							singleTargetIdx = curIdx;
						}
					}
				}
				if (flag == 1) {
					if (indiv.getOverallConstraintViolation() < multipleMax) {
						targetRegion = findRegion(multipleTargetIdx);
						population_.replace(multipleTargetIdx, indiv);
						subregionIdx[targetRegion][multipleTargetIdx] = 0;
						subregionIdx[location][multipleTargetIdx]  	  = 1;
					}
					
				} else {
					if (indiv.getOverallConstraintViolation() < singleMax) {
						targetRegion = findRegion(singleTargetIdx);
						population_.replace(singleTargetIdx, indiv);
						subregionIdx[targetRegion][singleTargetIdx] = 0;
						subregionIdx[location][singleTargetIdx]  	= 1;
					}
				}
			}
		}
		
		return;
	}
	
	/**
	 * ������Ⱥ���������ر���feasible�⣬ÿ��ȥ��һ��CV���Ľ�
	 * 
	 * @param indiv
	 */
	public void updateArchive2(Solution indiv) {

		// �ҵ�indiv��λ��
		setLocation(indiv, zp_, nzp_);
		int location = indiv.readRegion();
		
		// �ҳ���Ⱥ�е�infeasible��
		int num_infeasible = 0;
		Vector<Integer> infeasibleList = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			if (population_.get(i).getOverallConstraintViolation() > 0.0) {
				infeasibleList.addElement(i);
				num_infeasible++;
			}
		}
		
		if (indiv.getOverallConstraintViolation() == 0.0) {	// indiv��feasible��
			if (num_infeasible == 0) { // ���н��Ϊfeasible
				originalUpdate(indiv, location);
			} else {
				// �õ�indiv�ķ�֧����
				nondominated_sorting_add(indiv);
				
				int targetIdx = infeasibleList.get(0);
				double maxCV  = population_.get(targetIdx).getOverallConstraintViolation();
				for (int i = 1; i < num_infeasible; i++) {
					int curIdx   = infeasibleList.get(i);
					double curCV = population_.get(curIdx).getOverallConstraintViolation();
					if (curCV > maxCV) {
						maxCV     = curCV;
						targetIdx = curIdx;
					}
				}
				int targetRegion = findRegion(targetIdx);

				population_.replace(targetIdx, indiv);
				subregionIdx[targetRegion][targetIdx] = 0;
				subregionIdx[location][targetIdx]  	  = 1;
			}
		} else {	// indiv��infeasible
			if (num_infeasible == 0)
				return;
			else {
				int targetIdx = infeasibleList.get(0);
				double maxCV  = population_.get(targetIdx).getOverallConstraintViolation();
				for (int i = 1; i < num_infeasible; i++) {
					int curIdx   = infeasibleList.get(i);
					double curCV = population_.get(curIdx).getOverallConstraintViolation();
					if (curCV > maxCV) {
						maxCV     = curCV;
						targetIdx = curIdx;
					}
				}
				if (indiv.getOverallConstraintViolation() > maxCV)
					return;
				else {
					int targetRegion = findRegion(targetIdx);
					
					population_.replace(targetIdx, indiv);
					subregionIdx[targetRegion][targetIdx] = 0;
					subregionIdx[location][targetIdx]  	  = 1;
				}
			}
		}
		
		return;
	}
		
	/**
	 * ���нⶼ��feasible solutionʱ��ʹ��ԭʼ��update����
	 * 
	 * @param indiv
	 * @param location
	 */
	public void originalUpdate(Solution indiv, int location) {

		numRanks = nondominated_sorting_add(indiv);
		
		if (numRanks == 1) {
			deleteRankOne(indiv, location);
//					num_case1++;
		} else {
			SolutionSet lastFront = new SolutionSet(populationSize_);
			int frontSize = countRankOnes(numRanks - 1);
			if (frontSize == 0)	{	// ���һ�����ֻ��indiv
				frontSize++;
				lastFront.add(indiv);
			} else {
				for (int i = 0; i < populationSize_; i++) {
					if (rankIdx[numRanks - 1][i] == 1)
						lastFront.add(population_.get(i));
				}
				if (indiv.getRank() == (numRanks - 1)) {
					frontSize++;
					lastFront.add(indiv);
				}
			}
			
			if (frontSize == 1 && lastFront.get(0).equals(indiv)) {	// ���һ��ֻ��indivһ������
				int curNC = countOnes(location);
				if (curNC > 0) {	// ��subregion�и�����ڣ���ôindivû�д��ڵı�Ҫ
					nondominated_sorting_delete(indiv);
//							num_case2++;
					return;
				} else {	// ��subregion��û�и�����ڣ���ôindiv����Ҫ����
					deleteCrowdRegion1(indiv, location);
//							num_case3++;
				}
			} else if (frontSize == 1 && !lastFront.get(0).equals(indiv)) { // ���һ��ֻ��һ�����壬�ҷ�indiv
				int targetIdx 	   = findPosition(lastFront.get(0));
				int parentLocation = findRegion(targetIdx);
				int curNC		   = countOnes(parentLocation);
				if (parentLocation == location)
					curNC++;
				
				if (curNC == 1) {	// ��subregion����targetIdx��һ�����壬targetIdx�������Ҫ����
					deleteCrowdRegion2(indiv, location);
//							num_case4++;
				} else {	// ��subregion�л������������ڣ���ôtargetIdx����û�д��ڵı�Ҫ
					int indivRank  = indiv.getRank();
					int targetRank = population_.get(targetIdx).getRank();
					rankIdx[targetRank][targetIdx] = 0;
					rankIdx[indivRank][targetIdx]  = 1;
					
					Solution targetSol = new Solution(population_.get(targetIdx));
					
					population_.replace(targetIdx, indiv);
					subregionIdx[parentLocation][targetIdx] = 0;
					subregionIdx[location][targetIdx]  	    = 1;
					
					// ������Ⱥ�ķ�֧������
					nondominated_sorting_delete(targetSol);
//							num_case5++;
				}
			} else {
				double indivFitness = fitnessFunction(indiv, lambda_[location]);
				
				// �ҵ����һ������index������subregion
				int[] idxArray    = new int[frontSize];
				int[] regionArray = new int[frontSize];
				
				for (int i = 0; i < frontSize; i++) {
					idxArray[i] = findPosition(lastFront.get(i));
					if (idxArray[i] == -1)
						regionArray[i] = location;
					else
						regionArray[i] = findRegion(idxArray[i]);
				}
				
				// �ҵ���ӵ����subregion������ж����һ�����浽crowdList��
				Vector<Integer> crowdList = new Vector<Integer>();
				int crowdIdx;
				int nicheCount = countOnes(regionArray[0]);
				if (regionArray[0] == location)
					nicheCount++;
				crowdList.addElement(regionArray[0]);
				for (int i = 1; i < frontSize; i++) {
					int curSize = countOnes(regionArray[i]);
					if (regionArray[i] == location)
						curSize++;
					if (curSize > nicheCount) {
						crowdList.clear();
						nicheCount = curSize;
						crowdList.addElement(regionArray[i]);
					} else if (curSize == nicheCount) {
						crowdList.addElement(regionArray[i]);
					} else {
						continue;
					}
				}
				// ȷ��ӵ��subregion��index
				if (crowdList.size() == 1) {
					crowdIdx = crowdList.get(0);
				} else {
					int listLength = crowdList.size();
					crowdIdx = crowdList.get(0);
					double sumFitness = sumFitness(crowdIdx);
					if (crowdIdx == location)
						sumFitness = sumFitness + indivFitness;
					for (int i = 1; i < listLength; i++) {
						int curIdx = crowdList.get(i);
						double curFitness = sumFitness(curIdx);
						if (curIdx == location)
							curFitness = curFitness + indivFitness;
						if (curFitness > sumFitness) {
							crowdIdx   = curIdx;
							sumFitness = curFitness;
						}
					}
				}
				
				if (nicheCount == 0)
					System.out.println("Impossible empty subregion!!!");
				else if (nicheCount == 1) { // ���һ����ÿ���������ڵ�����ֻ��һ�����壬��ô�����˱�ȥ��
					deleteCrowdRegion2(indiv, location);
//							num_case6++;
				} else { // �����һ��������ڵ���ӵ����subregion��ɾȥһ�����Ľ�
//							num_case7++;
					Vector<Integer> list = new Vector<Integer>();
					for (int i = 0; i < frontSize; i++) {
						if (regionArray[i] == crowdIdx)
							list.addElement(i);
					}
					if (list.size() == 0) {
						System.out.println("Cannot happen!!!");
					} else {
						double maxFitness, curFitness;
						int targetIdx = list.get(0);
						if (idxArray[targetIdx] == -1)
							maxFitness = indivFitness;
						else
							maxFitness = fitnessFunction(population_.get(idxArray[targetIdx]), lambda_[crowdIdx]);
						for (int i = 1; i < list.size(); i++) {
							int curIdx = list.get(i);
							if (idxArray[curIdx] == -1)
								curFitness = indivFitness;
							else
								curFitness = fitnessFunction(population_.get(idxArray[curIdx]), lambda_[crowdIdx]);
							if (curFitness > maxFitness) {
								targetIdx  = curIdx;
								maxFitness = curFitness;
							}
						}
						if (idxArray[targetIdx] == -1) {
							nondominated_sorting_delete(indiv);
							return;
						} else {
							int indivRank  = indiv.getRank();
							int targetRank = population_.get(idxArray[targetIdx]).getRank();							
							rankIdx[targetRank][idxArray[targetIdx]] = 0;
							rankIdx[indivRank][idxArray[targetIdx]]  = 1;
							
							Solution targetSol = new Solution(population_.get(idxArray[targetIdx]));
							
							population_.replace(idxArray[targetIdx], indiv);
							subregionIdx[crowdIdx][idxArray[targetIdx]] = 0;
							subregionIdx[location][idxArray[targetIdx]] = 1;
							
							// ������Ⱥ�ķ�֧������
							nondominated_sorting_delete(targetSol);
						}
					}
				}
			}
		}
		
		return;
		
	}
	
	/**
	 * ��֧���θ��£�����Ӹ���ʱ
	 * 
	 * @param indiv
	 * @return
	 */
	public int nondominated_sorting_add(Solution indiv) {
		
		int flag = 0;
		int flag1, flag2, flag3;
		
		// �ҵ���ǰ�Ĳ���
		int num_ranks = 0;
		Vector<Integer> frontSize = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			int rankCount = countRankOnes(i);
			if (rankCount != 0) {
				frontSize.addElement(rankCount);
				num_ranks++;
			} else {
				break;
			}
		}
		
		Vector<Integer> dominateList = new Vector<Integer>();	// ���ڱ��汻indiv֧��ĸ���
		int level = 0;
		for (int i = 0; i < num_ranks; i++) {
			level = i;
			if (flag == 1) {	// indiv���i������֧�䣬��indiv����ò�
				indiv.setRank(i - 1);
				return num_ranks;
			} else if (flag == 2) {	// indiv֧���i��ĳЩ���壬����ò�ĳЩ���廥��֧�䣬��indiv����ò㣬���ҽ�indiv֧��ĸ����ҳ�����֮���ĸ���ϲ���������
				indiv.setRank(i - 1);
				
				int prevRank = i - 1;
				
				// ����prevRank�б�indiv֧��ģ��������Ƶ�prevRank+1�㣬���ҳ������ڸò�֧��ĸ���
				int curIdx;
				int newRank = prevRank + 1;
				int curListSize = dominateList.size();
				for (int j = 0; j < curListSize; j++) {
					curIdx = dominateList.get(j);
					rankIdx[prevRank][curIdx] = 0;
					rankIdx[newRank][curIdx]  = 1;
					population_.get(curIdx).setRank(newRank);
					
					for (int k = 0; k < populationSize_; k++) {
						if (rankIdx[newRank][k] == 1) {
							if (checkDominance(population_.get(curIdx), population_.get(k)) == 1) {
								dominateList.addElement(k);
							}
						}
					}
				}
				for (int j = 0; j < curListSize; j++)
					dominateList.remove(0);
				
				// ��������ϲ��������и��屻�ƶ�����һ�㣬���������ԭ�?�����Щ�������µĲ�ε�֧�����
				prevRank 	= newRank;
				newRank  	= newRank + 1;
				curListSize = dominateList.size();
				if (curListSize == 0)
					return num_ranks;
				else {
					do {
						for (int j = 0; j < curListSize; j++) {
							curIdx = dominateList.get(j);
							rankIdx[prevRank][curIdx] = 0;
							rankIdx[newRank][curIdx]  = 1;
							population_.get(curIdx).setRank(newRank);
							
							for (int k = 0; k < populationSize_; k++) {
								if (rankIdx[newRank][k] == 1) {
									if (checkDominance(population_.get(curIdx), population_.get(k)) == 1) {
										dominateList.addElement(k);
									}
								}
							}
						}
						for (int j = 0; j < curListSize; j++)
							dominateList.remove(0);
						
						curListSize = dominateList.size();
						if (curListSize != 0) {
							prevRank = newRank;
							newRank  = newRank + 1;
						}
					} while (curListSize != 0);
					
					if (newRank == num_ranks)
						num_ranks++;
					
					return num_ranks;
				}
			} else if (flag == 3 || flag == 0) {	// indiv����i��ĳЩ����֧�䣬���ü��ò㣬��������һ�㿪ʼ���
				flag1 = flag2 = flag3 = 0;
				for (int j = 0; j < populationSize_; j++) {
					if (rankIdx[i][j] == 1) {
						switch (checkDominance(indiv, population_.get(j))) {
							case 1: {
								flag1 = 1;
								dominateList.addElement(j);
								break;
							}
							case 0: {
								flag2 = 1;
								break;
							}
							case -1: {
								flag3 = 1;
								break;
							}
						}
						
						if (flag3 == 1) {
							flag = 3;
							break;
						} else if (flag1 == 0 && flag2 == 1)
							flag = 1;
						else if (flag1 == 1 && flag2 == 1)
							flag = 2;
						else if (flag1 == 1 && flag2 == 0)
							flag = 4;
						else
							continue;
					}
				}
					
			} else {	// (flag == 4) indiv֧���i�����и��壬��ò�����Ժ�����и����rank+1
				indiv.setRank(i - 1);
				i = i - 1;
				int remainSize = num_ranks - i;
				int[][] tempRecord = new int[remainSize][populationSize_];
				
				int k = 0;
				while (i < num_ranks) {
					int tempIdx = 0;
					for (int j = 0; j < populationSize_; j++) {
						if (rankIdx[i][j] == 1) {
							tempRecord[k][tempIdx] = j;
							tempIdx++;
						}
					}
					i++;
					k++;
				}
				
				k = 0;
				i = indiv.getRank();
				while (i < num_ranks) {
					int level_size = frontSize.get(i);
					
					int curIdx;
					int curRank, newRank;
					for (int j = 0; j < level_size; j++) {
						curIdx  = tempRecord[k][j];
						curRank = population_.get(curIdx).getRank();
						newRank = curRank + 1;
						population_.get(curIdx).setRank(newRank);
						
						rankIdx[curRank][curIdx] = 0;
						rankIdx[newRank][curIdx] = 1;
					}
					i++;
					k++;
				}
				num_ranks++;

				return num_ranks;
			}
		}
		// ���indiv�������forѭ�������flag��ȻΪ3�����ʾindivһ�����������һ��(�µ�һ��)
		if (flag == 1) {
			indiv.setRank(level);
		} else if (flag == 2) {
			indiv.setRank(level);

			int curIdx;
			int tempSize = dominateList.size();
			for (int i = 0; i < tempSize; i++) {
				curIdx = dominateList.get(i);
				population_.get(curIdx).setRank(level + 1);
				
				rankIdx[level][curIdx] 	   = 0;
				rankIdx[level + 1][curIdx] = 1;
			}
			num_ranks++;
		} else if(flag == 3) {
			indiv.setRank(level + 1);
			num_ranks++;
		} else {
			indiv.setRank(level);
			for (int i = 0; i < populationSize_; i++) {
				if (rankIdx[level][i] == 1) {
					population_.get(i).setRank(level + 1);
					
					rankIdx[level][i] 	  = 0;
					rankIdx[level + 1][i] = 1;
				}
			}
			num_ranks++;
		}
		
		return num_ranks;
	}
	
	/**
	 * ��֧���θ��£���ɾ�����ʱ
	 * 
	 * @param indiv
	 * @return
	 */
	public void nondominated_sorting_delete(Solution indiv) {
		
		// ���indiv���ڵķ�֧���
		int indivRank = indiv.getRank();
		
		Vector<Integer> curLevel     = new Vector<Integer>();	// ���ڱ��浱ǰ��֧���ĸ���
		Vector<Integer> dominateList = new Vector<Integer>();	// ���ڱ��汻��Ҫ���ƶ��ĸ���
		
		for (int i = 0; i < populationSize_; i++) {
			if (rankIdx[indivRank][i] == 1)
				curLevel.addElement(i);
		}
		
		int flag;
		// �ҵ�indiv֧��Ĵ���indivRank+1��ĸ���
		int investigateRank = indivRank + 1;
		if (investigateRank < numRanks) {
			for (int i = 0; i < populationSize_; i++) {
				if (rankIdx[investigateRank][i] == 1) {
					flag = 0;
					if (checkDominance(indiv, population_.get(i)) == 1) {
						for (int j = 0; j < curLevel.size(); j++) {
							if (checkDominance(population_.get(i), population_.get(curLevel.get(j))) == -1) {
								flag = 1;
								break;
							}
						}
						if (flag == 0) {	// ����i���Խ���ǰһ��
							dominateList.addElement(i);
							rankIdx[investigateRank][i] 	= 0;
							rankIdx[investigateRank - 1][i] = 1;
							population_.get(i).setRank(investigateRank - 1);
						}
					}
				}
			}
		}
		
		int curIdx;
		int curListSize = dominateList.size();
		while (curListSize != 0) {
			curLevel.clear();
			for (int i = 0; i < populationSize_; i++) {
				if (rankIdx[investigateRank][i] == 1)
					curLevel.addElement(i);
			}
			investigateRank = investigateRank + 1;
			
			if (investigateRank < numRanks) {
				for (int i = 0; i < curListSize; i++) {
					curIdx = dominateList.get(i);
					for (int j = 0; j < populationSize_; j++) {
						if (j == populationSize_)
							System.out.println("Fuck me!!!");
						if (rankIdx[investigateRank][j] == 1) {
							flag = 0;
							if (checkDominance(population_.get(curIdx), population_.get(j)) == 1) {
								for (int k = 0; k < curLevel.size(); k++) {
									if (checkDominance(population_.get(j), population_.get(curLevel.get(k))) == -1) {
										flag = 1;
										break;
									}
								}
								if (flag == 0) {
									dominateList.addElement(j);
									rankIdx[investigateRank][j] 	= 0;
									rankIdx[investigateRank - 1][j] = 1;
									population_.get(j).setRank(investigateRank - 1);
								}
							}
						}
					}
				}
			}
			for (int i = 0; i < curListSize; i++)
				dominateList.remove(0);
			
			curListSize = dominateList.size();
		}
		
	}
	
	
	/**
	 * ��rank matrix��ĳһ�е�1����Ŀ
	 * @param location
	 * @return
	 */
	public int countRankOnes(int location) {
		
		int count = 0;
		for (int i = 0; i < populationSize_; i++) {
			if (rankIdx[location][i] == 1)
				count++;
		}
		
		return count;
	}
	
	/**
	 * ������Ⱥ
	 * 
	 * @param indiv
	 */
	public void updateArchive1(Solution indiv) {

		// �ҵ�indiv��λ��
		setLocation(indiv, zp_, nzp_);
		int location = indiv.readRegion();
		
		SolutionSet indPop = new SolutionSet(1);
		indPop.add(indiv);
		SolutionSet union = ((SolutionSet) population_).union(indPop);
		
		Ranking ranking = new Ranking(union);
		
		int numRanks = ranking.getNumberOfSubfronts();
		
		if (numRanks == 1) {	// ���и��嶼�Ƿ�֧�����
			deleteRankOne(indiv, location);
		} else {
			SolutionSet lastFront = ranking.getSubfront(numRanks - 1);
			
			int frontSize = lastFront.size();
			
			double indivFitness = fitnessFunction(indiv, lambda_[location]);
			
			// �ҵ����һ������index������subregion
			int[] idxArray    = new int[frontSize];
			int[] regionArray = new int[frontSize];
			
			for (int i = 0; i < frontSize; i++) {
				idxArray[i] = findPosition(lastFront.get(i));
				if (idxArray[i] == -1)
					regionArray[i] = location;
				else
					regionArray[i] = findRegion(idxArray[i]);
			}
			
			// �ҵ���ӵ����subregion������ж����һ�����浽crowdList��
			Vector<Integer> crowdList = new Vector<Integer>();
			int crowdIdx;
			int nicheCount = countOnes(regionArray[0]);
			if (regionArray[0] == location)
				nicheCount++;
			crowdList.addElement(regionArray[0]);
			for (int i = 1; i < frontSize; i++) {
				int curSize = countOnes(regionArray[i]);
				if (regionArray[i] == location)
					curSize++;
				if (curSize > nicheCount) {
					crowdList.clear();
					nicheCount = curSize;
					crowdList.addElement(regionArray[i]);
				} else if (curSize == nicheCount) {
					crowdList.addElement(regionArray[i]);
				} else {
					continue;
				}
			}
			// ȷ��ӵ��subregion��index
			if (crowdList.size() == 1) {
				crowdIdx = crowdList.get(0);
			} else {
				int listLength = crowdList.size();
				crowdIdx = crowdList.get(0);
				double sumFitness = sumFitness(crowdIdx);
				if (crowdIdx == location)
					sumFitness = sumFitness + indivFitness;
				for (int i = 1; i < listLength; i++) {
					int curIdx = crowdList.get(i);
					double curFitness = sumFitness(curIdx);
					if (curIdx == location)
						curFitness = curFitness + indivFitness;
					if (curFitness > sumFitness) {
						crowdIdx   = curIdx;
						sumFitness = curFitness;
					}
				}
			}
			
			if (nicheCount == 0)
				System.out.println("Impossible empty subregion!!!");
			else if (nicheCount == 1) { // ���һ����ÿ���������ڵ�����ֻ��һ�����壬��ô�����˱�ȥ��
				deleteCrowdRegion2(indiv, location);
			} else { // �����һ��������ڵ���ӵ����subregion��ɾȥһ�����Ľ�
				Vector<Integer> list = new Vector<Integer>();
				for (int i = 0; i < frontSize; i++) {
					if (regionArray[i] == crowdIdx)
						list.addElement(i);
				}
				if (list.size() == 0) {
					System.out.println("Cannot happen!!!");
				} else {
					double maxFitness, curFitness;
					int targetIdx = list.get(0);
					if (idxArray[targetIdx] == -1)
						maxFitness = indivFitness;
					else
						maxFitness = fitnessFunction(population_.get(idxArray[targetIdx]), lambda_[crowdIdx]);
					for (int i = 1; i < list.size(); i++) {
						int curIdx = list.get(i);
						if (idxArray[curIdx] == -1)
							curFitness = indivFitness;
						else
							curFitness = fitnessFunction(population_.get(idxArray[curIdx]), lambda_[crowdIdx]);
						if (curFitness > maxFitness) {
							targetIdx  = curIdx;
							maxFitness = curFitness;
						}
					}
					if (idxArray[targetIdx] == -1) {
						return;
					} else {
						population_.replace(idxArray[targetIdx], indiv);
						subregionIdx[crowdIdx][idxArray[targetIdx]] = 0;
						subregionIdx[location][idxArray[targetIdx]] = 1;
					}
				}
			}
		}
		
		return;
	}
	
	/**
	 * ����ӵ����subregion��ȥ��һ����(�������������ڣ���Ӧ��ɾ��indiv������indiv����
	 * ��subregionֻ����һ���⣬��indiv��Ҫ����)
	 * 
	 * @param indiv
	 * @param location
	 */
	public void deleteCrowdRegion1(Solution indiv, int location) {
		
		// �ҵ���ӵ����subregion������ж����һ�����浽crowdList��
		Vector<Integer> crowdList = new Vector<Integer>();
		int crowdIdx;
		int nicheCount = countOnes(0);
		crowdList.addElement(0);
		for (int i = 1; i < populationSize_; i++) {
			int curSize = countOnes(i);
			if (curSize > nicheCount) {
				crowdList.clear();
				nicheCount = curSize;
				crowdList.addElement(i);
			} else if (curSize == nicheCount) {
				crowdList.addElement(i);
			} else {
				continue;
			}
		}
		// ȷ��ӵ��subregion��index
		if (crowdList.size() == 1) {
			crowdIdx = crowdList.get(0);
		} else {
			int listLength = crowdList.size();
			crowdIdx = crowdList.get(0);
			double sumFitness = sumFitness(crowdIdx);
			for (int i = 1; i < listLength; i++) {
				int curIdx = crowdList.get(i);
				double curFitness = sumFitness(curIdx);
				if (curFitness > sumFitness) {
					crowdIdx   = curIdx;
					sumFitness = curFitness;
				}
			}
		}
		
		// �ҵ�������crowdIdx����Ľ�����index
		Vector<Integer> indList = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			if (subregionIdx[crowdIdx][i] == 1)
				indList.addElement(i);
		}
		
		// �ҵ���Щ�������rank������Щ��
		Vector<Integer> maxRankList = new Vector<Integer>();
		int maxRank = population_.get(indList.get(0)).getRank();
		maxRankList.addElement(indList.get(0));
		for (int i = 1; i < indList.size(); i++) {
			int curRank = population_.get(indList.get(i)).getRank();
			if (curRank > maxRank) {
				maxRankList.clear();
				maxRank = curRank;
				maxRankList.addElement(indList.get(i));
			} else if (curRank == maxRank) {
				maxRankList.addElement(indList.get(i));
			} else {
				continue;
			}
		}
		
		// �ҵ�rank������Щ����fitness�����Ǹ���
		int rankSize  = maxRankList.size();
		int targetIdx = maxRankList.get(0);
		double maxFitness = fitnessFunction(population_.get(targetIdx), lambda_[crowdIdx]);
		for (int i = 1; i < rankSize; i++) {
			int curIdx = maxRankList.get(i);
			double curFitness = fitnessFunction(population_.get(curIdx), lambda_[crowdIdx]);
			if (curFitness > maxFitness) {
				targetIdx  = curIdx;
				maxFitness = curFitness;
			}
		}
		
		population_.replace(targetIdx, indiv);
		subregionIdx[crowdIdx][targetIdx] = 0;
		subregionIdx[location][targetIdx] = 1;
		
	}
	
	/**
	 * ����ӵ����subregion��ȥ��һ����(�������������ڣ���Ӧ��ɾ��parentLocation�еĸ�
	 * �壬���Ǹ�subregion�н���һ����)
	 * 
	 * @param indiv
	 * @param location
	 */
	public void deleteCrowdRegion2(Solution indiv, int location) {
		
		double indivFitness = fitnessFunction(indiv, lambda_[location]);
		
		// �ҵ���ӵ����subregion������ж����һ�����浽crowdList��
		Vector<Integer> crowdList = new Vector<Integer>();
		int crowdIdx;
		int nicheCount = countOnes(0);
		if (location == 0)
			nicheCount++;
		crowdList.addElement(0);
		for (int i = 1; i < populationSize_; i++) {
			int curSize = countOnes(i);
			if (location == i)
				curSize++;
			if (curSize > nicheCount) {
				crowdList.clear();
				nicheCount = curSize;
				crowdList.addElement(i);
			} else if (curSize == nicheCount) {
				crowdList.addElement(i);
			} else {
				continue;
			}
		}
		// ȷ��ӵ��subregion��index
		if (crowdList.size() == 1) {
			crowdIdx = crowdList.get(0);
		} else {
			int listLength    = crowdList.size();
			crowdIdx          = crowdList.get(0);
			double sumFitness = sumFitness(crowdIdx);
			if (crowdIdx == location)
				sumFitness = sumFitness + indivFitness;
			for (int i = 1; i < listLength; i++) {
				int curIdx        = crowdList.get(i);
				double curFitness = sumFitness(curIdx);
				if (curIdx == location)
					curFitness = curFitness + indivFitness;
				if (curFitness > sumFitness) {
					crowdIdx   = curIdx;
					sumFitness = curFitness;
				}
			}
		}
		
		// �ҵ�������crowdIdx����Ľ�����index
		Vector<Integer> indList = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			if (subregionIdx[crowdIdx][i] == 1)
				indList.addElement(i);
		}
		if (crowdIdx == location) {
			int temp = -1;
			indList.addElement(temp);
		}
		
		// �ҵ���Щ�������rank������Щ
		Vector<Integer> maxRankList = new Vector<Integer>();
		int maxRank = population_.get(indList.get(0)).getRank();
		maxRankList.addElement(indList.get(0));
		for (int i = 1; i < indList.size(); i++) {
			int curRank;
			if (indList.get(i) == -1)
				curRank = indiv.getRank();
			else
				curRank = population_.get(indList.get(i)).getRank();
			
			if (curRank > maxRank) {
				maxRankList.clear();
				maxRank = curRank;
				maxRankList.addElement(indList.get(i));
			} else if (curRank == maxRank) {
				maxRankList.addElement(indList.get(i));
			} else {
				continue;
			}
		}
		
		double maxFitness;
		int rankSize  = maxRankList.size();
		int targetIdx = maxRankList.get(0);
		if (targetIdx == -1)
			maxFitness = indivFitness;
		else
			maxFitness = fitnessFunction(population_.get(targetIdx), lambda_[crowdIdx]);
		for (int i = 1; i < rankSize; i++) {
			double curFitness;
			int curIdx = maxRankList.get(i);
			if (curIdx == -1)
				curFitness = indivFitness;
			else
				curFitness = fitnessFunction(population_.get(curIdx), lambda_[crowdIdx]);
			
			if (curFitness > maxFitness) {
				targetIdx  = curIdx;
				maxFitness = curFitness;
			}
		}
		
		if (targetIdx == -1) {
			return;
		} else {
			population_.replace(targetIdx, indiv);
			subregionIdx[crowdIdx][targetIdx] = 0;
			subregionIdx[location][targetIdx] = 1;
		}
		
	}
	
	/**
	 * ���ֻ��һ����֧���(���еĸ��嶼�Ƿ�֧�����)����Ӧ������ӵ����subregion��ȥ������
	 * 
	 * @param indiv
	 * @param location
	 */
	public void deleteRankOne(Solution indiv, int location) {
		
		double indivFitness = fitnessFunction(indiv, lambda_[location]);
		
		// �ҵ���ӵ����subregion������ж����һ�����浽crowdList��
		Vector<Integer> crowdList = new Vector<Integer>();
		int crowdIdx;
		int nicheCount = countOnes(0);
		if (location == 0)
			nicheCount++;
		crowdList.addElement(0);
		for (int i = 1; i < populationSize_; i++) {
			int curSize = countOnes(i);
			if (location == i)
				curSize++;
			if (curSize > nicheCount) {
				crowdList.clear();
				nicheCount = curSize;
				crowdList.addElement(i);
			} else if (curSize == nicheCount) {
				crowdList.addElement(i);
			} else {
				continue;
			}
		}
		// ȷ��ӵ��subregion��index
		if (crowdList.size() == 1) {
			crowdIdx = crowdList.get(0);
		} else {
			int listLength    = crowdList.size();
			crowdIdx          = crowdList.get(0);
			double sumFitness = sumFitness(crowdIdx);
			if (crowdIdx == location)
				sumFitness = sumFitness + indivFitness;
			for (int i = 1; i < listLength; i++) {
				int curIdx        = crowdList.get(i);
				double curFitness = sumFitness(curIdx);
				if (curIdx == location)
					curFitness = curFitness + indivFitness;
				if (curFitness > sumFitness) {
					crowdIdx   = curIdx;
					sumFitness = curFitness;
				}
			}
		}

		if (nicheCount == 0) {
			System.out.println("Empty subregion!!!");
		} else if (nicheCount == 1) { // ������е�subregion�ж�ֻ��һ�����壬��ô��indiv��subregion��ȥ��һ�����
			int targetIdx;
			for (targetIdx = 0; targetIdx < populationSize_; targetIdx++) {
				if (subregionIdx[location][targetIdx] == 1)
					break;
			}

			double prev_func = fitnessFunction(population_.get(targetIdx), lambda_[location]);
			if (indivFitness < prev_func)
				population_.replace(targetIdx, indiv);
		} else {
			if (location == crowdIdx) {	// ���indiv���ڵ�subregion��Ϊ��ӵ����subregion
				deleteCrowdIndiv_same(location, nicheCount, indivFitness, indiv);
			} else {
				int curNC   = countOnes(location);
				int crowdNC = countOnes(crowdIdx);

				if (crowdNC > (curNC + 1)) {	// crowdIdx��ӵ������Ӧ�ڸ�subregion�н���ɾ��
					deleteCrowdIndiv_diff(crowdIdx, location, crowdNC, indiv);
				} else if (crowdNC < (curNC + 1)) { // crowdNC == curNC����Ӧ��indiv����subregion����ɾ��
					deleteCrowdIndiv_same(location, curNC, indivFitness, indiv);
				} else { // crowdNC == (curNC + 1)
					if (curNC == 0)
						deleteCrowdIndiv_diff(crowdIdx, location, crowdNC, indiv);
					else {
						double rnd = PseudoRandom.randDouble();
						if (rnd < 0.5)
							deleteCrowdIndiv_diff(crowdIdx, location, crowdNC, indiv);
						else
							deleteCrowdIndiv_same(location, curNC, indivFitness, indiv);
					}
				}
			}
		}
		
	}
	
	/**
	 * ���location���subregion�����и����fitness֮��
	 * 
	 * @param location
	 * @return
	 */
	public double sumFitness(int location) {
		
		double sum = 0;
		for (int i = 0; i < populationSize_; i++) {
			if (subregionIdx[location][i] == 1)
				sum = sum + fitnessFunction(population_.get(i), lambda_[location]);
		}
		
		return sum;
			
	}
	
	/**
	 * ����ӵ����subregionɾ��һ���⣬����ӵ����subregion��Ϊindiv��subregion����Ҫ��
	 * ��indiv���subregion���������fitnessֵ��
	 * 
	 * @param crowdIdx
	 * @param nicheCount
	 * @param indiv
	 */
	public void deleteCrowdIndiv_same(int crowdIdx, int nicheCount, double indivFitness, Solution indiv) {

		// �ҵ�������crowdIdx����Ľ�����index
		Vector<Integer> indList = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			if (subregionIdx[crowdIdx][i] == 1)
				indList.addElement(i);
		}

		// �ҵ�fitness�����Ǹ���
		int listSize      = indList.size();
		int worstIdx      = indList.get(0);
		double maxFitness = fitnessFunction(population_.get(worstIdx), lambda_[crowdIdx]);
		for (int i = 1; i < listSize; i++) {
			int curIdx        = indList.get(i);
			double curFitness = fitnessFunction(population_.get(curIdx), lambda_[crowdIdx]);
			if (curFitness > maxFitness) {
				worstIdx   = curIdx;
				maxFitness = curFitness;
			}
		}
		
		// ���indiv��fitness��ã�����indiv�滻fitness�����Ǹ���
		if (indivFitness < maxFitness)
			population_.replace(worstIdx, indiv);
		
	}
	
	/**
	 * ����ӵ����subregionɾ��һ���⣬����ӵ����subregion��indiv��subregion��ͬ�����
	 * ֻ����indiv����ӵ��subregion�е������弴��
	 * 
	 * @param crowdIdx
	 * @param nicheCount
	 * @param indiv
	 */
	public void deleteCrowdIndiv_diff(int crowdIdx, int curLocation, int nicheCount, Solution indiv) {

		// �ҵ�������crowdIdx����Ľ�����index
		Vector<Integer> indList = new Vector<Integer>();
		for (int i = 0; i < populationSize_; i++) {
			if (subregionIdx[crowdIdx][i] == 1)
				indList.addElement(i);
		}
		
		// �ҵ�fitness�����Ǹ���
		int worstIdx      = indList.get(0);
		double maxFitness = fitnessFunction(population_.get(worstIdx), lambda_[crowdIdx]);
		for (int i = 1; i < nicheCount; i++) {
			int curIdx        = indList.get(i);
			double curFitness = fitnessFunction(population_.get(curIdx), lambda_[crowdIdx]);
			if (curFitness > maxFitness) {
				worstIdx   = curIdx;
				maxFitness = curFitness;
			}
		}
		
		// ��indiv�滻���ҵ������Ľ�
		population_.replace(worstIdx, indiv);
		subregionIdx[crowdIdx][worstIdx]    = 0;
		subregionIdx[curLocation][worstIdx] = 1;
		
	}
	
	
	public void deleteDominateOne(Vector<Integer> list, Solution indiv) {
		int listSize = list.size();
		
		int[] idxArray    = new int[listSize];
		int[] regionArray = new int[listSize]; 
		
		SolutionSet dominationPop = new SolutionSet(listSize);
		for (int i = 0; i < listSize; i++) {
			idxArray[i]    = list.get(i);
			regionArray[i] = findRegion(idxArray[i]);
			dominationPop.add(population_.get(idxArray[i]));
		}
		
		Ranking ranking = new Ranking(dominationPop);
		int numRanks = ranking.getNumberOfSubfronts();
		if (numRanks == 1) {
			int crowdIdx = regionArray[0];
			int nicheCount = countOnes(crowdIdx);
			for (int i = 1; i < listSize; i++) {
				int curSize = countOnes(regionArray[i]);
				if (curSize > nicheCount) {
					crowdIdx   = regionArray[i];
					nicheCount = curSize;
				}
			}
			
			Vector<Integer> candidateList = new Vector<Integer>();
			for (int i = 0; i < listSize; i++) {
				if (regionArray[i] == crowdIdx)
					candidateList.addElement(i);
			}
			int targetIdx = candidateList.get(0);
			double maxDist = subregionDist[crowdIdx][idxArray[targetIdx]];
			for (int i = 1; i < candidateList.size(); i++) {
				int idx = candidateList.get(i);
				if (subregionDist[crowdIdx][idxArray[idx]] < maxDist) {
					targetIdx = idx;
					maxDist   = subregionDist[crowdIdx][idxArray[idx]];
				}
			}
			population_.replace(idxArray[targetIdx], indiv);
			subregionIdx[crowdIdx][idxArray[targetIdx]]  = 0;
			subregionDist[crowdIdx][idxArray[targetIdx]] = 0;
			subregionIdx[indiv.readRegion()][idxArray[targetIdx]]  = 0;
			subregionDist[indiv.readRegion()][idxArray[targetIdx]] = 0;
		}
	}
	
	/**
	 * Count the number of 1s in the 'location'th subregion
	 * @param location
	 * @return
	 */
	public int countOnes(int location) {
		
		int count = 0;
		for (int i = 0; i < populationSize_; i++) {
			if (subregionIdx[location][i] == 1)
				count++;
		}
		
		return count;
	}
	
	/**
	 * find the index of the solution 'indiv' in the population_
	 * @param indiv
	 * @return
	 */
	public int findPosition(Solution indiv) {
		
		for (int i = 0; i < populationSize_; i++) {
			if (indiv.equals(population_.get(i)))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * find the subregion of the 'idx'th solution in the population_
	 * @param idx
	 * @return
	 */
	public int findRegion(int idx) {
		
		for (int i = 0; i < populationSize_; i++) {
			if (subregionIdx[i][idx] == 1)
				return i;
		}
	
		return -1;
	}
	

	/**
	 * Set the location of a solution based on the orthogonal distance
	 * 
	 * @param indiv
	 */
	public void setLocation(Solution indiv, double[] z_, double[] nz_) {

		int minIdx;
		double distance, minDist;

		minIdx   = 0;
		distance = calculateDistance2(indiv, lambda_[0], z_, nz_);
		minDist  = distance;
		for (int i = 1; i < populationSize_; i++) {
			distance = calculateDistance2(indiv, lambda_[i], z_, nz_);
			if (distance < minDist) {
				minIdx  = i;
				minDist = distance;
			}
		}

		indiv.setRegion(minIdx);
		indiv.Set_associateDist(minDist);
		
	}
	
	/**
	 * �жϸ���a��b��֧���ϵ��1��ʾa֧��b��0��ʾ���߻���֧�䣬-1��ʾa��b֧��
	 * @param a
	 * @param b
	 * @return
	 */
	public int checkDominance(Solution a, Solution b) {

		int flag1 = 0;
		int flag2 = 0;

		for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
			if (a.getObjective(i) < b.getObjective(i))
				flag1 = 1;
			else {
				if (a.getObjective(i) > b.getObjective(i))
					flag2 = 1;
			}
		}
		if (flag1 == 1 && flag2 == 0)
			return 1;
		else {
			if (flag1 == 0 && flag2 == 1)
				return -1;
			else
				return 0;
		}
	}

	/**
	 * Calculate the perpendicular distance between the solution and reference
	 * line
	 * 
	 * @param individual
	 * @param lambda
	 * @return
	 */
	public double calculateDistance(Solution individual, double[] lambda,
			double[] z_, double[] nz_) {

		double scale;
		double distance;

		double[] vecInd  = new double[problem_.getNumberOfObjectives()];
		double[] vecProj = new double[problem_.getNumberOfObjectives()];
		
		// normalize the weight vector (line segment)
		double nd = norm_vector(lambda);
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			lambda[i] = lambda[i] / nd;
		
		// vecInd has been normalized to the range [0,1]
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			vecInd[i] = (individual.getObjective(i) - z_[i]) / (nz_[i] - z_[i]);

		scale = innerproduct(vecInd, lambda);
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			vecProj[i] = vecInd[i] - scale * lambda[i];

		distance = norm_vector(vecProj);

		return distance;
	}

	public double calculateDistance2(Solution indiv, double[] lambda,
			double[] z_, double[] nz_) {

		// normalize the weight vector (line segment)
		double nd = norm_vector(lambda);
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			lambda[i] = lambda[i] / nd;

		double[] realA = new double[problem_.getNumberOfObjectives()];
		double[] realB = new double[problem_.getNumberOfObjectives()];

		// difference between current point and reference point
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			realA[i] = (indiv.getObjective(i) - z_[i]);

		// distance along the line segment
		double d1 = Math.abs(innerproduct(realA, lambda));

		// distance to the line segment
		for (int i = 0; i < problem_.getNumberOfObjectives(); i++)
			realB[i] = (indiv.getObjective(i) - (z_[i] + d1 * lambda[i]));

		double distance = norm_vector(realB);

		return distance;
	}
	
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

	public int countTest() {
		
		int sum = 0;
		for (int i = 0; i < populationSize_; i++) {
			for (int j = 0; j < populationSize_; j++) {
				if (subregionIdx[i][j] == 1)
					sum++;
			}
		}

		return sum;
	}
	
	public SolutionSet returnPopulation (SolutionSet population) {
		 // Return only feasible solutions
       int feasibleNo = 0;
       
       for (int i=0;i<population.size();i++) {
       	if (population.get(i).getOverallConstraintViolation() == 0.0) {
       		feasibleNo ++;
       	}
       }
      
       SolutionSet finalPop = new SolutionSet(feasibleNo);
       
       for (int i=0;i < population.size();i++) {
       	
       	if (population.get(i).getOverallConstraintViolation() == 0.0) {        	
       		finalPop.add(population.get(i));
       	}
       }       

		if (finalPop.size() != 0) {
			// Return feasible non-dominated solutions 
	        NondominatedRanking ranking = new NondominatedRanking(finalPop);
			return ranking.getSubfront(0);
		} else {
			ConstraintNondominatedRanking ranking = new ConstraintNondominatedRanking(population); // Return non-dominated solutions
			return ranking.getSubfront(0);
		}   
	}
	
	double fitnessFunction(Solution indiv, double[] lambda) {
		double fitness;
		fitness = 0.0;

		if (functionType_.equals("_TCHE1")) {
			double maxFun = -1.0e+30;

			for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
				double diff = Math.abs(indiv.getObjective(n) - zp_[n]);

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
				double diff = Math.abs(indiv.getObjective(i) - zp_[i]);

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
				realA[n] = (indiv.getObjective(n) - zp_[n]);

			// distance along the line segment
			double d1 = Math.abs(innerproduct(realA, lambda));

			// distance to the line segment
			for (int n = 0; n < problem_.getNumberOfObjectives(); n++)
				realB[n] = (indiv.getObjective(n) - (zp_[n] + d1 * lambda[n]));
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

} // MOEAD