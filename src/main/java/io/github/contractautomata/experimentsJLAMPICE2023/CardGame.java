package io.github.contractautomata.experimentsJLAMPICE2023;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.NewOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.operations.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.StrongAgreement;

import java.io.File;
import java.io.IOException;
import java.util.List;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

public class CardGame {
    public static final String dir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator;

    public static void main(String[] args) throws IOException {
        AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
        Instant start, stop;
        long elapsedTime;

        Function<Boolean,String> f = b -> b?"the orchestration is empty":"the orchestration is non-empty";
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> dealer = bdc.importMSCA(dir + "Dealer.data");
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player = bdc.importMSCA(dir + "Player.data");

        start = Instant.now();
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new NewOrchestrationSynthesisOperator(new StrongAgreement()).apply(List.of(dealer,player,player));
        stop = Instant.now();
        elapsedTime = Duration.between(start, stop).toMillis();
        System.out.println("Orchestration (encoded lazy): " +elapsedTime + " milliseconds "+ f.apply(Objects.isNull(orc)));

        start = Instant.now();
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp =
                new MSCACompositionFunction<>(List.of(dealer,player,player), t->new StrongAgreement().negate().test(t.getLabel()) ).apply(Integer.MAX_VALUE);
        orc = new OrchestrationSynthesisOperator<String>(new StrongAgreement()).apply(comp);
        stop = Instant.now();
        elapsedTime = Duration.between(start, stop).toMillis();
        System.out.println("Orchestration (original lazy) : " +elapsedTime + " milliseconds "+  f.apply(Objects.isNull(orc)));

        start = Instant.now();
        comp =
                new MSCACompositionFunction<>(List.of(dealer,player,player), t->new StrongAgreement().negate().test(t.getLabel()) ).apply(Integer.MAX_VALUE);
        OrchestrationSynthesisOperator.setRefinedLazy();
        orc = new OrchestrationSynthesisOperator<String>(new StrongAgreement()).apply(comp);
        stop = Instant.now();
        elapsedTime = Duration.between(start, stop).toMillis();
        System.out.println("Orchestration (refined lazy) : " +elapsedTime + " milliseconds "+ f.apply(Objects.isNull(orc)));

    }
}