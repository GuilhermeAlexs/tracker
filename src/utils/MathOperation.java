package utils;

public class MathOperation {
	public static double [][] divideMatrix(double [][] a, double [][] b){
		double [][] c = new double[a.length][a[0].length];
		
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				if(b[i][j] != 0)
					c[i][j] = a[i][j] / b[i][j];
			}
		}
		
		return c;
	}

	public static double [][] sumMatrix(double [][] a, double [][] b){
		double [][] c = new double[a.length][a[0].length];
		
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				c[i][j] = a[i][j] + b[i][j];
			}
		}
		
		return c;
	}
	
	public static double [][] subtractMatrix(double [][] a, double [][] b){
		double [][] c = new double[a.length][a[0].length];
		
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				c[i][j] = a[i][j] - b[i][j];
			}
		}
		
		return c;
	}
	
	public static double [][] multMatrix(double [][] a, double [][] b){
		double [][] c = new double[a.length][a[0].length];
		
		for(int i = 0; i < a.length; i++){
			for(int j = 0; j < a[0].length; j++){
				c[i][j] = a[i][j] * b[i][j];
			}
		}
		
		return c;
	}
}
