package Objects;

import java.net.Socket;

public class Player{
    private int id;
    private Socket socket;
    private int points;
    private String name;
    private int pause = 0;
    private boolean isConnected;
    private String status;


    public Player(int id, Socket socket, int points, boolean isConnected) {
        this.id = id;
        this.socket = socket;
        this.points = points;
        this.isConnected = isConnected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPause() {
        return pause;
    }

    public void setPause(int pause) {
        this.pause = pause;
    }

    public boolean isConnected() {return isConnected;}

    public void setConnected(boolean connected) {isConnected = connected;}

    public String getStatus() {
        if(!isConnected)
        {
            return "(rozłączony)";
        }
        return status = "";
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
