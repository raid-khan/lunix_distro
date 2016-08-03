package se.kth.ics.pwnpr3d.datatypes;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class BernoulliDistribution extends AbstractRealDistribution {

   double p;

   public BernoulliDistribution(double p) {
      this.p = p;
   }

   @Override
   public double density(double x) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public double cumulativeProbability(double x) {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public double getNumericalMean() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public double getNumericalVariance() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public double getSupportLowerBound() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public double getSupportUpperBound() {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public boolean isSupportLowerBoundInclusive() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isSupportUpperBoundInclusive() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isSupportConnected() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public double sample() {
      boolean b = (new UniformRealDistribution()).sample() < p;
      if (b)
         return 0;
      else {
         return Constants.infinity;
      }
   }

}
