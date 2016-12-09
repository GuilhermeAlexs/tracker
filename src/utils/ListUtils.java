package utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
	public static void sort(List<Double> independent, List<Double> dependent){
		List<Double> independentSorted = new ArrayList<Double>();
		List<Double> dependentSorted = new ArrayList<Double>();
		Double currIndependent, currDependent;
		int newIndex;
		
		independentSorted.add(0, independent.get(0));
		dependentSorted.add(0, dependent.get(0));
		
		for(int i = 1; i < independent.size(); i++){
			currIndependent = independent.get(i);
			currDependent = dependent.get(i);

			for(int j = independentSorted.size() - 1; j >= 0; j--){
				if(currIndependent >= independentSorted.get(j)){
					newIndex = j + 1;
					independentSorted.add(newIndex, currIndependent);
					dependentSorted.add(newIndex, currDependent);
					break;
				}
				
				if(j == 0){
					independentSorted.add(0, currIndependent);
					dependentSorted.add(0, currDependent);
					break;
				}
			}
		}
		
		independent.clear();
		dependent.clear();
		
		independent.addAll(independentSorted);
		dependent.addAll(dependentSorted);
	}
}
