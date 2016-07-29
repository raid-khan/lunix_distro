package se.kth.ics.pwnpr3d.layer2.software;

import se.kth.ics.pwnpr3d.datatypes.EncryptionType;
import se.kth.ics.pwnpr3d.datatypes.PrivilegeType;

import se.kth.ics.pwnpr3d.layer1.*;
import se.kth.ics.pwnpr3d.layer2.cwe.*;
import se.kth.ics.pwnpr3d.util.Sampler;

import java.util.*;

public class WebApplication extends Application {

    private Data sourceCode = new Data("source code",this, false);
    private Information sourceCodeInfo = new Information("sourceCodeInfo",20000,5000,4000);
    private Data staticData = new Data("static data",this, false);
    private WebServer webServer;
    private DatabaseServer dbServer;
    private String dbName;
    private HashMap<String,Account> accounts = new HashMap<>();
    private HashMap<Account,Data> accountsData = new HashMap<>();
    private Account dbAccount;
    private EncryptionType pwEncryption = EncryptionType.None;

    /**
     * WebApp constructor when there is no connection with a DBMS
     * @param name name of the application
     * @param webServer the webserver running the app
     */
    public WebApplication(String name, WebServer webServer) {
        super(name, webServer);
        this.webServer = webServer;
        sourceCodeInfo.addRepresentingData(sourceCode);
        addRequiredData(sourceCode);
        sourceCode.contains(staticData);
        addOwnedData(staticData);
        getGuest().addAuthorizedRead(staticData);
    }

    /**
     * WebApp constructor for apps that need a DBMS to run
     * @param name name of the application
     * @param webServer the webserver running the app
     * @param dbServer
     */
    public WebApplication(String name, WebServer webServer, DatabaseServer dbServer) {
        this(name, webServer);
        this.dbServer = dbServer;
        initializeVulnProbabilities();
    }

    /**
     * Method to hardcode default vulnerabilities' probability
     */
    private void initializeVulnProbabilities() {

    //    addVulnerabilityProbability(CWEType.CWE_89,PrivilegeType.User,AccessVectorType.Adjacent_Network,0);
    //    addVulnerabilityProbability(CWEType.CWE_89,PrivilegeType.Guest,AccessVectorType.Adjacent_Network,0);
    }

    /**
     * Creation of a logical account, e.g. web form registration
     * @param name name of the account
     * @param privileges level of privileges on the application
     * @return account object
     */
    public Account newAccount(String name, PrivilegeType privileges) {
        assert (dbServer != null);
        Account account = new Account(name,this);
        Data credentialsData;
        // TODO improve influence of encryption type (currently it is binary)
        if(pwEncryption == EncryptionType.None) {
            credentialsData = account.newCredentialsData(false);
        } else {
            credentialsData = account.newCredentialsData(true);
        }
        Data personalData = new Data("persoData",account,false);
        //TODO allow to define CIA cost per account
        Information personalInfo = new Information("persoInfo", 5, 5 ,5);
        personalInfo.addRepresentingData(personalData);
        account.addAuthorizedReadWrite(personalData);
        accountsData.put(account,personalData);
        dbServer.getDatabase(dbName).addBody(credentialsData);
        dbServer.getDatabase(dbName).addBody(personalData);
        dbServer.addOwnedData(credentialsData,personalData);
        accounts.put(account.getFullName(),account);
        switch (privileges) {
            // Admin of the application's logic, not of the program itself, unless there is a CMS backend
            case Administrator:
            case User:
                account.addGrantedIdentity(this.getUser());
                break;
            case Guest:
            case None:
                break;
        }
        return account;
    }

    public void setDbAccount(Account dbAccount) {
        this.dbAccount = dbAccount;
        Data credentialsData = this.dbAccount.newCredentialsData(false);
        sourceCode.addBody(credentialsData);
    }

    public Data getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(Data sourceCode) {
        sourceCodeInfo.removeRepresentingData(this.sourceCode);
        this.sourceCode = sourceCode;
        sourceCodeInfo.addRepresentingData(this.sourceCode);
    }

    public void setSourceCodeValue(double confidentialityCost, double integrityCost, double availabilityCost) {
        sourceCodeInfo.setConfidentialityCost(confidentialityCost);
        sourceCodeInfo.setIntegrityCost(integrityCost);
        sourceCodeInfo.setAvailabilityCost(availabilityCost);
    }

    @Override
    public void sampleVulnerabilities() {
        super.sampleVulnerabilities();

        for(CWEInfo cweInfo : getPotentialVulnerabilities()) {
            if (Sampler.bernoulliDistribution(cweInfo.getProbability())) {
                try {
                    addVulnerabilities(CWEFactory.newCWEVulnerability(cweInfo.getCWEType(),cweInfo.getPrivilegeType(),cweInfo.getAccessVectorType(),this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public WebServer getWebServer() {
        return webServer;
    }

    public Account getDbAccount() {
        return dbAccount;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Data getAccountData(Account account) {
        return accountsData.get(account);
    }

    public Collection<Account> getAccounts() {
        return accounts.values();
    }

    public Data getStaticData() {
        return staticData;
    }

    public EncryptionType getPwEncryption() {
        return pwEncryption;
    }

    public void setPwEncryption(EncryptionType pwEncryption) {
        this.pwEncryption = pwEncryption;
    }
}
