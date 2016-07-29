package se.kth.ics.pwnpr3d.layer2.network.protocolImplementations;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer2.network.ProtocolImplementation;

public class IPImplementation extends ProtocolImplementation {
   private Identity ipAddress = new Identity("ipAddress", this);

   public IPImplementation(String name, Asset superAsset) {
      super(name, superAsset, false, ProtocolType.IP);
      getAdministrator().addGrantedIdentity(ipAddress);

   }

   /**
    * Getters & Setters
    **/

   public Identity getIpAddress() {
      return ipAddress;
   }

}
