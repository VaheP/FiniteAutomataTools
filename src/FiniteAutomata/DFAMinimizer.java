package FiniteAutomata;

import java.util.*;

public class DFAMinimizer {

    public static DFA minimizeDFA(DFA dfa) {
        List<Set<State>> partitions = new ArrayList<>();

        Set<State> finalStates = new HashSet<>();
        Set<State> nonFinalStates = new HashSet<>();

        for (State state : dfa.states) {
            if (state.isFinal) {
                finalStates.add(state);
            } else {
                nonFinalStates.add(state);
            }
        }

        if (!finalStates.isEmpty()) partitions.add(finalStates);
        if (!nonFinalStates.isEmpty()) partitions.add(nonFinalStates);

        printEquivalenceClasses(partitions, "Initial Partitioning");
        boolean changed;
        do {
            changed = false;

            List<Set<State>> newPartitions = new ArrayList<>();

            for (Set<State> partition : partitions) {
                Map<String, Set<State>> transitionPartitions = new HashMap<>();

                for (State state : partition) {
                    StringBuilder keyBuilder = new StringBuilder();
                    for (String symbol : dfa.alphabet) {
                        State targetState = getTransitionState(dfa, state, symbol);
                        keyBuilder.append(getPartitionId(targetState, partitions));
                        keyBuilder.append(",");
                    }
                    String key = keyBuilder.toString();

                    transitionPartitions
                            .computeIfAbsent(key, k -> new HashSet<>())
                            .add(state);
                }

                newPartitions.addAll(transitionPartitions.values());
            }

            if (newPartitions.size() != partitions.size()) {
                partitions = newPartitions;
                changed = true;
            }

            printEquivalenceClasses(partitions, "Partitioning");

        } while (changed);

        var newDFA = buildNewDFA(dfa, partitions);
        RemoveDuplicateTransitionsFromDFA(newDFA);

        var state = newDFA.getStateByName("q0");
        state.name = newDFA.start.getName();
        newDFA.start.name = "q0";

        return newDFA;
    }

    private static void printEquivalenceClasses(List<Set<State>> partitions, String message) {
        System.out.println(message);
        for (Set<State> partition : partitions) {
            System.out.print("{ ");
            for (State s : partition) {
                System.out.print(s.name + " ");
            }
            System.out.println("}");
        }
        System.out.println();  // For an extra line break between prints
    }
    private static int getPartitionId(State state, List<Set<State>> partitions) {
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).contains(state)) {
                return i;
            }
        }
        return -1;
    }

    private static State getTransitionState(DFA dfa, State state, String symbol) {
        for (Transition t : dfa.deltaFunction) {
            if (t.from.equals(state) && t.symbol.equals(symbol)) {
                return t.to;
            }
        }
        return null;
    }

    private static DFA buildNewDFA(DFA dfa, List<Set<State>> partitions) {
        List<State> newStates = new ArrayList<>();
        List<Transition> newTransitions = new ArrayList<>();
        State newStartState = null;

        Map<State, State> oldToNewStateMap = new HashMap<>();

        int stateId = 0;
        for (Set<State> partition : partitions) {
            State representative = partition.iterator().next();
            State newState = new State("q" + stateId, representative.isFinal);
            newStates.add(newState);

            if (partition.contains(dfa.start)) {
                newStartState = newState;
            }

            for (State oldState : partition) {
                oldToNewStateMap.put(oldState, newState);
            }

            stateId++;
        }

        for (Transition oldTransition : dfa.deltaFunction) {
            State newFrom = oldToNewStateMap.get(oldTransition.from);
            State newTo = oldToNewStateMap.get(oldTransition.to);
            Transition newTransition = new Transition(newFrom, newTo, oldTransition.symbol);
            newTransitions.add(newTransition);
        }

        return new DFA(newStartState, newStates.toArray(new State[0]), newTransitions.toArray(new Transition[0]), dfa.alphabet);
    }

    private static void RemoveDuplicateTransitionsFromDFA(DFA dfa) {
        Set<Transition> transitions = new HashSet<>();
        Collections.addAll(transitions, dfa.deltaFunction);

        dfa.deltaFunction = transitions.toArray(new Transition[0]);
    }

}
