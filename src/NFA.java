import java.util.*;
import java.util.stream.Collectors;

public class NFA extends FA{

    public NFA(State start, State[] nodes, Transition[] transitions, String[] alphabet) {
        super(start, nodes, transitions, alphabet);
    }

    public NFA() {
        super();
    }

    public NFA(NFA from) {
        super(from);

    }

    public static DFA convertNFAtoDFA(NFA nfa) {
        Map<Set<State>, State> dfaStates = new HashMap<>();
        List<Transition> dfaTransitions = new ArrayList<>();
        List<Set<State>> statesToProcess = new ArrayList<>();

        Set<State> startSet = epsilonClosure(Collections.singleton(nfa.start), nfa);
        State dfaStart = new State(stateSetName(startSet), isFinal(startSet, nfa));
        statesToProcess.add(startSet);
        dfaStates.put(startSet, dfaStart);

        while (!statesToProcess.isEmpty()) {
            Set<State> currentSet = statesToProcess.remove(0);
            State currentState = dfaStates.get(currentSet);

            for (String symbol : nfa.alphabet) {
                Set<State> nextSet = epsilonClosure(move(currentSet, symbol, nfa), nfa);

                State nextState = dfaStates.get(nextSet);
                if (nextState == null) {
                    nextState = new State(stateSetName(nextSet), isFinal(nextSet, nfa));
                    dfaStates.put(nextSet, nextState);
                    statesToProcess.add(nextSet);
                }

                dfaTransitions.add(new Transition(currentState, nextState, symbol));
            }
        }

        return new DFA(dfaStart, dfaStates.values().toArray(new State[0]),
                dfaTransitions.toArray(new Transition[0]), nfa.alphabet);
    }

    private static String stateSetName(Set<State> states) {
        return states.stream()
                .map(State::getName)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static Set<State> move(Set<State> states, String symbol, NFA nfa) {
        Set<State> result = new HashSet<>();
        for (State state : states) {
            for (Transition transition : nfa.deltaFunction) {
                if (transition.from.equals(state) && transition.symbol.equals(symbol)) {
                    result.add(transition.to);
                }
            }
        }
        return result;
    }

    private static Set<State> epsilonClosure(Set<State> states, NFA nfa) {
        Set<State> closure = new HashSet<>(states);
        boolean changes;
        do {
            changes = false;
            Set<State> newStates = new HashSet<>(closure);
            for (State state : closure) {
                for (Transition transition : nfa.deltaFunction) {
                    if (transition.from.equals(state) && transition.symbol.equals("e")) {
                        newStates.add(transition.to);
                    }
                }
            }
            if (!closure.equals(newStates)) {
                changes = true;
                closure = newStates;
            }
        } while (changes);
        return closure;
    }

    private static boolean isFinal(Set<State> states, NFA nfa) {
        for (State state : states) {
            if (state.isFinal) {
                return true;
            }
        }
        return false;
    }



}
