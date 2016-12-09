package view.graphs;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class SpeedPerInclinationView {
	
	
	
	private CategoryDataset createDataset(double [][] data) {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 16; j++){
                dataset.addValue(data[i][j], Integer.valueOf(j), Integer.valueOf(i));
            }	
        }

        return dataset;
    }
    
}
