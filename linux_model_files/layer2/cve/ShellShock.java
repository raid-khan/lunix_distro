package se.kth.ics.pwnpr3d.layer2.CVE;

import se.kth.ics.pwnpr3d.datatypes.AccessVectorType;
import se.kth.ics.pwnpr3d.datatypes.CVEType;
import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.layer2.software.OperatingSystem;
import se.kth.ics.pwnpr3d.layer2.software.Software;

public class ShellShock extends CVE {

    CVEType CVE_Type = CVEType.ShellShock;

    public ShellShock(OperatingSystem os, PrivilegeType privilegeType, AccessVectorType avt) {
        super("ShellShock", os, privilegeType, avt);
    }
}
