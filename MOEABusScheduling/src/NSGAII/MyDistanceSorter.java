package NSGAII;

import java.util.Comparator;

import algorithm.Individual;

public class MyDistanceSorter implements Comparator<Individual> {
	public int compare(Individual x, Individual y) {
		return (int) (x.distance-y.distance);
	}

}

