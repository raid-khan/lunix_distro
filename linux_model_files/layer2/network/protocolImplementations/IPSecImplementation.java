package se.kth.ics.pwnpr3d.layer2.network.protocolImplementations;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer2.network.ProtocolImplementation;

public class IPSecImplementation extends ProtocolImplementation {

   public IPSecImplementation(String name, Asset superAsset) {
      // Always call the super.
      // This protocol is encrypted.
      super(name, superAsset, true, ProtocolType.IPSec);
   }
}
