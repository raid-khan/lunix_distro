package se.kth.ics.pwnpr3d.layer2.CVE;


import se.kth.ics.pwnpr3d.datatypes.AccessVectorType;
import se.kth.ics.pwnpr3d.datatypes.CVEType;
import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;

public class CVEInfo {

    private PrivilegeType privilegeType;
    private AccessVectorType accessVectorType;
    private CVEType CVEType;
    private double probability;

    public CVEInfo(PrivilegeType privilegeType, AccessVectorType accessVectorType, CVEType CVEType, double probability) {
        this.privilegeType = privilegeType;
        this.accessVectorType = accessVectorType;
        this.CVEType = CVEType;
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }

    public PrivilegeType getPrivilegeType() {
        return privilegeType;
    }

    public AccessVectorType getAccessVectorType() {
        return accessVectorType;
    }

    public se.kth.ics.pwnpr3d.datatypes.CVEType getCVEType() {
        return CVEType;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
