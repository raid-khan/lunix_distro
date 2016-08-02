package se.kth.ics.pwnpr3d.layer1;

import se.kth.ics.pwnpr3d.datatypes.ProtocolType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class Message extends Data {

   protected ProtocolType  protocol;
   protected Agent         source;
   protected Set<Identity> targets = new HashSet<>();

   public Message(String name, Agent source, boolean encrypted, ProtocolType protocolType) {
      super(name, source, encrypted);
      this.protocol = protocolType;
      this.source = source;
      this.addRequiredAgent(source);
   }

   public Message copy() {
      Message m = new Message(getName(), source, encrypted, protocol);
      m.source = this.source;
      m.targets.addAll(targets);
      m.addBodyAll(getBody());
      return m;
   }

   /**
    * Getters & Setters
    **/

   public Agent getSource() {
      return source;
   }

   public void addTargets(Identity... targets) {
      this.targets.addAll(Arrays.asList(targets));
   }

   public Set<Identity> getTargets() {
      return targets;
   }

   public ProtocolType getProtocol() {
      return protocol;
   }

   @Override
   public String getName() {
      if (getBody().isEmpty()) {
         return super.getName();
      }
      StringJoiner bodies = new StringJoiner(";", "{", "}");
      getBody().stream().forEach(body -> bodies.add(body.getName()));
      return "(" + protocol + ")" + bodies;
   }
}
