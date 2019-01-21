package NSGAII;

import java.util.Comparator;

import algorithm.Individual;

public class MyO3Sorter implements Comparator<Individual> {
	public int compare(Individual x, Individual y) {
		return (int) (x.geto3()-y.geto3());
	}

}
