package ru.nsu.fit.web.TCPOverUDP;

import ru.nsu.fit.web.TCPOverUDP.TCP.socket.TCPSocket;
import ru.nsu.fit.web.TCPOverUDP.SR.ClientTCPSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) {
//        try (TCPSocket socket = new ClientTCPSocket(7000)) {
//            InetAddress address = InetAddress.getByName("127.0.0.1");
//            socket.connect(address, 8000);
//            String str = "You picked the wrong house, fool!";
//            socket.send(str.getBytes());
//            str = "Roger, roger, Roger, roger, Roger, roger, Roger, roger, Roger, roger";
//            socket.send(str.getBytes(StandardCharsets.UTF_8));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try (ClientTCPSocket socket = new ClientTCPSocket(7000)) {
            InetAddress address = InetAddress.getByName("127.0.0.1");
            socket.connect(address, 8000);
            byte[] buf;
            buf = socket.receive();
            System.err.println("Received message 1: " + new String(buf));
            buf = socket.receive();
            System.err.println("Received message 2: " + new String(buf));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
