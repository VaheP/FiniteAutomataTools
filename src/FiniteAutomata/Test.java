package FiniteAutomata;

public class Test {
    public static void main(String[] args) {
        State a = new State("a", false);
        State b = new State("b", false);
        State c = new State("c", true);

        Transition[] transitions = {
                new Transition(a, b, "a"),
                new Transition(b, b, "b"),
                new Transition(b, c, "a"),
                new Transition(c, c, "a,b"),
                new Transition(a, c, "b")
        };

        DFA dfa = new DFA(a, new State[]{a, b, c}, transitions, new String[]{"a", "b"});

        System.out.println(dfa);
        System.out.println(FAUtils.toRegularExpression(dfa));
    }
}
