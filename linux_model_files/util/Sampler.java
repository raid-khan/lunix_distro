package se.kth.ics.pwnpr3d.util;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import se.kth.ics.pwnpr3d.layer0.Attacker;

public class Sampler {

    public static boolean isDeterministic = false;              // Used for testing.

    private static UniformRealDistribution urDist = new UniformRealDistribution();
    public int nSamples;
    public Attacker attacker;

    public Sampler(int nSamples, Attacker attacker) {
        this.nSamples = nSamples;
        this.attacker = attacker;
    }

    public static boolean bernoulliDistribution(double p) {
        if (isDeterministic)
            return p >= 0.5;
        else
            return urDist.sample() < p;
    }

}
