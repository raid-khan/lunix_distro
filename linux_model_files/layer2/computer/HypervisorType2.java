package se.kth.ics.pwnpr3d.layer2.computer;

import se.kth.ics.pwnpr3d.layer2.software.OperatingSystem;

//These hypervisors run on a conventional operating system just as other computer programs do.

public class HypervisorType2 extends Hypervisor {

   protected OperatingSystem host;

   public HypervisorType2(String name, Computer superAsset) {
      super(name, superAsset);
   }

}
