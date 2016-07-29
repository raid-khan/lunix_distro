package se.kth.ics.pwnpr3d.layer2.network;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Machine;

import java.util.HashSet;
import java.util.Set;

public abstract class ProtocolImplementation extends Machine {

   // The higher OSI layers require lower ones.
   public Set<ProtocolImplementation> connectedProtocolImplementation = new HashSet<>();
   protected Identity                 superLayerGuest                 = new Identity("superLayerGuest", this);

   // TODO !! Rename getAdministrator, getGuest and superLayerguest to something like
   // LocalParty, FullControlRemoteParty and LimitedControlRemoteParty.
   private ProtocolType               protocol;
   private boolean                    isEncrypted;

   protected ProtocolImplementation(String name, Asset superAsset, boolean isEncrypted, ProtocolType protocol) {
      super(name, superAsset);
      this.isEncrypted = isEncrypted;
      this.protocol = protocol;
      superLayerGuest.addAuthorizedAccess(this);
      getGuest().addGrantedIdentity(getSuperLayerGuest());
   }

   public void setSubLayerImplementation(ProtocolImplementation subLayerImplementation, boolean acceptingRemoteRequests) {
      getAdministrator().addGrantedIdentity(subLayerImplementation.getAdministrator());
      if (acceptingRemoteRequests && !subLayerImplementation.isEncrypted) {
         subLayerImplementation.getSuperLayerGuest().addGrantedIdentity(getSuperLayerGuest());
      }
   }

   public void addConnectedProtocolImplementation(ProtocolImplementation protocolImplementation) {
      connectedProtocolImplementation.add(protocolImplementation);
   }

   protected Set<ProtocolImplementation> getConnectedProtocolImplementations() {
      return connectedProtocolImplementation;
   }

   public Identity getSuperLayerGuest() {
      return superLayerGuest;
   }

   public ProtocolType getProtocol() {
      return protocol;
   }

   public boolean isEncrypted() {
      return isEncrypted;
   }

}
