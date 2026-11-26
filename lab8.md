# Sprawozdanie lab8

## Oliwia Dądela
## 54420

## Zad 1

Budowa programu io.c wraz z odpowiednimi fragmentami kodu:

1. Nagłówki, makra i stałe strumieni: dołączenie bibliotek, makra pomocnicze oraz wyliczenie standardowych deskryptorów UNIX-a.

```C


#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <string.h>

#define NELEMS(array) (sizeof(array)/sizeof(array[0]))
#define STRINGS_EQUAL(a, b) (!strcasecmp(a, b))
#define OUT(msg, ...) printf(msg"\n", ## __VA_ARGS__)

enum { STDIN = 0, STDOUT = 1, STDERR = 2 };
```

2. Definicja interfejsu i funkcje wywołujące: deklaracja typów wskaźników na funkcje, definicja struktury reprezentującej ogólny plik oraz funkcje IO_read i IO_write delegujące wywołanie przez wskaźnik zapisany w strukturze.

```C
typedef int (*ReadProc_t)(struct IO_struct *, char *, int);
typedef int (*WriteProc_t)(struct IO_struct *, const char *, int);

typedef struct IO_struct {
  ReadProc_t read;
  WriteProc_t write;
  void *data;
} IO_struct;

int IO_read(IO_struct *io, char *buffer, int maxlength) {
  return io->read(io, buffer, maxlength);
}
```
3. Konkretna implementacja urządzenia: funkcje realizujące odczyt ze standardowego wejścia i zapis na standardowe wyjście.

```C
int STDIO_read(IO_struct *io, char *output, int max_length) {
  return (int) read(STDIN, output, (size_t) max_length);
}

int STDIO_write(IO_struct *io, const char *input, int length) {
  return (int) write(STDOUT, input, length);
}

```
4. Rejestr urządzeń i otwieranie: statyczna tablica wiążąca nazwę tekstową z obiektem IO_struct oraz funkcja wyszukująca urządzenie po nazwie.

```C
struct {
  const char *name;
  IO_struct io;
} descriptors[] = {
  {
    .name = "stdio",
    .io = { .read = STDIO_read, .write = STDIO_write, .data = NULL }
  },
};

IO_struct *IO_open(const char *name) {
  for (int i = 0; i < NELEMS(descriptors); ++i) {
    if (STRINGS_EQUAL(descriptors[i].name, name)) {
      return &descriptors[i].io;
    }
  }
  return NULL;
}
```
5. Funkcja main i pętla główna: walidacja argumentów, otwarcie wskazanego urządzenia i nieskończona pętla przepisująca dane po 1 bajcie ze strumienia na ten sam strumień.

```C
IO_struct *io = IO_open(argv[1]);

while (true) {
  char c;
  int result = IO_read(io, &c, sizeof(c));
  if (result <= 0) {
    perror("read");
    break;
  }
  result = IO_write(io, &c, sizeof(c));
  if (result <= 0) {
    perror("write");
    break;
  }
}
```
interfejs - typ struktury IO_struct
klasa implementująca interfejs - zestaw konkretnych funkcji implementujących operacje danego urządzenia
obiekt - konkretny egzemplarz struktury IO_struct
pola obiektu - dane przechowywane pod adresem wskazywanym przez pole void
wywołanie metody przez referencję typu interfejsu - wywołanie funkcji przez wskaźnik zapisany w strukturze
this - pierwszy argument przekazywany jawnie do każdej funkcji implementującej
konstruktor - brak dedykowanej funkcji konstruktora w kodzie bazowym, czyli obiekt jest inicjalizowany statycznie przy definicji elementu tablicy descriptors

## Zad 2

1. Program przepisuje wprowadzany tekst na ekran zgodnie z przewidywaniami, ale po zakończeniu wejścia (Ctrl-D lub zakończeniu potoku) wypisuje na końcu komunikat:
"Plaintext read: Success", co jest mylące, ponieważ funkcja systemowa read w przypadku osiągnięcia końca pliku zwraca 0, czyli normalne zakończenie działania programu, a nie błąd.
2. Po uruchomieniu ./io dysk funkcja IO_open("dysk") zwraca NULL, lecz w main brakuje sprawdzenia tego wskaźnika. W rezultacie przy próbie odczytu program próbuje odwołać się do pola io->read spod adresu 0x0, co kończy się natychmiastową awarią procesu z błędem naruszenia ochrony pamięci. W Javie wywołanie metody na referencji null rzuciłoby wyjątek NullPointerException wraz z pełnym śladem stosu, dokładnym numerem linii i nazwą metody. Podejście z Javy jest lepsze dla programisty, ponieważ wskazuje miejsce i przyczynę błędu oraz pozwala go obsłużyć, podczas gdy Segmentation fault w C przerywa działanie programu bez żadnych informacji diagnostycznych.

```C
IO_struct *io = IO_open(argv[1]);
  if (io == NULL) {
    fprintf(stderr, "Błąd: nieznane urządzenie '%s'\n", argv[1]);
    return 1;
  }

  while (true) {
    char c;
    int result = IO_read(io, &c, sizeof(c));
    if (result < 0) {
      perror("read");
      break;
    }
    if (result == 0) {
      break;
    }
    result = IO_write(io, &c, sizeof(c));
    if (result <= 0) {
      perror("write");
      break;
    }
  }
  ```

  ## Zad 3

  ```C

  int NULL_read(IO_struct *io, char *output, int max_length) {
  return 0;
}

int NULL_write(IO_struct *io, const char *input, int length) {
  return length;
}

struct {
  const char *name;
  IO_struct io;
} descriptors[] = {
  {
    .name = "stdio",
    .io = {
      .read = STDIO_read,
      .write = STDIO_write,
      .data = NULL,
    }
  },
  {
    .name = "null",
    .io = {
      .read = NULL_read,
      .write = NULL_write,
      .data = NULL,
    }
  },
};

int main(int argc, const char *argv[]) {
  if (argc < 3) {
    OUT("Użycie: %s <źródło> <cel>", argv[0]);
    OUT();
    OUT("Dostępne urządzenia: ");
    for (int i = 0; i < NELEMS(descriptors); ++i) {
      OUT("\t%s", descriptors[i].name);
    }
    return -1;
  }

  IO_struct *src = IO_open(argv[1]);
  if (src == NULL) {
    fprintf(stderr, "Błąd: nieznane urządzenie źródłowe '%s'\n", argv[1]);
    return 1;
  }

  IO_struct *dst = IO_open(argv[2]);
  if (dst == NULL) {
    fprintf(stderr, "Błąd: nieznane urządzenie docelowe '%s'\n", argv[2]);
    return 1;
  }

  long total_bytes = 0;
  while (true) {
    char c;
    int result = IO_read(src, &c, sizeof(c));
    if (result < 0) {
      perror("read");
      break;
    }
    if (result == 0) {
      break;
    }
    result = IO_write(dst, &c, sizeof(c));
    if (result <= 0) {
      perror("write");
      break;
    }
    total_bytes += result;
  }

  fprintf(stderr, "Przepisano %ld bajtów.\n", total_bytes);
  return 0;
}
```

1. Nie, ponieważ pętla główna operuje wyłącznie na ogólnym interfejsie IO_struct poprzez funkcje IO_read i IO_write, nie wiedząc jaki konkretny typ urządzenia kryje się pod wskaźnikami. Aby dodać nowe urządzenie, wystarczyło napisać funkcje NULL_read i NULL_write oraz dopisać nowy element do tablicy descriptors.
2. W poleceniu cat > /dev/null powłoka otwiera plik /dev/null za pomocą wywołania systemowego open i przekierowuje na niego deskryptor STDOUT procesu cat, co wykonuje systemowe wywołania read i write w jądrze. Natomiast w ./io stdio null urządzenie null nie istnieje w systemie operacyjnym, a jego obsługa odbywa się w całości w przestrzeni użytkownika naszego procesu poprzez wywołanie zwykłej funkcji w C osobno dla każdego pojedynczego bajtu.

## Zad 4


```C
int RANDOM_read(IO_struct *io, char *output, int max_length) {
  unsigned int *seed = io->data;
  for (int i = 0; i < max_length; ++i) {
    *seed = (*seed) * 1103515245u + 12345u;
    output[i] = (char) ((*seed) >> 16);
  }
  return max_length;
}

int RANDOM_write(IO_struct *io, const char *input, int length) {
  unsigned int *seed = io->data;
  for (int i = 0; i < length; ++i) {
    *seed ^= ((unsigned int) (unsigned char) input[i]) << ((i % 4) * 8);
    *seed = (*seed) * 1103515245u + 12345u;
  }
  return length;
}

static unsigned int seed1 = 123456789u;
static unsigned int seed2 = 987654321u;
```

```C

{
    .name = "random",
    .io = {
      .read = RANDOM_read,
      .write = RANDOM_write,
      .data = &seed1,
    }
  },
  {
    .name = "random2",
    .io = {
      .read = RANDOM_read,
      .write = RANDOM_write,
      .data = &seed2,
    }
  },

```

1. Różnią się wyłącznie adresem zapisanym w polu data, gdzie random wskazuje na zmienną seed1, a random2 na seed2. W Javie odpowiada to dwóm osobnym obiektom tej samej klasy, gdzie każdy obiekt posiada własny zestaw pól instancyjnych ale wszystkie instancje współdzielą ten sam kod metod zdefiniowanych w klasie.
2. Odczyt lub zapis wykonany na urządzeniu random zmieniałby jednocześnie stan generatora dla random2. W Javie odpowiada to aliasingowi referencji lub użyciu pola statycznego w klasie, które jest jedno wspólne dla wszystkich obiektów tej klasy.
3. Program head -c 16 po wczytaniu dokładnie 16 bajtów ze standardowego wejścia zamyka swój deskryptor potoku i kończy działanie. Gdy nasz program ./io random stdio próbuje zapisać 17 bajt do potoku jądro systemu wysyła do procesu piszącego sygnał SIGPIPE.

## Zad 5

```C

#include <stdio.h>
#include <stdlib.h>

typedef struct Counter {
  void (*increment)(struct Counter *);
  int (*current_value)(struct Counter *);
  void (*reset)(struct Counter *);
  void *data;
} Counter;

void Simple_increment(Counter *c) { ++*(int *) c->data; }
int Simple_current_value(Counter *c) { return *(int *) c->data; }
void Simple_reset(Counter *c) { *(int *) c->data = 0; }

Counter *new_SimpleCounter(void) {
  Counter *c = malloc(sizeof(Counter));
  int *value = malloc(sizeof(int));
  *value = 0;
  *c = (Counter) {
    .increment = Simple_increment,
    .current_value = Simple_current_value,
    .reset = Simple_reset,
    .data = value,
  };
  return c;
}

typedef struct {
  int value;
  int limit;
} LimitedData;

void Limited_increment(Counter *c) {
  LimitedData *d = (LimitedData *) c->data;
  if (d->value < d->limit) {
    ++d->value;
  }
}

int Limited_current_value(Counter *c) {
  LimitedData *d = (LimitedData *) c->data;
  return d->value;
}

void Limited_reset(Counter *c) {
  LimitedData *d = (LimitedData *) c->data;
  d->value = 0;
}

Counter *new_LimitedCounter(int limit) {
  Counter *c = malloc(sizeof(Counter));
  LimitedData *d = malloc(sizeof(LimitedData));
  d->value = 0;
  d->limit = limit;
  *c = (Counter) {
    .increment = Limited_increment,
    .current_value = Limited_current_value,
    .reset = Limited_reset,
    .data = d,
  };
  return c;
}

void Beeping_increment(Counter *c) {
  Counter *inner = (Counter *) c->data;
  putchar('\a');
  fflush(stdout);
  inner->increment(inner);
}

int Beeping_current_value(Counter *c) {
  Counter *inner = (Counter *) c->data;
  return inner->current_value(inner);
}

void Beeping_reset(Counter *c) {
  Counter *inner = (Counter *) c->data;
  inner->reset(inner);
}

Counter *new_Beeping(Counter *inner) {
  Counter *c = malloc(sizeof(Counter));
  *c = (Counter) {
    .increment = Beeping_increment,
    .current_value = Beeping_current_value,
    .reset = Beeping_reset,
    .data = inner,
  };
  return c;
}

int main(void) {
  Counter *counters[] = {
    new_SimpleCounter(),
    new_LimitedCounter(3),
    new_Beeping(new_LimitedCounter(2)),
  };

  int n = sizeof(counters) / sizeof(counters[0]);
  for (int step = 0; step < 5; ++step) {
    for (int i = 0; i < n; ++i) {
      counters[i]->increment(counters[i]);
    }
  }

  printf("Wartości liczników po 5 zwiększeniach:\n");
  for (int i = 0; i < n; ++i) {
    printf("  counters[%d] = %d\n", i, counters[i]->current_value(counters[i]));
  }

  Counter *simple = new_SimpleCounter();
  Counter *limited = new_LimitedCounter(3);

  simple->data = limited->data;
  for (int i = 0; i < 10; ++i) {
    simple->increment(simple);
  }

  printf("Wartość limited po zwiększaniu przez simple: %d (limit wynosił 3)\n",
         limited->current_value(limited));

  return 0;
}


```

1. 
- Tablica metod wirtualnych
- Przekazywanie referencji this
- Alokacja pamięci obiektu
- Bezpieczeństwo typów i rzutowanie
- Hermetyzacja
- Zarządzanie pamięcią i sprawdzanie null

2. Kompilacja nie zgłosiła żadnego błędu ani ostrzeżenia, ponieważ oba pola data mają uniwersalny typ wskaźnika void *, dzięki czemu po przypisaniu simple->data = limited->data oba liczniki zaczęły wskazywać na tę samą strukturę LimitedData w pamięci. Wywołanie simple->increment uruchomiło funkcję Simple_increment, która zinterpretowała początek struktury LimitedData jako zwykły wskaźnik int * i zwiększyła pole value dziesięciokrotnie, całkowicie omijając sprawdzanie warunku d->value < d->limit, przez co limited->current_value wypisało wartość 10 pomimo ustawionego limitu równego 3. W Javie popełnienie takiego błędu uniemożliwiłyby hermetyzacja oraz typowanie statyczne.

3. W powyższym programie pamięć zaalokowana przez malloc nie została zwolniona z powodu braku wywołań funkcji free, co wywołało wycieki pamięci wewnątrz procesu, natomiast w Javie za automatyczne odnajdywanie i zwalnianie pamięci po nieużywanych obiektach odpowiada Garbage Collector będący częścią maszyny wirtualnej Javy.

## Zad 6

1. Zwracają dokładnie te same wartości, czyli funkcja odczytu zwraca 0, a funkcja zapisu zwraca liczbę przekazanych bajtów. Różnią się sygnaturami typów wymaganymi przez interfejs jądra Linuxa.
2. Naszej strukturze IO_struct odpowiada w jądrze struct file oraz const struct file_operations.
3. Struktura cookie_io_functions_t składa się z czterech wskaźników na funkcje: read, write, seek oraz close, podczas gdy nasza IO_struct posiada tylko dwa (read i write). Rolę naszego pola void *data pełni osobny parametr void *cookie przekazywany przy tworzeniu strumienia, który następnie biblioteka standardowa glibc przekazuje jako pierwszy argument do każdej z czterech funkcji zwrotnych.
4. Ręczna budowa obiektowości w C wymaga pisania powtarzalnego kodu szablonowego i jest podatna na groźne błędy. Dedykowana składnia w Javie pozwala kompilatorowi i maszynie wirtualnej automatycznie generować tablice metod wirtualnych, egzekwować hermetyzację, sprawdzać bezpieczeństwo typów w czasie kompilacji, rzucać czytelne wyjątki NullPointerException zamiast awarii Segmentation fault oraz automatycznie zwalniać pamięć przez Garbage Collector.
5. W naszych strukturach w C nie ma natywnego mechanizmu implementowania wielu interfejsów przez jeden obiekt, gdzie zmienna typu Counter * nie może zostać przekazana do funkcji oczekującej IO_struct *, nawet jeśli dany obiekt realizuje oba zestawy metod. Brakuje również wbudowanego dziedziczenia klas i nadpisywania metod oraz kontroli typu w czasie wykonania.