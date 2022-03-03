package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRunnable implements Runnable {

    private Socket socket;
    private BufferedReader input;
    private boolean isRunning;

    public ClientRunnable(Socket s) throws IOException {
        this.socket = s;
        this.input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
        this.isRunning = true;
    }

    @Override
    public void run() {

        try {
            while(true) {
                String response = input.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            System.out.print("Koniec quizu, dziękuję za udział w grze!");
        } finally {

            try {
                input.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("a");
            }

        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }
}
