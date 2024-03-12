This repository contains the source code for the experiments of the paper 

"Advancing Orchestration Synthesis for Contract Automata" by Davide Basile and Maurice ter Beek.

The automata used in the experiments are located in `src/main/resources`.

This repository uses a branch of `CATLib` currently under development, imported as a jar under the folder `lib`.


### Composition Experiments

The class `src/main/java/io/github/contractautomata/experimentsJLAMPICE2023/compositions/CardGame.java` is used for producing the results in Table 1 (Comparison of different state-space reduction techniques for the card game).

Logs:
CardGame Composition Example
Composition with addressess and committed states: states=36, transitions=37 computed in 126 milliseconds
Orchestration with addressess and committed states: states=24, transitions=23 computed in 84 milliseconds

Composition with committed states:   states=73, transitions=82 computed in 82 milliseconds
Composition no pruning  with  committed states:   states=177, transitions=224 computed in 137 milliseconds
Orchestration with committed states: states=49, transitions=54 computed in 47 milliseconds

Composition: states=85, transitions=94 computed in 52 milliseconds
Composition no pruning:  states=560, transitions=935 computed in 337 milliseconds
Orchestration with property: states=49, transitions=54 computed in 127 milliseconds

### Orchestration Experiments

The class `src/main/java/io/github/contractautomata/experimentsJLAMPICE2023/orchestrations/CardGame.java` is used for producing the results in Table 2 (Comparison of different orchestrations for the card game).

Logs:
CardGame Orchestration Example
Splitting orchestration synthesis: 241 milliseconds the orchestration is non-empty
Splitting orchestration : states=41, transitions=54

Conditional orchestration synthesis: 52 milliseconds the orchestration is non-empty
Conditional orchestration : states=49, transitions=54

Refined conditional orchestration synthesis:  42 milliseconds the orchestration is empty


The class `src/main/java/io/github/contractautomata/experimentsJLAMPICE2023/orchestrations/Railway.java` is used for producing the results in Table 3 (Comparison of different orchestrations for the railway example).

Logs:
Railway Orchestration Example
Splitting orchestration synthesis: 207 milliseconds the orchestration is non-empty
Splitting orchestration : states=90, transitions=159

Conditional orchestration synthesis: 40 milliseconds the orchestration is non-empty
Conditional orchestration : states=32, transitions=44

Refined conditional orchestration synthesis: 31 milliseconds the orchestration is non-empty
Refined orchestration : states=32, transitions=44