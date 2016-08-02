package se.kth.ics.pwnpr3d.layer2.CVE;

import se.kth.ics.pwnpr3d.datatypes.AccessVectorType;
import se.kth.ics.pwnpr3d.datatypes.CVEType;
import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.layer2.software.*;

public class CVEFactory {

    public static CVE newCVEVulnerability(CVEType ct, PrivilegeType pt, AccessVectorType avt, Software software) throws Exception {
        switch (ct) {
            case CVE_79:
                return new CVE79(software,pt,avt);
            case CVE_89:
                if (!(software instanceof Application)) throw new Exception("CVE creation: Invalid owning Agent");
                return new CVE89(((Application) software),pt,avt);
            case CVE_119:
                return new CVE119(software,pt,avt);
            case HeartBleed:
                if (!(software instanceof WebServer)) throw new Exception("CVE creation: Invalid owning Agent");
                return new HeartBleed((WebServer)software,pt,avt);
            case CVE_639:
                if (!(software instanceof Application)) throw new Exception("CVE creation: Invalid owning Agent");
                return new CVE639(((Application) software),pt,avt);
            case CVE_152:
                if (!(software instanceof WebApplication)) throw new Exception("CVE creation: Invalid owning Agent");
                return new CVE152(((WebApplication) software),pt,avt);
            case ShellShock:
                if (!(software instanceof OperatingSystem)) throw new Exception("CVE creation: Invalid owning Agent");
                return new ShellShock((OperatingSystem)software,pt,avt);
            case CVE_22:
            case NONE:
            default:
                throw new Exception("This CVE is not implemented yet");
        }
    }

}
