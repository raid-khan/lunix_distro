package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.datatypes.ProtocolType;
import se.kth.ics.pwnpr3d.layer1.Account;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;
import se.kth.ics.pwnpr3d.layer1.Message;
import se.kth.ics.pwnpr3d.layer2.computer.Computer;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.IPEthernetARPNetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.IPSecNetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.networkInterfaces.SessionLayerNetworkInterface;
import se.kth.ics.pwnpr3d.layer2.network.protocolImplementations.SessionLayerClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// TODO Antimalware should be introduced. Because it is so invasive, a simple way to model it is by specializing the OS depending on the existence (and, perhaps even brand) of antimalware solution.

public class OperatingSystem extends Software {

   protected IPEthernetARPNetworkInterface ieaStack;
   protected IPSecNetworkInterface         ipSecStack;
   protected Set<Application>              applications      = new HashSet<>();
   protected HashMap<String,Account> userAccounts      = new HashMap<>();
   // TODO The passwd file is not yet implemented. Credentials should be
   // enclosed in that file set, each with a probability of being cracked.
   protected Set<Data>                     hashedCredentials = new HashSet<>();
   private Data fileSystem = new Data("fileSystem",this,false);

   public OperatingSystem(String name, Computer superAsset) {
      super(name, superAsset);
      ieaStack = new IPEthernetARPNetworkInterface("ieaStack", this, 51);
      ipSecStack = new IPSecNetworkInterface("ipSecStack", this, ieaStack);
      getAdministrator().addAuthorizedReadWrite(fileSystem);
      newUserAccount("admin",PrivilegeType.Administrator);
      own(ieaStack);
      own(ipSecStack);
   }

   /**
    * Component connections
    **/

   // TODO !# It should be possible, through a spoofing vulnerability, to assume
   // the identity (and thus gain the privileges) of the application. Perhaps
   // a test case on that?
   protected Application newApplication(String name, PrivilegeType privileges, boolean isNetworked) {
      Account applicationIdentity = newUserAccount(name, privileges);
      Application application;
      if (isNetworked)
         application = new NetworkedApplication(name, this, applicationIdentity);
      else
         application = new Application(name, this, applicationIdentity);
      applications.add(application);
      application.addRequiredAgent(this);
      own(application);
      application.setPrivilegesOnOS(applicationIdentity);
      return application;
   }

   public WebServer newWebServer(String name, PrivilegeType privilegeType,
                                 ProtocolType protocolType,
                                 boolean overIPSec, boolean server) {
      Account wsIdentity = newUserAccount(name, privilegeType);
      WebServer ws = new WebServer(name, this, wsIdentity);
      applications.add(ws);
      ws.addRequiredAgent(this);
      own(ws);
      SessionLayerNetworkInterface port;
      port = ws.newPort(this, name + "Port", overIPSec, protocolType, server);
      Data data = new Data(name + "Data", false);
      Message message = port.newMessage(data);
      port.receiveMessage(message);
      return ws;
   }

   // TODO refactor constructors
   public DatabaseServer newDatabaseServer(String databaseServerName, PrivilegeType privilegeType, ProtocolType protocolType,
                                           boolean overIPSec, boolean server) {
      Account dbmsIdentity = newUserAccount(databaseServerName, privilegeType);
      DatabaseServer dbms = new DatabaseServer(databaseServerName, this, dbmsIdentity);
      applications.add(dbms);
      dbms.setPrivilegesOnOS(dbmsIdentity);
      dbms.addRequiredAgent(this);
      own(dbms);
      SessionLayerNetworkInterface port;
      port = dbms.newPort(this, databaseServerName + "Port", overIPSec, protocolType, server);
      Data data = new Data(databaseServerName + "Data", false);
      Message message = port.newMessage(data);
      port.receiveMessage(message);
      return  dbms;
   }

   public NetworkedApplication newNetworkedApplication(String name, PrivilegeType privilegeType, ProtocolType protocolType, boolean overIPSec, boolean server) {
      NetworkedApplication application = ((NetworkedApplication) newApplication(name, privilegeType, true));
      SessionLayerNetworkInterface port;
      port = application.newPort(this, name + "Port", overIPSec, protocolType, server);
      Data data = new Data(name + "Data", false);
      Message message = port.newMessage(data);
      port.receiveMessage(message);
      return application;
   }

   public Account newUserAccount(String name, PrivilegeType privileges) {
      Account userAccount = new Account(name+"Identity", this);
      userAccounts.put(name+"Identity",userAccount);
      userAccount.addAuthorizedAccess(this);
      userAccount.addGrantedIdentity(ieaStack.getAdministrator());
      switch (privileges) {
         case Administrator:
            userAccount.addGrantedIdentity(this.getAdministrator());
            break;
         case User:
            userAccount.addGrantedIdentity(this.getUser());
            break;
         case Guest:
            userAccount.addGrantedIdentity(this.getGuest());
            break;
         case None:
            break;
      }
      return userAccount;
   }

   public void addServerIP(NetworkedApplication client, Identity ipAddress) {
      ((SessionLayerClient) client.getSessionLayerNetworkInterface().getSessionLayerImplementation()).addServerIPAddress(ipAddress);
   }

   /**
    * Getters & Setters
    **/
   public Application getApplication(String name) {
      for (Application app : getApplications()){
         if (app.getName().equals(name)) {
            return app;
         }
      }
      return null;
   }

   public IPEthernetARPNetworkInterface getIPEthernetARPNetworkInterface() {
      return ieaStack;
   }

   public IPSecNetworkInterface getIpSecNetworkInterface() {
      return ipSecStack;
   }

   public Identity getIpAddress() {
      return ieaStack.getIpAddress();
   }

   public Set<Application> getApplications() {
      return applications;
   }

   public Account getUserAccount(String name) {
      return userAccounts.get(name+"Identity");
   }

   public void printReceivedMessages() {
      for (Message m : ieaStack.getReceivedMessages()) {
         System.out.println(m.getFullName());
      }
   }

   public Data getFileSystem() {
      return fileSystem;
   }

}
