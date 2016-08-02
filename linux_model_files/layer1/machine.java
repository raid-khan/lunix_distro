package se.kth.ics.pwnpr3d.layer1;

import se.kth.ics.pwnpr3d.datatypes.ImpactType;
import se.kth.ics.pwnpr3d.layer0.Asset;

public class Machine extends Agent {

   private Identity administrator = new Identity("Administrator", this);
   private Identity user          = new Identity("User", this);
   private Identity guest         = new Identity("Guest", this);

   protected Machine(String name) {
      super(name);
      init();
   }

   protected Machine(String name, Asset superAsset) {
      super(name, superAsset);
      init();
   }

   @Override
   public void sampleVulnerabilities() {
      super.sampleVulnerabilities();
   }

   private void init() {
      getAdministrator().addGrantedIdentity(getUser());
      getUser().addGrantedIdentity(getGuest());
      guest.addAuthorizedAccess(this);
      Vulnerability vulnerability = new Vulnerability("vulnerability", this, ImpactType.High);
      getAdministrator().addVulnerability(vulnerability);
   }

   // TODO rename Machine.own method
   // action method with stative verb as name -> misleading (could be boolean,
   // e.g., if A.own(B) then..)
   public void own(Machine machine) {
      machine.addRequiredAgent(this);
      getAdministrator().addGrantedIdentity(machine.getAdministrator());
   }

   /**
    * Getters & Setters
    **/

   public Identity getAdministrator() {
      return administrator;
   }

   public Identity getUser() {
      return user;
   }

   public Identity getGuest() {
      return guest;
   }
}
