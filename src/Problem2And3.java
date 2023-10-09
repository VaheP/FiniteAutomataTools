public class Problem2And3 {


    public static void main(String[] args) {
        State h0 = new State("h0", false);
        State h1 = new State("h1", false);
        State h2 = new State("h2", true);

        Transition[] transitions = {
            new Transition(h0, h0, "N,O,T,F"),
                new Transition(h0, h1, "N"),
            new Transition(h1, h2, "N")
        };

        NFA A100 = new NFA(h0, new State[]{h0, h1, h2}, transitions, new String[]{"N", "O", "T", "F"});

        State s0 = new State("s0", false);
        State s1 = new State("s1", false);
        State s2 = new State("s2", true);
        Transition[] transitions2 = {
                new Transition(s0, s0, "T"),
                new Transition(s0, s2, "N,F"),
                new Transition(s0, s1, "O"),
                new Transition(s1, s1, "O"),
                new Transition(s1, s2, "T"),
                new Transition(s1, s0, "N,F"),
                new Transition(s2, s2, "N,F"),
                new Transition(s2, s0, "T"),
                new Transition(s2, s1, "O")
        };
        DFA A4 = new DFA(s0, new State[]{s0, s1, s2}, transitions2, new String[]{"N", "O", "T", "F"});

        DFA B = new DFA(A4);
        State k1 = new State("k1", false);
        State k2 = new State("k2", true);
        B.addState(k1);
        B.addState(k2);
        B.getStateByName("s2").isFinal = false;
        B.addTransition(new Transition(B.getStateByName("s2"), k1, "N"));
        B.addTransition(new Transition(k1, k2, "N"));
        System.out.println("L(B) = L(A4)NN: \n");
        System.out.println(B);

        DFA A4Complement = new DFA(A4);
        A4Complement.complement();
        NFA A100Temp = new NFA(A100);

        NFA C = FAUtils.Union(A4Complement, A100Temp);
        System.out.println(C);

        C.splitTransitions();

        DFA CDFA = NFA.convertNFAtoDFA(C);
        C.compressTransitions();
        CDFA.compressTransitions();
        CDFA.complement();

        System.out.println("L(C) = L(A4) – L(A100) = L(A4) \uF0C7 (\uF053* – L(A100)) = \uF053*\n" +
                "– ((\uF053* – L(A4)) \uF0C8 L(A100): \n");
        System.out.println(CDFA);

        System.out.println("L(Y) = L(B) U L(C)");
        NFA Y = FAUtils.Union(B, CDFA);
        System.out.println(Y);

        System.out.println(FAUtils.toRegularExpression(Y));
    }
}
