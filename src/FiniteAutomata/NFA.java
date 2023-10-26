package FiniteAutomata;

import java.util.*;
import java.util.stream.Collectors;

public class NFA extends FA {

    public NFA(State start, State[] nodes, Transition[] transitions, String[] alphabet) {
        super(start, nodes, transitions, alphabet);
    }

    public NFA() {
        super();
    }

    public NFA(FA from) {
        super(from);

    }

    public static final String EPSILON = "e";

    public static DFA convert(NFA nfa) {
        Map<Set<State>, State> dfaStatesMap = new HashMap<>();
        List<Transition> dfaTransitions = new ArrayList<>();
        Queue<Set<State>> worklist = new LinkedList<>();

        Set<State> startSet = epsilonClosure(Collections.singleton(nfa.start), nfa);
        State dfaStartState = new State(setToStateName(startSet), containsFavorable(startSet));
        dfaStatesMap.put(startSet, dfaStartState);
        worklist.add(startSet);

        while (!worklist.isEmpty()) {
            Set<State> currentStateSet = worklist.poll();
            State dfaCurrentState = dfaStatesMap.get(currentStateSet);

            for (String symbol : nfa.alphabet) {
                Set<State> nextStateSet = epsilonClosure(move(currentStateSet, symbol, nfa), nfa);

                if (!dfaStatesMap.containsKey(nextStateSet)) {
                    State newState = new State(setToStateName(nextStateSet), containsFavorable(nextStateSet));
                    dfaStatesMap.put(nextStateSet, newState);
                    worklist.add(nextStateSet);
                }

                State dfaNextState = dfaStatesMap.get(nextStateSet);
                dfaTransitions.add(new Transition(dfaCurrentState, dfaNextState, symbol));
            }
        }

        State[] dfaStates = dfaStatesMap.values().toArray(new State[0]);

        for (State dfaState : dfaStates) {
            for (State nfaFinalState : nfa.finalStates()) {
                if (dfaState.name.contains(nfaFinalState.name)) {
                    dfaState.isFinal = true;
                    break;
                }
            }
        }

        return new DFA(dfaStartState, dfaStates, dfaTransitions.toArray(new Transition[0]), nfa.alphabet);
    }

    private static Set<State> move(Set<State> states, String symbol, NFA nfa) {
        Set<State> resultSet = new HashSet<>();
        for (State state : states) {
            resultSet.addAll(nfa.getTransition(state, symbol));
        }
        return resultSet;
    }

    private static Set<State> epsilonClosure(Set<State> states, NFA nfa) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            State state = stack.pop();
            for (State next : nfa.getTransition(state, NFA.EPSILON)) {
                if (!closure.contains(next)) {
                    closure.add(next);
                    stack.push(next);
                }
            }
        }

        return closure;
    }

    private static boolean containsFavorable(Set<State> states) {
        for (State state : states) {
            if (state.isFinal) {
                return true;
            }
        }
        return false;
    }

//    public static DFA convertNFAtoDFA(NFA nfa) {
//        Map<Set<State>, State> dfaStates = new HashMap<>();
//        List<Transition> dfaTransitions = new ArrayList<>();
//        List<Set<State>> statesToProcess = new ArrayList<>();
//
//        Set<State> startSet = epsilonClosure(Collections.singleton(nfa.start), nfa);
//        State dfaStart = new State(stateSetName(startSet), isFinal(startSet, nfa));
//        statesToProcess.add(startSet);
//        dfaStates.put(startSet, dfaStart);
//
//        while (!statesToProcess.isEmpty()) {
//            Set<State> currentSet = statesToProcess.remove(0);
//            State currentState = dfaStates.get(currentSet);
//
//            for (String symbol : nfa.alphabet) {
//                Set<State> nextSet = epsilonClosure(move(currentSet, symbol, nfa), nfa);
//
//                State nextState = dfaStates.get(nextSet);
//                if (nextState == null) {
//                    nextState = new State(stateSetName(nextSet), isFinal(nextSet, nfa));
//                    dfaStates.put(nextSet, nextState);
//                    statesToProcess.add(nextSet);
//                }
//
//                dfaTransitions.add(new Transition(currentState, nextState, symbol));
//            }
//        }
//
//        return new DFA(dfaStart, dfaStates.values().toArray(new State[0]),
//                dfaTransitions.toArray(new Transition[0]), nfa.alphabet);
//    }
//
//    private static String stateSetName(Set<State> states) {
//        return states.stream()
//                .map(State::getName)
//                .sorted()
//                .collect(Collectors.joining(","));
//    }
//
//    private static Set<State> move(Set<State> states, String symbol, NFA nfa) {
//        Set<State> result = new HashSet<>();
//        for (State state : states) {
//            for (Transition transition : nfa.deltaFunction) {
//                if (transition.from.equals(state) && transition.symbol.equals(symbol)) {
//                    result.add(transition.to);
//                }
//            }
//        }
//        return result;
//    }
//
//    private static Set<State> epsilonClosure(Set<State> states, NFA nfa) {
//        Set<State> closure = new HashSet<>(states);
//        boolean changes;
//        do {
//            changes = false;
//            Set<State> newStates = new HashSet<>(closure);
//            for (State state : closure) {
//                for (Transition transition : nfa.deltaFunction) {
//                    if (transition.from.equals(state) && transition.symbol.equals("e")) {
//                        newStates.add(transition.to);
//                    }
//                }
//            }
//            if (!closure.equals(newStates)) {
//                changes = true;
//                closure = newStates;
//            }
//        } while (changes);
//        return closure;
//    }

    private static boolean isFinal(Set<State> states, NFA nfa) {
        for (State state : states) {
            if (state.isFinal) {
                return true;
            }
        }
        return false;
    }

    private static String setToStateName(Set<State> states) {
        return states.stream()
                .map(State::getName)
                .sorted()
                .collect(Collectors.joining(""));
    }


}
