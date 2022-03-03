package Server;

import Objects.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ServerThread extends Thread {
    private Player player;
    private ArrayList<ServerThread> threadList; // list of all connections
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private boolean readedLines = false;
    private boolean ifStarted = false;
    private String fullLine = "empty";
    private boolean stopTheMenu = false;


    public ServerThread(Player player, ArrayList<ServerThread> threads, Socket socket) {
        this.player = player;
        this.threadList = threads;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // reading the input from Client
            input = new BufferedReader( new InputStreamReader(socket.getInputStream()));

            // returning the output to the client : true statement is to flush the buffer otherwise we have to do it manualy
            output = new PrintWriter(socket.getOutputStream(),true);

            String line = "empty";
            String temp = "";

            // inifite loop for server
            while(true) {
                line = input.readLine();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                temp = line + " " + timestamp;
                setFullLine(temp);

                if (player.getPause() != 3) {
                    // setting name for object `player`
                    if (!readedLines) {
                        player.setName(line);
                        readedLines = true;
                        output.println("Czesc " + player.getName() + "!");
                    }
                    if (getPlayerPause() == 4) {
                        output.println("Jestes zapauzowany! Poczekaj na swoja kolej!");
                    } else {
                        if (!stopTheMenu) {
                            if (player.getId() == 1) {
                                output.println("Jestes hostem gry! Napisz [START], aby rozpoczac gre: ");
                                line = input.readLine();
                                timestamp = new Timestamp(System.currentTimeMillis());

                                temp = line + " " + timestamp;
                                setFullLine(temp);

                                if (line.equals("START")) {
                                    //poczekaj az wszyscy gracze się połączą
                                    setIfStarted(true);
                                } else {
                                    output.println("Sprobuj ponownie! Napisz [Okej]");
                                }
                            } else {
                                output.println("Zostales polaczony do gry, poczekaj na start\n");
                            }
                        }
                    }
                }
                else {
                    output.println("Masz pauze! Poczekaj na nastepna runde");
                }
            }
        } catch (Exception e) {
            System.out.println("Gracz " + player.getName() + " się rozłączył.");
            player.setConnected(false);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                    player.getSocket().close();
                }
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    public void printToALlClients(String outputString) {
        for( ServerThread sT: threadList) {
            sT.output.println(outputString);
        }
    }

    public void printToOneClient(String outputString, ServerThread sT) {sT.output.println(outputString);}

    public boolean isIfStarted() {
        return ifStarted;
    }

    public void setIfStarted(boolean ifStarted) {
        this.ifStarted = ifStarted;
    }

    public String getFullLine() {
        return fullLine;
    }

    public void setFullLine(String fullLine) {
        this.fullLine = fullLine;
    }

    public boolean isStopTheMenu() {return stopTheMenu;}

    public void setStopTheMenu(boolean stopTheMenu) {
        this.stopTheMenu = stopTheMenu;
    }

    public void playerPoints(int points)
    {
        player.setPoints(player.getPoints()+ points);
        System.out.println("Gracz " + player.getName() + " punkty: " + player.getPoints());
    }

    public Player getPlayer(){
        return player;
    }

    public void playerPause(){
        player.setPause(player.getPause() + 1);
    }

    public void resetPlayerPause() { player.setPause(0); }

    public int getPlayerPause(){
        return player.getPause();
    }

    public void closeEverything() throws IOException {
        input.close();
    }

    public ArrayList<ServerThread> getThreadList() {
        return threadList;
    }

    public void setThreadList(ArrayList<ServerThread> threadList) {
        this.threadList = threadList;
    }
}