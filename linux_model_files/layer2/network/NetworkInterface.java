package se.kth.ics.pwnpr3d.layer2.network;

import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Message;

public abstract class NetworkInterface extends NetworkMachine {

   public NetworkInterface(String name, Asset superAsset) {
      super(name, superAsset);
   }

   public void own(NetworkInterface protocolStack) {
      super.own(protocolStack);
      getGuest().addGrantedIdentity(protocolStack.getGuest());
   }

   public void own(ProtocolImplementation protocolImplementation) {
      super.own(protocolImplementation);
      getGuest().addGrantedIdentity(protocolImplementation.getSuperLayerGuest());
      protocolImplementation.getSuperLayerGuest().addGrantedIdentity(getGuest());
   }

   @Override
   public void receiveMessage(Message message) {
      if (!getReceivedMessages().contains(message)) {
         addReceivedMessage(message);
         addOwnedData(message);
         if (!message.isEncrypted()) {
            getAdministrator().addAuthorizedRead(message);
         }
      }
   }

}
