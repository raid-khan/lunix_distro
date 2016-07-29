package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.datatypes.CWEType;
import se.kth.ics.pwnpr3d.datatypes.ImpactType;
import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.layer1.*;
import se.kth.ics.pwnpr3d.layer2.cwe.CWE;
import se.kth.ics.pwnpr3d.layer2.cwe.CWEFactory;
import se.kth.ics.pwnpr3d.layer2.cwe.CWEInfo;
import se.kth.ics.pwnpr3d.util.Sampler;

import java.util.HashMap;

//TODO rename this Apache, or even ApacheOnLinux?

public class WebServer extends NetworkedApplication {

   private final Data webServerMemory;
   private DatabaseServer databaseServer;
   private Data varSlashWww;
   private OperatingSystem os;
   private HashMap<String,WebApplication> applicationPool = new HashMap<>();

   //TODO Virtual Hosts

   /**
    * A WebServer that runs web applications. Has been implemented by looking at Apache. This class should therefore
    * ultimately be renamed accordingly, and a more generic class should be implemented.
    * The webServer has a memory, which can be accessed to some extent without authorization, if the webServer is
    * vulnerable to Heartbleed.
    * /var/www is the folder where all the source code goes.
    * @param name name of the web server
    * @param os operating system running the web server
    * @param osPrivileges privileges of the web server on the OS
    */
   public WebServer(String name, OperatingSystem os, Account osPrivileges) {
      super(name, os, osPrivileges);

      this.os = os;
      Information clientsTraficInfo = new Information("clientTraficData",1000, 10000, 100);
      webServerMemory = new Data("webServerMemory",false);
      clientsTraficInfo.addRepresentingData(webServerMemory);
      addOwnedData(webServerMemory);
      // the server needs its memory
      addRequiredData(webServerMemory);
      getAdministrator().addAuthorizedReadWrite(webServerMemory);

      varSlashWww = new Data("varSlashWww", false);
      addOwnedData(varSlashWww);
      getAdministrator().addAuthorizedReadWrite(varSlashWww);

   }

   public void sampleVulnerabilities() {
      super.sampleVulnerabilities();

      for(CWEInfo cweInfo : getPotentialVulnerabilities()) {
         if (Sampler.bernoulliDistribution(cweInfo.getProbability())) {
            try {
               CWE vuln = CWEFactory.newCWEVulnerability(cweInfo.getCWEType(),cweInfo.getPrivilegeType(),cweInfo.getAccessVectorType(),this);
               addVulnerabilities(vuln);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   // TODO allow multiple dbServers
   public void connect(DatabaseServer databaseServer) {
      super.logicalConnect(databaseServer);
      this.databaseServer = databaseServer;
   }

   /**
    * Add a new application. The source code must be contained in /var/www. The application then requires the WebServer
    * in order to function. So if the WebServer is DoS, so is the application.
    * @param webApplication
     */
   public void run(WebApplication webApplication) {
      own(webApplication);
      varSlashWww.addBody(webApplication.getSourceCode());
      applicationPool.put(webApplication.getFullName(),webApplication);
   }

   /**
    * Creation of a new Web Application, not connected to a DBMS.
    * @param name name of the application
    * @return WebApplication object
     */
   public WebApplication newWebApplication (String name) {
      WebApplication webApp = new WebApplication(name, this);
      run(webApp);
      return webApp;
   }

   /**
    * Creation of a new Web Application that relies on a DBMS.
    * @require the Webserver must be connected to a DBMS
    * @param name name of the application
    * @param dbName name of the application's database
    * @param privilegeOnDB privilege of the DB account that the app uses to CRUD info
     * @return
     */
   public WebApplication newWebApplicationWithDB (String name, String dbName, PrivilegeType privilegeOnDB) {
      assert (databaseServer != null);
      WebApplication webApp = new WebApplication(name, this, databaseServer);
      Data db = databaseServer.newDatabase(dbName);
       webApp.setDbName(dbName);
      Account webAppAccountOnDb = databaseServer.newAccount(dbName+"_account", privilegeOnDB);
      databaseServer.setAccountPrivileges(webAppAccountOnDb,db,true,true);
      webApp.setDbAccount(webAppAccountOnDb);
      webApp.getSourceCode().addBody(webAppAccountOnDb.newCredentialsData(false));
      switch (privilegeOnDB) {
         case Administrator:
            webAppAccountOnDb.addGrantedIdentity(this.getAdministrator());
            break;
         case User:
            webAppAccountOnDb.addGrantedIdentity(this.getUser());
            break;
         case Guest:
            webAppAccountOnDb.addGrantedIdentity(this.getGuest());
            break;
         case None:
            break;
      }
      run(webApp);
      return webApp;
   }

   public Data getWebServerMemory() {
      return webServerMemory;
   }

   public OperatingSystem getOS() {
      return os;
   }

}
