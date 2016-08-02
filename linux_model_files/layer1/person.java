package se.kth.ics.pwnpr3d.layer1;

public class Person extends Agent {

   public Identity getPhysicalIdentity() {
      return physicalIdentity;
   }

   private Identity physicalIdentity;
   public Person(String name) {
      super(name);
      physicalIdentity = new Identity("physical_ID",this);
   }

   @Override
   public void sampleVulnerabilities() {
      super.sampleVulnerabilities();
   }

}
