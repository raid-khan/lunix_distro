package se.kth.ics.pwnpr3d.layer0;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.Arrays;

public class AttackStepMax extends AttackStep {

   public AttackStepMax(String name, Asset asset) {
      this(name, asset, null);
   }

   public AttackStepMax(String name, Asset asset, AbstractRealDistribution probabilityDistribution) {
      super(name, asset, probabilityDistribution);
   }

   @Override
   public boolean gateCriteria() {
      return getRemainingParents().size() == 0;
   }

   @Override
   public void initTTC() {
      Arrays.fill(getTTCs(), -1);
   }

}
