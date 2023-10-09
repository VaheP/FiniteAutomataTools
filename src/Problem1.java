public class Problem1 {
    public static void main(String[] args) {
        State s2 = new State("s2", true);
        State t = new State("t", false);
        Transition[] transitionsD2 = {
                new Transition(s2, s2, "0,2,4,6,8"),
                new Transition(s2, t, "1,3,5,7,9"),
                new Transition(t, t, "1,3,5,7,9"),
                new Transition(t, s2, "0,2,4,6,8"),
        };

        DFA div2compl = new DFA(s2, new State[]{s2, t}, transitionsD2, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});

        State s3 = new State("s3", true);
        State p = new State("p", false);
        State q = new State("q", false);

        Transition[] transitionsD3 = {
                new Transition(s3, s3, "0,3,6,9"),
                new Transition(s3, p, "1,4,7"),
                new Transition(s3, q, "2,5,8"),
                new Transition(p, p, "0,3,6,9"),
                new Transition(p, s3, "2,5,8"),
                new Transition(p, q, "1,4,7"),
                new Transition(q, q, "0,3,6,9"),
                new Transition(q, p, "2,5,8"),
                new Transition(q, s3, "1,4,7")
        };
        DFA div3compl = new DFA(s3, new State[]{s3, p, q}, transitionsD3, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});

        div2compl.complement();
        div3compl.complement();

        System.out.println("Divisible by 2 complement (C2)\n");
        System.out.println(div2compl);
        System.out.println('\n');
        System.out.println("Divisible by 3 complement (C3)\n");
        System.out.println(div3compl);

        NFA B = FAUtils.Union(div2compl, div3compl);
        System.out.println('\n');
        System.out.println("Union of C2 and C3 (B)\n");
        System.out.println(B);

        B.splitTransitions();
        DFA A = NFA.convertNFAtoDFA(B);
        B.compressTransitions();
        A.compressTransitions();

        System.out.println('\n');
        System.out.println("DFA of B (A)\n");
        System.out.println(A);

        A.complement();
        System.out.println('\n');
        System.out.println("Complement of A (D6)\n");
        System.out.println(A);

    }
}
