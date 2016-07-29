package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;
import se.kth.ics.pwnpr3d.layer1.Account;
import se.kth.ics.pwnpr3d.layer1.Data;
import se.kth.ics.pwnpr3d.layer1.Identity;

import java.util.HashMap;

// TODO rename this class "MySQL Server" or "mysqld" at some point
public class DatabaseServer extends NetworkedApplication {

   /**
    * systemDB contains tables that store information required by the MySQL server as it runs.
    * @implNote We consider systemDB as the root DB, which contains all the others. It means that if the attacker
    * get access to systemDB, he would also get access to the regular databases.
    * @url http://dev.mysql.com/doc/refman/5.7/en/system-database.html
    */
   private Data systemDB = new Data("systemDB", this, false);
   private HashMap<String,Data> databases = new HashMap<>();
   private HashMap<String,Account> dbAccounts = new HashMap<>();
   private Account adminAccount = newAccount("DBA",PrivilegeType.Administrator);
   private OperatingSystem os;

   public DatabaseServer(String name, OperatingSystem os, Account osPrivileges) {
      super(name, os, osPrivileges);
      this.os = os;
      addOwnedData(systemDB);
      addRequiredData(systemDB);
      getAdministrator().addAuthorizedReadWrite(systemDB);
   }

   public Data getSystemDB() {
      return systemDB;
   }

   /**
    * Creation of a Database. The server has logical access to it, and administrator privileges allows r/w access
    * since the db is contained in SystemDB
    * @param name
    * @return
     */
   public Data newDatabase(String name) {
      Data db = new Data(name+"_DB",this,false);
      addOwnedData(db);
      systemDB.addBody(db);
      databases.put(name,db);
      return db;
   }

   // TODO Account Creation: method with costs included

   /**
    * Creation of a new account, typically for a Web Application to access its data.
    * @param name name of the account
    * @param privileges level of privileges on the DBMS. Simply put, is DBA or not.
    * @return
     */
   public Account newAccount(String name, PrivilegeType privileges) {
      Account account = new Account(name,this);
      Data credentialsDataInDB = account.newCredentialsData(true);
      systemDB.addBody(credentialsDataInDB);
      // TODO make clean getter for unencrypted form from encrypted data
      getAdministrator().addAuthorizedReadWrite(credentialsDataInDB.getBody().iterator().next());
      dbAccounts.put(account.getFullName(),account);
      switch (privileges) {
         case Administrator:
            account.addGrantedIdentity(this.getAdministrator());
            break;
         case User:
            account.addGrantedIdentity(this.getUser());
            break;
         case Guest:
         case None:
            break;
      }
      return account;
   }

   /**
    * Define read/write privileges of a given account to a given database
    * @param account
    * @param database
    * @param canRead
    * @param canWrite
     */
   public void setAccountPrivileges(Identity account, Data database, boolean canRead, boolean canWrite) {
      assert (dbAccounts.containsKey(account.getFullName()));
      assert (systemDB.contains(database));

      if (canRead) account.addAuthorizedRead(database);
      if(canWrite) account.addAuthorizedWrite(database);
   }

   public boolean databaseExists(String name) { return databases.containsKey(name);}

   public Data getDatabase(String name) { return databases.get(name);}

   public OperatingSystem getOs() {
      return os;
   }

}
