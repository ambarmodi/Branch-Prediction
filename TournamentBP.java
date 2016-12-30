import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.LinkedHashMap;

public class TournamentBP {

	//Predictors holding the branch prediction data.
	public static LinkedHashMap<Integer, Predictor> globalPredictor = new LinkedHashMap<Integer, Predictor>();
	public static LinkedHashMap<Integer, Predictor> localPredictor = new LinkedHashMap<Integer, Predictor>();
	public static LinkedHashMap<Integer, Predictor> selector = new LinkedHashMap<Integer, Predictor>();

	//File to write the statistics for the predicted algorithm.
	public static final String FILE_STATISTICS = "statistics.txt";
	
	//Index to maintain the global predictor index
	public static int global_index = 0;
	public static int next_global_index=0;
	
	//Counter maintaining the individual prediction result for statistics
	public static int tournament_correctly_predicted=0;
	public static int local_correctly_predicted=0;
	public static int global_correctly_predicted=0;
	
	public static void main(String[] args) {
		String inputFile = args[0];
		String outputFile = args[1];
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			FileReader fileReader = new FileReader(inputFile);
			bufferedReader = new BufferedReader(fileReader);

			File file = new File(outputFile);
			if(file.exists()) {
				file.delete();
			}
			FileWriter fileWriter = new FileWriter(outputFile, true);
			bufferedWriter = new BufferedWriter(fileWriter);

			//For each of the instruction sequence, predict, read and write the output
			String instr_seq, output_seq;
			while ((instr_seq = bufferedReader.readLine()) != null) {
				output_seq = predict(instr_seq);
				bufferedWriter.write(output_seq);
				bufferedWriter.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Printing the statistics
		generateStatistics();
	}

	private static void generateStatistics() {
		String stats = "Local Correctly Predicted: " + local_correctly_predicted + "\nGlobal Correctly Predicted: " + global_correctly_predicted + "\nTournament Correctly Predicted: " + tournament_correctly_predicted;
		
		File file = new File(FILE_STATISTICS);
		BufferedWriter bufferedWriter = null;
		try {
		if(file.exists()) {
			file.delete();
		}
		FileWriter fileWriter = new FileWriter(FILE_STATISTICS, true);
		bufferedWriter = new BufferedWriter(fileWriter);
		
		bufferedWriter.write(stats);
		bufferedWriter.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	private static String predict(String instr_seq) {
		int pc_index = Integer.parseInt(Character.toString(instr_seq.charAt(0)));
		String act_res = Character.toString(instr_seq.charAt(1));
		int nxt_index = Integer.parseInt(Character.toString(instr_seq.charAt(2)));

		String local_pred = localPrediction(pc_index, act_res, nxt_index);
		String global_pred = globalPrediction(pc_index, act_res, nxt_index);
		String selector_pred = selector(pc_index, act_res, nxt_index);
		
		String final_pred;
		if(selector_pred.equals("l")){ 
			final_pred=local_pred;
		}
		else {
			final_pred=global_pred;
		}
		
		//Generating data for statistics
		String output_seq = pc_index + local_pred + global_pred + selector_pred + final_pred  + act_res;
		if(local_pred.equals(act_res)) {
			local_correctly_predicted ++;
		}
		if(global_pred.equals(act_res)) {
			global_correctly_predicted ++;
		}
		if(final_pred.equals(act_res)) {
			tournament_correctly_predicted ++;
		}
		return output_seq;
	}

	private static String localPrediction(int pc_index, String act_res, int nxt_index) {
		String local_pred = "n";
		if (localPredictor.containsKey(pc_index)) {
			Predictor lp = localPredictor.get(pc_index);

			lp.setOld_counter(lp.getNew_counter());
			if (lp.getOld_counter() > 1) {
				local_pred="t";
				lp.setPrediction(local_pred);
			} else {
				local_pred = "n";
				lp.setPrediction(local_pred);
			}
			if (act_res.equals("t")) {
				if (lp.getNew_counter() < 3)
					lp.setNew_counter(lp.getNew_counter() + 1);
			} else {
				if (lp.getNew_counter() > 0)
					lp.setNew_counter(lp.getNew_counter() - 1);
			}
		} else {
			//New branch encountered for the predictor. Add in the map.
			int new_cntr = 0;
			if (act_res.equals("t")) {
				new_cntr += 1;
			}
			localPredictor.put(pc_index, new Predictor(pc_index, 0, new_cntr, local_pred));
		}
		
		return local_pred;
	}
	
	
	private static String globalPrediction(int pc_index, String act_res, int nxt_index) {
		global_index=next_global_index;
		String global_pred = "n";
		
		if (globalPredictor.containsKey(global_index)) {
			Predictor lp = globalPredictor.get(global_index);
			
			lp.setOld_counter(lp.getNew_counter());
			if (lp.getOld_counter() > 1) {
				global_pred="t";
				lp.setPrediction(global_pred);
			} else {
				global_pred = "n";
				lp.setPrediction(global_pred);
			}
			if (act_res.equals("t")) {
				if (lp.getNew_counter() < 3)
					lp.setNew_counter(lp.getNew_counter() + 1);
			} else {
				if (lp.getNew_counter() > 0)
					lp.setNew_counter(lp.getNew_counter() - 1);
			}
			
		} else {
			//New branch encountered for the predictor. Add in the map.
			int new_cntr = 0;
			if (act_res.equals("t")) {
				new_cntr += 1;
			}
			globalPredictor.put(global_index, new Predictor(global_index, 0, new_cntr, global_pred));	
		}
		
		//Generate next global index. 
		if (act_res.equals("t")) {
			next_global_index = global_index << 1;
			next_global_index += 1;
		} else if (act_res.equals("n")) {
			next_global_index = global_index << 1;
		}
		//As we are maintaining the last six instruction. Need to handle the 6 bit value only.
		if(next_global_index > 63) {
			next_global_index = (next_global_index & 63);
		}

		return global_pred;
	}

	private static String selector(int pc_index, String act_res, int nxt_index) {
		String selector_pred = "l";
		
		if (selector.containsKey(pc_index)) {
			Predictor lp = selector.get(pc_index);
			lp.setOld_counter(lp.getNew_counter());

			if (!localPredictor.get(pc_index).getPrediction()
					.equals(globalPredictor.get(global_index).getPrediction())) {
				if (globalPredictor.get(global_index).getPrediction()
						.equals(act_res)) {
					if (lp.getNew_counter() < 3)
						lp.setNew_counter(lp.getNew_counter() + 1);
				} else {
					if (lp.getNew_counter() > 0)
						lp.setNew_counter(lp.getNew_counter() - 1);
				}
			}
			if (lp.getOld_counter() > 1) {
				selector_pred="g";
				lp.setPrediction(selector_pred);
			} else {
				selector_pred="l";
				lp.setPrediction(selector_pred);
			}
		} else {
			//Selector unaware of the instruction. Add to the map.
			selector.put(pc_index, new Predictor(pc_index, 0, 0, selector_pred));
		}
		
		return selector_pred;
	}
}
