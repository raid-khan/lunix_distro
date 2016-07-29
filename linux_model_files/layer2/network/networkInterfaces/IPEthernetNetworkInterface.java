package se.kth.ics.pwnpr3d.layer2.network.networkInterfaces;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.network.NetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.EthernetImplementation;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.IPImplementation;

import java.util.HashSet;
import java.util.Set;

// TODO !# Eventually make this class concrete. There should be a probabilistic distribution for whether the interface
// has a protocolImpl vulnerable to ARP Spoofing

public abstract class IPEthernetNetworkInterface extends NetworkInterface {

   protected IPImplementation       ipImplementation;
   private EthernetImplementation   ethernetImplementation;
   private Set<NetworkInterface>    parentInterfaces;

   public IPEthernetNetworkInterface(String name, Asset superAsset) {
      super(name, superAsset);
      ipImplementation = new IPImplementation("ipImplementation", this);
      ethernetImplementation = new EthernetImplementation("ethernetImplementation", this);
      parentInterfaces = new HashSet<>();

      ipImplementation.setSubLayerImplementation(ethernetImplementation, true);
      setPhysicalLayerImplementation(ethernetImplementation);

      own(ipImplementation);
      own(ethernetImplementation);
   }

   // Wrap data in IP and then in Ethernet.
   @Override
   public Message newMessage(Data body) {
      Message ipMessage = new Message(body.getName(), this, ipImplementation.isEncrypted(), ipImplementation.getProtocol());
      ipMessage.addBody(body);
      Message ethernetMessage = new Message(ipMessage.getName(), this, ethernetImplementation.isEncrypted(), ethernetImplementation.getProtocol());
      ethernetMessage.addBody(ipMessage);
      return ethernetMessage;
   }

   @Override
   public void receiveMessage(Message message) {
      super.receiveMessage(message);
      assert (message.getProtocol() == ProtocolType.Ethernet);
      Message ipMessage = (Message) message.getBody().iterator().next();
      assert (ipMessage.getProtocol() == ProtocolType.IP);
      if (!ipMessage.getBody().isEmpty() && ipMessage.getBody().iterator().next() instanceof Message) {
         Message tcpMessage =  (Message) ipMessage.getBody().iterator().next();
         if (tcpMessage.getProtocol() == ProtocolType.TCP) {
            for (NetworkInterface ni : parentInterfaces) {
               SessionLayerNetworkInterface slni = (SessionLayerNetworkInterface) ni;
               if (message.getTargets().contains(slni.getPortNumber())) slni.receiveMessage(message);
            }
         }
      }
   }

   public IPImplementation getIpImplementation() {
      return ipImplementation;
   }

   public EthernetImplementation getEthernetImplementation() {
      return ethernetImplementation;
   }

   public Identity getIpAddress() {
      return ipImplementation.getIpAddress();
   }

   public Set<NetworkInterface> getParentInterfaces() {
      return parentInterfaces;
   }

   public void addParentInterface(NetworkInterface owningInterface) {
      this.parentInterfaces.add(owningInterface);
   }
}
