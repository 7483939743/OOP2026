## Zad 1

```java
package counters;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TestSimpleCounter {

    @Test
    void startsAtZero() {
        assertEquals(0, new SimpleCounter().currentValue());
    }

    @Test
    void increasesThreeTimes() {
        SimpleCounter counter = new SimpleCounter();

        counter.increase();
        counter.increase();
        counter.increase();

        assertEquals(3, counter.currentValue());
    }

    @Test
    void resetsToZero() {
        SimpleCounter counter = new SimpleCounter();

        counter.increase();
        counter.reset();

        assertEquals(0, counter.currentValue());
    }
}
```

W metodzie "increase()" zostało zmienione:

```java
++counter;
```


```java
counter += 2;
```

Wynik:

```text
expected: <3> but was: <6>
```

Test wykrył nieprawidłowe zwiększanie licznika.

1.Testy korzystają z publicznych metod klasy zamiast bezpośrednio z pola "counter". W innym pakiecie również mogłyby się skompilować ale bez dostępu do pola "counter".

2.Testy powinny sprawdzać działanie przez publiczny interfejs, zamiast wewnętrznych szczegółów klasy, ponieważ zmiana implementacji nie wymaga zmiany testów, jeśli zachowane zostanie działanie metod.

## Zad 2

```java
package counters;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TestOverflowCounter {

    @Test
    void startsAtZero() {
        assertEquals(0, new OverflowCounter().currentValue());
    }

    @Test
    void increasesCorrectly() {
        OverflowCounter counter = new OverflowCounter();

        counter.increase();
        counter.increase();
        counter.increase();

        assertEquals(3, counter.currentValue());
    }

    @Test
    void overflowsAfterTen() {
        OverflowCounter counter = new OverflowCounter();

        for (int i = 0; i < 10; i++) {
            counter.increase();
        }

        assertEquals(10, counter.currentValue());

        counter.increase();

        assertEquals(0, counter.currentValue());
    }
}
```

Sprawdzenie wersji A, B i C:

| Wersja |     Wynik    |                       Przyczyna                          |
|--------|--------------|----------------------------------------------------------|
|   A    |Nie przechodzi| Licznik zeruje się po osiągnięciu 9 zamiast 10.          |
|   B    |Przechodzi    | Poprawnie zwiększa licznik do 10, a następnie zeruje go. |
|   C    |Nie przechodzi| Licznik zeruje się po osiągnięciu wartości 9 zamiast 10. |

1.Testy wykryły błędy w wersjach A i C, a wersja B przeszła testy, ponieważ działa tak samo jak oryginalna metoda. Sprawdzenie wartości granicznej 10 pozwoliło wykryć nieprawidłowe przepełnienie.

2.Wersja B jest poprawna, ponieważ zwiększa licznik do 10, a przy następnym wywołaniu zeruje jego wartość.

## Zad 3

```java
package counters;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TestNotifications {

    static class TestListener implements CounterListener {
        int notifications = 0;
        Counter source;

        @Override
        public void valueChanged(Counter counter) {
            notifications++;
            source = counter;
        }
    }

    @Test
    void notifiesAfterIncreaseAndReset() {
        SimpleCounter counter = new SimpleCounter();
        TestListener listener = new TestListener();

        counter.addListener(listener);

        counter.increase();
        assertEquals(1, listener.notifications);

        counter.reset();
        assertEquals(2, listener.notifications);
    }

    @Test
    void decoratorNotifiesAsItself() {
        Beeping counter = new Beeping(new SimpleCounter());
        TestListener listener = new TestListener();

        counter.addListener(listener);
        counter.increase();

        assertSame(counter, listener.source);
    }

    @Test
    void nestedDecoratorsNotifyOnlyOnce() {
        Beeping counter =
            new Beeping(new Beeping(new DoubleCounter()));

        TestListener listener = new TestListener();
        counter.addListener(listener);

        counter.increase();

        assertEquals(1, listener.notifications);
    }
}
```
1.Użyto własnej klasy "TestListener", która implementuje interfejs "CounterListener". W przeciwieństwie do "CounterApp" nie wyświetla okna, a zlicza powiadomienia i zapamiętuje ich źródło, co jest w pełni wystarczające.

2.Tak, ponieważ automatyczne testy pozwalają szybko wykryć błędy w powiadomieniach bez konieczności uruchamiania całej aplikacji i ręcznego sprawdzania działania.

3.Komunikaty są generowane przez bibliotekę JUnit.

## Zad 4

```java
package counters;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TestLimitedCounter {

    @Test
    void countsUpToLimit() {
        Counter counter = new LimitedCounter(2);
        counter.increase();
        counter.increase();
        assertEquals(2, counter.currentValue());
    }

    @Test
    void refusesToExceedLimit() {
        Counter counter = new LimitedCounter(1);
        counter.increase();
        assertThrows(IllegalStateException.class,
                () -> counter.increase());
        assertEquals(1, counter.currentValue());
    }

    @Test
    void zeroLimitRefusesAtOnce() {
        Counter counter = new LimitedCounter(0);
        assertThrows(IllegalStateException.class,
                () -> counter.increase());
    }

    @Test
    void rejectsNegativeLimit() {
        assertThrows(IllegalArgumentException.class,
                () -> new LimitedCounter(-1));
    }

    @Test
    void resetAllowsCountingAgain() {
        Counter counter = new LimitedCounter(1);
        counter.increase();
        counter.reset();
        counter.increase();
        assertEquals(1, counter.currentValue());
    }

    @Test
    void notifiesListenerWhenLimitIsReached() {
        LimitedCounter counter = new LimitedCounter(1);
        TestListener listener = new TestListener();
        counter.addListener(listener);

        counter.increase();
        assertEquals(1, listener.notifications);

        assertThrows(IllegalStateException.class,
                () -> counter.increase());

        assertEquals(1, listener.notifications);
    }

    static class TestListener implements CounterListener {
        int notifications = 0;

        @Override
        public void valueChanged(Counter counter) {
            notifications++;
        }
    }
}
```
```java
package counters;

public class LimitedCounter extends AbstractCounter {

    private final int limit;

    public LimitedCounter(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException(
                    "Limit nie może być ujemny");
        }
        this.limit = limit;
    }

    @Override
    public void increase() {
        if (counter >= limit) {
            throw new IllegalStateException(
                    "Osiągnięto maksymalną wartość licznika");
        }

        ++counter;
        notifyListeners();
    }
}
```

1.Nie.

2.Nie, ponieważ interfejs "Counter" nie informuje o możliwości wystąpienia wyjątku, a klasy korzystające wyłącznie z tego interfejsu nie muszą wiedzieć, że zwiększenie może się nie udać. "LimitedCounter" może być problematyczny, ponieważ nie zachowuje się tak, jak użytkownik interfejsu mógłby oczekiwać.

3.Możliwe rozwiązania problemu:

- Zmiana zachowania "LimitedCounter", aby po osiągnięciu limitu nie zwiększał wartości i nie zgłaszał wyjątku. Wymaga zmiany tylko tej klasy, ale trzeba zmienić testy sprawdzające wyjątki.
- Zmiana interfejsu "Counter", aby informował o możliwości nieudanego zwiększenia. Wymaga dostosowania klas implementujących i korzystających z interfejsu.

## Zad 5

```java
package app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HistoryFile {

    public static void save(Path path, List<String> lines)
            throws IOException {
        Files.write(path, lines);
    }
}
```

```java
package app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class TestHistoryFile {

    @TempDir
    Path directory;

    @Test
    void savesHistoryToFile() throws IOException {
        Path file = directory.resolve("historia.txt");

        List<String> history = List.of(
                "Odczyt 1",
                "Odczyt 2"
        );

        HistoryFile.save(file, history);

        assertEquals(history, Files.readAllLines(file));
    }

    @Test
    void throwsExceptionForMissingDirectory() {
        Path file = directory.resolve(
                "nie-ma-takiego-katalogu/historia.txt");

        assertThrows(IOException.class,
                () -> HistoryFile.save(file, List.of("Odczyt")));
    }
}
```

```java
try {
    HistoryFile.save(
        Path.of("historia.txt"),
        Collections.list(history.elements())
    );
    JOptionPane.showMessageDialog(frame,
            "Historia została zapisana.");
} catch (IOException e) {
    JOptionPane.showMessageDialog(frame,
            "Błąd zapisu: " + e.getMessage());
}
```

1."IOException" jest wyjątkiem kontrolowanym (checked), ponieważ błędy wejścia i wyjścia mogą wystąpić niezależnie od poprawności kodu."IllegalStateException" i "IllegalArgumentException" są wyjątkami niekontrolowanymi (unchecked), które zazwyczaj wynikają z błędnego użycia programu.

2.Klasa "HistoryFile" nie powinna samodzielnie obsługiwać tego wyjątku, ponieważ nie wie jak poinformować użytkownika o błędzie. 

3.Tak.

## Zad 6

```java
@Test
void tickingCounterCountsThreeTimes() throws InterruptedException {
    Counter counter = new Ticking(new SimpleCounter());

    Thread.sleep(3000);

    assertEquals(3, counter.currentValue());
}
```
```java

package counters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TestTickingCounter {

    @Test
    void tickingCounterCountsThreeTimes() throws InterruptedException {
        Counter counter = new Ticking(new SimpleCounter());

        Thread.sleep(3000);

        assertEquals(3, counter.currentValue());
    }
}

```

1.Test nie zawsze musi dawać ten sam wynik i działa z opóźnieniem, przez co test może być niestabilny.

2.Należałoby oddzielić odmierzanie czasu od samego licznika. Można przekazać mechanizm tykania do konstruktora klasy "Ticking", wtedy w teście można użyć zamiennika, który ręcznie wywołuje tyknięcia.

3.Nie było słychać dźwięku, ponieważ użyto własnej klasy TestListener, która tylko zlicza powiadomienia i zapamiętuje ich źródło. 
Gdyby dekorator zamiast dźwięku wysyłał SMS test mógłby wysyłać prawdziwe wiadomości.

4.Nieprzetestowane mogą być obsługa przycisków, wyświetlanie wartości licznika, zapisywanie historii oraz obsługa błędów. 

