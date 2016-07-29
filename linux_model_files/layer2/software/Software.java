package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.datatypes.AccessVectorType;
import se.kth.ics.pwnpr3d.datatypes.CWEType;
import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer1.Machine;
import se.kth.ics.pwnpr3d.layer2.cwe.CWEInfo;

import java.util.HashSet;
import java.util.Optional;

public abstract class Software extends Machine {

   protected double vulnerabilityDiscoveryTheta;
   private HashSet<CWEInfo> potentialVulnerabilities = new HashSet<>();

   public Software(String name, Asset superAsset) {
      super(name, superAsset);
   }

   public HashSet<CWEInfo> getPotentialVulnerabilities() {
      return potentialVulnerabilities;
   }

   public void addVulnerabilityProbability(CWEType cweType, PrivilegeType privilegeType,
                                           AccessVectorType avt, double probability) {
      Optional<CWEInfo> cwe = potentialVulnerabilities.stream().filter(cweInfo -> cweInfo.getCWEType().equals(cweType)
              && cweInfo.getAccessVectorType().equals(avt) && cweInfo.getPrivilegeType().equals(privilegeType)).
              findAny();
      if (cwe.isPresent()) {
         cwe.get().setProbability(probability);
      } else {
         potentialVulnerabilities.add(new CWEInfo(privilegeType,avt,cweType,probability));
      }
   }
}
