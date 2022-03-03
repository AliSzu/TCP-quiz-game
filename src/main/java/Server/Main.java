package Server;

import Objects.Player;
import Objects.Question;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    static ArrayList<String> answers = new ArrayList<>();
    static ServerSocket server;

    public static void main(String[] args) {
        ServerSocket server = null;
        Scanner scanner = new Scanner(System.in);
        ArrayList<ServerThread> threadList = new ArrayList<>();
        int numerOfPlayers;

        System.out.println("Jest to quiz dla [2-4] graczy, prosze podaj ilosc graczy:");
        numerOfPlayers = scanner.nextInt();
        while(numerOfPlayers > 4 || numerOfPlayers < 2)
        {
            System.out.println("Proszę podać poprawną liczbę graczy");
            numerOfPlayers = scanner.nextInt();
        }

        try {
            server = new ServerSocket(1234);
            server.setReuseAddress(true);

            for (int i = 0; i < numerOfPlayers; i++) {
                // socket object to receive incoming client request
                Socket client = server.accept();

                // displaying the new client is connected to server
                System.out.println("Nowe polaczenie z portu: " + client.getPort());

                // create a new thread object
                ServerThread clientSock = new ServerThread(new Player(i + 1, client, 0, true), threadList, client);

                // information about all sockets connected
                threadList.add(clientSock);

                // this thread will handle the client separately
                new Thread(clientSock).start();
            }
            server.close();
            // WANNA PLAY A GAME?
            while (true) {
                if (threadList.get(0).isIfStarted() == true) {
                    for (ServerThread sT : threadList) {
                        sT.setStopTheMenu(true);
                    }
                    threadList.get(0).printToALlClients("Gra rozpoczyna sie!\n");
                    game(threadList, numerOfPlayers);
                    break;
                } else {
                    System.out.println("");
                }
            }

        } catch (IOException | InterruptedException | ParseException err) {
            err.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }
    }

    public static void game(ArrayList<ServerThread> threadList, int numOfPlay) throws InterruptedException, ParseException, FileNotFoundException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type questionListType = new TypeToken<ArrayList<Question>>() {
        }.getType();

        List<Question> questions = gson.fromJson(new FileReader("api.json"), questionListType);
        List<Integer> allRandoms = new ArrayList<>();

        int randomQuestion;

        for (int i = 0; i < 10; i++) {
            randomQuestion = ThreadLocalRandom.current().nextInt(1, questions.size() + 1);

            // to not repeat any question
            while (true) {
                if (allRandoms.contains(randomQuestion)) {
                    randomQuestion = ThreadLocalRandom.current().nextInt(1, questions.size() + 1);
                } else {
                    break;
                }
            }

            TimeUnit.SECONDS.sleep(2);

            allRandoms.add(randomQuestion);
            Question question = questions.get(randomQuestion - 1);
            String printQuestion = question.getId() + ". " + question.getContent();
            threadList.get(0).printToALlClients(printQuestion);

            // resetowanie inputu
            for (int j = 0; j < numOfPlay; j++) {
                threadList.get(j).setFullLine("blank");
            }

            long start = System.currentTimeMillis();
            long end = start + 5 * 1000;

            //zczytywanie w przeciągu 5 sekund
            while (System.currentTimeMillis() < end) {
                for (int j = 0; j < numOfPlay; j++) {
                    if (answers.size() != numOfPlay)
                        answers.add(j, threadList.get(j).getFullLine());
                    else
                        answers.set(j, threadList.get(j).getFullLine());
                    //szukanie czy ktoras z podanych odpowiedzi jest 'poprawna'
                    for (int m = 0; m < answers.size(); m++) {
                        if ((!answers.get(m).equals("blank"))) {
                            if (answers.get(m).substring(0, String.valueOf(question.getId()).length()).contains(String.valueOf(question.getId())) && threadList.get(m).getPlayerPause() != 3) {
                                if (answers.get(m).substring(String.valueOf(question.getId()).length() + 1, String.valueOf(question.getId()).length() + 4).contains(question.getAnswer())) {
                                    end = -1;
                                }
                            } else {
                                answers.set(m, "error");
                            }
                        }
                    }
                }
            }
            //sprawdzanie odpowiedzi, przyznawanie punktow etc
            for (int k = 0; k < answers.size(); k++) {
                if (!answers.get(k).equals("blank")) {
                    if (threadList.get(k).getPlayerPause() != 3) {
                        if (answers.get(k).equals("error")) {
                        threadList.get(k).printToOneClient("Twoja odpowiedź jest niezrozumiała. Proszę zwracać uwagę na numer podawanego ID pytania!", threadList.get(k));
                        threadList.get(k).playerPause();}
                        else if (answers.get(k).substring(String.valueOf(question.getId()).length() + 1, String.valueOf(question.getId()).length() + 4).contains(question.getAnswer())) {
                            threadList.get(k).printToOneClient("Gratulacje! Poprawna odpowiedź", threadList.get(k));
                            threadList.get(k).playerPoints(2);
                        } else {
                            if(answers.get(k).substring(String.valueOf(question.getId()).length() + 1, String.valueOf(question.getId()).length() + 4).contains("TAK") || answers.get(k).substring(String.valueOf(question.getId()).length() + 1, String.valueOf(question.getId()).length() + 4).contains("NIE")) {
                                threadList.get(k).printToOneClient("Nieprawidłowa odpowiedź", threadList.get(k));
                            }
                            else
                            {
                                threadList.get(k).printToOneClient("Twoja odpo", threadList.get(k));
                            }
                            threadList.get(k).playerPoints(-2);
                            threadList.get(k).playerPause();
                        }
                    } else {
                        threadList.get(k).resetPlayerPause();
                    }
                } else {
                    threadList.get(k).printToOneClient("Zero odpowiedzi - zero punktow :(", threadList.get(k));
                }
            }
            if (i < 9) {
                threadList.get(0).printToALlClients("Następne pytanie za chwilę...");
            }

            continue;
        }

    // 5 seconds of sleep
        TimeUnit.SECONDS.sleep(5);

    // LEADERBOARD
        threadList.get(0).printToALlClients("TABELA WYNIKOW");
        threadList.get(0).printToALlClients("==============");
        threadList.get(0).printToALlClients("Imie | Punkty");
        threadList.get(0).printToALlClients("==============");

    List<Player> players = new ArrayList<>();

        for (ServerThread serverThread : threadList) {
                players.add(serverThread.getPlayer());
    }

    List<Player> sortedPlayers = players.stream()
            .sorted(Comparator.comparing(Player::getPoints).reversed())
            .sorted(Comparator.comparing(Player::getStatus))
            .collect(Collectors.toList());

        for (int element = 0; element < sortedPlayers.size(); element++) {
        threadList.get(0).printToALlClients(sortedPlayers.get(element).getName() + " " + sortedPlayers.get(element).getStatus() + " | " + sortedPlayers.get(element).getPoints());}

        System.exit(-1);


    }
}


