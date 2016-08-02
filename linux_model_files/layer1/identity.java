package se.kth.ics.pwnpr3d.layer1;

import org.apache.commons.math3.distribution.GammaDistribution;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer0.AttackStepMin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Identity extends Asset {

   private Set<Agent>         authorizedAgents        = new HashSet<>();
   private Set<Identity>      authenticatedIdentities = new HashSet<>();
   private Set<Vulnerability> vulnerabilities         = new HashSet<>();
   private Set<Data>          readData                = new HashSet<>();
   private Set<Data>          writeData               = new HashSet<>();

   private AttackStepMin compromise = new AttackStepMin("compromise", this);

   public Identity(String name, Asset superAsset) {
      super(name, superAsset);
   }

   @Override
   public void initializeCausality() {
      for (Agent authorizedAgent : authorizedAgents) {
         this.compromise.addChildren(authorizedAgent.getAuthorized(), new GammaDistribution(0.5,1.0));
      }
      for (Identity authenticatedIdentity : authenticatedIdentities) {
         this.compromise.addChildren(authenticatedIdentity.getCompromise(), new GammaDistribution(0.7,1.3));
      }
      for (Vulnerability vulnerability : vulnerabilities) {
         this.compromise.addChildren(vulnerability.getAuthorized(), new GammaDistribution(0.4,0.8));
      }
      for (Data readDatum : readData) {
         this.compromise.addChildren(readDatum.getAuthorizedRead(), new GammaDistribution(0.6,1.0));
      }
      for (Data writeDatum : writeData) {
         this.compromise.addChildren(writeDatum.getAuthorizedWrite(), new GammaDistribution(0.8,1.4));
      }
      super.initializeCausality();
   }

   public void addGrantedIdentity(Identity identity) {
      authenticatedIdentities.add(identity);
   }

   public void addAuthorizedAccess(Agent... agents) {
      authorizedAgents.addAll(Arrays.asList(agents));
   }

   public void addAuthorizedRead(Data readData) {
      this.readData.add(readData);
   }

   public void addAuthorizedWrite(Data writeData) {
      this.writeData.add(writeData);
   }

   public void addAuthorizedReadWrite(Data data) {
      readData.add(data);
      writeData.add(data);
   }

   public void addVulnerability(Vulnerability vulnerability) {
      this.vulnerabilities.add(vulnerability);
   }

   public Set<Agent> getAuthorizedAgents() {
      return authorizedAgents;
   }

   public Set<Identity> getAuthenticatedIdentities() {
      return authenticatedIdentities;
   }

   public Set<Vulnerability> getVulnerabilities() {
      return vulnerabilities;
   }

   public Set<Data> getReadData() {
      return readData;
   }

   public Set<Data> getWriteData() {
      return writeData;
   }

   public AttackStepMin getCompromise() {
      return compromise;
   }
}
