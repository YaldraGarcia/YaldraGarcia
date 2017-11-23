

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Cliente {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoCliente mimarco = new MarcoCliente();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}

class MarcoCliente extends JFrame {

    public MarcoCliente() {

        setBounds(600, 300, 280, 350);

        LaminaMarcoCliente milamina = new LaminaMarcoCliente();

        add(milamina);

        setVisible(true);

        addWindowListener(new EnvioOnline());
    }

}
//---------------envio de señal online----------

class EnvioOnline extends WindowAdapter {

    @Override
    public void windowOpened(WindowEvent e) {
        try {
            Socket misocket = new Socket("192.168.1.68", 9999);
            PaqueteEnvio datos = new PaqueteEnvio();
            datos.setMensaje(" online");
            ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
            paquete_datos.writeObject(datos);
            misocket.close();

        } catch (Exception e2) {

        }
    }
}
//-------------------------------------------------

class LaminaMarcoCliente extends JPanel implements Runnable {

    public LaminaMarcoCliente() {
        String nick_usuario = JOptionPane.showInputDialog("Nick: ");
        JLabel n_nick = new JLabel("Nick: ");
        add(n_nick);
        nick = new JLabel();
        nick.setText(nick_usuario);
        add(nick);
        JLabel texto = new JLabel("Online: ");
        add(texto);
        ip = new JComboBox();
        add(ip);
        campoChat = new JTextArea(12, 20);
        add(campoChat);
        campo1 = new JTextField(20);

        add(campo1);

        miboton = new JButton("Enviar");
        EnviaTexto miEvento = new EnviaTexto();
        miboton.addActionListener(miEvento);

        add(miboton);
        Thread miHilo = new Thread(this);
        miHilo.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket servidorCliente = new ServerSocket(9090);
            Socket cliente;
            PaqueteEnvio paqueteRecibido;
            while (true) {
                cliente = servidorCliente.accept();
                ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
                paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();

                if (!paqueteRecibido.getMensaje().equals(" online")) {
                    campoChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
                } else {
                    ArrayList<String> IpsMenu = new ArrayList<String>();
                    IpsMenu = paqueteRecibido.getIps();
                    ip.removeAllItems();
                    for (String z : IpsMenu) {
                        ip.addItem(z);
                    }

                }

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private class EnviaTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            campoChat.append("\n" + campo1.getText());
            try {
                Socket miSocket = new Socket("192.168.1.68", 9999);
                PaqueteEnvio datos = new PaqueteEnvio();
                datos.setNick(nick.getText());
                datos.setIp(ip.getSelectedItem().toString());
                datos.setMensaje(campo1.getText());
                ObjectOutputStream paqueteDatos = new ObjectOutputStream(miSocket.getOutputStream());
                paqueteDatos.writeObject(datos);
                miSocket.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
    private JTextField campo1;
    private JComboBox ip;
    private JLabel nick;
    private JTextArea campoChat;
    private JButton miboton;
}

class PaqueteEnvio implements Serializable {

    private String nick, ip, mensaje;
    private ArrayList<String> Ips;

    public ArrayList<String> getIps() {
        return Ips;
    }

    public void setIps(ArrayList<String> Ips) {
        this.Ips = Ips;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

}
