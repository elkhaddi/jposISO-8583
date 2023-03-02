package org.exemple;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.ISO87APackager;
import java.io.IOException;
public class ClientApp {
    public static void main(String[] args) throws ISOException, IOException {
        ASCIIChannel channel = new ASCIIChannel("localhost", 8000, new ISO87APackager());
        channel.connect();
        ISOMsg connectMsg = new ISOMsg();
        connectMsg.setMTI("1800");
        connectMsg.set(11, "123456"); // Numéro de référence
        connectMsg.set(33, "123456"); // Numéro de terminal
        channel.send(connectMsg);
        System.out.println("****************Response *********************");
        ISOMsg connectResp = channel.receive();
        if(connectResp.getMTI().equals("1810") && connectResp.getString(39).equals("00")){
            System.out.println("Connexion établie avec succès");
            // Créer un message de type 1200 pour effectuer une transaction
            ISOMsg transMsg = new ISOMsg();
            transMsg.setMTI("1200");
            transMsg.set(2, "1234567890123456"); // Numéro de carte
            transMsg.set(3, "000000"); // Code de transaction
            transMsg.set(4, "000000001000"); // Montant de la transaction
            transMsg.set(11, "123456"); // Numéro de référence
            transMsg.set(33, "123456"); // Numéro de terminal
            channel.send(transMsg);
            ISOMsg transResp = channel.receive();
            if(transResp.getMTI().equals("1210") && transResp.getString(39).equals("00")){
                System.out.println("Transaction effectuée avec succès");
            }
            else{
                System.out.println("Erreur lors de la transaction : " + transResp.getString(39));
            }
        }
        else{
            System.out.println("Erreur lors de la connexion : " + connectResp.getString(39));
        }
        channel.disconnect();
    }
}
