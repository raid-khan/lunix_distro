package se.kth.ics.pwnpr3d.layer2.network.protocolImplementations;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer2.network.ProtocolImplementation;

// TODO Inherit from NetworkInterface
public class SessionLayerClient extends SessionLayerImplementation {

   public SessionLayerClient(String name, Asset superAsset, ProtocolType protocolType) {
      super(name, superAsset, protocolType);
   }

   @Override
   public void setSubLayerImplementation(ProtocolImplementation subLayerImplementation) {
      super.setSubLayerImplementation(subLayerImplementation, false);
   }

   public void addServerIPAddress(Identity serverIPAddress) {
      serverIPAddress.addGrantedIdentity(getGuest());
   }
}
