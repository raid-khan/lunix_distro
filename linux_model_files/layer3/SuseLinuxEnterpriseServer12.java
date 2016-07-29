package se.kth.ics.pwnpr3d.layer3;

import se.kth.ics.pwnpr3d.datatypes.CWEType;
import se.kth.ics.pwnpr3d.datatypes.ImpactType;
import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Information;
import se.kth.ics.pwnpr3d.layer1.Vulnerability;
import se.kth.ics.pwnpr3d.layer2.computer.Computer;
import se.kth.ics.pwnpr3d.layer2.cwe.CWE;
import se.kth.ics.pwnpr3d.layer2.cwe.CWEFactory;
import se.kth.ics.pwnpr3d.layer2.cwe.CWEInfo;
import se.kth.ics.pwnpr3d.layer2.software.*;
import se.kth.ics.pwnpr3d.util.Sampler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by avernotte on 1/20/16.
 */
public class SuseLinuxEnterpriseServer12 extends UnixOS {

    static String VERSION = "Linux Kernel 3.8";

    /**
     * ** RESERVED **
     * reference leak in the linux keyring facility allows attackers with legitimate or lower privileges to execute code
     * in the Linux kernel, ultimately leading to root access
     *
     * @url https://threatpost.com/serious-linux-kernel-vulnerability-patched/115923/
     * @exploit https://gist.github.com/PerceptionPointTeam/18b1e86d1c0f8531ff8f
     */
    private Vulnerability cve20160728;
    private CWE shellshock;
    //TODO Add distribution
    private boolean hasCVE20160728 = false;
    private Data telnetAdminCredentialsData;
    private Information telnetAdminCredentials;
    private Information dbInfo1;
    private Information dbInfo2;
    private Information dbInfo3;
    private Data sensitiveData;
    private NetworkedApplication telnet;
    private HashSet<WebServer> webServers = new HashSet<>();
    private Application ntpd;

    //TODO check that the var-www group is predefined in Suse, it does not look like it
    private Identity varwww = new Identity("varwww",this);

    public SuseLinuxEnterpriseServer12(String name, Computer superAsset) {
        super(name, superAsset, 0.05);
        ntpd = newNetworkedApplication("ntpd", PrivilegeType.Administrator, ProtocolType.UDP, false, true);
        telnet = this.newNetworkedApplication("TelnetServer", PrivilegeType.User, ProtocolType.TCP, false, true);
        telnetAdminCredentialsData = new Data("adminCredentials", this, false);
        telnetAdminCredentials = new Information("DB_Information1",5439,2725,200);
        telnetAdminCredentials.addRepresentingData(telnetAdminCredentialsData);
        telnetAdminCredentials.addAuthenticatedIdentities(this.getAdministrator());
        sensitiveData = new Data("Code Source Data",false);
        dbInfo1 = new Information("DB_Information1",54539,27725,2000);
        dbInfo2 = new Information("DB_Information2",296487,13786,1000);
        dbInfo1.addRepresentingData(sensitiveData);
        dbInfo2.addRepresentingData(sensitiveData);
        Data sensitiveData2 = new Data("db_data2",false);
        dbInfo3 = new Information("DB_Information3",88575,39672,2500);
        dbInfo3.addRepresentingData(sensitiveData2);

        addOwnedData(sensitiveData);
        addRequiredData(sensitiveData);
        addOwnedData(sensitiveData2);
        getAdministrator().addAuthorizedRead(sensitiveData);
        getAdministrator().addAuthorizedRead(sensitiveData2);
        getAdministrator().addAuthorizedWrite(sensitiveData);
        getAdministrator().addAuthorizedWrite(sensitiveData2);

    }

    @Override
    public void sampleVulnerabilities() {
        for(CWEInfo cweInfo : getPotentialVulnerabilities()) {
            if (Sampler.bernoulliDistribution(cweInfo.getProbability())) {
                try {
                    if (cweInfo.getCWEType().equals(CWEType.ShellShock)) {
                        for (WebServer webServer: webServers) {
                            shellshock = CWEFactory.newCWEVulnerability(cweInfo.getCWEType(),cweInfo.getPrivilegeType(),cweInfo.getAccessVectorType(),this);
                            addVulnerabilities(shellshock);
                            shellshock.addSpoofedIdentity(webServer.getPrivilegesOnOS());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public NetworkedApplication getTelnetServer() {
        return telnet;
    }

    public WebServer newWebServer(String name, PrivilegeType privilegeType,
                                  ProtocolType protocolType,
                                  boolean overIPSec, boolean server) {
        WebServer webServer = super.newWebServer(name,privilegeType,protocolType,
                overIPSec, server);
        webServers.add(webServer);
        return webServer;
    }

    public CWE getShellshock() {
        return shellshock;
    }

    public Data getTelnetCredentials() {
        return telnetAdminCredentialsData;
    }

    public Data getSensitiveData() {
        return sensitiveData;
    }
}
