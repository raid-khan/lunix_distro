package se.kth.ics.pwnpr3d.layer3;

import se.kth.ics.pwnpr3d.layer1.Account;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer2.computer.HardwareComputer;
import se.kth.ics.pwnpr3d.layer2.network.EthernetSwitch;
import se.kth.ics.pwnpr3d.layer2.network.Firewall;
import se.kth.ics.pwnpr3d.layer2.network.Router;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.IPEthernetARPNetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.ARPImplementation;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.EthernetImplementation;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.IPImplementation;
import se.kth.ics.pwnpr3d.layer2.software.Application;
import se.kth.ics.pwnpr3d.layer2.software.DatabaseServer;
import se.kth.ics.pwnpr3d.layer2.software.UnixOS;

public class LinuxDistro {

	public LinuxDistro() {
		HardwareComputer computer = new HardwareComputer("computer");
		//OperatingSystem os = new OperatingSystem("Linux", computer);
		UnixOS os = new UnixOS("UNIX", computer, 0.2);
		computer.logicalConnect(os);
		Account account = new Account("USER", os);
		Application SSHClient = new Application("SSHClient", os, account);
		os.logicalConnect(SSHClient);
		DatabaseServer dataSever = new DatabaseServer("DataBase", os, account);
		Data folders = new Data("Data", true);
		dataSever.addOwnedData(folders);
		Data backup = new Data("Backup", true);
		dataSever.addOwnedData(backup);
		Data contacts = new Data("Contacts", false);
		dataSever.addOwnedData(contacts);
		Data archive = new Data("Archive", true);
		dataSever.addOwnedData(archive);
		account.addAuthorizedAccess(dataSever);
		os.logicalConnect(dataSever);
		SSHClient.logicalConnect(dataSever);
		EthernetSwitch ethernetSwitch = new EthernetSwitch("EthernetSwicth");
		Router ipRouter = new Router("Router");
		ipRouter.connect(os, ethernetSwitch);
		Firewall firewall = new Firewall("Firewall");
		firewall.connect(ipRouter, true);
		ethernetSwitch.connect(firewall);
		IPEthernetARPNetworkInterface ipNetIface = new IPEthernetARPNetworkInterface("IPFACE", computer, 0.3);
		EthernetImplementation networkProtocols = new EthernetImplementation("NetworkProtocols", ipNetIface);
		IPImplementation tcpIP = new IPImplementation("TCP/IP", ipNetIface);
		tcpIP.logicalConnect(networkProtocols);
		ARPImplementation arpImplementation = new ARPImplementation("ARPImplementation", ipNetIface, networkProtocols, 0.3);
		arpImplementation.logicalConnect(networkProtocols);
		Data ARPTable = new Data("ARPTable", true);
		arpImplementation.addOwnedData(ARPTable);
		firewall.addOwnedData(ARPTable);
		networkProtocols.logicalConnect(firewall);
		networkProtocols.logicalConnect(SSHClient);
	}


}
