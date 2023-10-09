public class FAUtils {
    public static NFA Union (FA a, FA b) {
//        rename all states to avoid name conflicts
        int stateCounter = 0;
        for (State node : a.states) {
            node.name = "s" + stateCounter;
            stateCounter++;
        }
        for (State node : b.states) {
            node.name = "s" + stateCounter;
            stateCounter++;
        }
        FA aClone = new FA(a);
        FA bClone = new FA(b);
        NFA result = new NFA();
        result.start = new State("start", false);
        result.states = new State[aClone.states.length + bClone.states.length + 1];
        result.deltaFunction = new Transition[aClone.deltaFunction.length + bClone.deltaFunction.length + 2];
        result.alphabet = aClone.alphabet;

        int i = 0;
        for (State node : aClone.states) {
            result.states[i] = node;
            i++;
        }
        for (State node : bClone.states) {
            result.states[i] = node;
            i++;
        }
        result.states[i] = result.start;

        i = 0;
        for (Transition transition : aClone.deltaFunction) {
            result.deltaFunction[i] = transition;
            i++;
        }
        for (Transition transition : bClone.deltaFunction) {
            result.deltaFunction[i] = transition;
            i++;
        }
        result.deltaFunction[i] = new Transition(result.start, aClone.start, "e");
        i++;
        result.deltaFunction[i] = new Transition(result.start, bClone.start, "e");

        return result;
    }

    public static String toRegularExpression(FA fa) {
        DFA eDFA = new DFA();
        eDFA.start = new State("begin", false);
        State end = new State("end", true);

        eDFA.states = new State[fa.states.length + 2];
        eDFA.deltaFunction = fa.deltaFunction.clone();
        eDFA.alphabet = fa.alphabet;

        int i = 0;
        for (State node : fa.states) {
            eDFA.states[i] = node;
            i++;
        }
        eDFA.states[i] = eDFA.start;
        i++;
        eDFA.states[i] = end;


        fa.finalStates().forEach(state -> {
            eDFA.addTransition(new Transition(state, end, "e"));
        });
        eDFA.addTransition(new Transition(eDFA.start, fa.start, "e"));

        eDFA.compressTransitions();

        // start removing states


        System.out.println(eDFA);

        for(State s : eDFA.states) {
            if(s == eDFA.start || s == end) {
                continue;
            }

            var fromS = eDFA.getAllTransitionsFrom(s);
            var toS = eDFA.getAllTransitionsTo(s);
            var self = eDFA.getSelfTransitions(s);

            String selfRegex = "";
            if (self.length > 0) {
                selfRegex = "(" + self[0].symbol + ")*";
                eDFA.removeTransition(self[0]);
            }

            for(Transition t : toS) {
                if (t.from == t.to) {
                    continue;
                }
                for(Transition t2 : fromS) {
                    if (t2.from == t2.to) {
                        continue;
                    }

                    Transition newTransition = new Transition(t.from, t2.to, ("(" + t.symbol + selfRegex + t2.symbol + ")").replace("e", "").replace(",", "∪"));
                    eDFA.addTransition(newTransition);
                    eDFA.removeTransition(t);
                    eDFA.removeTransition(t2);
                }
            }
            eDFA.removeState(s);
            eDFA.compressTransitions();
            allCommasToUnions(eDFA);
            System.out.println(eDFA);
        }
        return eDFA.deltaFunction[0].symbol;

    }

    public static void allCommasToUnions(FA fa) {
        for (Transition transition : fa.deltaFunction) {
            transition.symbol = transition.symbol.replace(",", "∪");
        }
    }


}
