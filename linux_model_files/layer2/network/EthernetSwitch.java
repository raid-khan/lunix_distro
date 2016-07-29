package se.kth.ics.pwnpr3d.layer2.network;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.IPEthernetNetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.EthernetImplementation;
import se.kth.ics.pwnpr3d.layer2.software.OperatingSystem;

// TODO !# Switches should run on computers with operating systems
// TODO !# Improve message switching algorithm. Currently, the switch acts as a hub spraying connected nodes with each message, but it should instead be more selective.

public class EthernetSwitch extends NetworkMachine {

   private EthernetImplementation ethernetImplementation = new EthernetImplementation("ethernetImplementation", this);

   public EthernetSwitch(String name) {
      super(name);
      init();
   }

   public EthernetSwitch(String name, Asset superAsset) {
      super(name, superAsset);
      init();
   }

   private void init() {
      own(ethernetImplementation);
      ethernetImplementation.getSuperLayerGuest().addGrantedIdentity(getGuest());
      getGuest().addGrantedIdentity(ethernetImplementation.getSuperLayerGuest());
      setPhysicalLayerImplementation(ethernetImplementation);
   }

   public void connect(OperatingSystem operatingSystem) {
      connect(operatingSystem.getIPEthernetARPNetworkInterface());
   }

   // TODO Eventually make this private
   public void connect(IPEthernetNetworkInterface ipEthernetNetworkInterface) {
      super.logicalConnect(ipEthernetNetworkInterface);
      ethernetConnect(ipEthernetNetworkInterface.getEthernetImplementation());
   }

   public void connect(NetworkMachine networkMachine) {
      super.logicalConnect(networkMachine);
      if (networkMachine.getPhysicalLayerImplementation() instanceof EthernetImplementation)
         ethernetConnect((EthernetImplementation) networkMachine.getPhysicalLayerImplementation());
      else {
         //TODO In the future, we could implement a network integrity checking system
         System.err.println("/!\\ Trying to connect two machines that don't share the same network physical layer /!\\");
      }
   }

   private void ethernetConnect(EthernetImplementation remoteEthernetImplementation) {
       // The remote ethernet writing port (admin) can write to the listening port (guest) of the local one
      remoteEthernetImplementation.getAdministrator().addGrantedIdentity(getEthernetImplementation().getGuest());
      remoteEthernetImplementation.getSuperLayerGuest().addGrantedIdentity(getEthernetImplementation().getSuperLayerGuest());

      getEthernetImplementation().getGuest().addGrantedIdentity(remoteEthernetImplementation.getGuest());
   }

   @Override
   public void receiveMessage(Message message) {
      assert (message.getProtocol() == ProtocolType.Ethernet);
      if (!getReceivedMessages().contains(message)) {
         addReceivedMessage(message);
         addOwnedData(message);
         if (!message.isEncrypted()) {
            getAdministrator().addAuthorizedRead(message);
         }
         sendMessage(message);
      }
   }

   // Setters & getters

   public EthernetImplementation getEthernetImplementation() {
      return ethernetImplementation;
   }

}
