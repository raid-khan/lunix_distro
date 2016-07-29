package se.kth.ics.pwnpr3d.layer2.network.networkInterfaces;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.network.NetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.*;

public class SessionLayerNetworkInterface extends NetworkInterface {

   protected IPEthernetNetworkInterface ipNetworkInterface;
   private SessionLayerImplementation sessionLayerImplementation;
   private ProtocolType protocolType;

   public SessionLayerNetworkInterface(String name, Asset superAsset, IPEthernetNetworkInterface ipNetworkInterface, ProtocolType protocolType, boolean isServer) {
      super(name, superAsset);
      this.ipNetworkInterface = ipNetworkInterface;
      this.protocolType = protocolType;
      // TODO !# Can we get rid of SessionLayerServer and SessionLayerClient and only retain SessionLayerImpl
      if (isServer)
         sessionLayerImplementation = new SessionLayerServer(name, superAsset, protocolType);
      else
         sessionLayerImplementation = new SessionLayerClient(name, superAsset, protocolType);
      addRequiredAgent(sessionLayerImplementation);
      own(sessionLayerImplementation);
      own(ipNetworkInterface);
   }

   // Wrap data in TCP, IP and then in Ethernet.
   @Override
   public Message newMessage(Data body) {
      Message sessionLayerMessage;
      if (protocolType == ProtocolType.TCP)
         sessionLayerMessage = new Message(body.getName(), this, sessionLayerImplementation.isEncrypted(), sessionLayerImplementation.getProtocol());
      else
         sessionLayerMessage = new Message(body.getName(), this, sessionLayerImplementation.isEncrypted(), sessionLayerImplementation.getProtocol());
      sessionLayerMessage.addBody(body);
      return ipNetworkInterface.newMessage(sessionLayerMessage);
   }

   @Override
   public void sendMessage(Message message) {
      addSentMessage(message);
      addOwnedData(message);
      if (!message.isEncrypted()) {
         getAdministrator().addAuthorizedWrite(message);
      }
      ipNetworkInterface.sendMessage(message);
   }

   @Override
   public void own(NetworkInterface protocolStack) {
      super.own(protocolStack);
      if (protocolStack instanceof IPEthernetNetworkInterface) {
         ((IPEthernetNetworkInterface) protocolStack).addParentInterface(this);
      }
   }

   public EthernetImplementation getEthernetImplementation() {
      return ipNetworkInterface.getEthernetImplementation();
   }

   // If the IP layer is not specified specifically, the lower one is assumed.
   public IPImplementation getIpImplementation() {
      return ipNetworkInterface.getIpImplementation();
   }

   public SessionLayerImplementation getSessionLayerImplementation() {
      return sessionLayerImplementation;
   }

   public Identity getIpAddress() {
      return ipNetworkInterface.getIpImplementation().getIpAddress();
   }

   public ProtocolType getProtocolType() {
      return protocolType;
   }

   public Identity getPortNumber() {
      return sessionLayerImplementation.getPortNumber();
   }

}
