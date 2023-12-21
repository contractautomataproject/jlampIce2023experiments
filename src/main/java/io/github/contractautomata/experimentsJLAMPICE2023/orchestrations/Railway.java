package io.github.contractautomata.experimentsJLAMPICE2023.orchestrations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.operations.SplittingOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.StrongAgreement;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Railway {
    private static final String dir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator;

    private static final int junctionStartAt = 4;

    private static final int junctionStopAt = 4;

    private static final int semaphoreX = 2;

    public static void main(String[] args) throws IOException {
        System.out.println("Railway Orchestration Example");
        AutDataConverter<CALabel> adc = new AutDataConverter<>(CALabel::new);


        Instant start, stop;
        long elapsedTime;

        Function<Boolean,String> f = b -> b?"the orchestration is empty":"the orchestration is non-empty";
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> train1 = adc.importMSCA(dir + "train1.data");
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> train2 = adc.importMSCA(dir + "train2.data");
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> driver = adc.importMSCA(dir + "driver.data");
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>> semaphoreContr = adc.importMSCA(dir + "semaphoreContr.data");
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>> semaphore = adc.importMSCA(dir + "semaphore.data");
        List<Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>> list = List.of(train1,train2,driver,semaphoreContr,semaphore);


        Predicate<State<String>> badState = getBadStatePredicate();
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc;



        start = Instant.now();
        orc = new SplittingOrchestrationSynthesisOperator(new StrongAgreement(),  t->badState.test(t.getTarget())).apply(list);
        stop = Instant.now();
        elapsedTime = Duration.between(start, stop).toMillis();
        System.out.println("Splitting orchestration synthesis: " +elapsedTime + " milliseconds "+ f.apply(Objects.isNull(orc)));
        System.out.println("Splitting orchestration : states="+orc.getStates().size()+", transitions="+orc.getTransition().size()+System.lineSeparator());

        adc.exportMSCA(dir+"SplittingOrcTrain",orc);


        start = Instant.now();
        MSCACompositionFunction<String> cf = new MSCACompositionFunction<>(list,
                t->new StrongAgreement().negate().test(t.getLabel()) || (badState.test(t.getTarget())));
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>  comp =
                cf.apply(Integer.MAX_VALUE);
        orc = new OrchestrationSynthesisOperator<String>(new StrongAgreement()).apply(comp);
        stop = Instant.now();
        elapsedTime = Duration.between(start, stop).toMillis();
        System.out.println("Conditional orchestration synthesis: " +elapsedTime + " milliseconds "+ f.apply(Objects.isNull(orc)));
        System.out.println("Conditional orchestration : states="+orc.getStates().size()+", transitions="+orc.getTransition().size()+System.lineSeparator());
        adc.exportMSCA(dir+"ConditionalOrcTrain",orc);

        start = Instant.now();
        cf = new MSCACompositionFunction<>(list,
                t->new StrongAgreement().negate().test(t.getLabel()) || (badState.test(t.getTarget())));
        comp = cf.apply(Integer.MAX_VALUE);
        OrchestrationSynthesisOperator.setRefinedLazy();
        orc = new OrchestrationSynthesisOperator<String>(new StrongAgreement()).apply(comp);
        stop = Instant.now();
        elapsedTime = Duration.between(start, stop).toMillis();
        System.out.println("Refined conditional orchestration synthesis: " +elapsedTime + " milliseconds "+ f.apply(Objects.isNull(orc)));
        System.out.println("Refined orchestration : states="+orc.getStates().size()+", transitions="+orc.getTransition().size()+System.lineSeparator());

    }

    private static Predicate<State<String>> getBadStatePredicate() {
        BiFunction<State<String>, Integer, Integer> getX =  (s,i) ->
                Integer.parseInt(s.getState().get(i).getState().split(";")[0].substring(1));

        return s-> {
            int xt0 = getX.apply(s,0);
            int xt1 = getX.apply(s,1);
            boolean train1insideJunction = xt0>=junctionStartAt && xt0<=junctionStopAt;
            boolean train2insideJunction = xt1>=junctionStartAt && xt1<=junctionStopAt;
            boolean train1AtSemaphore = xt0>=semaphoreX-1 && xt0<=semaphoreX+1;
            boolean train2AtSemaphore = xt1>=semaphoreX-1 && xt1<=semaphoreX+1;
            boolean semaphoreOpen = s.getState().get(3).getState().contains("Open");
            boolean semaphoreClose = s.getState().get(3).getState().contains("Close");

            return
                //two agents on the same cell
                    (s.getState().get(0).getState().equals(s.getState().get(1).getState())&&!s.getState().get(0).getState().contains("OUT"))
                        ||
                        IntStream.range(0,2)
                                .mapToObj(i->getX.apply(s,i))
                                .anyMatch(x->x==semaphoreX && semaphoreClose)  //either train is traversing the semaphore whilst is closed
                || (train1insideJunction && train2insideJunction) //both trains inside the junction
                || ((train1insideJunction || train2insideJunction) && semaphoreOpen) //semaphore must be closed when a train is inside the junction
                || (!train1AtSemaphore && !train2AtSemaphore && semaphoreOpen);//the semaphore is open only when a train is near it
        };
    }
}

