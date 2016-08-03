package se.kth.ics.pwnpr3d.layer0;

import se.kth.ics.pwnpr3d.layer1.Agent;
import se.kth.ics.pwnpr3d.layer1.Information;
import se.kth.ics.pwnpr3d.util.ChartDrawer;

import java.util.*;
import java.util.stream.Collectors;

// TODO !! TTC algorithm
// TODO !! Test cost of security

public class Attacker {

    public static int SAMPLING_SIZE = 8000;

    private Set<AttackStep> pointsOfAttack = new HashSet<AttackStep>();
    private Set<ArrayList<double[]>> costOfSecurity;
    private double cost = 0;
    private HashSet<AttackStep> compromisedAttackSteps;
    private HashSet<AttackStep> priorityList;
    private int iteration;

 /*   public void attackWithTTC() { attack();}*/

    public void attackWithTTC() {
        iteration = 0;
        compromisedAttackSteps = new HashSet<>();
        costOfSecurity = new HashSet<>();
        priorityList = new HashSet<>();

        Asset.getAllAssets().stream().
                filter(asset -> Asset.class.isAssignableFrom(asset.getClass())).forEach(Asset::initializeCausality);

        AttackStep.getAllAttackSteps().forEach(AttackStep::initTTC);

        while(iteration < SAMPLING_SIZE) {
            priorityList.clear();
            ArrayList<double[]> tempCoS = new ArrayList<>();
            AttackStep.getAllAttackSteps().forEach(AttackStep::initStatus);

            for (AttackStep pointOfAttack : pointsOfAttack) {
                pointOfAttack.sampleChildren();
                pointOfAttack.removeAllRemainingParents();
                pointOfAttack.setTTC(iteration, 0);
                pointOfAttack.setCompromised(true);
                this.priorityList.add(pointOfAttack);
            }

            while (priorityList.size() > 0) {
                AttackStep as = getMinimum(priorityList, iteration);
                compromisedAttackSteps.add(as);
                priorityList.remove(as);
                visitChildren(as, iteration);
            }

            Asset.getAllAssets().stream().filter(asset -> asset instanceof Information).forEach(asset -> {
                Information i = ((Information) asset);
                if (i.getConfidentialityBreach().isCompromised()) {
                    tempCoS.add(new double[]{i.getConfidentialityBreach().getTTC(iteration), i.getConfidentialityCost()});
                    i.addTtcConfidentiality(iteration, i.getConfidentialityBreach().getTTC(iteration));
                } else {
                    i.addTtcConfidentiality(iteration, -1);
                }
                if (i.getIntegrityBreach().isCompromised()) {
                    tempCoS.add(new double[]{i.getIntegrityBreach().getTTC(iteration), i.getIntegrityCost()});
                    i.addTtcIntegrity(iteration, i.getIntegrityBreach().getTTC(iteration));
                } else {
                    i.addTtcIntegrity(iteration, -1);
                }
                if (i.getAvailabilityBreach().isCompromised()) {
                    tempCoS.add(new double[]{i.getAvailabilityBreach().getTTC(iteration), i.getAvailabilityCost()});
                    i.addTtcAvailability(iteration, i.getAvailabilityBreach().getTTC(iteration));
                } else {
                    i.addTtcAvailability(iteration, -1);
                }
            });

            costOfSecurity.add(tempCoS);
            iteration++;
        }
        for (AttackStep as : compromisedAttackSteps) {
            if(as.getName().endsWith("compromiseRead"))
                ChartDrawer.getTTCHistogram(as.getFullName(),as.getTTCs());
        }
        if (!costOfSecurity.iterator().next().isEmpty()) {
            ChartDrawer.getInterpolatedCoSv2(costOfSecurity);
        }
    }

    private void visitChildren(AttackStep caller, int iteration) {
        for (AttackStep child : caller.getChildren()) {
            child.removeRemainingParent(caller);
            if (child instanceof AttackStepMin && (child.getTTC(iteration) == -1 ||
                    (child.getTTC(iteration) > caller.getTTC(iteration)
                    + caller.getSampledChildren().get(child)))) {
                child.setTTC(iteration, caller.getTTC(iteration) + caller.getSampledChildren().get(child));
                child.setCompromised(true);
                priorityList.add(child);
            } else if (child instanceof AttackStepMax) {
                if (child.getTTC(iteration) < caller.getTTC(iteration) + caller.getSampledChildren().get(child)) {
                    child.setTTC(iteration, caller.getTTC(iteration) + caller.getSampledChildren().get(child));
                }
                if (child.getRemainingParents().isEmpty()) {
                    child.setCompromised(true);
                    priorityList.add(child);
                }
            }
        }
    }

    private AttackStep getMinimum(Set<AttackStep> attackSteps, int iteration) {
        AttackStep minimum = null;
        for (AttackStep as : attackSteps) {
            if (minimum == null) {
                minimum = as;
            } else {
                if (as.getTTC(iteration) < minimum.getTTC(iteration)) {
                    minimum = as;
                }
            }
        }
        return minimum;
    }


   public void attack() {

      List elt = Asset.getAllAssets().stream().filter(asset -> asset instanceof Agent).collect(Collectors.toList());
       for (Object agent :elt) {
           ((Agent)agent).sampleVulnerabilities();
       }

       Asset.getAllAssets().stream().
               filter(asset -> Asset.class.isAssignableFrom(asset.getClass())).forEach(Asset::initializeCausality);

      AttackStep firstAttackStep = new AttackStepMin("firstAttackStep", null);

      for (AttackStep pointOfAttack : pointsOfAttack) {
         pointOfAttack.removeAllRemainingParents();
         pointOfAttack.compromise(firstAttackStep);
      }

      for (Asset asset : Asset.getAllAssets()) {
         if (Information.class.isAssignableFrom(asset.getClass())) {
            Information i = ((Information) asset);
            if (i.getConfidentialityBreach().isCompromised()) {
                cost += +i.getConfidentialityCost();
            }
            if (i.getIntegrityBreach().isCompromised()) {
                cost += +i.getIntegrityCost();
            }
            if (i.getAvailabilityBreach().isCompromised()) {
                cost += +i.getAvailabilityCost();
            }
         }
      }
      // System.out.printf("\nThe expected cost of security is %2.0f\n\n\n",
      // cost);
   }

    public void addAttackPoint(AttackStep as) {
        this.pointsOfAttack.add(as);
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }



}
