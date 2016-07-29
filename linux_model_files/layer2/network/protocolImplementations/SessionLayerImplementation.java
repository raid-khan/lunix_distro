package se.kth.ics.pwnpr3d.layer2.network.protocolImplementations;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer2.network.ProtocolImplementation;

public abstract class SessionLayerImplementation extends ProtocolImplementation {

   private Identity portNumber = new Identity("portNumber", this);

   protected SessionLayerImplementation(String name, Asset superAsset, ProtocolType protocolType) {
      super(name, superAsset, false, protocolType);
      getAdministrator().addGrantedIdentity(portNumber);
   }

   public abstract void setSubLayerImplementation(ProtocolImplementation subLayerImplementation);

   /**
    * Getters & Setters
    **/

   public Identity getPortNumber() {
      return portNumber;
   }

}
