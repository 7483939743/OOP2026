# Laboratorium 2 — Interfejsy i ergonomia programowania obiektowego

Oliwia Dądela
54420 14.10.2026

## Rozgrzewka

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}

Static deklaruje, że metoda nie wymaga obiektu klasy na którym ma być wywołana

## Zad 1

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CounterApp {

    JFrame frame = new JFrame("Licznik");
    JButton button = new JButton("Kliknij!");
    JLabel label = new JLabel("0");

    public static void main(String[] args) {
        new CounterApp().run();
    }

    void run() {
        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.add(label);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

Wyświetla się okienko aplikacji desktopowej z przyciskiem "Kliknij", który nie jest funkcjonalny i jego kliknięcie nie wywołuje żadnej akcji.

Zad 2

public class CounterApp implements ActionListener {

    JFrame frame = new JFrame("Licznik");
    JButton button = new JButton("Kliknij!");
    JLabel label = new JLabel("0");

    public static void main(String[] args) {
        new CounterApp().run();
    }

    void run() {
        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.add(label);
        button.addActionListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
        int count = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        count++;
        label.setText(Integer.toString(count));

        ...}
}

Teraz po naciśnięciu przycisku "Kliknij" liczba 0 na liczniku zwiększa się o 1 za każdym kolejnym naciśnięciem.

## Zad 3

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CounterApp implements ActionListener {

    JFrame frame = new JFrame("Licznik");
    JButton button = new JButton("Kliknij!");
    JLabel label = new JLabel("0");
    JComboBox<Counter> counters = new JComboBox<>(new Counter[]{
            new SimpleCounter(),
            new DoubleCounter(),
    });
    public static void main(String[] args) {
        new CounterApp().run();


    }

    void run() {
        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.add(label);
        button.addActionListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.add(counters);
    }
        int count = 0;

    @Override
    public void actionPerformed(ActionEvent e) {
        Counter counter = (Counter) counters.getSelectedItem();
        counter.increase();
        label.setText(Integer.toString(counter.currentValue()));
        frame.pack();
    }
}

Klasa CounterApp implementuje interfejs ActionListener, dzięki czemu może reagować na moment naciśnięcia przycisku.

## Zad 4

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

## Zad 5


Klasa java.lang.Object udostepnia metody takie jak: equals(),finalize(),getClass(),hashCode(),notify(),notifyAll(),toString(),wait()

1.Należy nadpisać metodę toString(), gdzie JComboBox automatycznie z niej korzysta gdy wyświetla obiekty na liście.
2. Jest to zapis szesnastkowy wartości zwracanej przez domyślną implementację hashCode().

## Zad 6

Sposób pierwszy

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

    