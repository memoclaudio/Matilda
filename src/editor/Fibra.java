package editor;

public class Fibra {
	private double [][] vertices;
	
	public Fibra(int dim){
		vertices=new double[dim][3];
		
	}
	
	public Fibra(double [][] v){
		vertices=v;
	}
	
	public double[][] getVertex(){
		return vertices;
	}
	

}
