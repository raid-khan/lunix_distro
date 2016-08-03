package se.kth.ics.pwnpr3d.layer0;

import org.apache.commons.math3.distribution.AbstractRealDistribution;

import java.util.Arrays;

public class AttackStepMin extends AttackStep {

   public AttackStepMin(String name, Asset asset) {
      super(name, asset, null);
   }

   public AttackStepMin(String name, Asset asset, AbstractRealDistribution probabilityDistribution) {
      super(name, asset, probabilityDistribution);
   }


   @Override
   public boolean gateCriteria() {
      return true;
   }

   @Override
   public void initTTC() {
      Arrays.fill(getTTCs(),-1);
   }

}
