# Sprawozdanie 4

### Oliwia Dądela 54420

## Zad 1

```java

package counters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Ticking implements Counter {
    private Counter counter;

    public Ticking(Counter counter) {
        this.counter = counter;
        new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increase();
            }
        }).start();
    }

    @Override
    public void increase() {
        counter.increase();
    }

    @Override
    public int currentValue() {
        return counter.currentValue();
    }

    @Override
    public void reset() {
        counter.reset();
    }

    @Override
    public String toString() {
        return counter.toString() + " (tykający)";
    }
}


```
Metoda actionPerformed wywołuje metodę increase() obiektu klasy Ticking, który następnie przekazuje to wywołanie do opakowanego wewnątrz licznika.

Przed kliknięciem przycisku etykieta wyświetla cały czas wartość 0, mimo że licznik w tle zwiększa swoją wartość co sekundę. Dopiero po kliknięciu przycisku etykieta aktualizuje się i pokazuje aktualną wartość powiększoną o wszystkie dotychczasowe tyknięcia. Dzieje się tak, ponieważ etykieta jest odświeżana wyłącznie wewnątrz metody actionPerformed obsługującej przycisk.

O zmianie wartości wie sam licznik (Ticking oraz opakowany w nim SimpleCounter), natomiast powinna się o tym dowiedzieć aplikacja (CounterApp), aby mogła odświeżyć wyświetlaną etykietę.

## Zad 2

```java

for (int i = 0; i < counters.getItemCount(); i++) {
    counters.getItemAt(i).addListener(this);
}
counters.addActionListener(this);

```


```java

package counters;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCounter implements Counter {
    protected int counter = 0;
    private List<CounterListener> listeners = new ArrayList<>();

    @Override
    public void addListener(CounterListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (CounterListener listener : listeners) {
            listener.counterChanged(this);
        }
    }

    @Override
    public final void increase() {
        step();
        notifyListeners();
    }

    protected abstract void step();

    @Override
    public int currentValue() {
        return counter;
    }

    @Override
    public void reset() {
        counter = 0;
        notifyListeners();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + counter;
    }
}


```
```java

package counters;

public class SimpleCounter extends AbstractCounter {
    @Override
    protected void step() {
        ++counter;
    }
}

```

Powiadamianie słuchaczy jest wywoływane w metodach increase() oraz reset() w klasie AbstractCounter. Oznaczenie metody increase() jako final i wydzielenie abstrakcyjnej metody step() gwarantuje, że każda podklasa automatycznie powiadomi słuchaczy po zmianie stanu.

## Zad 3

```java

for (int i = 0; i < counters.getItemCount(); i++) {
    counters.getItemAt(i).addListener(this);
}
counters.addActionListener(this);

```

```java

@Override
public void counterChanged(Counter source) {
    if (source == counters.getSelectedItem()) {
        updateView();
    }
}

@Override
public void actionPerformed(ActionEvent e) {
    Counter selected = (Counter) counters.getSelectedItem();
    if (e.getSource() == incButton) {
        selected.increase();
    } else if (e.getSource() == resetButton) {
        selected.reset();
    } else if (e.getSource() == counters) {
        updateView();
    }
}


```

Po wybraniu zwykłego licznika etykieta poprawnie zmienia się po kliknięciu przycisków. Bez dodania ActionListener do samej listy JComboBox, etykieta po przełączeniu licznika pokazywała starą wartość z poprzedniego licznika zarejestrowanie this jako słuchacza listy naprawiło ten błąd.
Dla licznika tykającego etykieta nadal nie zmienia się sama.

1.W metodzie actionPerformed pozostało wyłącznie wywoływanie metod increase() i reset() na wybranym liczniku po kliknięciu przycisków oraz odświeżenie widoku po zmianie elementu na liście.

2.Klasy z pakietu counters nie muszą nic wiedzieć o Swingu ani o klasie CounterApp. Jest to bardzo dobre rozwiązanie, ponieważ oddziela logikę liczników od interfejsu graficznego i pozwala łatwo wykorzystać je w innych aplikacjach.

## Zad 4

```java

package counters;

import java.util.ArrayList;
import java.util.List;

public abstract class CounterDecorator implements Counter, CounterListener {
    protected Counter counter;
    private List<CounterListener> listeners = new ArrayList<>();

    public CounterDecorator(Counter counter) {
        this.counter = counter;
        this.counter.addListener(this);
    }

    @Override
    public void addListener(CounterListener listener) {
        listeners.add(listener);
    }

    @Override
    public void counterChanged(Counter source) {
        for (CounterListener listener : listeners) {
            listener.counterChanged(this);
        }
    }

    @Override
    public void increase() {
        counter.increase();
    }

    @Override
    public int currentValue() {
        return counter.currentValue();
    }

    @Override
    public void reset() {
        counter.reset();
    }

    @Override
    public String toString() {
        return counter.toString();
    }
}

```
```java

package counters;

import java.awt.Toolkit;

public class Beeping extends CounterDecorator {
    public Beeping(Counter counter) {
        super(counter);
    }

    @Override
    public void increase() {
        super.increase();
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public String toString() {
        return super.toString() + " (z dźwiękiem)";
    }
}


```

```java

package counters;

import javax.swing.Timer;

public class Ticking extends CounterDecorator {
    public Ticking(Counter counter) {
        super(counter);
        new Timer(1000, e -> increase()).start();
    }

    @Override
    public String toString() {
        return super.toString() + " (tykający)";
    }
}



```
Problem polegał na tym, że wcześniej dekorator przekazywał rejestrację słuchacza bezpośrednio do wewnętrznego licznika (np. SimpleCounter). W efekcie jako parametr source w metodzie counterChanged przychodził SimpleCounter, podczas gdy na liście w CounterApp wybrany był obiekt dekoratora

Bez wspólnej klasy abstrakcyjnej CounterDecorator cały kod przechowujący listę słuchaczy, przekazujący powiadomienia dalej oraz delegujący metody interfejsu Counter trzeba by powtórzyć w każdej klasie dekoratora.

## Zad 5

Pole counter w AbstractCounter     --- protected --- 
Musi być widoczne dla podklas liczników, ale ukryte przed klasami spoza hierarchii dziedziczenia. 

Lista słuchaczy w AbstractCounter  --- private  ---
Zarządzanie listą odbywa się wyłącznie wewnątrz AbstractCounter.

Metoda powiadamiająca słuchaczy    --- private  ---
Wywoływana jest tylko wewnątrz metod increase() i reset() w AbstractCounter.

Metoda step() w AbstractCounter    --- protected ---
Implementują ją podklasy liczników, ale nie powinna być wywoływana z zewnątrz.

Konstruktory i klasy dekoratorów   --- public ---
Obiekty dekoratorów są tworzone w klasie CounterApp znajdującej się w innym pakiecie


Po tych zmianach można napisać nową podklasę AbstractCounter w pakiecie app, ponieważ zarówno klasa bazowa, jak i jej pole counter oraz metoda step() są dostępne dla podklas z innych pakietów. Jest to dobre rozwiązanie, ponieważ pozwala rozszerzać program o nowe liczniki bez modyfikowania pakietu counters.

## Zad 6

```java

private void updateView() {
    Counter selected = (Counter) counters.getSelectedItem();
    int value = selected.currentValue();
    label.setText(String.valueOf(value));
    progressBar.setValue(value);
}


```
 Trzeba było zmienić tylko jedno miejsce w logice odświeżania widoku (updateView()). Gdyby aplikacja nadal odpytywała licznik po kliknięciu, zmianę trzeba by było wprowadzić w każdym miejscu obsługującym kliknięcia przycisków oraz zmianę wyboru na liście.

 ## Zad 7

 ```java

package app;

import counters.Counter;
import counters.CounterListener;
import javax.swing.DefaultComboBoxModel;

public class CounterListModel extends DefaultComboBoxModel<Counter> implements CounterListener {

    public CounterListModel(Counter[] items) {
        super(items);
        for (Counter item : items) {
            item.addListener(this);
        }
    }

    @Override
    public void counterChanged(Counter source) {
        int index = getIndexOf(source);
        if (index != -1) {
            fireContentsChanged(this, index, index);
        }
    }
}



 ```


 ```java


sequenceDiagram
    participant Timer
    participant Ticking
    participant SimpleCounter
    participant CounterListModel
    participant JComboBox

    Timer->>Ticking: actionPerformed()
    Ticking->>SimpleCounter: increase()
    SimpleCounter->>Ticking: counterChanged(SimpleCounter)
    Ticking->>CounterListModel: counterChanged(Ticking)
    CounterListModel->>JComboBox: fireContentsChanged(this, i, i)


 ```

 1.Klasa CounterListModel musi dziedziczyć po DefaultComboBoxModel, ponieważ metoda fireContentsChanged ma modyfikator protected — przy użyciu kompozycji nie moglibyśmy jej wywołać z pakietu app
 
 2.Gdyby dekoratory nadal podawały jako źródło powiadomienia opakowany licznik, metoda getIndexOf(source) nie znalazłaby go w modelu (zwróciłaby -1) i lista JComboBox nie przerysowałaby się dla liczników z dekoratorami.

 3.W łańcuchu powiadomień prowadzącym do przerysowania listy występują 3 obserwatory: Ticking (obserwuje SimpleCounter), CounterListModel (obserwuje liczniki na liście) oraz JComboBox (obserwuje swój model danych).




