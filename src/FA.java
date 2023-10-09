import java.util.*;

public class FA {
    public State start;
    public State[] states;
    public Transition[] deltaFunction;

    public String[] alphabet;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph FA {\n");
        sb.append("rankdir=LR;\n");
        sb.append("node [shape = circle];\n");
        for (State node : states) {
            sb.append(node.toString()).append("\n");
        }
        for (Transition transition : deltaFunction) {
            sb.append(transition.toString()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    public FA(FA from) {
        //clone states
        this.states = new State[from.states.length];
        this.deltaFunction = new Transition[from.deltaFunction.length];

        for (int i = 0; i < from.states.length; i++) {
            this.states[i] = from.states[i].clone();

            if (from.states[i] == from.start) {
                this.start = this.states[i];
            }
        }

        for (int i = 0; i < from.deltaFunction.length; i++) {
            this.deltaFunction[i] = new Transition(this.getStateByName(from.deltaFunction[i].from.name), this.getStateByName(from.deltaFunction[i].to.name), from.deltaFunction[i].symbol);
        }

        this.alphabet = from.alphabet;

    }

    public FA() {
    }

    public FA(State start, State[] nodes, Transition[] transitions, String[] alphabet) {
        this.start = start;
        this.states = nodes;
        this.deltaFunction = transitions;
        this.alphabet = alphabet;
    }



    private static class Edge implements Comparable<Edge> {
        public State from;
        public State to;

        @Override
        public int compareTo(Edge o) {
            if (from.name.equals(o.from.name)) {
                return to.name.compareTo(o.to.name);
            }
            return from.name.compareTo(o.from.name);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Edge) {
                Edge e = (Edge) o;
                return from.name.equals(e.from.name) && to.name.equals(e.to.name);
            }
            return false;
        }

    }
    public void compressTransitions() {
        Map<Edge, String> transitions = new TreeMap<>();

        for (Transition transition : deltaFunction) {
            Edge edge = new Edge();
            edge.from = transition.from;
            edge.to = transition.to;
            if (transitions.containsKey(edge)) {
                String symbol = transitions.get(edge);
                symbol += "," + transition.symbol;
                transitions.put(edge, symbol);
            } else {
                transitions.put(edge, transition.symbol);
            }
        }

        Transition[] newTransitions = new Transition[transitions.size()];
        int i = 0;
        for (Map.Entry<Edge, String> entry : transitions.entrySet()) {
            Edge edge = entry.getKey();
            String symbol = entry.getValue();
            newTransitions[i] = new Transition(edge.from, edge.to, symbol);
            i++;
        }

        deltaFunction = newTransitions;
    }

    public void splitTransitions() {
        List<Transition> newTransitions = new ArrayList<>();

        for (Transition transition : deltaFunction) {
            String[] symbols = transition.symbol.split(",");
            for (String symbol : symbols) {
                newTransitions.add(new Transition(transition.from, transition.to, symbol));
            }
        }

        deltaFunction = newTransitions.toArray(new Transition[newTransitions.size()]);
    }



    public boolean addTransition(Transition ... toAdd) {
        Transition[] newTransitions = new Transition[deltaFunction.length + toAdd.length];
        int i = 0;
        for (Transition transition : deltaFunction) {
            newTransitions[i] = transition;
            i++;
        }
        for (Transition transition : toAdd) {
            newTransitions[i] = transition;
            i++;
        }
        deltaFunction = newTransitions;
        return true;
    }

    public boolean addState(State ... toAdd) {
        State[] newStates = new State[states.length + toAdd.length];
        int i = 0;
        for (State state : states) {
            newStates[i] = state;
            i++;
        }
        for (State state : toAdd) {
            newStates[i] = state;
            i++;
        }
        states = newStates;
        return true;
    }

    public Iterable<State> finalStates() {
        List<State> finalStates = new ArrayList<>();
        for (State state : states) {
            if (state.isFinal) {
                finalStates.add(state);
            }
        }
        return finalStates;
    }

    public void removeTransition(Transition transition) {
        // check if transition exists
        if (!hasTransition(transition)) {
            return;
        }

        Transition[] newTransitions = new Transition[deltaFunction.length - 1];
        int i = 0;
        for (Transition t : deltaFunction) {
            if (t != transition) {
                newTransitions[i] = t;
                i++;
            }
        }
        deltaFunction = newTransitions;

    }

    public void removeState(State state) {
        // check if state exists
        if (!hasState(state)) {
            return;
        }

        // remove all transitions from state
        for (Transition transition : getAllTransitionsFrom(state)) {
            removeTransition(transition);
        }

        // remove all transitions to state
        for (Transition transition : getAllTransitionsTo(state)) {
            removeTransition(transition);
        }

        // remove all self transitions
        for (Transition transition : getSelfTransitions(state)) {
            removeTransition(transition);
        }

        // remove state
        State[] newStates = new State[states.length - 1];
        int i = 0;
        for (State s : states) {
            if (s != state) {
                newStates[i] = s;
                i++;
            }
        }
        states = newStates;
    }

    private boolean hasState(State state) {
        boolean found = false;
        for (State s : states) {
            if (s == state) {
                found = true;
                break;
            }
        }
        return found;
    }

    private boolean hasTransition(Transition transition) {
        boolean found = false;
        for (Transition t : deltaFunction) {
            if (t == transition) {
                found = true;
                break;
            }
        }
        return found;
    }


    public Transition[] getAllTransitionsFrom(State from) {
        List<Transition> transitions = new ArrayList<>();
        for (Transition transition : deltaFunction) {
            if (transition.from == from) {
                transitions.add(transition);
            }
        }
        return transitions.toArray(new Transition[transitions.size()]);
    }

    public Transition[] getAllTransitionsTo(State to) {
        List<Transition> transitions = new ArrayList<>();
        for (Transition transition : deltaFunction) {
            if (transition.to == to) {
                transitions.add(transition);
            }
        }
        return transitions.toArray(new Transition[transitions.size()]);
    }

    public Transition[] getSelfTransitions(State state) {
        List<Transition> transitions = new ArrayList<>();
        for (Transition transition : deltaFunction) {
            if (transition.from == transition.to && transition.from == state) {
                transitions.add(transition);
            }
        }
        return transitions.toArray(new Transition[transitions.size()]);
    }

    public State getStateByName(String name) {
        for (State state : states) {
            if (state.name.equals(name)) {
                return state;
            }
        }
        return null;
    }

}
