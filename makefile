all: TournamentBP 

TournamentBP: TournamentBP.java Predictor.java
	javac *.java

clean:
	rm -rf *.class

