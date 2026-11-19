# Sprawozdanie 7
## Oliwia Dądela
## 54420

## Zad 1

```java

package objects;


public interface Expression {
    double evaluate();
}


```

```java

package objects;

public record Constant(double value) implements Expression {
    @Override
    public double evaluate() {
        return value;
    }
}

```


```java

package objects;

public record Product(Expression left, Expression right) implements Expression{
    @Override

    public double evaluate(){
        return left.evaluate() * right.evaluate();
    }

}

```
```java

package objects;


public record Sum(Expression left, Expression right) implements Expression {
    @Override
    public double evaluate() {
        return left.evaluate() + right.evaluate();
    }
}

```

```java

import objects.*;

public class Main {
    public static void main(String[] args) {


Expression e = new Product(new Sum(new Constant(2), new Constant(2)), new Constant(2));

System.out.println(e.evaluate());
System.out.println(e);

     }
    
    
    
    
    }

```

1.Tak, wartość jest zgodna z oczekiwaniami i wynosi 8.0, ponieważ wyrażenie reprezentuje działanie matematyczne (2+2)*2
Jako pierwsza zostaje wywołana metoda evaluate() na głównym, zewnętrznym obiekcie Product, a jako ostatnia evaluate() na prawym komponencie obiektu Product, czyli na zewnętrznym Constant(2).

2.Pochodzi z automatycznie generowanej metody toString(). Od razu widać dokładną strukturę drzewa obiektów, typy oraz nazwy pól składowych, ale jest mało czytelny z punktu widzenia matematyki. Zapis algebraiczny jest prostszy do czytania człowiekowi.

## Zad 2

1. Trzeba było zmienić 3 pliki (Constant, Product i Sum) oraz dopisać wypisanie wyniku na konsoli w klasie Main
2. Kod odpowiedzialny za zamianę wyrażenia na tekst (czyli implementacja metody show()) jest rozproszony pomiędzy wszystkie klasy (rekordy) implementujące interfejs Expression wewnątrz pakietu objects. W najgorszym przypadku trzeba obejrzeć wszystkie klasy implementujące Expression.
3. Tak, dałoby się to zrobić bez modyfikowania kodu tych klas, ale nie jako zwykłą metodę wywoływaną bezpośrednio na obiekcie, lecz jako zewnętrzną funkcję pomocniczą.

## Zad 3 

```java

package functions;

public class Expressions {

    public static double evaluateIf(Expression e) {
        if (e instanceof Constant c) {
            return c.value();
        } else if (e instanceof Sum s) {
            return evaluateIf(s.left()) + evaluateIf(s.right());
        } else if (e instanceof Product p) {
            return evaluateIf(p.left()) * evaluateIf(p.right());
        }
        throw new IllegalArgumentException("nieznane wyrażenie: " + e);
    }

    public static double evaluate(Expression e) {
        return switch (e) {
            case Constant(double value) -> value;
            case Sum(Expression left, Expression right) -> evaluate(left) + evaluate(right);
            case Product(Expression left, Expression right) -> evaluate(left) * evaluate(right);
        };
    }

    public static String show(Expression e) {
        return switch (e) {
            case Constant(double value) -> String.valueOf(value);
            case Sum(Expression left, Expression right) -> "(" + show(left) + " + " + show(right) + ")";
            case Product(Expression left, Expression right) -> "(" + show(left) + " * " + show(right) + ")";
        };
    }
}

```

```java

import functions.*;

public class Main {
    public static void main(String[] args) {
        Expression e = new Product(new Sum(new Constant(2), new Constant(2)), new Constant(2));

        System.out.println("evaluateIf: " + Expressions.evaluateIf(e));
        System.out.println("evaluate:   " + Expressions.evaluate(e));
        System.out.println("show:       " + Expressions.show(e));
    }
}


```


1. Cały kod obu operacji znajduje się w klasie Expressions (w metodach show oraz evaluate), podczas gdy same klasy danych (Constant, Sum, Product) są całkowicie puste i przechowują wyłącznie dane.
 W wersji z zadań 1 i 2 logika ta była rozproszona po wszystkich klasach danych, gdzie każda klasa sama wiedziała, jak się obliczyć i jak się wypisać.

 2. EvaluateIf wykorzystuje łańcuch instrukcji warunkowych if-else z operatorem instanceof i rzutowaniem zmiennej, natomiast evaluate korzysta z wyrażenia switch oraz dekonstrukcji wzorców rekordów.

 3. Zapieczętowanie interfejsu daje pełną kontrolę nad hierarchią dziedziczenia, gdzie autor kodu dokładnie określa zamknięty zbiór klas, które mogą implementować dany interfejs.
 Gdyby interfejs nie był zapieczętowany i ktoś dopisałby własną klasę program wywaliłby się w trakcie działania.


## Zad 4


```java

package functions;

public class Expressions {

    public static double evaluateIf(Expression e) {
        if (e instanceof Constant c) {
            return c.value();
        } else if (e instanceof Sum s) {
            return evaluateIf(s.left()) + evaluateIf(s.right());
        } else if (e instanceof Product p) {
            return evaluateIf(p.left()) * evaluateIf(p.right());
        } else if (e instanceof Negation n) {
            return -evaluateIf(n.operand());
        }
        throw new IllegalArgumentException("nieznane wyrażenie: " + e);
    }

    public static double evaluate(Expression e) {
        return switch (e) {
            case Constant(double value) -> value;
            case Sum(Expression left, Expression right) -> evaluate(left) + evaluate(right);
            case Product(Expression left, Expression right) -> evaluate(left) * evaluate(right);
            case Negation(Expression operand) -> -evaluate(operand);
        };
    }

    public static String show(Expression e) {
        return switch (e) {
            case Constant(double value) -> String.valueOf(value);
            case Sum(Expression left, Expression right) -> "(" + show(left) + " + " + show(right) + ")";
            case Product(Expression left, Expression right) -> "(" + show(left) + " * " + show(right) + ")";
            case Negation(Expression operand) -> "(-" + show(operand) + ")";
        };
    }

    public static String showPolish(Expression e) {
        return switch (e) {
            case Constant(double value) -> String.valueOf(value);
            case Sum(Expression left, Expression right) -> "+ " + showPolish(left) + " " + showPolish(right);
            case Product(Expression left, Expression right) -> "* " + showPolish(left) + " " + showPolish(right);
            case Negation(Expression operand) -> "~ " + showPolish(operand);
        };
    }
}


```

```java

public class Main {
    public static void main(String[] args) {
        // Test pakietu objects
        objects.Expression e1 = new objects.Negation(
            new objects.Product(new objects.Sum(new objects.Constant(2), new objects.Constant(2)), new objects.Constant(2))
        );
        System.out.println("OBJECTS:");
        System.out.println("Wartość: " + e1.evaluate());      // -8.0
        System.out.println("Zwykła:  " + e1.show());          // (-((2.0 + 2.0) * 2.0))
        System.out.println("Polska:  " + e1.showPolish());    // ~ * + 2.0 2.0 2.0

        // Test pakietu functions
        functions.Expression e2 = new functions.Negation(
            new functions.Product(new functions.Sum(new functions.Constant(2), new functions.Constant(2)), new functions.Constant(2))
        );
        System.out.println("\nFUNCTIONS:");
        System.out.println("Wartość (switch): " + functions.Expressions.evaluate(e2));   // -8.0
        System.out.println("Wartość (if):     " + functions.Expressions.evaluateIf(e2)); // -8.0
        System.out.println("Zwykła:           " + functions.Expressions.show(e2));       // (-((2.0 + 2.0) * 2.0))
        System.out.println("Polska:           " + functions.Expressions.showPolish(e2)); // ~ * + 2.0 2.0 2.0
    }
}

```

nowy rodzaj wyrażenia (negation) -1 nowy plik | 0 zmienionych - 1 nowy plik | 2 zmienione
nowa operacja (showPolish) - 0 nowych plików | 5 zmienionych - 0 nowych plików | 1 zmieniony

1. Wersja objects lepiej znosi dodawanie nowych rodzajów wyrażeń, ponieważ wystarczy utworzyć jeden nowy plik i zaimplementować wymagane metody bez modyfikacji istniejących klas, natomiast wersja functions lepiej znosi dodawanie nowych operacji, ponieważ całą logikę nowej funkcji dopisuje się w jednym miejscu.

2.Kompilator pomógł znaleźć wszystkie miejsca wymagające zmiany w obu wersjach (zarówno w objects przy implementacji interfejsu, jak i w functions przy wyrażeniach switch na interfejsie sealed).
Gdyby w wersji objects zapomnieć o zaimplementowaniu którejś metody w nowym rekordzie, program by się nie skompilował.
Gdyby zapomnieć o nowym przypadku w metodzie evaluateIf, program skompilowałby się poprawnie, ale podczas jego uruchomienia i napotkania nowego obiektu rzuciłby wyjątek IllegalArgumentException.

3. Bez modyfikacji plików biblioteki nowy rodzaj wyrażenia można dodać wyłącznie w wersji objects (implementując niezapieczętowany interfejs Expression), natomiast nową operację można dodać wyłącznie w wersji functions (tworząc własną zewnętrzną klasę ze statyczną metodą analizującą typ obiektu Expression).

## Zad 5

```java

package functions;

import java.lang.reflect.RecordComponent;

public class Expressions {

    // ... poprzednie metody (evaluateIf, evaluate, show, showPolish) ...

    public static String describe(Object o) throws ReflectiveOperationException {
        if (!o.getClass().isRecord()) {
            return String.valueOf(o);
        }
        StringBuilder result = new StringBuilder("(" + o.getClass().getSimpleName());
        for (RecordComponent component : o.getClass().getRecordComponents()) {
            Object value = component.getAccessor().invoke(o);
            result.append(" ").append(describe(value));
        }
        return result.append(")").toString();
    }
}

```

```java

import functions.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws ReflectiveOperationException {
        Expression e1 = new Product(new Sum(new Constant(2), new Constant(2)), new Constant(2));
        Expression e2 = new Negation(e1);

        System.out.println("describe(e1): " + Expressions.describe(e1));
        System.out.println("describe(e2): " + Expressions.describe(e2));
        System.out.println("Permitted:    " + Arrays.toString(Expression.class.getPermittedSubclasses()));
    }
}


```

1. Nie trzeba było zmieniać metody describe, ponieważ dzięki refleksji dynamicznie odczytuje ona nazwę i składowe dowolnego rekordu w czasie działania programu.
2. Metoda normalnie wypisze java.awt.Point[x=1,y=2] jako zwykłą klasę lub (Point 1 2), a kompilator przed tym nie chroni, ponieważ parametr metody ma najbardziej ogólny typ Object zamiast Expression.
3. Nie dałoby się napisać uniwersalnej metody evaluate samą refleksją, ponieważ refleksja ujawnia jedynie strukturę danych (nazwy klas i wartości pól), ale nie dostarcza informacji o semantyce, czyli jakie działanie matematyczne ma zostać wykonane dla danej klasy.
4. Tak, jest to wyjątek kontrolowany (checked exception, wymuszający obsługę lub deklarację throws), który mógłby zostać rzucony np. w przypadku próby wywołania prywatnej składowej rekordu spoza modułu bez uprawnień dostępu (IllegalAccessException) lub błędu wewnątrz samej metody dostępowej (InvocationTargetException).


## Zad 6

```java
import java.util.function.DoubleUnaryOperator;

public class Main {

    static void tabulate(DoubleUnaryOperator f) {
        for (int x = 0; x <= 5; x++) {
            System.out.println(x + " " + f.applyAsDouble(x));
        }
    }

    public static void main(String[] args) {
      
        DoubleUnaryOperator f1 = new Square();
        System.out.println("--- 1. Klasa Square (" + f1.getClass() + ") ---");
        tabulate(f1);

        
        DoubleUnaryOperator f2 = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double x) {
                return x * x;
            }
        };
        System.out.println("\n--- 2. Klasa anonimowa (" + f2.getClass() + ") ---");
        tabulate(f2);

        
        DoubleUnaryOperator f3 = (double x) -> { return x * x; };
        System.out.println("\n--- 3. Pełna lambda (" + f3.getClass() + ") ---");
        tabulate(f3);

    
        DoubleUnaryOperator f4 = x -> x * x;
        System.out.println("\n--- 4. Skrócona lambda (" + f4.getClass() + ") ---");
        tabulate(f4);

        
        System.out.println("\n--- 5. Referencja Math::sqrt ---");
        tabulate(Math::sqrt);

       
    }
}


```

Kompilator zgłasza błąd: incompatible types: TwoMethods is not a functional interface (multiple non-overriding abstract methods found in interface TwoMethods), informując, że interfejs z więcej niż jedną metodą abstrakcyjną nie jest interfejsem funkcyjnym i nie można go zapisać jako lambdy.

1. W zapisie 2 względem 1 pominięto nazwę nowej klasy i osobny plik, w zapisie 3 względem 2 pominięto konstruktor new DoubleUnaryOperator() oraz nagłówek metody public double applyAsDouble, w zapisie 4 względem 3 pominięto typ parametru double, nawiasy okrągłe, nawiasy klamrowe, słowo kluczowe return oraz średnik, a kompilator wie, jakiego interfejsu i jakiej metody dotyczy lambda w zapisie 4, dzięki wnioskowaniu typów z kontekstu wywołania metody tabulate(DoubleUnaryOperator f), ponieważ DoubleUnaryOperator ma tylko jedną metodę abstrakcyjną applyAsDouble(double x).

2. Wynik getClass() to odpowiednio class Square dla sposobu 1, class Main$1 dla sposobu 2 oraz dynamicznie generowane nazwy typu class Main$$Lambda/0x0000000100000c00 dla sposobów 3 i 4, co pokazuje, że z punktu widzenia Javy lambda jest obiektem implementującym interfejs funkcyjny, którego klasa jest tworzona dynamicznie w czasie działania programu przez maszynę wirtualną za pomocą instrukcji invokedynamic, a nie jako osobny plik .class podczas kompilacji.

3. Lambdy można użyć zamiast klasy anonimowej wyłącznie wtedy, gdy implementujemy interfejs funkcyjny, czyli interfejs posiadający dokładnie jedną metodę abstrakcyjną, natomiast nie można jej użyć, gdy rozszerzamy zwykłą lub abstrakcyjną klasę, gdy interfejs ma zero lub więcej niż jedną metodę abstrakcyjną, ani wtedy, gdy potrzebujemy zdefiniować własne pola stanu lub odwołać się do instancji obiektu za pomocą słowa kluczowego this.

4. Po zastąpieniu klasy anonimowej w klasie Ticking lambdą new Timer(1000, e -> increase()) program nadal działa dokładnie tak samo, ponieważ ActionListener jest interfejsem funkcyjnym z jedną metodą abstrakcyjną actionPerformed(ActionEvent e).

## Zad 7

```java


package functions;

import java.lang.reflect.RecordComponent;
import java.util.function.DoubleUnaryOperator;

public class Expressions {

    public static double evaluateIf(Expression e, double x) {
        if (e instanceof Constant c) {
            return c.value();
        } else if (e instanceof Variable) {
            return x;
        } else if (e instanceof Sum s) {
            return evaluateIf(s.left(), x) + evaluateIf(s.right(), x);
        } else if (e instanceof Product p) {
            return evaluateIf(p.left(), x) * evaluateIf(p.right(), x);
        } else if (e instanceof Negation n) {
            return -evaluateIf(n.operand(), x);
        }
        throw new IllegalArgumentException("nieznane wyrażenie: " + e);
    }

    public static double evaluate(Expression e, double x) {
        return switch (e) {
            case Constant(double value) -> value;
            case Variable() -> x;
            case Sum(Expression left, Expression right) -> evaluate(left, x) + evaluate(right, x);
            case Product(Expression left, Expression right) -> evaluate(left, x) * evaluate(right, x);
            case Negation(Expression operand) -> -evaluate(operand, x);
        };
    }

    public static DoubleUnaryOperator compile(Expression e) {
        return switch (e) {
            case Constant(double value) -> x -> value;
            case Variable() -> x -> x;
            case Sum(Expression left, Expression right) -> {
                DoubleUnaryOperator f = compile(left);
                DoubleUnaryOperator g = compile(right);
                yield new SumFunction(f, g);
            }
            case Product(Expression left, Expression right) -> {
                DoubleUnaryOperator f = compile(left);
                DoubleUnaryOperator g = compile(right);
                yield x -> f.applyAsDouble(x) * g.applyAsDouble(x);
            }
            case Negation(Expression operand) -> {
                DoubleUnaryOperator f = compile(operand);
                yield x -> -f.applyAsDouble(x);
            }
        };
    }

    public static String show(Expression e) {
        return switch (e) {
            case Constant(double value) -> String.valueOf(value);
            case Variable() -> "x";
            case Sum(Expression left, Expression right) -> "(" + show(left) + " + " + show(right) + ")";
            case Product(Expression left, Expression right) -> "(" + show(left) + " * " + show(right) + ")";
            case Negation(Expression operand) -> "(-" + show(operand) + ")";
        };
    }

    public static String showPolish(Expression e) {
        return switch (e) {
            case Constant(double value) -> String.valueOf(value);
            case Variable() -> "x";
            case Sum(Expression left, Expression right) -> "+ " + showPolish(left) + " " + showPolish(right);
            case Product(Expression left, Expression right) -> "* " + showPolish(left) + " " + showPolish(right);
            case Negation(Expression operand) -> "~ " + showPolish(operand);
        };
    }

    public static String describe(Object o) throws ReflectiveOperationException {
        if (!o.getClass().isRecord()) {
            return String.valueOf(o);
        }
        StringBuilder result = new StringBuilder("(" + o.getClass().getSimpleName());
        for (RecordComponent component : o.getClass().getRecordComponents()) {
            Object value = component.getAccessor().invoke(o);
            result.append(" ").append(describe(value));
        }
        return result.append(")").toString();
    }
}


```
```java

import functions.*;
import java.util.function.DoubleUnaryOperator;

public class Main {

    static void tabulate(DoubleUnaryOperator f) {
        for (int x = 0; x <= 5; x++) {
            System.out.println(x + " " + f.applyAsDouble(x));
        }
    }

    public static void main(String[] args) {
        // Wyrażenie: x * x + -x
        Expression expr = new Sum(
            new Product(new Variable(), new Variable()),
            new Negation(new Variable())
        );

        System.out.println("Wyrażenie: " + Expressions.show(expr));

        System.out.println("\n--- Wyniki tabulate(compile(expr)) ---");
        DoubleUnaryOperator compiled = Expressions.compile(expr);
        tabulate(compiled);

        System.out.println("\n--- Wyniki evaluate(expr, x) ---");
        for (int x = 0; x <= 5; x++) {
            System.out.println(x + " " + Expressions.evaluate(expr, x));
        }
    }
}


```

1. Klasa SumFunction musiała mieć dwa prywatne pola finalne typu DoubleUnaryOperator przechowujące funkcje f i g oraz dwuargumentowy konstruktor przypisujący te wartości do pól, co dowodzi, że lambda przechowuje przechwycone ze swojego otoczenia zmienne lokalne jako ukryte pola wygenerowanego obiektu, dzięki czemu pozostają one dostępne po zakończeniu działania metody compile.

2. Instrukcja switch w metodzie compile wykonuje się tylko podczas jednorazowego kompilowania drzewa wyrażenia, a następnie powstała funkcja jest wywoływana dla sześciu argumentów x bez ponownego sprawdzania typów węzłów, podczas gdy w metodzie evaluate wywołanej sześć razy instrukcja switch musi wykonać się 6 razy przy każdym pojedynczym obliczeniu, za każdym razem od nowa analizując strukturę drzewa.

3. Metody describe z zadania 5 nie trzeba było w ogóle zmieniać, ponieważ dzięki refleksji dynamicznie odczytuje ona nazwę klasy i pustą tablicę składowych rekordu Variable, natomiast metodę evaluateIf trzeba było rozbudować o obsługę przypadku Variable oraz przekazanie parametru x w wywołaniach rekurencyjnych.