package se.kth.ics.pwnpr3d.layer2.network.protocolImplementations;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer2.network.ProtocolImplementation;

// This is switched Ethernet, so the client communicates only with the switch.
// Eavesdropping is, for instance, not possible by default (requires, e.g. ARP spoofing).
public class EthernetImplementation extends ProtocolImplementation {

   public EthernetImplementation(String name, Asset superAsset) {
      super(name, superAsset, false, ProtocolType.Ethernet);
   }

}
