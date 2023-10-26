package FiniteAutomata;

import java.util.Objects;

public class Transition{
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return from.equals(that.from) &&
                to.equals(that.to) &&
                symbol.equals(that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, symbol);
    }
}
