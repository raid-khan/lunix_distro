package se.kth.ics.pwnpr3d.layer3;

import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer2.computer.HardwareComputer;
import se.kth.ics.pwnpr3d.layer2.network.EthernetSwitch;
import se.kth.ics.pwnpr3d.layer2.network.Firewall;
import se.kth.ics.pwnpr3d.layer2.network.Router;
import se.kth.ics.pwnpr3d.layer2.software.DatabaseServer;
import se.kth.ics.pwnpr3d.layer2.software.NetworkedApplication;
import se.kth.ics.pwnpr3d.layer2.software.OperatingSystem;

import java.util.HashSet;

public class NetworkZone {

    HashSet<OperatingSystem> operatingSystems = new HashSet<>();
    HashSet<HardwareComputer> computers = new HashSet<>();
    HashSet<DatabaseServer> databaseServers = new HashSet<>();

    EthernetSwitch networkZoneSwitch;
    Router networkZoneRouter;
    Firewall networkZoneFirewall;
    String name;
    Identity ca;

    public NetworkZone (EthernetSwitch networkZoneSwitch) {
        this.networkZoneSwitch = networkZoneSwitch;
    }

    public NetworkZone(String name, EthernetSwitch networkZoneSwitch) {
        this.name = name;
        this.networkZoneSwitch = networkZoneSwitch;
    }

    public NetworkZone(String name) {
        this.networkZoneRouter = new Router(name+"Router");
        this.networkZoneSwitch = new EthernetSwitch(name+"Switch");
        this.networkZoneFirewall = new Firewall(name+"Firewall");
        this.networkZoneFirewall.connect(networkZoneRouter, true);
    }

    public void connect(NetworkZone zone, boolean trusted){
        this.networkZoneRouter.connect(zone.networkZoneRouter);
        this.networkZoneFirewall.connect(zone.networkZoneRouter,trusted);
    }

    private void connect(OperatingSystem os){
        networkZoneRouter.connect(os, networkZoneSwitch);
    }


    public OperatingSystem newWindows2008Server(String name) {
        HardwareComputer hc = new HardwareComputer(name+"HC");
        Windows2008Server os = new Windows2008Server(name,hc);
        operatingSystems.add(os);
        computers.add(hc);
        connect(os);
        return os;
    }

    public OperatingSystem newWindows7(String name) {
        HardwareComputer hc = new HardwareComputer(name+"HC");
        Windows7 os = new Windows7(name,hc);
        operatingSystems.add(os);
        computers.add(hc);
        connect(os);
        return os;
    }

    public OperatingSystem newVXWorks65(String name) {
        HardwareComputer hc = new HardwareComputer(name+"HC");
        VXWorks65 os = new VXWorks65(name,hc);
        operatingSystems.add(os);
        computers.add(hc);
        connect(os);
        return os;
    }

    public DatabaseServer newDatabase(String name, OperatingSystem os) {
        DatabaseServer db = new DatabaseServer("scadaDatabase", os, os.getUserAccount("admin"));

        return db;
    }

    public OperatingSystem getOS(String name) {
        for (OperatingSystem os : operatingSystems) {
            if (os.getName().equals(name)) {
                return os;
            }
        }
        return null;
    }

    public void permit(OperatingSystem source, OperatingSystem destination){
        networkZoneFirewall.permit(source.getIpAddress(), destination.getIpAddress());
    }

    public void permit(OperatingSystem destination){
        networkZoneFirewall.permit(destination.getIpAddress());
    }

    public void issueCertificate(NetworkZone nwz) {
        this.ca.addGrantedIdentity(nwz.ca);
    }

    public void issueCertificate(OperatingSystem subjectOS) {
        this.ca.addGrantedIdentity(subjectOS.newUserAccount("Certificate", PrivilegeType.Administrator));
    }

    public void setCA(String name) {
        OperatingSystem ca = getOS(name);
        Identity issuer = ca.newUserAccount("Certificate", PrivilegeType.Administrator);
        this.ca = issuer;
    }

    public void addServer(String clientOSName, String clientName, NetworkZone serverZone, String serverOSName){
        OperatingSystem clientOS = getOS(clientOSName);
        OperatingSystem serverOS = serverZone.getOS(serverOSName);
        NetworkedApplication client = (NetworkedApplication)clientOS.getApplication(clientName);
        clientOS.addServerIP(client,serverOS.getIpAddress());
    }

    public void addServer(String clientOSName, String clientName, String serverOSName){
        addServer(clientOSName, clientName, this, serverOSName);
    }








}
