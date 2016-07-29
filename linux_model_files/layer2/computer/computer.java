package se.kth.ics.pwnpr3d.layer2.computer;

import java.util.HashSet;
import java.util.Set;

import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Machine;
import se.kth.ics.pwnpr3d.layer2.software.OperatingSystem;
import se.kth.ics.pwnpr3d.layer2.software.Software;
import se.kth.ics.pwnpr3d.layer3.SuseLinuxEnterpriseServer12;
import se.kth.ics.pwnpr3d.layer3.XenHypervisor;

public abstract class Computer extends Machine {

   protected Set<HypervisorType1> hyperVisorsType1 = new HashSet<>();
   protected Set<OperatingSystem> operatingSystems = new HashSet<>();

   public Computer(String name, Asset superAsset) {
      super(name, superAsset);
   }

   public Computer(String name) {
      super(name);
   }

   public Set<OperatingSystem> getOperatingSystems() {
      return operatingSystems;
   }

   public void own(Software software) {
      super.own(software);
      software.getAdministrator().addGrantedIdentity(getGuest());
   }

   public OperatingSystem newOperatingSystem(String name, double probabilityOfCWE119) {
      OperatingSystem operatingSystem = new OperatingSystem(name, this);
      operatingSystems.add(operatingSystem);
      own(operatingSystem);
      return operatingSystem;
   }

   public OperatingSystem newOperatingSystem(String name) {
      return newOperatingSystem(name, 0.0);
   }

   public SuseLinuxEnterpriseServer12 newSuseEnterpriseServer(String name) {
      SuseLinuxEnterpriseServer12 operatingSystem = new SuseLinuxEnterpriseServer12(name, this);
      operatingSystems.add(operatingSystem);
      own(operatingSystem);
      return operatingSystem;
   }

   public HypervisorType1 newHypervisorType1(String name) {
      HypervisorType1 hypervisor = new HypervisorType1(name, this);
      hyperVisorsType1.add(hypervisor);
      own(hypervisor);
      return hypervisor;
   }

   public XenHypervisor newXenHypervisor(String name) {
      XenHypervisor hypervisor = new XenHypervisor(name, this);
      hyperVisorsType1.add(hypervisor);
      own(hypervisor);
      return hypervisor;
   }

   public void addOS(OperatingSystem os) {
      operatingSystems.add(os);
      own(os);
   }

}
