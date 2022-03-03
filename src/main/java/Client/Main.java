package Client;

import Objects.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 1234)){
            // reading the input from server
            BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));

            // returning the output to the server : true statement is to flush the buffer otherwise
            // we have to do it manualy
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);

            // taking the user input
            Scanner scanner = new Scanner(System.in);
            String userInput;
            String clientName = "empty";
            Player player;

            ClientRunnable clientRun = new ClientRunnable(socket);

            new Thread(clientRun).start();


            // write rules of game
            Path file = Path.of("rules.txt");
            String rules = Files.readString(file, StandardCharsets.US_ASCII);
            System.out.println(rules);

            // loop closes when user enters exit command
            do {
                if (clientName.equals("empty")) {
                    System.out.print("Wprowadz imie: ");
                    userInput = scanner.nextLine();
                    clientName = userInput;
                    output.println(userInput);
                    if (userInput.equals("exit")) {
                        break;
                    }
                }
                else {
                    userInput = scanner.nextLine();
                    output.println(userInput);
                    if (userInput.equals("exit")) {
                        //reading the input from server
                        break;
                    }
                }


            } while (!userInput.equals("exit"));

        } catch (Exception e) {
            System.out.println("Serwer obecnie jest wyłączony lub nie przyjmuje już więcej graczy.");
        }
    }

}