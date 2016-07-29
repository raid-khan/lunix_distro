package se.kth.ics.pwnpr3d.layer3;

import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Information;
import se.kth.ics.pwnpr3d.layer2.computer.Computer;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.SessionLayerClient;
import se.kth.ics.pwnpr3d.layer2.software.Application;
import se.kth.ics.pwnpr3d.layer2.software.NetworkedApplication;
import se.kth.ics.pwnpr3d.layer2.software.OperatingSystem;

/**
 * Created by avernotte on 1/21/16.
 */
public class Ubuntu1404 extends OperatingSystem {

    public Data privateData;

    public Identity userAccount;
    private Application ntpd;
    private Data sensitiveData;
    private Information sensitiveInfo1;
    private Information sensitiveInfo2;
    private NetworkedApplication telnet;

    public Ubuntu1404(String name, Computer superAsset) {
        super(name, superAsset);

        vulnerabilityDiscoveryTheta = 102;

        privateData = new Data("privateData", this, false);
        Data etcShadowHashData = new Data("etcShadowHashData", privateData, true);
        privateData.addBody(etcShadowHashData);
        this.addOwnedData(privateData);

        getAdministrator().addAuthorizedRead(privateData);
        getAdministrator().addAuthorizedWrite(privateData);

        sensitiveData = new Data("Ubuntu_data",false);
        sensitiveInfo1 = new Information("Ubuntu_Information1",50,200,20);
        sensitiveInfo2 = new Information("Ubuntu_Information2",20,130,10);
        sensitiveInfo1.addRepresentingData(sensitiveData);
        sensitiveInfo2.addRepresentingData(sensitiveData);
        addOwnedData(sensitiveData);
        getAdministrator().addAuthorizedRead(sensitiveData);
        getAdministrator().addAuthorizedWrite(sensitiveData);

        userAccount = super.newUserAccount("userAccount", PrivilegeType.User);
        userAccount.addAuthorizedRead(etcShadowHashData);

        ntpd = newNetworkedApplication("ntpd", PrivilegeType.Administrator, ProtocolType.UDP, false, true);

        telnet = this.newNetworkedApplication("TelnetClient", PrivilegeType.User, ProtocolType.TCP, false, false);
    }

    public Application getNtpd() {
        return ntpd;
    }

    public void addTelnetServerIP(Identity telnetServerIP) {
        ((SessionLayerClient) telnet.getSessionLayerNetworkInterface().getSessionLayerImplementation())
                .addServerIPAddress(telnetServerIP);
    }

    public Identity getUserAccount() {
        return userAccount;
    }

    public NetworkedApplication getTelnetClient() {
        return telnet;
    }
}
