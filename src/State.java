public class State implements Cloneable, Comparable<State> {
    public String name;
    public boolean isFinal;

    public String toString() {
        return "node [" + "shape = " + (isFinal ? "doublecircle" : "circle") + "];" + " " + name.replace(",", "") + ";";
    }

    public State(String name, boolean isFinal) {
        if (name.contains(" ")) {
            throw new IllegalArgumentException("State name cannot contain spaces");
        }
        this.name = name;
        this.isFinal = isFinal;
    }

    public String getName() {
        return name.replace(",", "");
    }


    @Override
    public int compareTo(State o) {
        return name.compareTo(o.name) + (isFinal ? 1 : 0) - (o.isFinal ? 1 : 0);
    }

    public boolean equals(Object o) {
        if (o instanceof State s) {
            return name.equals(s.name) && isFinal == s.isFinal;
        }
        return false;
    }

    @Override
    public State clone() {

        State clone = null;
        try {
            clone = (State) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.name = name;
        clone.isFinal = isFinal;
        return clone;
    }


    public int hashCode() {
        return name.hashCode();
    }
}
