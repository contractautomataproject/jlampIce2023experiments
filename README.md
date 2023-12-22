This repository contains the source code for the experiments of the paper 

"Advancing Orchestration Synthesis for Contract Automata" by Davide Basile and Maurice ter Beek.

The automata used in the experiments are located in `src/main/resources`.

This repository uses a branch of `CATLib` currently under development, imported as a jar under the folder `lib`.


### Composition Experiments

The class `src/main/java/io/github/contractautomata/experimentsJLAMPICE2023/compositions/CardGame.java` is used for producing the results in Table 1 (Comparison of different state-space reduction techniques for the card game).

### Orchestration Experiments

The class `src/main/java/io/github/contractautomata/experimentsJLAMPICE2023/orchestrations/CardGame.java` is used for producing the results in Table 2 (Comparison of different orchestrations for the card game).

The class `src/main/java/io/github/contractautomata/experimentsJLAMPICE2023/orchestrations/Railway.java` is used for producing the results in Table 3 (Comparison of different orchestrations for the railway example).
