package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.layer1.Account;
import se.kth.ics.pwnpr3d.layer1.Identity;

public class Application extends Software {

   private Account privilegesOnOS;

   public Application(String name, OperatingSystem superAsset, Account osPrivileges) {
      super(name, superAsset);
      // This does not make sense to me, so I commented it, and test cases still pass.
      //   osPrivileges.addAuthorizedAccess(this);
      getAdministrator().addGrantedIdentity(osPrivileges);
      getGuest().addGrantedIdentity(superAsset.getGuest());
      superAsset.getGuest().addGrantedIdentity(getGuest());
      privilegesOnOS = osPrivileges;
   }

   public Application(String name, Application superAsset) {
      super(name, superAsset);
      // This does not make sense to me, so I commented it, and test cases still pass.
      //   osPrivileges.addAuthorizedAccess(this);
      getGuest().addGrantedIdentity(superAsset.getGuest());
      superAsset.getGuest().addGrantedIdentity(getGuest());

   }

   public Account getPrivilegesOnOS() {
      return privilegesOnOS;
   }

   public void setPrivilegesOnOS(Account privilegesOnOS) {
      this.privilegesOnOS = privilegesOnOS;
   }
}
