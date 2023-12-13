package io.github.contractautomata.experimentsJLAMPICE2023;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.NewOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.StrongAgreement;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static final String dir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator;

    public static void main(String[] args) throws IOException {
        AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> dealer = bdc.importMSCA(dir + "Dealer.data");
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player = bdc.importMSCA(dir + "Player.data");
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new NewOrchestrationSynthesisOperator(new StrongAgreement()).apply(List.of(dealer,player,player));

        System.out.println(orc);
    }
}