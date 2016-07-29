package se.kth.ics.pwnpr3d.layer2.network.networkInterfaces;

import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.network.NetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.EthernetImplementation;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.IPImplementation;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.IPSecImplementation;

// IPSecNetworkInterface cannot extend from IPEthernetARPStack since it gets that from the OS.

public class IPSecNetworkInterface extends NetworkInterface {

   protected IPImplementation upperLayerIPImpl;
   protected IPSecImplementation        ipSecImplementation;
   protected IPEthernetNetworkInterface ipNetworkInterface;

   public IPSecNetworkInterface(String name, Asset superAsset, IPEthernetNetworkInterface ipNetworkInterface) {
      super(name, superAsset);

      upperLayerIPImpl = new IPImplementation("upperLayerIPImpl", this);
      ipSecImplementation = new IPSecImplementation("ipSecImplementation", this);
      this.ipNetworkInterface = ipNetworkInterface;

      upperLayerIPImpl.setSubLayerImplementation(ipSecImplementation, true);
      ipSecImplementation.setSubLayerImplementation(ipNetworkInterface.getIpImplementation(), true);
      getIpAddress().addGrantedIdentity(ipNetworkInterface.getEthernetImplementation().getSuperLayerGuest());
      setPhysicalLayerImplementation(ipNetworkInterface.getEthernetImplementation());

      own(upperLayerIPImpl);
      own(ipSecImplementation);
      own(ipNetworkInterface);
   }

   @Override
   public Message newMessage(Data body) {
      Message upperIPMessage = new Message(body.getName() + "(IP)", this, upperLayerIPImpl.isEncrypted(), upperLayerIPImpl.getProtocol());
      upperIPMessage.addBody(body);
      Message ipSecMessage = new Message(upperIPMessage.getName() + "(IPSec)", this, getIPSecImplementation().isEncrypted(), getIPSecImplementation().getProtocol());
      ipSecMessage.addBody(upperIPMessage);
      Message ethernetMessage = ipNetworkInterface.newMessage(ipSecMessage);
      ethernetMessage.addBody(ipSecMessage);
      return ethernetMessage;
   }

   public IPImplementation getIpImplementation() {
      return ipNetworkInterface.getIpImplementation();
   }

   public IPSecImplementation getIPSecImplementation() {
      return ipSecImplementation;
   }

   public EthernetImplementation getEthernetImplementation() {
      return ipNetworkInterface.getEthernetImplementation();
   }

   public Identity getIpAddress() {
      return ipNetworkInterface.getIpImplementation().getIpAddress();
   }

}
