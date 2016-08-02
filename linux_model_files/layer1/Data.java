package se.kth.ics.pwnpr3d.layer1;

import org.apache.commons.math3.distribution.GammaDistribution;
import se.kth.ics.pwnpr3d.layer0.Asset;
import se.kth.ics.pwnpr3d.layer0.AttackStepMax;
import se.kth.ics.pwnpr3d.layer0.AttackStepMin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Data extends Asset {

   protected boolean encrypted;
   private Set<Data>        body                    = new HashSet<>();
   private Set<Agent>       requiredAgents          = new HashSet<>();
   private Set<Information> representedInfo         = new HashSet<>();

   private AttackStepMin access = new AttackStepMin("access", this);
   private AttackStepMin authorizedRead = new AttackStepMin("authorizedRead", this);
   private AttackStepMin authorizedWrite = new AttackStepMin("authorizedWrite", this);
   private AttackStepMax compromiseRead = new AttackStepMax("compromiseRead", this);
   private AttackStepMax compromiseWrite = new AttackStepMax("compromiseWrite", this);
   private AttackStepMin denyService = new AttackStepMin("denyService", this);

   public Data(String name, boolean encrypted) {
      super(name);
      this.encrypted = encrypted;
   }

   public Data(String name, Asset superAsset, boolean encrypted) {
      super(name, superAsset);
      this.encrypted = encrypted;
   }

   @Override
   public void initializeCausality() {
      access.addChildren(compromiseRead, new GammaDistribution(0.5,1.0));
      access.addChildren(compromiseWrite, new GammaDistribution(0.7,1.2));
      authorizedRead.addChildren(compromiseRead, new GammaDistribution(0.6,1.1));
      authorizedWrite.addChildren(compromiseWrite, new GammaDistribution(0.9,1.3));
      for (Data datum : body) {
         compromiseRead.addChildren(datum.getAccess(), new GammaDistribution(0.5,0.9));
         compromiseWrite.addChildren(datum.getAccess(), new GammaDistribution(0.9,1.2));
         denyService.addChildren(datum.getDenyService(), new GammaDistribution(0.3,0.4));
         if (!datum.isEncrypted()) {
            compromiseRead.addChildren(datum.getAuthorizedRead(), new GammaDistribution(0.5,0.8));
            compromiseWrite.addChildren(datum.getAuthorizedWrite(), new GammaDistribution(0.7,1.1));
         }
      }
      super.initializeCausality();
   }

   /**
    * Getters & Setters
    *
    * @param data*/

   public void addBody(Data data) {
      this.body.add(data);
   }

   public void addBodyAll(Set<Data> data) {
      this.body.addAll(data);
   }

   public void addRequiredAgent(Agent agent) {
      this.requiredAgents.add(agent);
   }

   protected void addRepresentedInfo(Information representedInfo) {
      this.representedInfo.add(representedInfo);
   }

   public void removeRepresentedInfo(Information representedInfo) {
      this.representedInfo.remove(representedInfo);
   }

   public boolean contains(Data soughtData) {
      if (body.isEmpty()) {
         return false;
      }
      else if (body.contains(soughtData)) {
         return true;
      }
      else {
         boolean someBodyContainsSoughtData = false;
         Iterator it = body.iterator();
         while (!someBodyContainsSoughtData && it.hasNext()) {
            if (((Data) it.next()).contains(soughtData)) {
               someBodyContainsSoughtData = true;
            }
         }
         return someBodyContainsSoughtData;
      }
   }

   public Set<Information> getRepresentedInfo() {
      return representedInfo;
   }

   public Set<Data> getBody() {
      return body;
   }

   public Set<Agent> getRequiredAgents() {
      return requiredAgents;
   }

   public boolean isEncrypted() {
      return encrypted;
   }

   public AttackStepMin getAccess() {
      return access;
   }

   public AttackStepMin getAuthorizedRead() {
      return authorizedRead;
   }

   public AttackStepMin getAuthorizedWrite() {
      return authorizedWrite;
   }

   public AttackStepMax getCompromiseRead() {
      return compromiseRead;
   }

   public AttackStepMax getCompromiseWrite() {
      return compromiseWrite;
   }

   public AttackStepMin getDenyService() {
      return denyService;
   }

}
