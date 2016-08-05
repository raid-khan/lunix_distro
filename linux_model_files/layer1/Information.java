package se.kth.ics.pwnpr3d.layer1;

import org.apache.commons.math3.distribution.GammaDistribution;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer0.AttackStepMax;
import se.kth.ics.pwnpr3d.layer0.AttackStepMin;
import se.kth.ics.pwnpr3d.layer0.Attacker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Information extends Asset {

   // TODO add more restrictions, for now only dependency cycles between are prevented

   private double confidentialityCost;
   private double integrityCost;
   private double availabilityCost;
   private double[] confidentialityTtcs;
   private double[] integrityTtcs;
   private double[] availabilityTtcs;

   private Set<Identity> authenticatedIdentities = new HashSet<>();
   private Information parentInfo;
   private HashSet<Data> representingData    = new HashSet<>();
   private HashSet<Information> ownedSubInfo = new HashSet<>();

   private AttackStepMin confidentialityBreach = new AttackStepMin("confidentialityBreach", this);
   private AttackStepMax integrityBreach = new AttackStepMax("integrityBreach", this);
   private AttackStepMax availabilityBreach = new AttackStepMax("availabilityBreach", this);

   public Information(String name, double confidentialityCost, double integrityCost, double availabilityCost) {
      super(name);
      setCost(confidentialityCost, integrityCost, availabilityCost);
   }

   public Information(String name, Asset superAsset, double confidentialityCost, double integrityCost, double availabilityCost) {
      super(name, superAsset);
      setCost(confidentialityCost, integrityCost, availabilityCost);
   }

   @Override
   public void initializeCausality() {
      for (Data datum : representingData) {
         datum.getCompromiseRead().addChildren(getConfidentialityBreach(), new GammaDistribution(0.36,0.72));
         datum.getCompromiseWrite().addChildren(getIntegrityBreach(), new GammaDistribution(0.001,0.001));
         datum.getDenyService().addChildren(getAvailabilityBreach(), new GammaDistribution(0.001,0.001));
         //TODO add possibility to define direct causality, i.e. with TTC from Attacksteps A to B = 0
      }
      for (Information subInfo : ownedSubInfo) {
         subInfo.getConfidentialityBreach().addChildren(getConfidentialityBreach());
         subInfo.getIntegrityBreach().addChildren(getIntegrityBreach());
         subInfo.getAvailabilityBreach().addChildren(getAvailabilityBreach());
      }
      for (Identity authenticatedIdentity : authenticatedIdentities) {
         confidentialityBreach.addChildren(authenticatedIdentity.getCompromise(), new GammaDistribution(0.5,1.0));
      }
      super.initializeCausality();
   }

   private void setCost(double confidentialityCost, double integrityCost, double availabilityCost) {
      this.confidentialityCost = confidentialityCost;
      this.integrityCost = integrityCost;
      this.availabilityCost = availabilityCost;
      confidentialityTtcs = new double[Attacker.SAMPLING_SIZE];
      integrityTtcs = new double[Attacker.SAMPLING_SIZE];
      availabilityTtcs = new double[Attacker.SAMPLING_SIZE];
   }

   private boolean isLeadingToForwardDependencyCycle(Information potentialSubInfo) {
      if (potentialSubInfo.isParentOf(this)) return true;
      else if (parentInfo != null) return isLeadingToForwardDependencyCycle(parentInfo);
      else return false;
   }

   public Information getParentInfo() {
      return parentInfo;
   }

   public void setParentInfo(Information parentInfo) {
      this.parentInfo = parentInfo;
   }

   private boolean isParentOf(Information subInfo){
      return ownedSubInfo.contains(subInfo);
   }

   private boolean isChildOf(Information parentInfo){
      return this.parentInfo.equals(parentInfo);
   }

   public AttackStepMin getConfidentialityBreach() {
      return confidentialityBreach;
   }

   public AttackStepMax getIntegrityBreach() {
      return integrityBreach;
   }

   public AttackStepMax getAvailabilityBreach() {
      return availabilityBreach;
   }

   public void addOwnedSubInfo(Information ownedSubInfo) throws Exception {
      if(isLeadingToForwardDependencyCycle(ownedSubInfo))
         throw new Exception("Dependency cycle detected between Information "+this.getName()+" and "+ownedSubInfo.getName()+". Abort.");
      this.ownedSubInfo.add(ownedSubInfo);
      ownedSubInfo.setParentInfo(this);
   }

   public void addRepresentingData(Data representingData) {
      this.representingData.add(representingData);
      representingData.addRepresentedInfo(this);
   }

   public void removeRepresentingData(Data representingData) {
      this.representingData.remove(representingData);
      representingData.removeRepresentedInfo(this);
   }

   public HashSet<Data> getRepresentingData() {
      return representingData;
   }

   public HashSet<Information> getSubInfo() {
      return ownedSubInfo;
   }

   public double getConfidentialityCost() {
      return confidentialityCost;
   }

   public double getIntegrityCost() {
      return integrityCost;
   }

   public double getAvailabilityCost() {
      return availabilityCost;
   }

   public void addTtcConfidentiality(int index, double ttc) {
      confidentialityTtcs[index] = ttc;
   }

   public void addTtcIntegrity(int index, double ttc) {
      integrityTtcs[index] = ttc;
   }

   public void addTtcAvailability(int index, double ttc) {
      availabilityTtcs[index] = ttc;
   }

   public void setConfidentialityCost(double confidentialityCost) {
      this.confidentialityCost = confidentialityCost;
   }

   public void setIntegrityCost(double integrityCost) {
      this.integrityCost = integrityCost;
   }

   public void setAvailabilityCost(double availabilityCost) {
      this.availabilityCost = availabilityCost;
   }

   public Set<Identity> getAuthenticatedIdentities() {
      return authenticatedIdentities;
   }

   public void addAuthenticatedIdentities(Identity... identity) {
      this.authenticatedIdentities.addAll(Arrays.asList(identity));
   }

}
