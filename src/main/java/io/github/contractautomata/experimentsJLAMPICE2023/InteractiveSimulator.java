package io.github.contractautomata.experimentsJLAMPICE2023;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class InteractiveSimulator {
    public static final String dir = System.getProperty("user.dir")+ File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator;

    public static void main(String[] args) throws IOException {
//        if (args.length==0){
//            System.out.println("java -jar Simulator [file.data]");
//            return;
//        }
        Scanner scanner = new Scanner(System.in);
        AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> aut = bdc.importMSCA(dir+"orc.data"); //args[0]);
        State<String> currentState = aut.getInitial();

        while (true){
            System.out.println("Current state: "+currentState);
            List<ModalTransition<String, Action, State<String>, CALabel>> trans;
            int choice;
            do {
                System.out.println("Select transition:");

                trans = new ArrayList<>(aut.getForwardStar(currentState));

                if (trans.isEmpty()) {
                    System.out.println("No outgoing transitions.");
                    return;
                }
                List<ModalTransition<String, Action, State<String>, CALabel>> finalTrans = trans;
                IntStream.range(0, finalTrans.size())
                        .forEach(i -> System.out.println(i + ") " + finalTrans.get(i)));

                choice = scanner.nextInt();
                if (choice<0 || choice>= trans.size()) System.out.println("Out of range, select again.");
            } while (choice<0 || choice>= trans.size());
            System.out.println("Executing transition "+choice+": "+trans.get(choice));
            currentState=trans.get(choice).getTarget();
        }
    }
}
