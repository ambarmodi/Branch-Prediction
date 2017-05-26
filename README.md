## Branch Predictor

# Description:
The project is the implementation of Tournament Branch Prediction, also known as "Hybrid Predictor". 
Please read the Wikipedia article https://en.wikipedia.org/wiki/Branch_prediction for more details. 
This predictor parses the input file, perform processing and produce the output. 
For simplicity all the non-Branch instructions have been removed and the branch addresses have been renumbered from 0 to 9.

# Detailed Working:
Step 1: Parse the input file. The input file is in the following format:
0n1
1t7
7n8
8t3
3n4
On each line:
1. The first character is the address of the branch being executed.
2. The second character is ‘n’ if the branch was not resolved to be taken or ‘t’ if the branch was resolved to be taken. This field indicates the true (non-speculative) direction of the branch.
3. The third character is the address of the next instruction executed (the target if taken, thefall-through address otherwise).

Step 2: Predict branches. 
This is to be a tournament predictor, which is comprised of four parts.
  Part A is a local predictor: An array of 2-bit saturating counters, indexed by the instruction address. All counters initialize to zero.
  Part B is a global predictor: An array of 2-bit saturating counters, indexed by the sequence of six resolved branch that occurred in time just prior to the branch being predicted. (This predictor considers only global history, not the PC of the branch.) All counters initialize to zero, and the running branch history initializes to six not-taken branches.
  Part C is a selector: An array of 2-bit saturating counters, indexed by the instruction address, used to select between the local and global predictors when making a final prediction. All counters initialize to zero.
  Part D is the tournament selection: Use the selector to choose between the local and global predictors to get the final prediction.
You will compare each of your predictions to the corresponding branch resolution. After making each prediction, counters from each of your three predictors must be updated to learn from the newly resolved branch. Your program should keep track of the number of correctly predicted branches for the local predictor, the global predictor, and the tournament predictor.
Note: Your tournament predictor will not be 100% accurate. The assignment is not to correctly predict every branch but instead to match the behavior of the tournament predictor specified above.

Updating counters:
Based on the true outcome of each branch, updates to counters are as follows:
•For the local and global predictors:
If the branch was taken, increment the saturating counter. If the branch was not taken, decrement the counter.
For the selector:
  If the local and global predictors agree (right or wrong), do not modify the counter.
  If the local predictor is correct and the global predictor is incorrect, decrement the counter.
  If the local predictor is incorrect and the global predictor is correct, increment the counter.

Step 3: Output your results. The output file shall be in the following format. Note that what is shown here is the actual expected output for the first five executed branches.
0nnlnn
1nnlnt
7nnlnn
8nnlnt
3nnlnn
On each line:
•The first character is the program address of the branch being predicted.
•The second character is the local prediction: ‘t’ if taken, ‘n’ if not taken.
•The third character is the global prediction: ‘t’ if taken, ‘n’ if not taken.
•The fourth character is the selector prediction: ‘l’ if local, ‘g’ if global.
•The fifth character is the final prediction: ‘t’ if taken, ‘n’ if not taken.
•The sixth character is the actual direction of the branch: ‘t’ if taken, ‘n’ if not taken.


# Files:
1. makefile
2. TournamentBP.java
3. Predictor.java
4. Sample-prediction.xlx
5. statistics.txt
6. sample_branch_sequence

# Instructions to execute:
1. make 						       (This will compile the program)
2. java TournamentBP <input_file> <output_file>  	(This will generate output_file and statistics.txt)
3. make clean 						(Optional : This will clean compiled .class files)

Output of the program:
1. <output_file>
2. statistics.txt

Note: I am generating the statistics.txt through the code itself. After executing the program statistics.txt will be generated.
