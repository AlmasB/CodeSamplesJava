package com.almasb.common.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.almasb.common.net.DataPacket.Signature;


/**
 * A general tcp connection
 *
 * @author Almas
 * @version 1.0
 */
public abstract class TCPConnection extends SocketConnection {

    /**
     * The socket of remote end of the connection
     */
    protected Socket remoteSocket;

    /**
     * This connection's object input stream
     */
    protected ObjectInputStream ois;

    /**
     * This connection's object output stream
     */
    protected ObjectOutputStream oos;

    /**
     * Separate packet listener thread
     */
    protected TCPConnectionThread conn;

    /**
     * Default constructor with parser
     *
     * Subclasses must perform the actual binding
     *
     * @param parser
     *            packet parser
     */
    public TCPConnection(DataPacketParser parser) {
        super(parser);
    }

    /**
     * Initialises I/O streams and packet receiver thread Must be called from
     * subclasses' constructor after initialising {@link #remoteSocket}
     *
     * @throws IOException
     */
    public void init() throws IOException {
        oos = new ObjectOutputStream(remoteSocket.getOutputStream());
        ois = new ObjectInputStream(remoteSocket.getInputStream());
        conn = new TCPConnectionThread();
        new Thread(conn).start();
    }

    /**
     * Must be overriden to be signed
     */
    @Override
    public synchronized void send(DataPacket packet) throws IOException {
        oos.writeObject(packet);
        oos.flush();
    }

    OutputStream os;

    public void send(String s) throws IOException {
        os = remoteSocket.getOutputStream();
        byte[] b = s.getBytes();
        os.write(b);
        //os.flush();
    }

    /**
     * Completely closes this connection and its streams
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        conn.stop();
        ois.close();
        oos.close();
        remoteSocket.close();
    }

    /**
     * Class for constant listening for input from socket
     *
     * Runs in a separate thread
     *
     * TODO: handle exceptions better
     *
     * @author Almas
     * @version 1.0
     */
    class TCPConnectionThread implements Runnable {
        private boolean running = true;

        @Override
        public void run() {
            while (running) {
                try {
                    DataPacket packet = (DataPacket) ois.readObject();
                    if (packet.getSignature() == Signature.SERVER) {
                        parser.parseServerPacket(packet);
                    }
                    else if (packet.getSignature() == Signature.CLIENT) {
                        parser.parseClientPacket(packet);
                    }
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    stop();
                }
                catch (IOException e) {
                    if (!running)
                        System.out.println("Connection closed");
                    else {
                        System.out.println("I/O Error");
                        stop();
                    }
                }

                /*try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(remoteSocket.getInputStream()));
                    String line;

                    while (null != (line = br.readLine())) {
                        System.out.println(line);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        }

        public void stop() {
            running = false;
        }
    }
}
