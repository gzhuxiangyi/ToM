package jmetal.CHTs.ToM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.Niching;
import jmetal.util.ranking.NondominatedRanking;
import jmetal.util.ranking.Ranking;
import jmetal.util.vector.TwoLevelWeightVectorGenerator;
import jmetal.util.vector.VectorGenerator;

public class CNSGAIII extends Algorithm {

	private int populationSize_;
	private int div1_;
	private int div2_;

	private SolutionSet population_;
	SolutionSet offspringPopulation_;
	SolutionSet union_;

	int generations_;

	Operator crossover_;
	Operator mutation_;
	Operator selection_;

	double[][] lambda_; // reference points

	boolean normalize_; // do normalization or not


	String dataDirectory_;


	public CNSGAIII(Problem problem) {
		super(problem);
	} // NSGAII

	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int maxGenerations_;

		generations_ = 0;
		normalize_ = ((Boolean) this.getInputParameter("normalize")).booleanValue();

//		String methodName_      = this.getInputParameter("AlgorithmName").toString();

		// for many-objective optimization problems
		div1_ = ((Integer) this.getInputParameter("div1")).intValue();
		div2_ = ((Integer) this.getInputParameter("div2")).intValue();
		VectorGenerator vg = new TwoLevelWeightVectorGenerator(div1_, div2_, problem_.getNumberOfObjectives());
		lambda_ = vg.getVectors();
//
//		populationSize_ = vg.getVectors().length;
//		if (populationSize_ % 2 != 0)
//			populationSize_ += 1;

		dataDirectory_  = this.getInputParameter("dataDirectory").toString();
		populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();

//		lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];
//		initUniformWeight();

		maxGenerations_ = (Integer)getInputParameter("maxEvaluations") / populationSize_;
		
		System.out.println("CNSGA-III maxEvaluations = " +  (Integer)getInputParameter("maxEvaluations") );		
		System.out.println("CNSGA-III popsize = " +  populationSize_ );		
		
		mutation_ = operators_.get("mutation");
		crossover_ = operators_.get("crossover");
		selection_ = operators_.get("selection");
//		int  dif_index      = ((Integer)this.getInputParameter("difcultyIndex")).intValue() + 1; // start from 1
//		int  runningTime      = (Integer)getInputParameter("runningTime") + 1; // start from 1

		//creat database
//		String problemName = problem_.getName() +"_" + Integer.toString(dif_index) + "_" + Integer.toString(runningTime);
//		SqlUtils.CreateTable(problemName,methodName_);

		initPopulation();

		while (generations_ < maxGenerations_) {
			offspringPopulation_ = new SolutionSet(populationSize_);
			//Solution[] parents = new Solution[2];
			for (int i = 0; i < (populationSize_ / 2); i++) {
				if (generations_ < maxGenerations_) {
					// obtain parents

                    Solution[] offSpring = new Solution[2];
					if(crossover_.getClass().getSimpleName().equalsIgnoreCase("SBXCrossover")){
						Solution[] parents = new Solution[2];
						parents[0] = (Solution) selection_.execute(population_);
						parents[1] = (Solution) selection_.execute(population_);
						offSpring = ((Solution[])crossover_.execute(parents));
					}
					// Apply DE crossover
					else if(crossover_.getClass().getSimpleName().equalsIgnoreCase("DifferentialEvolutionCrossover")){
						Solution[] parents = new Solution[3];
						parents[0] = (Solution) selection_.execute(population_);
						parents[1] = (Solution) selection_.execute(population_);
						parents[2] = parents[0];
						offSpring[0] = (Solution) crossover_.execute(new Object[]{parents[0], parents});
						offSpring[1] = (Solution) crossover_.execute(new Object[]{parents[1], parents});
					}
					else {
						System.out.println("unknown crossover" );

					}

					mutation_.execute(offSpring[0]);
					mutation_.execute(offSpring[1]);

					problem_.evaluate(offSpring[0]);
					problem_.evaluateConstraints(offSpring[0]);
					problem_.evaluate(offSpring[1]);
					problem_.evaluateConstraints(offSpring[1]);

					offspringPopulation_.add(offSpring[0]);
					offspringPopulation_.add(offSpring[1]);

				} // if
			} // for
			

			union_ = ((SolutionSet) population_).union(offspringPopulation_);

			// Ranking the union
			Ranking ranking = new NondominatedRanking(union_);

			int remain = populationSize_;
			int index = 0;
			SolutionSet front = null;
			population_.clear();

			// Obtain the next front
			front = ranking.getSubfront(index);


            // if all of solutions are infeasible or the number of feasible solutions equals to N.
			while ((remain > 0) && (remain >= front.size())) {

				for (int k = 0; k < front.size(); k++) {
					population_.add(front.get(k));
				} // for

				// Decrement remain
				remain = remain - front.size();

				// Obtain the next front
				index++;
				if (remain > 0) {
					front = ranking.getSubfront(index);
				} // if
			}

			// if the number of feasible solutions is great than N
			if (remain > 0) { // front contains individuals to insert

				new Niching(population_, front, lambda_, remain, normalize_).execute();
				remain = 0;
			}

			generations_++;


		}

//		SqlUtils.InsertSolutionSet(problemName,population_);
		SolutionSet finalSet = removeSame(population_);    
		Ranking ranking = new NondominatedRanking(finalSet);
		return ranking.getSubfront(0);

	}

	public void initPopulation() throws JMException, ClassNotFoundException {

		population_ = new SolutionSet(populationSize_);

		for (int i = 0; i < populationSize_; i++) {
			Solution newSolution = new Solution(problem_);

			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);

			population_.add(newSolution);
		} // for
	} // initPopulation

	public void initUniformWeight() {
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
	} // initUniformWeight

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

} // NSGA-III

