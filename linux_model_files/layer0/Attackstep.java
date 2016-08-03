package se.kth.ics.pwnpr3d.layer0;

import java.util.*;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

public abstract class AttackStep {

   private static HashSet<AttackStep> allAttackSteps = new HashSet<>();

   public double[] ttcs;
   private String          name;
   private Asset           asset;

   private Set<AttackStep> children          = new HashSet<>();
   private HashMap<AttackStep,AbstractRealDistribution> valuedChildren = new HashMap<>();
   private HashMap<AttackStep,Double> sampledChildren = new HashMap<>();
   private Set<AttackStep> parents           = new HashSet<>();
   private Set<AttackStep> remainingParents  = new HashSet<>();
   private AbstractRealDistribution          probaDistribution;

   private boolean         compromised       = false;
   private boolean         sampled           = false;

   public AttackStep(String name, Asset asset, AbstractRealDistribution probabilityDistribution) {
      this(name, asset);
      this.probaDistribution = probabilityDistribution;
      allAttackSteps.add(this);
      ttcs = new double[Attacker.SAMPLING_SIZE];
}

   public AttackStep(String name, Asset asset) {
      this.name = name;
      this.asset = asset;
   }

   public void compromise(AttackStep caller) {
      assert (asset.isInitialized());
      if (!isCompromised()) {
         remainingParents.remove(caller);
         if (gateCriteria()) {
            compromised = true;
            for (AttackStep child : children) {
               child.compromise(this);
            }
         }
      }
   }

   public void sampleChildren() {
      if (!sampled) {
         sampled = true;
         for (Map.Entry entry : valuedChildren.entrySet()) {
            AbstractRealDistribution distrib = (AbstractRealDistribution) entry.getValue();
            sampledChildren.put((AttackStep) entry.getKey(), distrib.sample());
            ((AttackStep) entry.getKey()).sampleChildren();
         }
      }
   }

   public abstract boolean gateCriteria();

    /**
    * Getters & Setters
    **/

   public String getFullName() {
      return asset.getFullName() + "." + name;
   }

   @Override
   public String toString() {
      return getFullName();
   }

   public void addChildren(AttackStep... attackSteps) {
      for (AttackStep child : attackSteps) {
         children.add(child);
         if (!child.getParents().contains(this)) {
            child.addParents(this);
         }
      }
   }

   public void addChildren(AttackStep child, AbstractRealDistribution ttc) {
      valuedChildren.put(child, ttc);
      addChildren(child);
   }

   public void removeChildren(AttackStep... attackSteps) {
      for (AttackStep child : attackSteps) {
         children.remove(child);
         if (child.getParents().contains(this)) {
            child.removeParents(this);
         }
      }
   }

   public void addParents(AttackStep... attackSteps) {
      for (AttackStep parent : attackSteps) {
         parents.add(parent);
         this.addRemainingParent(parent);
         if (!parent.getChildren().contains(this)) {
            parent.addChildren(this);
         }
      }
   }

   public void removeParents(AttackStep... attackSteps) {
      for (AttackStep parent : attackSteps) {
         parents.remove(parent);
         this.removeRemainingParent(parent);
         if (parent.getChildren().contains(this)) {
            parent.removeChildren(this);
         }
      }
   }

   public HashMap<AttackStep, AbstractRealDistribution> getValuedChildren() {
      return valuedChildren;
   }

   public Set<AttackStep> getAllAncestors(int n) {
      return getAllAncestors(n, new HashSet<AttackStep>());
   }

   private Set<AttackStep> getAllAncestors(int n, Set<AttackStep> visitedSteps) {
      Set<AttackStep> attackSteps = new HashSet<>();
      attackSteps.addAll(visitedSteps);
      for (AttackStep parent : parents) {
         if (!visitedSteps.contains(parent)) {
            attackSteps.add(parent);
            attackSteps.addAll(parent.getAllAncestors(n - 1, attackSteps));
         }
         if (n < 1) {
            break;
         }
      }
      return attackSteps;
   }

   public Set<AttackStep> getAllProgeny(int n) {
      return getAllChildren(n, new HashSet<AttackStep>());
   }

   private Set<AttackStep> getAllChildren(int n, Set<AttackStep> visitedSteps) {
      Set<AttackStep> attackSteps = new HashSet<>();
      attackSteps.addAll(visitedSteps);
      for (AttackStep child : children) {
         if (!visitedSteps.contains(child)) {
            attackSteps.add(child);
            attackSteps.addAll(child.getAllChildren(n - 1, attackSteps));
         }
         if (n < 1) {
            break;
         }
      }
      return attackSteps;
   }

   public HashMap<AttackStep, Double> getSampledChildren() {
      return sampledChildren;
   }

   public void setTTC(int iteration, double ttc) {
      ttcs[iteration] = ttc;
   }

   public double getTTC(int iteration) {
      return ttcs[iteration];
   }

   public double[] getTTCs() {
      return ttcs;
   }

   public void addRemainingParent(AttackStep attackStep) {
      remainingParents.add(attackStep);
   }

   public void removeRemainingParent(AttackStep attackStep) {
      remainingParents.remove(attackStep);
   }

   public String getName() {
      return name;
   }

   public Asset getAsset() {
      return asset;
   }

   public Set<AttackStep> getChildren() {
      return children;
   }

   public Set<AttackStep> getParents() {
      return parents;
   }

   public Set<AttackStep> getRemainingParents() {
      return remainingParents;
   }

   public boolean isCompromised() {
      return compromised;
   }

   public void removeAllRemainingParents() {
      remainingParents.clear();
   }

   public void setCompromised(boolean cmpr) {
      compromised = cmpr;
   }

   public abstract void initTTC();

   public void initStatus() {
      sampled = false;
      compromised = false;
      remainingParents.addAll(parents);
   }

   public static HashSet<AttackStep> getAllAttackSteps() {
      return allAttackSteps;
   }

   public static void clearAllAttackSteps() {
      allAttackSteps.clear();
   }
}
