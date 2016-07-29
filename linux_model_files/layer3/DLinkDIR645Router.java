package se.kth.ics.pwnpr3d.layer3;

import se.kth.ics.pwnpr3d.layer1.Vulnerability;
import se.kth.ics.pwnpr3d.layer2.network.Router;

/**
 * Created by avernotte on 1/19/16.
 */
public class DLinkDIR645Router extends Router {

    static String VERSION = "Rev. Ax; fw 1.04b12";

    /**
     * Stack-based buffer overflow in the DIR-645 Wired/Wireless Router Rev. Ax with firmware 1.04b12 and earlier
     * allows remote attackers to execute arbitrary code via a long string in a GetDeviceSettings action
     * to the HNAP interface
     *
     * @url https://www.cvedetails.com/cve/CVE-2015-2052/
     */
    private Vulnerability cve20152052;
    //TODO Add distribution
    private boolean hasCve20152052 = true;
    /**
     * The D-Link DIR-645 Wired/Wireless Router Rev. Ax with firmware 1.04b12 and earlier allows remote attackers to
     * execute arbitrary commands via a GetDeviceSettings action to the HNAP interface.
     *
     * @url https://www.cvedetails.com/cve/CVE-2015-2051/
     */
    private Vulnerability cve20152051;
    private boolean hasCve20152051 = true;

    public DLinkDIR645Router(String name) {
        super(name);
    }
}
