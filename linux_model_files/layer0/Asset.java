package se.kth.ics.pwnpr3d.layer0;

import java.util.HashSet;
import java.util.Set;

// TODO !! Determine what data can be extracted from the CVE
public abstract class Asset {

   private static Set<Asset> allAssets = new HashSet<>();
   private String name;
   private Asset superAsset;
   private boolean isInitialized = false;

   protected Asset(String name, Asset superAsset) {
      this.name = name;
      this.superAsset = superAsset;
      allAssets.add(this);
   }

   protected Asset(String name) {
      this.name = name;
      allAssets.add(this);
   }

   public static Set<Asset> getAllAssets() {
      return allAssets;
   }

   public static void clearAllAssets() {
      allAssets.clear();
   }

   public void initializeCausality() {
      this.isInitialized = true;
   }

   /**
    * Getters & Setters
    **/

   public Set<AttackStep> getAllAttackSteps() {
      return null;
   }

   public boolean isInitialized() {
      return isInitialized;
   }

   public String getName() {
      return name;
   }

   public String getFullName() {
      if (superAsset == null)
         return name;
      else
         return superAsset.getFullName() + "." + name;
   }
}
