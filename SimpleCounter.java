public class SimpleCounter implements Counter {
    int counter = 0;
    public void increase() {
        ++counter;
    }

    public int currentValue() {
        return counter;
    }

    @Override
    public String toString() {
        return "SimpleCounter";
    }

    @Override
    public void reset(){

        counter = 0;

    }
}
