package io.github.contractautomata.experimentsJLAMPICE2023.compositions;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.StrongAgreement;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardGame {
    public static final String dir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator;

    public static void main(String[] args) throws IOException {

        System.out.println("CardGame Composition Example");

        AutDataConverter<CALabel> adc = new AutDataConverter<>(CALabel::new);

        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>, Label<Action>>> prop = getProp();
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> dealerAddrComm =
                adc.importMSCA(dir + "DealerAddress.data");
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player1 =
                adc.importMSCA(dir + "Player1.data");
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player2 =
                adc.importMSCA(dir + "Player2.data");

        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> dealer =
                adc.importMSCA(dir + "Dealer.data");
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player =
                adc.importMSCA(dir + "Player.data");

        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> dealerNoComm =
                adc.importMSCA(dir + "DealerNoComm.data");


        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp =
                new MSCACompositionFunction<>(List.of(dealerAddrComm,player1,player2), t->new StrongAgreement().negate().test(t.getLabel())
                ).apply(Integer.MAX_VALUE);

        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> compNoPrun;

        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc =
                new OrchestrationSynthesisOperator<String>(new StrongAgreement()).apply(comp);

        System.out.println("Composition with addressess and committed states:   states="+comp.getStates().size()+", transitions="+comp.getTransition().size());
             System.out.println("Orchestration with addressess and committed states: states="+orc.getStates().size()+", transitions="+orc.getTransition().size()+System.lineSeparator());
        adc.exportMSCA(dir+"Table1_CompositionWithPruningAndCommittedStatesAndSymmetricReduction",comp);


        comp = new MSCACompositionFunction<>(List.of(dealer,player,player), t->new StrongAgreement().negate().test(t.getLabel())
                ).apply(Integer.MAX_VALUE);
        compNoPrun = new MSCACompositionFunction<>(List.of(dealer,player,player), t->false).apply(Integer.MAX_VALUE);
        orc = new OrchestrationSynthesisOperator<String>(new StrongAgreement()).apply(comp);

        System.out.println("Composition with committed states:   states="+comp.getStates().size()+", transitions="+comp.getTransition().size());
        System.out.println("Composition no pruning  with  committed states:   states="+compNoPrun.getStates().size()+", transitions="+compNoPrun.getTransition().size());
        System.out.println("Orchestration with committed states: states="+orc.getStates().size()+", transitions="+orc.getTransition().size()+System.lineSeparator());

        adc.exportMSCA(dir+"Table1_CompositionWithCommittedStates",compNoPrun);
        adc.exportMSCA(dir+"Table1_CompositionWithPruningAndCommittedStates",comp);


        comp = new MSCACompositionFunction<>(List.of(dealerNoComm,player,player), t->new StrongAgreement().negate().test(t.getLabel())
        ).apply(Integer.MAX_VALUE);
        compNoPrun = new MSCACompositionFunction<>(List.of(dealerNoComm,player,player), t->false).apply(Integer.MAX_VALUE);
        orc = new OrchestrationSynthesisOperator<>(new StrongAgreement(),prop).apply(comp);

        System.out.println("Composition: states="+comp.getStates().size()+", transitions="+comp.getTransition().size());
        System.out.println("Composition no pruning:  states="+compNoPrun.getStates().size()+", transitions="+compNoPrun.getTransition().size());
        System.out.println("Orchestration with property: states="+orc.getStates().size()+", transitions="+orc.getTransition().size()+System.lineSeparator());

        adc.exportMSCA(dir+"Table1_CompositionWithPruning",comp);
        adc.exportMSCA(dir+"Table1_PlainComposition",compNoPrun);
    }

    private static Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>, Label<Action>>>  getProp() {
        BasicState<String> s0 = new BasicState<>("WaitFirstCard", true, false, false);
        BasicState<String> s1 = new BasicState<>("WaitSecondCard", false, false, false);
        BasicState<String> s2 = new BasicState<>("Go", false, true, false);
        State<String> cs0 = new State<>(List.of(s0));
        State<String> cs1 = new State<>(List.of(s1));
        State<String> cs2 = new State<>(List.of(s2));

        Set<ModalTransition<String,Action,State<String>, Label<Action>>> setTr = new HashSet<>();
        setTr.add(new ModalTransition<>(cs0, new Label<>(List.of(new Action("pair1"))), cs1, ModalTransition.Modality.PERMITTED));
        setTr.add(new ModalTransition<>(cs0, new Label<>(List.of(new Action("pair2"))), cs1, ModalTransition.Modality.PERMITTED));
        setTr.add(new ModalTransition<>(cs0, new Label<>(List.of(new Action("pair3"))), cs1, ModalTransition.Modality.PERMITTED));

        setTr.add(new ModalTransition<>(cs1, new Label<>(List.of(new Action("pair1"))), cs2, ModalTransition.Modality.PERMITTED));
        setTr.add(new ModalTransition<>(cs1, new Label<>(List.of(new Action("pair2"))), cs2, ModalTransition.Modality.PERMITTED));
        setTr.add(new ModalTransition<>(cs1, new Label<>(List.of(new Action("pair3"))), cs2, ModalTransition.Modality.PERMITTED));


        setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("1"))), cs2, ModalTransition.Modality.PERMITTED));
        setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("2"))), cs2, ModalTransition.Modality.PERMITTED));
        setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("3"))), cs2, ModalTransition.Modality.PERMITTED));
        setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("4"))), cs2, ModalTransition.Modality.PERMITTED));

        return  new Automaton<>(setTr);
    }

}
