package se.kth.ics.pwnpr3d.layer2.computer;


// These hypervisors run directly on the host's hardware to control the hardware and to manage getGuest operating systems.

public class HypervisorType1 extends Hypervisor {

   public HypervisorType1(String name, Computer superAsset) {
      super(name, superAsset);
   }

}
