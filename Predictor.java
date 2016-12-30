
public class Predictor {
	private int index;
	private int old_counter;
	private int new_counter;
	private String prediction;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getOld_counter() {
		return old_counter;
	}

	public void setOld_counter(int old_counter) {
		this.old_counter = old_counter;
	}

	public int getNew_counter() {
		return new_counter;
	}

	public void setNew_counter(int new_counter) {
		this.new_counter = new_counter;
	}

	public String getPrediction() {
		return prediction;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

	public Predictor(int index,int old_counter,int new_counter, String prediction) {
		this.index=index;
		this.old_counter=old_counter;
		this.new_counter=new_counter;
		this.prediction=prediction;
	}
	
}
