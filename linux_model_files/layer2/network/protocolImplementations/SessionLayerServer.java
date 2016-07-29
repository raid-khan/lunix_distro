package se.kth.ics.pwnpr3d.layer2.network.protocolImplementations;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer2.network.ProtocolImplementation;

public class SessionLayerServer extends SessionLayerImplementation {

   public SessionLayerServer(String name, Asset superAsset, ProtocolType protocolType) {
      super(name, superAsset, protocolType);
      // Remote parties with ipUser identity also gain tcpUser identity.
   }

   @Override
   public void setSubLayerImplementation(ProtocolImplementation subLayerImplementation) {
      super.setSubLayerImplementation(subLayerImplementation, true);
      subLayerImplementation.getGuest().addGrantedIdentity(getGuest());
   }
}
