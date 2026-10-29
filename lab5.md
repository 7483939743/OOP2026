# Zad 1
```java
public class Points {
    public static void main(String[] args) {
        
        System.out.println("--- Porównania podstawowe ---");
        System.out.println("new Point(1, 2) == new Point(1, 2) -> " + (new Point(1, 2) == new Point(1, 2)));
        System.out.println("new Point(1, 2).equals(new Point(1, 2)) -> " + new Point(1, 2).equals(new Point(1, 2)));
        
        System.out.println();

       
        List<Point> pointList = new ArrayList<>();
        Set<Point> pointSet = new HashSet<>();

        
        pointList.add(new Point(1, 2));
        pointList.add(new Point(1, 2));

        pointSet.add(new Point(1, 2));
        pointSet.add(new Point(1, 2));

        
        System.out.println("--- Rozmiary kolekcji ---");
        System.out.println("Rozmiar listy (ArrayList): " + pointList.size());
        System.out.println("Rozmiar zbioru (HashSet): " + pointSet.size());

        
        System.out.println("\n--- Metoda contains ---");
        Point searchPoint = new Point(1, 2);
        System.out.println("Lista contains(new Point(1, 2)) -> " + pointList.contains(searchPoint));
        System.out.println("Zbiór contains(new Point(1, 2)) -> " + pointSet.contains(searchPoint));
    }
}

```
1.Klasa Point dziedziczy metodę equals bezpośrednio od klasy java.lang.Object. Metoda porównuje tożsamość obiektów dokładnie tak jak operator "==", czyli sprawdza, czy obie referencje wskazują dokładnie na to samo miejsce w pamięci.
2.Nie

# Zad 2

```java

public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Point p && p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }
}

```
Kiedy sprawdzamy obecność elementu to HashSet najpierw wywołuje metodę hashCode(), aby szybko namierzyć odpowiedni element w swojej wewnętrznej strukturze. W jednym może znajdować się wiele obiektów o takim samym kodzie, więc zbiór musi przeprowadzić ich dokładniejszą weryfikację. Potem przechodzi przez zawarte w nim elementy i używa metody equals(), aby precyzyjnie porównać szukany obiekt z obiektami znajdującymi się w tym obszarze. Jeśli metoda equals zwróci true zbiór potwierdza obecność elementu zwracając true.

# Zad 3

```java

public class Point {
    final int x;
    final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point moved(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Point p && p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }
}

```
W klasie String problem rozwiązano w sposób taki, że obiekty tej klasy są całkowicie niezmienne, a metody takie jak toUpperCase() nie modyfikują istniejącego ciągu znaków w pamięci tylko alokują i zwracają nowy obiekt typu String, który zawiera wynik.

# Zad 4

Po wywołaniu new Range(3, 1) nastąpi natychmiastowe przerwanie działania programu i rzucenie wyjątku IllegalArgumentException, co jest wystarczające, ponieważ pola rekordu są stałe i nie można ich zmodyfikować po utworzeniu obiektu,a poprawność stanu zagwarantowana w konstruktorze jest niezmienna przez cały cykl życia obiektu.W zwykłej klasie z polami modyfikowalnymi stan obiektu mógłby zostać zepsuty w późniejszym czasie poprzez bezpośrednią modyfikację pól.

# Zad 5

```java

record Reading(Counter counter, int value) {
    @Override
    public String toString() {
        return counter.getLabel() + ": " + value;
    }
}

private final JButton rememberButton = new JButton("Zapamiętaj");
private final DefaultListModel<Reading> history = new DefaultListModel<>(); 
private final JList<Reading> historyList = new JList<>(history);

JPanel controls = new JPanel();   
controls.add(counters);
controls.add(button);
controls.add(resetButton);
controls.add(rememberButton);
controls.add(label);

frame.setLayout(new BorderLayout());
frame.add(controls, BorderLayout.NORTH);                       
frame.add(new JScrollPane(historyList), BorderLayout.CENTER);  


rememberButton.addActionListener(e -> {
    Counter selected = (Counter) counters.getSelectedItem();
    if (selected != null) {
        
        history.addElement(new Reading(selected, selected.currentValue()));
    }
});

```
1.Przechowywanie licznika zapisuje referencje do tego samego obiektu, a przechowywanie odczytu tworzy niezmienną wartość, która zatrzymuje tą od licznika w momencie kliknięcia przycisku.
2.Rekord Reading jest niezmienny ale pole counter przechowuje referencję do obiektu licznika.

