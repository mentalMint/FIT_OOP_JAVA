package ru.nsu.fit.oop.chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private static final String POISON_PILL = "POISON_PILL";

    public Server() {
    }

    private void register(Selector selector, ServerSocketChannel serverSocket)
            throws IOException {

        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

    }

    private void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        if (new String(buffer.array()).trim().equals(POISON_PILL)) {
            client.close();
            System.out.println("Not accepting client messages anymore");
        } else {
            buffer.flip();
            client.write(buffer);
            buffer.clear();
        }
    }

    public void start() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", 5454));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                try {
                    if (key.isAcceptable()) {
                        register(selector, serverSocket);
                    }

                    if (key.isReadable()) {
                        SocketChannel clientSender = (SocketChannel) key.channel();
                        clientSender.read(buffer);
                        if (new String(buffer.array()).trim().equals(POISON_PILL)) {
                            clientSender.close();
                            key.cancel();
                            System.out.println("Not accepting client messages anymore");
                        } else {
                            buffer.flip();

                            selector.keys().forEach(selectionKey -> {
                                try {
                                    if (!selectionKey.isAcceptable()) {
                                        SocketChannel clientReceiver = (SocketChannel) selectionKey.channel();
                                        clientReceiver.write(buffer);
                                        buffer.rewind();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    try {
                                        clientSender.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                            buffer.clear();
                        }
                    }
                    iterator.remove();
                } catch (IOException e) {
                    key.cancel();
                }
            }
        }
    }
}
