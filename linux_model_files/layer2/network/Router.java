package se.kth.ics.pwnpr3d.layer2.network;

import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.IPEthernetNetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.EthernetImplementation;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.IPImplementation;
import se.kth.ics.pwnpr3d.layer2.software.OperatingSystem;

// TODO !# Routers should run on computers with operating systems

// TODO Implement static routing.

public class Router extends IPNetworkMachine {

   public Router(String name) {
      super(name);
   }

   public Router(String name, Asset superAsset) {
      super(name, superAsset);
   }

   public void connect(OperatingSystem operatingSystem, EthernetSwitch ethernetSwitch) {
      connect(operatingSystem.getIPEthernetARPNetworkInterface(), ethernetSwitch);
   }

   public void connect(Router ipRouter) {
      ethernetConnect(ipRouter.getIpEthernetNetworkInterface().getEthernetImplementation());
      super.logicalConnect(ipRouter);
//      ipConnect(ipRouter.getIpEthernetNetworkInterface());
      ipConnect(ipRouter);
   }

   // TODO Eventually make this private
   public void connect(IPEthernetNetworkInterface ipEndpoint, EthernetSwitch ethernetSwitch) {
      super.logicalConnect(ethernetSwitch);
      ethernetConnect(ethernetSwitch.getEthernetImplementation());
      ipConnect(ipEndpoint);
   }

   private void ipConnect(IPEthernetNetworkInterface remoteIpEndpoint) {
      IPImplementation localIpImpl = ipEthernetNetworkInterface.getIpImplementation();
      IPImplementation remoteIpImpl = remoteIpEndpoint.getIpImplementation();

      remoteIpImpl.getAdministrator().addGrantedIdentity(localIpImpl.getGuest());
      localIpImpl.getGuest().addGrantedIdentity(remoteIpImpl.getGuest());
   }

   private void ipConnect(Router remoteRouter) {
      IPImplementation localIpImpl = ipEthernetNetworkInterface.getIpImplementation();
      IPImplementation remoteIpImpl = remoteRouter.getIpEthernetNetworkInterface().getIpImplementation();

      remoteIpImpl.getGuest().addGrantedIdentity(localIpImpl.getGuest());
      localIpImpl.getGuest().addGrantedIdentity(remoteIpImpl.getGuest());
   }

   private void ethernetConnect(EthernetImplementation remoteEthImpl) {

      EthernetImplementation localEthImpl = ipEthernetNetworkInterface.getEthernetImplementation();
      localEthImpl.getSuperLayerGuest().addGrantedIdentity(remoteEthImpl.getSuperLayerGuest());
      // used to have:
      // localEthImpl.getGuest().addGrantedIdentity(remoteEthImpl.getGuest());

      remoteEthImpl.getSuperLayerGuest().addGrantedIdentity(localEthImpl.getSuperLayerGuest());
   }

}
