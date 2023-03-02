package org.exemple;
import org.jpos.iso.*;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.ISO87APackager;
import org.jpos.util.LogSource;
import org.jpos.util.Logger;

import java.io.IOException;

public class ServerApp implements ISORequestListener {

    public ServerApp(){super();}
    public static  void main (String[] args) {
        Logger logger = new Logger();
        ServerChannel channel = new ASCIIChannel("localhost",8000,new ISO87APackager());
        ((LogSource)channel).setLogger (logger, "channel");
        ISOServer isoServer = new ISOServer(8000,channel,null);
        isoServer.setLogger(logger,"server");
        isoServer.addISORequestListener(new ServerApp());
        System.out.println("Serveur démarré et en attente de connexions");
        new Thread(isoServer).start();
    }

    @Override
    public boolean process(ISOSource source, ISOMsg m) {
        try {
            if (m.getMTI().equals("1800")){
                System.out.println("Demande de connexion : " + m);
                ISOMsg connectResp = (ISOMsg) m.clone();
                connectResp.setMTI("1810"); // Set the response MTI
                connectResp.set(39, "00"); // Set the response code to "00"
                // Send the response message back to the client
                source.send(connectResp);
            }
            if (m.getMTI().equals("1200")){
                System.out.println("Demande de transaction : " + m);
                ISOMsg transResp = (ISOMsg) m.clone();
                transResp.setMTI("1210"); // Set the response MTI
                transResp.set(39, "00"); // Set the response code to "00"
                // Send the response message back to the client
                source.send(transResp);
            }
        } catch (ISOException | IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
