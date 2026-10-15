# Sprawozdanie lab03

Oliwia Dądela
54420
15.10.2026

## Zad 1

### SimpleCounter

```java

public class SimpleCounter implements Counter {
    int counter =0;

    public void increase() {

        ++counter;
    }
    public int currentValue(){
        return counter;
    }
    public void reset(){
      counter=0;
    }
    @Override
    public String toString(){return "Prosty Licznik";}
}

```

### DoubleCounter

```java

public class DoubleCounter extends SimpleCounter {
    @Override
    public void increase() {
        counter += 2;
    }
    @Override
    public String toString(){
        return "Licznik dwójkowy;";
    }
    }


### OverflowCounter

```java

public class OverflowCounter extends SimpleCounter {

    public void increase() {
        if(counter==10){
            counter=0;
        }
        else{
            ++counter;
        }
    }
    @Override
    public String toString(){
        return "Licznik do 10;";
    }
}

```

Lista liczników wygląda tak samo jak przed zmianami

## Zad 2

### SimpleCounter

```java
 public class SimpleCounter extends AbstractCounter {
    int counter =0;

    public void increase() {

        ++counter;
    }

}

```

### DoubleCounter

```java

public class DoubleCounter extends AbstractCounter {
    @Override
    public void increase() {
        counter += 2;
    }

    }

```

 ### OverflowCounter

 ```java
 public class OverflowCounter extends AbstractCounter {

    public void increase() {
        if (counter == 10) {
            counter = 0;
        } else {
            ++counter;
        }

    }
}

```

### AbstractCounter

```java
public abstract class AbstractCounter implements Counter {
    protected   int counter =0;

    @Override
    public int currentValue() {
        return counter;
    }
    @Override
    public void reset(){
        counter = 0;
    }
    @Override
    public abstract void increase();

    @Override
    public String toString() {
       return getClass().getSimpleName();
    }


}

```
## Zad 3

Pierwsza metoda wymaga mniej warstw abstrakcji, gdzie dopisujemy kolejną klasę rozszerzającą istniejący licznik, co może być korzystniejsze przy mniejszych projektach. Druga metoda zapewnia o wiele czystsze rozwiązanie, gdzie dodanie nowego licznika nie wymaga dziedziczenia po innej jego implementacji.

W pierwszym rozwiązaniu nazwy były wpisywane ręcznie w każdej klasie z osobna, natomiast w drugim zastosowane zostało automatyczne generowanie nazwy w klasie bazowej.

Podoba mi się drugie rozwiązanie wykorzystujące klasę AbstractCounter.

## Zad 4

