package se.kth.ics.pwnpr3d.layer1;

import org.apache.commons.math3.distribution.GammaDistribution;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer0.AttackStepMax;
import se.kth.ics.pwnpr3d.layer0.AttackStepMin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// TODO #! Add redundancy relationship to DoS logic

public abstract class Agent extends Asset {

   private Set<Agent>         requiredAgents  = new HashSet<>();
   private Set<Agent>         usedAgents      = new HashSet<>();
   private Set<Data>          requiredData    = new HashSet<>();
   private Set<Data>          ownedData       = new HashSet<>();
   private Set<Vulnerability> vulnerabilities = new HashSet<>();

   // TODO !# Should discovery of agents and data constitute part of the
   // attack graph? If so, which are parents and children?
   // private AttackStepMin discover = new AttackStepMin("discover", this);
   private AttackStepMin access = new AttackStepMin("access", this);
   private AttackStepMin authorized = new AttackStepMin("authorized", this);
   private AttackStepMax compromise = new AttackStepMax("compromise", this);
   private AttackStepMin denyService = new AttackStepMin("denyService", this);

   private boolean vulnerabilitiesSampled = false;

   public Agent(String name) {
      super(name);
   }

   public Agent(String name, Asset superAsset) {
      super(name, superAsset);
   }

   public void logicalConnect(Agent agent) {
         usedAgents.add(agent);
         agent.usedAgents.add(this);
   }

   public void addRequiredAgent(Agent agent) {
      requiredAgents.add(agent);
      logicalConnect(agent);
   }

   @Override
   public void initializeCausality() {
      access.addChildren(compromise, new GammaDistribution(0.5,1.0));
      authorized.addChildren(compromise, new GammaDistribution(0.6,1.1));

      for (Agent requiredAgent : requiredAgents) {
         requiredAgent.getDenyService().addChildren(denyService, new GammaDistribution(0.5,0.9));
      }
      for (Agent usedAgent : usedAgents) {
         compromise.addChildren(usedAgent.getAccess(), new GammaDistribution(0.8,1.0));
      }
      for (Vulnerability vulnerability : vulnerabilities) {
         compromise.addChildren(vulnerability.getAccess(), new GammaDistribution(0.4,1.1));
      }
      for (Data requiredDatum : requiredData) {
         requiredDatum.getDenyService().addChildren(denyService, new GammaDistribution(0.5,0.8));
      }
      for (Data ownedDatum : ownedData) {
         compromise.addChildren(ownedDatum.getAccess(), new GammaDistribution(0.7,1.0));
         denyService.addChildren(ownedDatum.getDenyService(), new GammaDistribution(0.5,0.6));
      }
      super.initializeCausality();
   }

   public void sampleVulnerabilities() {
      if (vulnerabilitiesSampled == true) return;
      else vulnerabilitiesSampled = true;
   }

   /**
    * Getters & Setters
    **/

   protected void addVulnerabilities(Vulnerability... vulnerabilities) {
      this.vulnerabilities.addAll(Arrays.asList(vulnerabilities));
   }

   public void addOwnedData(Data... data) {
      ownedData.addAll(Arrays.asList(data));
   }

   public void addRequiredData(Data... data) {
      requiredData.addAll(Arrays.asList(data));
   }

   public Set<Data> getOwnedData() {
      return ownedData;
   }

   public AttackStepMin getAccess() {
      return access;
   }

   public AttackStepMin getAuthorized() {
      return authorized;
   }

   public AttackStepMax getCompromise() {
      return compromise;
   }

   public AttackStepMin getDenyService() {
      return denyService;
   }

}
