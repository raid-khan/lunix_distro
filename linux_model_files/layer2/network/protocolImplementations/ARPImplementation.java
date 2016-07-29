package se.kth.ics.pwnpr3d.layer2.network.protocolImplementations;

import se.kth.ics.pwnpr3d.datatypes.ImpactType;
import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer1.Vulnerability;
import se.kth.ics.pwnpr3d.layer2.network.ProtocolImplementation;
import se.kth.ics.pwnpr3d.util.Sampler;

public class ARPImplementation extends ProtocolImplementation {
   // ARP spoofing lets the attacker assume the administrative rights of the IP
   // implementation, and hence act in the name of that implementation, reading
   // and writing IP messages.
   private Vulnerability arpSpoofing = new Vulnerability("arpSpoofing", this, ImpactType.High);
   private boolean hasARPSpoofing;
   private Message arpSentBroadcast;
   private Message arpReceivedBroadcast;

   public ARPImplementation(String name, Asset superAsset, ProtocolImplementation ipImplementation,
           double probabilityOfARPSpoofing) {
      super(name, superAsset, false, ProtocolType.ARP);
      hasARPSpoofing = Sampler.bernoulliDistribution(probabilityOfARPSpoofing);
      if (hasARPSpoofing) {
         arpSpoofing.addSpoofedIdentity(ipImplementation.getAdministrator());
         // Successful ARp spoofing lets the attacker assume the administrative
         // rights of the IP implementation, and hence act in the name of that
         // implementation, reading and writing IP messages.
      }
   }

   @Override
   public void setSubLayerImplementation(ProtocolImplementation subLayerImplementation, boolean acceptingRemoteRequests) {
      super.setSubLayerImplementation(subLayerImplementation, acceptingRemoteRequests);
      if (hasARPSpoofing)
         subLayerImplementation.getGuest().addVulnerability(arpSpoofing);
   }

   public Vulnerability getArpSpoofing() {
      return arpSpoofing;
   }

   public Message getArpSentBroadcast() {
      return arpSentBroadcast;
   }

   public void setArpSentBroadcast(Message arpSentBroadcast) {
      this.arpSentBroadcast = arpSentBroadcast;
   }

   public Message getArpReceivedBroadcast() {
      return arpReceivedBroadcast;
   }

   public void setArpReceivedBroadcast(Message arpReceivedBroadcast) {
      this.arpReceivedBroadcast = arpReceivedBroadcast;
   }

}
