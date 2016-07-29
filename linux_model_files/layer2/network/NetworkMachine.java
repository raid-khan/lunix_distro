package se.kth.ics.pwnpr3d.layer2.network;

import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Machine;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.EthernetImplementation;

import java.util.HashSet;
import java.util.Set;

public abstract class NetworkMachine extends Machine {

   public Set<NetworkMachine>     connectedNetworkMachines = new HashSet<>();

   private Set<Message>           sentMessages             = new HashSet<>();
   private Set<Message>           receivedMessages         = new HashSet<>();
   private ProtocolImplementation physicalLayerImplementation;

   protected NetworkMachine(String name) {
      super(name);
   }

   protected NetworkMachine(String name, Asset superAsset) {
      super(name, superAsset);
   }

   public void logicalConnect(NetworkMachine networkMachine) {
//      super.logicalConnect(this);
      physicalLayerImplementation.logicalConnect(networkMachine.physicalLayerImplementation);
      connectedNetworkMachines.add(networkMachine);
      networkMachine.connectedNetworkMachines.add(this);
   }

   public void sendMessage(Message message) {
      this.sentMessages.add(message);
      addOwnedData(message);
      if (!message.isEncrypted()) {
         getAdministrator().addAuthorizedWrite(message);
      }
      for (NetworkMachine connectedNetworkMachine : connectedNetworkMachines) {
         connectedNetworkMachine.receiveMessage(message);
      }
   }

   public Message newMessage(Data body) {
      // TODO body has to be a message already  when this code is reached, right?
      // Is this method should have really been lifted here?
      if (!(body instanceof Message))
         throw new ClassCastException("message body should also be a message");
      return (Message) body;
      //
//      if (body instanceof Message) {
//         Message innerMsg = (Message) body;
//         return new Message(innerMsg.getName(),innerMsg.getSource(),innerMsg.isEncrypted(),innerMsg.getProtocol());
//      } else {
//         return new Message(body.getName(),this,physicalLayerImplementation.isEncrypted(),physicalLayerImplementation.getProtocol());
//      }
   }

   public void PhysicalLayerConnect(ProtocolImplementation remotePhyLayerImpl) {
      //TODO condition should be: if the two physical layers are of the same kind
      if (remotePhyLayerImpl instanceof EthernetImplementation &&
              physicalLayerImplementation instanceof EthernetImplementation) {
         remotePhyLayerImpl.getAdministrator().addGrantedIdentity(physicalLayerImplementation.getGuest());
         remotePhyLayerImpl.getSuperLayerGuest().addGrantedIdentity(physicalLayerImplementation.getSuperLayerGuest());
         physicalLayerImplementation.getGuest().addGrantedIdentity(remotePhyLayerImpl.getGuest());
      } else {
         System.err.println("/!\\ Trying to connect two machines that don't share the same network physical layer /!\\");
      }
   }

   public abstract void receiveMessage(Message message);

   public void addSentMessage(Message message) {
      sentMessages.add(message);
   }

   public void addReceivedMessage(Message message) {
      receivedMessages.add(message);
   }

   public Set<Message> getSentMessages() {
      return sentMessages;
   }

   public Set<Message> getReceivedMessages() {
      return receivedMessages;
   }

   public boolean containsReceivedMessageData(Data data) {
      for (Message rm : receivedMessages) {
         if (rm.contains(data))
            return true;
      }
      return false;
   }

   public boolean containsSentMessageData(Data data) {
      for (Message sm : sentMessages) {
         if (sm.contains(data))
            return true;
      }
      return false;
   }

   public void addSentMessages(Message sentMessages) {
      this.sentMessages.add(sentMessages);
   }

   public void addReceivedMessages(Message receivedMessages) {
      this.receivedMessages.add(receivedMessages);
   }

   public ProtocolImplementation getPhysicalLayerImplementation() {
      return physicalLayerImplementation;
   }

   public void setPhysicalLayerImplementation(ProtocolImplementation physicalLayerImplementation) {
      this.physicalLayerImplementation = physicalLayerImplementation;
   }

}
