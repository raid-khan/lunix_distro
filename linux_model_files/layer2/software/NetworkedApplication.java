package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer1.Account;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.SessionLayerNetworkInterface;

public class NetworkedApplication extends Application {

   SessionLayerNetworkInterface sessionLayerInterface;

   public NetworkedApplication(String name, OperatingSystem superAsset, Account applicationIdentity) {
      super(name, superAsset, applicationIdentity);
   }

   public SessionLayerNetworkInterface newPort(OperatingSystem operatingSystem, String portName, boolean overIPSec, ProtocolType protocolType, boolean isServer) {
      assert (operatingSystem.getApplications().contains(this));
      // A sessionLI is created for each application. Plus it override the previous one (in case of several comms)
      // Shouldn't app share the same sessionLI and operate on different ports?
      sessionLayerInterface = new SessionLayerNetworkInterface(portName, operatingSystem, operatingSystem.getIPEthernetARPNetworkInterface(), ProtocolType.TCP, isServer);
      this.addRequiredAgent(sessionLayerInterface);
      if (overIPSec)
         sessionLayerInterface.getSessionLayerImplementation().setSubLayerImplementation(operatingSystem.getIpSecNetworkInterface().getIpImplementation());
      else
         sessionLayerInterface.getSessionLayerImplementation().setSubLayerImplementation(operatingSystem.getIPEthernetARPNetworkInterface().getIpImplementation());
      own(sessionLayerInterface);
      sessionLayerInterface.getGuest().addGrantedIdentity(getGuest());
      return sessionLayerInterface;
   }

   public Message newMessage(Data body) {
      return sessionLayerInterface.newMessage(body);
   }

   public void sendMessage(Message message) {
      sessionLayerInterface.sendMessage(message);
   }

   public void receiveMessage(Message message) {
      sessionLayerInterface.receiveMessage(message);
   }

   public SessionLayerNetworkInterface getSessionLayerNetworkInterface() {
      return sessionLayerInterface;
   }

   public Identity getPortNumber() {
      return sessionLayerInterface.getPortNumber();
   }

}
