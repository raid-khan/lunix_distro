package se.kth.ics.pwnpr3d.layer2.network.networkInterfaces;

import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.ARPImplementation;

// TODO !# Eventually remove this class. There should be a probabilistic distribution for whether the interface
// has a protocolImpl vulnerable to ARP Spoofing

public class IPEthernetARPNetworkInterface extends IPEthernetNetworkInterface {

   private ARPImplementation arpImplementation;

   public IPEthernetARPNetworkInterface(String name, Asset superAsset, double probabilityOfARPSpoofing) {
      super(name, superAsset);
      arpImplementation = new ARPImplementation("arpImplementation", this, getIpImplementation(),
              probabilityOfARPSpoofing);
      own(arpImplementation);
      arpImplementation.setSubLayerImplementation(getEthernetImplementation(), true);
      getIpAddress().addGrantedIdentity(arpImplementation.getSuperLayerGuest());
   }

   public ARPImplementation getArpImplementation() {
      return arpImplementation;
   }

}
