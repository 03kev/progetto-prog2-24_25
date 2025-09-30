# Mole

Questo repository contiene il **progetto d'esame** della sessione estiva
per l'insegnamento di "Programmazione II" all'a.a 2024/2025.

Obiettivo del progetto è realizzare una libreria per la gestione di
**dati tabellari** in grado di svolgere un sottoinsieme minimale delle
funzionalità di strumenti analoghi, come la libreria
[Pandas](https://pandas.pydata.org/) per Python, o
[Polars](https://docs.pola.rs/) per Rust.

Per portare a termine il lavoro dovrà decidere se e quali classi
(concrete o astratte) e quali interfacce implementare. Per ciascuna di
esse **dovrà descrivere** (in formato Javadoc attraverso commenti
presenti nel codice) le scelte relative alla **rappresentazione** dello
stato (con particolare riferimento all'**invariante di
rappresentazione** e alla **funzione di astrazione** così come definiti
nel libro di testo dell'insegnamento e illustrati a lezione) e ai
**metodi** (con particolare riferimento a *pre/post-condizioni* ed
*effetti collaterali*, soffermandosi ad illustrare le ragioni della
*correttezza* solo per le implementazioni che riterrà più critiche).
Osservi che l'esito di questa prova, che le consentirà di accedere o
meno all'orale, si baserà tanto su questa documentazione quanto sul
codice sorgente.

Può prendere visione dei dettagli tecnici riguardanti la realizzazione
del progetto nelle apposite [istruzioni](ISTRUZIONI.html). Osservi che
**la presenza di errori o fallimenti nella compilazione, generazione
della documentazione ed esecuzione dei test impedisce il superamento
dell'esame**.

> **Nota bene**: prenda *attentamente* visione della *checklist*
> presente nelle istruzioni, in modo da assicurarsi di aver completato
> tutti i punti richiesti. prima di consegnare. Non sarà consentita
> *nessuna eccezione* a tale regola.

## Descrizione delle entità coinvolte

Le entità coinvolte nel progetto sono: **indici**, **colonne** e
**tabelle**. Di seguito ne vengono illustrate le caratteristiche
principali (le informazioni che le descrivono e le competenze che
possiedono).

### Indice

Un **indice** è una entità immutabile data da sequenza di *etichette*
distinte, al fine di consentire la massima libertà implementativa è
conveniente rappresentare le etichette tramite `Object`. Un indice ha
una *lunghezza* <span class="arithmatex">\\\ell\> 0\\</span>
(corrispondente al numero delle sue etichette) e opzionalmente un
*nome*. Le operazioni fondamentali di un indice sono:

- determinare l'etichetta alla posizione
  <span class="arithmatex">\\i\\</span>-esima (per
  <span class="arithmatex">\\0\leq i\< \ell\\</span>);
- determinare la posizione di una etichetta
  <span class="arithmatex">\\e\\</span> (se presente) nell'indice
  (eventualmente indicando -1 se non presente);
- enumerare le proprie etichette.

> **Più formalmente**: nel seguito, useremo la notazione
> <span class="arithmatex">\\\mathcal{I}\_{\ell}\\</span> per denotare
> un indice di lunghezza <span class="arithmatex">\\\ell\\</span> e le
> notazioni <span class="arithmatex">\\\mathcal{I}\_{\ell}\[i\]\\</span>
> per indicare la prima operazione e
> <span class="arithmatex">\\\mathcal{I}\_{\ell}^{-1}\[e\]\\</span> per
> la seconda. Noti che, per ogni etichetta
> <span class="arithmatex">\\e\in \mathcal{I}\_{\ell}\\</span> e
> posizione <span class="arithmatex">\\0\leq i\<\ell\\</span> vale che
> <span class="arithmatex">\\\mathcal{I}\_{\ell}\[\mathcal{I}\_{\ell}^{-1}\[e\]\]\\</span>
> e
> <span class="arithmatex">\\\mathcal{I}\_{\ell}^{-1}\[\mathcal{I}\_{\ell}\[i\]\]\\</span>
> equivalgono entrambe alla funzione identità (la prima sul
> <span class="arithmatex">\\\\e\mid e\in \mathcal{I}\_{\ell}\\\\</span>
> e la seconda su <span class="arithmatex">\\\[0, \ell-1\]\subset
> \mathbb{N}\\</span>).

Un indice è in grado di *fondersi* con un altro indice, ossia di
produrre un nuovo indice contenente le etichette del primo seguite da
quelle del secondo che non sono contenute nel primo (attenzione però:
questa operazione non è però sempre possibile: potrebbe produrre un
indice con troppe etichette). Un indice è il *medesimo* di un altro se
(eventualmente con nomi diversi) hanno la stessa lunghezza e le
etichette ordinatamente uguali (secondo il metodo `equals`). Un indice
può produrre un indice a se medesimo, ma con un nuovo nome (questo è
utile per "rinominare" un indice, mantenendo la richiesta di
immutabilità).

#### Casi esemplari

Alcuni casi esemplari di indici possono essere costruiti a partire da
una *lista* di `String`, come ad esempio l'indice di nome "Giorni"

<div class="highlight">

       Giorni
    ---------
       Lunedì
      Martedì
    Mercoledì
      Giovedì
      Venerdì
       Sabato
     Domenica

</div>

Un altro esempio è dato dagli *indici numerici* costruiti a partire da
una *progressione aritmetica*, in cui le etichette sono `Integer`, come
ad esempio l'indice dei numeri dispari minori di 10:

<div class="highlight">

    Dispari
    -------
          1
          3
          5
          7
          9

</div>

la progressione aritmetica è completamente specificata dati il suo
valore *iniziale*, il *passo* e quello *finale* (che non appartiene alla
progressione); l'esempio precedente corrisponde alla terna 1, 10, 1.

### Colonna

Una **colonna** è una entità immutabile data da sequenza di *valori*, i
valori possono essere ad esempio numeri, booleani, stringhe o date (si
veda la sezione sui "Generici" per una discussione su come
rappresentarli). Una colonna ha un *numero di righe*
<span class="arithmatex">\\\rho \> 0\\</span> (corrispondente al numero
dei suoi valori), un *indice* (di lunghezza
<span class="arithmatex">\\\ell = \rho\\</span>) e opzionalmente un
*nome*. Le operazioni fondamentali di una colonna sono:

- determinare il valore alla posizione
  <span class="arithmatex">\\i\\</span>-esima (per
  <span class="arithmatex">\\0\leq i\<\rho\\</span>);
- determinare il valore corrispondente ad una etichetta
  <span class="arithmatex">\\e\\</span> dell'indice,
- enumerare i propri valori.

> **Più formalmente**: useremo la notazione
> <span class="arithmatex">\\C^\mathcal{I}\_{\rho}\\</span> per denotare
> una colonna di lunghezza <span class="arithmatex">\\\rho\\</span> con
> indice <span class="arithmatex">\\\mathcal{I}\_{\rho}\\</span> (dove
> nella notazione della colonna si è omessa la dimensione a pedice
> dell'indice in quanto identica a
> <span class="arithmatex">\\\rho\\</span>), useremo la notazione
> <span class="arithmatex">\\C^\mathcal{I}\_{\rho}\[i\]\\</span> e (con
> leggero abuso)
> <span class="arithmatex">\\C^\mathcal{I}\_{\rho}\[e\]\\</span> per
> indicare, rispettivamente, la prima e seconda operazione. Si osservi
> che se <span class="arithmatex">\\e\in \mathcal{I}\_{\rho}\\</span>,
> allora <span class="arithmatex">\\C^\mathcal{I}\_{\rho}\[e\] =
> C^\mathcal{I}\_{\rho}\[\mathcal{I}\_{\rho}^{-1}\[e\]\]\\</span>, fatto
> che mette formalmente in evidenza il ruolo dell'indice della colonna.

Una colonna può produrre una nuova colonna con i medesimi valori e un
nuovo indice, o nome (questo è utile per "cambiare" l'indice, o
"rinominare" la colonna, mantenendo la richiesta di immutabilità).

Una operazione più complessa è data dalla *reindicizzazione*: dato un
nuovo indice <span class="arithmatex">\\J\\</span>, una colonna
<span class="arithmatex">\\C\\</span> con indice
<span class="arithmatex">\\I\\</span> può produrre una nuova colonna
<span class="arithmatex">\\R\\</span> che abbia per numero di righe la
lunghezza di <span class="arithmatex">\\J\\</span> e tale che il valore
di <span class="arithmatex">\\R\\</span> in corrispondenza di una
etichetta di <span class="arithmatex">\\J\\</span> sia:

- il valore di <span class="arithmatex">\\C\\</span> in corrispondenza
  della stessa etichetta, se presente in
  <span class="arithmatex">\\I\\</span>, oppure
- il valore `null`.

Questo implica inoltre che il valore di
<span class="arithmatex">\\R\\</span> in corrispondenza della posizione
<span class="arithmatex">\\i\\</span> è pari al valore di
<span class="arithmatex">\\R\\</span> in corrispondenza dell'etichetta
in posizione <span class="arithmatex">\\i\\</span> di
<span class="arithmatex">\\J\\</span>.

> **Più formalmente**: data una colonna
> <span class="arithmatex">\\C^\mathcal{I}\_{\rho}\\</span> e un indice
> <span class="arithmatex">\\\mathcal{J}\_{\ell}\\</span> (eventualmente
> con <span class="arithmatex">\\\ell\neq\rho\\</span>), la
> reindicizzazione è la nuova colonna
> <span class="arithmatex">\\R^{\mathcal{J}}\_{\ell}\\</span> tale che
> \$\$ R^{\mathcal{J}}*{\ell}\[e\] = \begin{cases}
> C^{\mathcal{I}}*{\rho}\[e\] & \text{se } e\in \mathcal{I}*{\rho} \\
> \texttt{null} & \text{altrimenti}. \end{cases} \$\$ da questo deriva
> che, per <span class="arithmatex">\\i\in \[0, \ell-1\]\\</span> si ha
> \$\$ R^{\mathcal{J}}*{\ell}\[i\] = \begin{cases}
> C^{\mathcal{I}}*{\rho}\[
> \mathcal{I}*{\rho}^{-1}\[\mathcal{J}*{\ell}\[i\]\] \] & \text{se }
> i\in \mathcal{I}*{\rho}^{-1}\[\mathcal{J}\_{\ell}\] \\ \texttt{null} &
> \text{altrimenti}.
>
> \end{cases} \$\$

Si consideri ad esempio la colonna di nome "Lunghezza" con indice
"Giorni" in cui il valore corrispondente a ciascun giorno è il numero di
lettere che ne compongono il nome:

<div class="highlight">

       Giorni | Lunghezza
    ----------+----------
       Lunedì | 6        
      Martedì | 7        
    Mercoledì | 9        
      Giovedì | 7        
      Venerdì | 7        
       Sabato | 6        
     Domenica | 8        

</div>

dato l'indice "Fantasia":

<div class="highlight">

     Fantasia
    ---------
      Venerdì
       Luvedì
      Giovedì
      Marvedì
    Mercoledì
       Lunedì

</div>

la reindicizzazione della colonna "Lunghezza" con tale indice produce la
nuova colonna:

<div class="highlight">

     Fantasia | Lunghezza
    ----------+----------
      Venerdì | 7        
       Luvedì |          
      Giovedì | 7        
      Marvedì |          
    Mercoledì | 9        
       Lunedì | 6        

</div>

dove i valori corrispondenti a giorni di fantasia, assenti nella tabella
"Lunghezza", risultano `null` (valore visivamente rappresentato con una
stringa vuota).

### Tabella

Una **tabella** è una entità immutabile data da una sequenza di
<span class="arithmatex">\\\kappa \> 0\\</span> *colonne* con nomi
distinti che hanno il medesimo indice (e quindi lo stesso numero di
righe), per semplicità assumeremo che le colonne abbiano valori tutti
dello stesso tipo (o tutti sottotipi di un medesimo tipo — come nel caso
delle colonne, si veda la sezione sui "Generici" per una discussione più
approfondita). Oltre al *numero di colonne*
<span class="arithmatex">\\\kappa\\</span>, la tabella ha un *numero di
righe* e un *indice di riga* (che corrispondono ai rispettivi valori
della prima colonna), ma *non* ha un nome.

> **Più formalmente**: useremo la notazione
> <span class="arithmatex">\\T^\mathcal{I\_{\rho}}\_{\kappa}=
> T^\mathcal{I\_{\rho}}\_{\kappa}\langle C^{(0)}\_{\rho},
> C^{(1)}\_{\rho}, \ldots, C^{(\kappa-1)}\_{\rho} \rangle\\</span> per
> denotare una tabella con <span class="arithmatex">\\\kappa\\</span>
> colonne <span class="arithmatex">\\C^{(0)}\_{\rho}, C^{(1)}\_{\rho},
> \ldots, C^{(\kappa-1)}\_{\rho}\\</span> (si noti che in luogo degli
> indici delle colonne, viene indicato solo l'*indice di riga*
> <span class="arithmatex">\\\mathcal{I\_{\rho}}\\</span> medesimo di
> tutti quelli delle colonne. La tabella è una matrice
> <span class="arithmatex">\\\rho \times \kappa\\</span> di valori.

All'atto della creazione di una tabella, le colonne senza nome vengono
rinominate con un nome dato da `Column_` seguito dalla posizione
(numerata da zero) della colonna nella sequenza. In questo modo è
garantito che i nomi delle colonne siano distinti.

> **Più formalmente**: useremo la notazione
> <span class="arithmatex">\\\mathcal{H}\_{\kappa}\\</span> per denotare
> l'*indice di intestazioni di colonne*, altrimenti detto, se
> <span class="arithmatex">\\0\<j\leq \kappa\\</span>, allora
> <span class="arithmatex">\\\mathcal{H}\_{\kappa}\[j\]\\</span> è il
> nome della <span class="arithmatex">\\j\\</span>-esima colonna.

Le operazioni fondamentali di una tabella sono:

- determinare la colonna alla posizione
  <span class="arithmatex">\\j\\</span>-esima (con
  <span class="arithmatex">\\0\<j\leq\kappa\\</span>);
- determinare la colonna corrispondente all'etichetta
  <span class="arithmatex">\\e\\</span> dell'indice delle colonne;
- determinare il valore alla posizione
  <span class="arithmatex">\\i\\</span>-esima (con
  <span class="arithmatex">\\0\<i\leq \rho\\</span>) della
  <span class="arithmatex">\\j\\</span>-esima colonna (con
  <span class="arithmatex">\\0\<j\leq \kappa\\</span>);
- determinare il valore corrispondente alla riga data dall'etichetta
  <span class="arithmatex">\\e\in \mathcal{I}\_{\rho}\\</span> della
  colonna data dall'etichetta <span class="arithmatex">\\f\in
  \mathcal{H}\_{\kappa}\\</span>;
- enumerare le proprie colonne.

> **Più formalmente**: data la tabella
> <span class="arithmatex">\\T^\mathcal{I\_{\rho}}\_{\kappa}\langle
> C^{(0)}\_{\rho}, C^{(1)}\_{\rho}, \ldots, C^{(\kappa-1)}\_{\rho}
> \rangle\\</span> e seguendo la notazione usata per indici e colonne,
> avremo la seguente notazione per le prime quattro operazioni di cui
> sopra.
>
> - <span class="arithmatex">\\T^\mathcal{I\_{\rho}}\_{\kappa}\[j\] =
>   C^{(j)}\_{\rho}\\</span>
> - <span class="arithmatex">\\T^\mathcal{I\_{\rho}}\_{\kappa}\[f\] =
>   C^{(\mathcal{H}^{-1}\_{\kappa}\[f\])}\_{\rho}\\</span>
> - <span class="arithmatex">\\T^\mathcal{I\_{\rho}}\_{\kappa}\[i, j\] =
>   C^{(j)}\_{\rho}\[i\]\\</span>
> - <span class="arithmatex">\\T^\mathcal{I\_{\rho}}\_{\kappa}\[e, f\] =
>   C^{(\mathcal{H}^{-1}\_{\kappa}\[f\])}\_{\rho}\[\mathcal{H}^{-1}\_{\kappa}\[e\]\]\\</span>
>
> dove l'uguaglianza pone in relazione la notazione di tabella con
> quella di colonna.

Una tabella può produrre una nuova tabella con i medesimi valori e un
altro indice di riga, o di intestazioni di colonna (al solito, questo è
utile per "cambiare" gli indici, ossia le etichette di righe e colonne,
mantenendo la richiesta di immutabilità).

## Operazioni avanzate

### Impilare e affiancare

Talvolta può essere utile "unire" colonne, o tabelle. Il fatto che le
colonne abbiano una sola dimensione, mentre le tabelle due, rende
l'operazione sostanzialmente diversa nei due casi.

#### Impilare colonne

Nel caso delle colonne, l'unica possibilità è **impilare** (in
verticale) due colonne (i cui indici non abbiano etichette in comune)
ottenendo una nuova colonna che ha per indice e valori la concatenazione
rispettivamente dei due indici e valori. Occorre ovviamente fare un po'
di attenzione ai tipi: in genere è possibile impilare alla prima colonna
una i cui valori siano (al più) sottotipi del tipo dei valori della
prima.

Ad esempio, potrebbe essere utile impilare le due colonne

<div class="highlight">

      |          
    --+----------
    0 | Lunedì   
    1 | Martedì  
    2 | Mercoledì

</div>

e

<div class="highlight">

      |         
    --+---------
    0 | Giovedì 
    1 | Venerdì 
    2 | Sabato  
    3 | Domenica

</div>

occorre però notare che gli indici non sono distinti, pertanto dapprima
va sostituito l'indice della seconda, ad esempio con l'indice numerico
da 3 a 6, ottenendo la colonna

<div class="highlight">

      |         
    --+---------
    3 | Giovedì 
    4 | Venerdì 
    5 | Sabato  
    6 | Domenica

</div>

che è finalmente possibile impilare alla prima, ottenendo

<div class="highlight">

      |          
    --+----------
    0 | Lunedì   
    1 | Martedì  
    2 | Mercoledì
    3 | Giovedì  
    4 | Venerdì  
    5 | Sabato   
    6 | Domenica

</div>

#### Impilare e affiancare tabelle

Nel caso delle tabelle, è possibile sia porre una tabella "sotto" che "a
fianco" dell'altra. Ovviamente l'indice (di riga, o delle intestazioni
di colonna) che sarà concatenato non può contenere duplicati (detto
altrimenti, gli indici di riga, o intestazioni di colonna, non devono
avere etichette in comune); si potrebbe richiedere che l'altro indice
(di intestazioni di colonna, o riga), sia il medesimo nelle due tabelle,
ma questo è troppo restrittivo, è preferibile consentire che i due
indici si fondano.

In maggior dettaglio, si consideri dapprima il caso in cui due tabelle
(i cui indici di riga non devono avere etichette comuni) si **impilino
in verticale**. Gli indici delle intestazioni di colonna e di riga
risultanti sono dati dalla fusione dei rispettivi indici delle due
tabelle. Le colonne relative ad etichette presenti in una sola delle due
tabelle sono date dalla *reindicizzazione* rispetto ai nuovi indici,
mentre quelle relative a etichette presenti in entrambe le tabelle sono
ottenute *impilando* le colonne corrispondenti delle due tabelle. Si
considerino per esempio le tabelle:

<div class="highlight">

    zero | A | B
    -----+---+--
       a | 1 | x
       b | 2 | y

</div>

e

<div class="highlight">

    one | B | C
    ----+---+--
      c | 4 | i
      d | 5 | j

</div>

Per prima cosa, si osservi che gli indici di nome "zero" e "uno" non
hanno etichette in comune, inoltre le tabelle hanno tipo di dato
`Object` di cui sono sottotipi si gil `Integer` della colonna di nome
"A" che le `String` della colonna di nome "B". Il risultato che si
ottiene impilando in verticale alla prima la seconda è

<div class="highlight">

    zero | A | B | C
    -----+---+---+--
       a | 1 | x |  
       b | 2 | y |  
       c |   | 4 | i
       d |   | 5 | j

</div>

Si nota che i `null` sono in corrispondenza delle etichette di riga non
presenti negli indici delle colonne in cui appaiono. La richiesta che
gli indici di riga non abbiano etichette in comune è necessaria per
evitare la possibilità che in corrispondenza di una etichetta di riga ci
siano più valori per una certa colonna.

Passiamo ora al caso in cui due tabelle (i cui indici di intestazione di
colonna non devono avere etichette comuni) si **affianchino in
orizzontale**. Gli indici delle intestazioni di colonna e di riga
risultanti sono dati dalla fusione dei rispettivi indici delle due
tabelle. Le colonne sono date dalla *reindicizzazione* rispetto ai nuovi
indici. Si considerino per esempio le tabelle:

<div class="highlight">

    first | A | B
    ------+---+--
        a | 1 | x
        b | 2 | y
        c | 3 | z

</div>

e

<div class="highlight">

    second | C | D
    -------+---+--
         b | 4 | i
         c | 5 | j
         d | 6 | k

</div>

Il risultato che si ottiene impilando in orizzontale alla prima la
seconda è

<div class="highlight">

    first | A | B | C | D
    ------+---+---+---+--
        a | 1 | x |   |  
        b | 2 | y | 4 | i
        c | 3 | z | 5 | j
        d |   |   | 6 | k

</div>

### Trasformare

Talvolta è utile "trasformare" i dati in una colonna, o tabella. Questo
può avvenire in due modi:

- data una funzione <span class="arithmatex">\\f\\</span> che trasforma
  un valore in un altro, è possibile applicarla a tutti i valori di una
  colonna, o tabella, ottenendo una nuova colonna, o tabella;

- analogamente, data una funzione
  <span class="arithmatex">\\\phi\\</span> che trasforma una colonna in
  un'altra, è possibile applicarla a tutte le colonne di una tabella,
  ottenendo una nuova tabella.

#### I valori (nelle colonne e tabelle)

Per descrivere le **funzioni** in Java è molto comodo usare
l'interfaccia
[`Function`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html)
(o le sue varianti nel pacchetto
[`java.util.function`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/package-summary.html)).

Ad esempio, per tradurre le stringhe nella loro lunghezza si può usare
la seguente classe:

<div class="highlight">

    class StringaALunghezza implements Function<String, Integer> {
      @Override
      public Integer apply(String s) {
        return s.length();
      }
    }

</div>

che, applicata alla colonna

<div class="highlight">

      | Giorni   
    --+----------
    0 | Lunedì   
    1 | Martedì  
    2 | Mercoledì
    3 | Giovedì  
    4 | Venerdì  
    5 | Sabato   
    6 | Domenica 

</div>

produce la colonna

<div class="highlight">

      | Giorni
    --+-------
    0 | 6     
    1 | 7     
    2 | 9     
    3 | 7     
    4 | 7     
    5 | 6     
    6 | 8     

</div>

#### Le colonne (nelle tabelle)

Se invece volessimo fare un po' di statistica, potremmo usare (una volta
terminata l'implementazione) la seguente classe:

<div class="highlight">

        class MinMaxSum implements Function<Column<Integer>, Column<Integer>> {
          @Override
          public Column<Integer> apply(Column<Integer> i) {
            ...
          }
        }

</div>

che a partire dalla tabella

<div class="highlight">

      | C0  | C1 | C2 | C3  | C4
    --+-----+----+----+-----+---
    0 | 170 | 85 | 34 | 170 | 85
    1 | 100 | 50 | 20 | 100 | 50
    2 | 50  | 25 | 10 | 50  | 25
    3 | 20  | 10 | 4  | 20  | 10
    4 | 10  | 5  | 2  | 10  | 5 
    5 | 20  | 10 | 4  | 20  | 10
    6 | 50  | 25 | 10 | 50  | 25
    7 | 100 | 50 | 20 | 100 | 50

</div>

produce la tabella

<div class="highlight">

      | C0  | C1  | C2  | C3  | C4 
    --+-----+-----+-----+-----+----
    0 | 10  | 5   | 2   | 10  | 5  
    1 | 170 | 85  | 34  | 170 | 85 
    2 | 520 | 260 | 104 | 520 | 260

</div>

in cui ogni colonna è stata sostituita da una nuova colonna in cui
compaiono il suo minimo e massimo valore e la somma di tutti i valori.

> **Nota bene**: occorre fare attenzione però: la trasformazione non può
> limitarsi ad applicare la funzione, perché potrebbe produrre colonne
> con indici diversi o nomi ripetuti. Per questa ragione, la
> trasformazione deve curarsi di ripristinare i nomi originali e di
> imporre che tutte le colonne risultati abbiano l'indice della prima.

## Uso dei generici

Dato che le colonne possono contenere valori di tipo diverso, al fine di
favorire un migliore controllo dei tipi, piuttosto che sviluppare classi
indipendenti per ciascun tipo di dato (prive di una interfaccia comune)
o ancor peggio utilizzare solo `Object` come per gli indici, può avere
senso utilizzare il meccanismo dei generici.

Tramite essi sarebbe possibile costruire una bozza di *interfaccia* per
le colonne, come ad esempio:

<div class="highlight">

    public interface Column<V> extends Iterable<V> {
      ...
      V atPosition(int position);
      V atLabel(Object label);
      <U> Column<U> map(Function<V, U> func);
      ...
    }

</div>

così che, una volta estratti i valori, si possa operare su di essi a
seconda del tipo (ad esempio, effettuando operazioni aritmetiche sui
numeri, o manipolazioni specifiche sulle date ed orari); similmente, le
tabelle potrebbero soddisfare la seguente:

<div class="highlight">

    public interface Table<V> extends Iterable<Column<V>> {
      ...
      Column<V> columnAtPosition(int colPosition);
      Column<V> columnAtLabel(Object colLabel);
      V atPosition(int rowPosition, int colPosition);
      V atLabel(Object rowLabel, Object colLabel);
      <U> Table<U> map(Function<V,U> func);
      <U> Table<U> mapColumn(Function<Column<V>, Column<U>> func);
      ...
    }

</div>

> **Nota bene**: per eccesso di zelo si potrebbe assumere lo stesso
> comportamento anche per gli indici, arrivando ad avere un tipo
> generico `Index<I>` e quindi colonne e tabelle a due parametri, come
> `Column<I, V>` o `Column<Index<I>, V>`. Questo approccio è però
> **fortemente sconsigliato** perché complica inutilmente l'architettura
> del codice e le segnature dei vari metodi e costruttori.

Per l'indice è sufficiente usare i soli `Object` (dal momento che le
etichette, una volta estratte, non sono di norma soggette ad operazioni
che dipendano dal loro tipo).

<div class="highlight">

    public interface Index extends Iterable<Object> {
      ...
      int positionOf(Object label);
      Object labelAt(int position);
      ...
    }

</div>

Potrebbe essere opportuno consentire una certa libertà nei costruttori,
se `ColumnImpl` e `TableImpl` fossero classi che implementano
rispettivamente l'interfaccia delle colonne e tabelle, alcuni
costruttori potrebbe avere la segnatura

<div class="highlight">

    ColumnImpl(String name, Index index, List<? extends V> values)
    TableImpl(List<Column<? extends V>> columns)

</div>

piuttosto che la più elementare

<div class="highlight">

    ColumnImpl(String name, Index index, List<V> values)
    TableImpl(List<Column<V>> columns)

</div>

così come usare una segnatura più "ampie" per `map`, come ad esempio

<div class="highlight">

    <U> Column<U> map(Function<? super V, ? extends U> func)

</div>

Attenzione però: l'uso delle *bounded wildcard* è molto complicato ed è
**fortemente sconsigliato a chi ha poca esperienza** nella costruzione
di generici.

> **Nota bene**: osservi che **una soluzione semplice e funzionante è
> sempre preferibile ad una soluzione eccessivamente complicata e
> involuta**, che per giunta potrebbe presentare difetti di
> funzionamenti di cui potrebbe non rendersi conto (ma che molto
> probabilmente porterebbe al mancato superamento della prova, se
> presenti).
>
> **Nota bene**: le interfacce abbozzate in questa sezione hanno uno
> scopo esclusivamente esemplificativo, non sono da intendersi come una
> indicazione su come è necessario strutturare il codice; si osserva
> viceversa che *l'uso di interfacce, in presenza di una singola
> implementazione per ciascuna, è in generale sovrabbondante*
> caratteristica che, non plausibilmente giustificata, può essere
> valutata negativamente.

## Input/Output

I *client* richiedono di leggere e scrivere colonne e tabelle in formato
testuale; mentre generare l'output è elementare (a meno della
giustificazione a destra, o sinistra), la lettura è più complessa per
cui è provvisto del supporto che può liberamente (cioè non
necessariamente) usare.

### Output

L'unico output richiesto è di tipo testuale. Come ha notato in tutti gli
esempi, il comportamento è omogeneo e molto semplice:

- gli indici (se emessi non assieme a colonne, o tabelle) sono dati dal
  nome (eventualmente vuoto in caso di `null`) e dalle etichette,
  separate da una linea di `-` e giustificate a sinistra;

- le colonne hanno alla loro destra l'indice con le etichette
  giustificate a destra seguito dai valori giustificati a sinistra; tra
  i nomi di indice, colonna etichette e valori sono presenti gli
  opportuni separatori `|`, `-` e `+`;

- le tabelle hanno la prima colonna (stampata come sopra) seguita dalla
  giustapposizione delle altre colonne (prive di indice).

### Parsing

Leggere i dati di ingresso è più complesso. Per questa ragione è stata
approntata la class di support `utils.InputParsing` che contiene i
metodi

<div class="highlight">

      public static Descriptor parseDescriptor(String line) {
      public static Object[] parseValues(String line, int n) {

</div>

che, ricevuta una lina di testo contenente l'entità da analizzare,
riportano rispettivamente:

- un `Descriptor` che descrive cosa ci si appresta a leggere,
- un array di `Object` che contiene i valori letti (ciascuno con il tipo
  più appropriato tra `Boolean`, `Integer`, `Double`, `String` e
  `LocalDateTime`).

I descrittori sono una famiglia di `record` che analizzano una linea di
testo della forma:

<div class="highlight">

    #index[len]
    #index[len, name]
    #column[cols]
    #column[cols, type]
    #column[cols, type, name]
    #table[rows, cols]
    #table[rows, cols, type]

</div>

dove `len`, `rows` e `cols` sono interi positivi (che denotano,
rispettivamente, la lunghezza dell'indice, o il numero di colonne della
tabella o riga, e il numero di righe della tabella), `name` è una
stringa e `type` è una tra le stringhe: `string` `boolean` `number`,
`integer`, `double`, o `datetime`.

La funzione `parseDescriptor` restituisce un oggetto avente uno dei
seguenti tipi:

<div class="highlight">

    record IndexDescriptor(int len, String name) implements Descriptor;
    record ColumnDescriptor(int rows, Class<?> type, String name) implements Descriptor;
    record TableDescriptor(int rows, int cols, Class<?> type) implements Descriptor;

</div>

popolati con i dati presi dalla linea di testo, rimpiazzando i nomi non
presenti con `null` e i tipi non presenti con `Object`.

Usando opportunamente tali funzioni è abbastanza semplice leggere tutti
i file di test relativi ai vari *client*.

## Cosa è necessario implementare

Dovrà implementare una gerarchia di oggetti utili a:

- rappresentare le entità fondamentali **indice**, **colonna** e
  **tabella**;

- gestire (oltre alla costruzione delle entità) le competenze
  fondamentali come l'accesso ai valori e la fabbricazione di entità con
  nuovi nomi, o indici;

- gestire le operazioni avanzate di *impilamento*, *affiancamento* e
  *trasformazione* delle entità (limitatamente a quanto necessario per
  realizzare in modo ragionevole i *client*);

- implementare tutti i *client*, ossia le classi di test (come descritto
  di seguito).

Al fine di evitare confusione con i client (descritti nella prossima
sottosezione), è consigliabile che il suo codice sia contenuto in una
gerarchia di pacchetti, ad esempio nel pacchetto `mole`, i cui sorgenti
dovranno essere nella directory `src/main/java/mole`.

### Le classi client

Facendo uso delle classi della sua soluzione, dovrà quindi implementare
una serie di classi *client* secondo quanto illustrato nelle
[istruzioni](ISTRUZIONI.html) e seguendo attentamente le [specifiche del
docente](CLIENTS.html).

Si ricorda che **il progetto non sarà valutato a meno che tutti i test**
svolti tramite il comando `gradle test` che esercita tali client **diano
esito positivo**.

## Codice di condotta

Dovendo svolgere il progetto a casa non le vengono imposte particolari
restrizioni delle quali sarebbe peraltro difficile verificare il
rispetto.

Le è pertanto **consentito** di avvalersi:

- di qualunque risorsa disponibile in rete,
- di strumenti di supporto basati sull'AI (come [GitHub
  Copilot](https://github.com/features/copilot), o
  [ChatGPT](https://chat.openai.com/)),
- del *confronto* con altri studenti, o professionisti,

sia per la *progettazione* che per l'*implementazione* e
*documentazione* del codice. Ogni supporto che la aiuti a apprendere e
dominare gli obiettivi culturali dell'insegnamento è benvenuto!

D'altro canto le viene **formalmente richiesto di elencare** (nella
documentazione del codice) in modo chiaro ed esaustivo **ogni risorsa di
cui si è avvalso al di fuori di quelle esplicitamente indicate come
materiale didattico** dell'insegnamento. L'omissione di tale elenco può
costituire motivo di **respingimento** del progetto e, in gravi casi di
*plagio* alle **sanzioni disciplinari** previste.

Si sottolinea che consegnando il progetto lei dichiara di fatto di
esserne l'**unico autore**, assumendosi la piena responsabilità
dell'**originalità** del codice e della documentazione che esso include,
nonché della completezza e veridicità del suddetto elenco. Per questa
ragione **non le è consentito condividere il suo codice con altri
studenti**.

Durante la discussione orale, eventuali incertezze nell'*illustrare*,
*giustificare* o *modificare* il materiale consegnato non potranno che
essere a lei esclusivamente addebitate e, come tali, **valutate
negativamente**.

> **Nota bene**: la violazione del presente codice di condotta, qualora
> ne venga accertato il dolo, può condurre a **sanzioni disciplinari**
> come previste dal comma d) dell'art. 52 del [Regolamento generale
> d'Ateneo](https://www.unimi.it/sites/default/files/regolamenti/Regolamento%20generale%20d%27Ateneo_in%20vigore%20dal%202%20giugno%202020.pdf)

## Note legali e copyright

Ai sensi della Legge n. 633/1941 e successive modificazioni, l'autore si
riserva, in ogni forma e modo nei limiti fissati dalla legge, il diritto
esclusivo di pubblicare e di utilizzare il materiale contenuto nel
presente repository.

Più specificatamente, è fatto **divieto di riprodurre, trascrivere,
comunicare al pubblico, distribuire, tradurre, elaborare e modificare il
presente materiale** (codice sorgente compreso), in tutto o in parte,
senza specifica autorizzazione scritta dell'autore.
