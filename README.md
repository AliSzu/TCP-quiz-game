# TCP-quiz-game
Projekt wykonany razem z Dominiką Leśniewską (@kwasiarkowy) na zajęcia Sieci Komputerowe w semestrze zimowym 2021

### Zasady Gry
- Gra dla 2-4 osób,
- Każda gra zawiera zbiór 10 pytań losowanych z 20 pytań, na które należy odpowiedzieć TAK lub NIE
- Pierwszy podłączony gracz zarządza grą (tzn. może ją wystartować)
- W każdej z rund serwer rozsyła zapytania do graczy i czeka na tego, który da poprawną odpowiedź najszybciej
- Jeżeli w przeciągu 5 sekund nikt nie poda odpowiedzi to serwer przechodzi do następnej rundy
- Każda poprawna odpowiedź to 1 pkt, a błędna to minus 2 pkt
- Po 3 błędnych odpowiedziach z rzędu gracz pauzuje 1 rundę
- Jeżeli gra się rozpoczęła Serwer uniemożliwia dołączenie nowego gracza.

### Specyfikacja programu
- Program został wykonany, jak nazwa wskazuje, przy pomocy połączenia `TCP`
- Cała gra posiada dwa osobne programy, dla klienta i dla serwera osobno
- Aby kilka graczy podłączyło się i grało w tym samym czasie, zostały użyte wątki 
- Gra jest konsolowa i nie posiada GUI
