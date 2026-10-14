import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CounterApp implements ActionListener {

    JFrame frame = new JFrame("Licznik");
    JButton button = new JButton("Kliknij!");
    JButton button2 = new JButton("Reset");
    JLabel label = new JLabel("0");


    public static void main(String[] args) {
        new CounterApp().run();


    }

    void run() {
        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.add(label);
        frame.add(button2);
        button.addActionListener(this);
        button2.addActionListener(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.add(counters);

    }

    //6. Przycisk reset działa tak jak przycisk kliknij.
    //Przycisk Reset działa tak samo jak przycisk "Kliknij!", ponieważ w metodzie actionPerformed nie sprawdza, który przycisk został wciśnięty.



//6.Sposob1

    @Override
    public void actionPerformed(ActionEvent e) {
        Counter counter = (Counter) counters.getSelectedItem();
        if (e.getSource() == button) {
            counter.increase();
        } else if (e.getSource() == button2) {
            counter.reset();
        }
        label.setText(Integer.toString(counter.currentValue()));
        frame.pack();
    }

    //6.Sposob2



    JComboBox<Counter> counters = new JComboBox<>(new Counter[]{
            new SimpleCounter(),
            new DoubleCounter(),
            new OverflowCounter(),
    });

        //dodanie linii button.addAction... sprawiło, że przy kazdym naciskaniu przycisku program zwiększa wyswietlana liczbę o 1

// program przy kliknieciu przycisku wyswietla nam wybor miedzy simplecounter i doublecounter








}
