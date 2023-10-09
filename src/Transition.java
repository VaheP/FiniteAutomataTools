public class Transition {
    public State from;
    public State to;
    public String symbol;

    public String toString() {
        return from.getName() + "->" + to.getName() + "[label = \"" + symbol + "\"]";
    }

    public Transition(State from, State to, String symbol) {
        if (symbol.contains(" ")) {
            throw new IllegalArgumentException("Symbol cannot contain spaces");
        }
        this.from = from;
        this.to = to;
        this.symbol = symbol;
    }
}
