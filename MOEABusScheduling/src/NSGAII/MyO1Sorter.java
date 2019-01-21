package NSGAII;

import java.util.Comparator;

import algorithm.Individual;

public class MyO1Sorter implements Comparator<Individual> {
	public int compare(Individual x, Individual y) {
		return (int) (x.geto1()-y.geto1());
	}

}

