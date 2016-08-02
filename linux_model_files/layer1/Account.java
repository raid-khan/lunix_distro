package se.kth.ics.pwnpr3d.layer1;

import se.kth.ics.pwnpr3d.layer0.Asset;

import java.util.Set;

public class Account extends Identity {

    private Information credentials;
    private Set<Data> credentialsData;

    // TODO Account credentialsData must be information contained in Data
    public Account(String name, Asset superAsset) {
        super(name, superAsset);
        credentials = new Information(name+"_creds", 5, 5 , 5);
        credentials.addAuthenticatedIdentities(this);
    }

    public Account(String name, Asset superAsset, Information credentials) {
        super(name, superAsset);
        this.credentials = credentials;
        credentials.addAuthenticatedIdentities(this);
    }

    public Information getAccountCredentials() {
        return credentials;
    }

    public Data newCredentialsData(boolean encrypted) {
        Data nonEncryptedCredentialsData = new Data("nonEncryptedData", this, false);
        credentials.addRepresentingData(nonEncryptedCredentialsData);
        if (encrypted) {
            Data encryptedCredentialsData = new Data("encryptedData", this, true);
            encryptedCredentialsData.addBody(nonEncryptedCredentialsData);
            return encryptedCredentialsData;
        }
        return nonEncryptedCredentialsData;
    }
}
