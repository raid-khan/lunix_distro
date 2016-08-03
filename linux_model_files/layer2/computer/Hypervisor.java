package se.kth.ics.pwnpr3d.layer2.computer;

// TODO !# Model a virtual data center, including virtual networking.

public abstract class Hypervisor extends Computer {

   public Hypervisor(String name, Computer superAsset) {
      super(name, superAsset);
   }

}
