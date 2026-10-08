public class OverflowCounter implements Counter {

    int counter = 0;
    public void increase() {
        ++counter;

        if(counter==11){

            counter = 0;


        }
    }

    public int currentValue() {
        return counter;
    }

    @Override
    public String toString() {
        return "OverflowCounter";
    }

    @Override
    public void reset(){

        counter = 0;
    }



}
