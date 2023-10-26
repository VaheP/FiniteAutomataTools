package FiniteAutomata;

import java.util.*;

public class DFA extends FA {

    public DFA(State start, State[] nodes, Transition[] transitions, String[] alphabet) {
        super(start, nodes, transitions, alphabet);
    }

    public DFA() {
        super();
    }

    public DFA(DFA from) {
        super(from);
    }


    public void complement() {
        for (State node : states) {
            node.isFinal = !node.isFinal;
        }
    }


}
