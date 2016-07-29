package se.kth.ics.pwnpr3d.layer2.network;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.IPEthernetARPNetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.IPEthernetNetworkInterface;

abstract class IPNetworkMachine extends NetworkMachine {

   protected IPEthernetNetworkInterface ipEthernetNetworkInterface = new IPEthernetARPNetworkInterface("ipStack", this, 0);

   protected IPNetworkMachine(String name) {
      super(name);
      init();
   }

   protected IPNetworkMachine(String name, Asset superAsset) {
      super(name, superAsset);
      init();
   }

   private void init() {
      own(ipEthernetNetworkInterface);
      setPhysicalLayerImplementation(ipEthernetNetworkInterface.getEthernetImplementation());
   }

   @Override
   public void receiveMessage(Message message) {
      assert (message.getProtocol() == ProtocolType.Ethernet);
      boolean isDuplicated = false;
      if (getReceivedMessages().contains(message))
         isDuplicated = true;
      // If the Ethernet wrapper is new, but the IP message is old, it is
      // still a duplicate.
      for (Message receivedMessage : getReceivedMessages())
         if (receivedMessage.getBody().containsAll(message.getBody()))
            isDuplicated = true;
      if (!isDuplicated) {
         addReceivedMessage(message);
         addOwnedData(message);
         if (!message.isEncrypted()) {
            getAdministrator().addAuthorizedRead(message);
         }
         sendMessage(message);
      }
   }

   public Identity getIpAddress() {
      return getIpEthernetNetworkInterface().getIpAddress();
   }

   public IPEthernetNetworkInterface getIpEthernetNetworkInterface() {
      return ipEthernetNetworkInterface;
   }

}
