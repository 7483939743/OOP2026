public class DoubleCounter implements Counter {
    int counter = 0;
    public void increase() {
        counter += 2;
    }

    public int currentValue() {
        return counter;
    }

    @Override
    public String toString() {
        return "DoubleCounter";
    }

    @Override
    public void reset(){

        counter = 0;
    }
}
