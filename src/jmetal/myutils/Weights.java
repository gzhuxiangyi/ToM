package jmetal.myutils;

import jmetal.core.Solution;

public class Weights {
	private double[] weight_; 		// weight vectors
	private Solution solution_;     // the solution corresponding to the weight
	private String weightType_;     // "ideal", "nadir" and "random"
	
	public Weights(int objective) {
		weight_ = new double [objective];
	}
	
	public String getWeightType() {
		return weightType_;
	}

	public void setWeightType(String weightType) {
		this.weightType_ = weightType;
	}

	public double[] getWeight() {
		return weight_;
	}
	public void setWeight(double[] weight) {
		this.weight_ = weight;
	}
	public Solution getSolution() {
		return solution_;
	}
	public void setSolution(Solution solution) {
		this.solution_ = solution;
	}
	public String toString() {
		String str ="";
		for (int i = 0 ; i < weight_.length;i++) {
			str = str +  weight_[i] + "," ;
		}
		return str;
	}
}
