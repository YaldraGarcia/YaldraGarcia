

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    public static void main(String[] args) {

        MarcoServidor mimarco = new MarcoServidor();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class MarcoServidor extends JFrame implements Runnable {

    public MarcoServidor() {

        setBounds(1200, 300, 280, 350);

        JPanel milamina = new JPanel();

        milamina.setLayout(new BorderLayout());

        areatexto = new JTextArea();

        milamina.add(areatexto, BorderLayout.CENTER);

        add(milamina);

        setVisible(true);
        Thread miHilo = new Thread(this);
        miHilo.start();

    }

    private JTextArea areatexto;

    @Override
    public void run() {
        try {
            ServerSocket servidor = new ServerSocket(9999);
            String nick, ip, mensaje;
            ArrayList<String> listaIp = new ArrayList<String>();
            PaqueteEnvio paqueteRecibido;

            while (true) {
                Socket miSocket = servidor.accept();

                ObjectInputStream paqueteDatos = new ObjectInputStream(miSocket.getInputStream());
                paqueteRecibido = (PaqueteEnvio) paqueteDatos.readObject();
                nick = paqueteRecibido.getNick();
                ip = paqueteRecibido.getIp();
                mensaje = paqueteRecibido.getMensaje();

                if (!mensaje.equals(" online")) {

                    areatexto.append("\n" + nick + ": " + mensaje + " para: " + ip);
                    Socket enviaDestinatario = new Socket(ip, 9090);
                    ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                    paqueteReenvio.writeObject(paqueteRecibido);
                    paqueteReenvio.close();
                    enviaDestinatario.close();
                    miSocket.close();
                } else {
                    //---------detecta online-------
                    InetAddress localizacion = miSocket.getInetAddress();
                    String IpRemota = localizacion.getHostAddress();
                    System.out.println("Online " + IpRemota);
                    listaIp.add(IpRemota);
                    paqueteRecibido.setIps(listaIp);

                    for (String z : listaIp) {
                        System.out.println("Array: " + z);

                        Socket enviaDestinatario = new Socket(z, 9090);
                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                        paqueteReenvio.writeObject(paqueteRecibido);
                        paqueteReenvio.close();
                        enviaDestinatario.close();
                        miSocket.close();

                    }

                    //------------------------------------   
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MarcoServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
