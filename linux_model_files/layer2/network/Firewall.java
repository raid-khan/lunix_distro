package se.kth.ics.pwnpr3d.layer2.network;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Message;

import java.util.HashSet;
import java.util.Set;

//TODO !# Firewalls should run on computers with operating systems

public class Firewall extends IPNetworkMachine {

   private Router        trustedIPRouter;
   private Router        untrustedIPRouter;
   private Set<Identity> permittedSources      = new HashSet<>();
   private Set<Identity> permittedDestinations = new HashSet<>();

   // TODO !# Complete implementation of TCP-based filtering. Eventually also DPI.
   // TODO !# Add IDS (or at least IPS).

   public Firewall(String name, Asset superAsset) {
      super(name, superAsset);
   }

   public Firewall(String name) {
      super(name);
   }

   public void connect(Router ipRouter, boolean trusted) {
      super.logicalConnect(ipRouter);
      ethernetConnect(ipRouter);
      ipConnect(ipRouter);
      if (trusted)
         trustedIPRouter = ipRouter;
      else
         untrustedIPRouter = ipRouter;
   }

   private void ipConnect(Router ipRouter) {
      Identity myIPSuperLayerGuest = ipEthernetNetworkInterface.getIpImplementation().getSuperLayerGuest();
      Identity routerIPSuperLayerGuest = ipRouter.getIpEthernetNetworkInterface().getIpImplementation().getSuperLayerGuest();
      routerIPSuperLayerGuest.addGrantedIdentity(myIPSuperLayerGuest);
   }

   private void ethernetConnect(Router ipRouter) {
      Identity routerEthernetSuperLayerGuest = ipRouter.getIpEthernetNetworkInterface().getIpImplementation().getSuperLayerGuest();
      Identity myEthernetSuperLayerGuest = ipEthernetNetworkInterface.getEthernetImplementation().getSuperLayerGuest();
      routerEthernetSuperLayerGuest.addGrantedIdentity(myEthernetSuperLayerGuest);
   }

   public void permit(Identity destinationIPAddress) {
      permittedDestinations.add(destinationIPAddress);
      Identity trustedRouterIPSuperLayerGuest = trustedIPRouter.getIpEthernetNetworkInterface().getIpImplementation().getSuperLayerGuest();
      Identity myIPSuperLayerGuest = ipEthernetNetworkInterface.getIpImplementation().getSuperLayerGuest();
      Identity destinationEthernetSuperLayerGuest = destinationIPAddress;
      myIPSuperLayerGuest.addGrantedIdentity(trustedRouterIPSuperLayerGuest);
      myIPSuperLayerGuest.addGrantedIdentity(destinationEthernetSuperLayerGuest);
   }

   public void permit(Identity sourceIPAddress, Identity destinationIPAddress) {
      permittedSources.add(sourceIPAddress);
      permittedDestinations.add(destinationIPAddress);
      Identity trustedRouterIPSuperLayerGuest = trustedIPRouter.getIpEthernetNetworkInterface().getIpImplementation().getSuperLayerGuest();
      sourceIPAddress.addGrantedIdentity(trustedRouterIPSuperLayerGuest);
      sourceIPAddress.addGrantedIdentity(destinationIPAddress);
   }

   public void permit(ProtocolType protocol, Identity sourceIPAddress, Identity sourcePort, Identity destinationIPAddress, Identity destinationPort) {
      permit(sourceIPAddress, destinationIPAddress);
   }

   @Override
   public void receiveMessage(Message message) {
      assert (message.getProtocol() == ProtocolType.Ethernet);
      boolean isDuplicated = false;
      if (getReceivedMessages().contains(message))
         isDuplicated = true;
      // If the Ethernet wrapper is new, but the IP message is old, it is
      // still a duplicate.
      for (Message receivedMessage : getReceivedMessages())
         if (receivedMessage.getBody().containsAll(message.getBody()))
            isDuplicated = true;
      if (!isDuplicated) {
         addReceivedMessage(message);
         addOwnedData(message);
         if (!message.isEncrypted()) {
            getAdministrator().addAuthorizedRead(message);
         }
         sendMessage(message);
      }
   }

   @Override
   public void sendMessage(Message message) {
      for (NetworkMachine connectedNetworkMachine : connectedNetworkMachines) {
         Message newMessage = message.copy();
         if (connectedNetworkMachine.equals(trustedIPRouter)) {
            newMessage.getTargets().retainAll(permittedDestinations);
         }
         if (!newMessage.getTargets().isEmpty() || connectedNetworkMachine.equals(untrustedIPRouter)) {
            addSentMessage(newMessage);
            addOwnedData(newMessage);
            if (!newMessage.isEncrypted()) {
               getAdministrator().addAuthorizedWrite(newMessage);
            }
            connectedNetworkMachine.receiveMessage(newMessage);
         }
      }
   }

   /**
    * Getters & Setters
    **/

   public Router getTrustedIPRouter() {
      return trustedIPRouter;
   }

   public Router getUntrustedIPRouter() {
      return untrustedIPRouter;
   }

   public Set<Identity> getPermittedSources() {
      return permittedSources;
   }

   public Set<Identity> getPermittedDestinations() {
      return permittedDestinations;
   }
}
