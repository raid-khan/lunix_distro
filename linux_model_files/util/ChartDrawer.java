package se.kth.ics.pwnpr3d.util;

/**
 * Created by avernotte on 3/2/16.
 */
import java.io.*;
import java.util.*;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jfree.chart.*;
import org.jfree.data.statistics.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;

public class ChartDrawer {
    public static void getTTCHistogram(String asName, double[] ttcs) {
            int number = 25;
            HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramType.SCALE_AREA_TO_1);
            dataset.addSeries(asName + " TTC",ttcs,number);
            String plotTitle = asName + " TTC";
            String xaxis = "days";
            String yaxis = "frequency";
            PlotOrientation orientation = PlotOrientation.VERTICAL;
            boolean show = false;
            boolean toolTips = false;
            boolean urls = false;
            JFreeChart chart = ChartFactory.createHistogram( plotTitle, xaxis, yaxis,
                    dataset, orientation, show, toolTips, urls);
            int width = 500;
            int height = 300;
            try {
                ChartUtilities.saveChartAsPNG(new File(asName+".PNG"), chart, width, height);
            } catch (IOException e) {}
    }

    public static void getInterpolatedCoSv2(Set<ArrayList<double[]>> ttcs) {
        ArrayList<double[]> sortedTtcos = sumCostSortByTTCAndFlattenAll(ttcs);
        ArrayList<double[]> statValues = new ArrayList<>();
        double maxTTC = sortedTtcos.get(sortedTtcos.size()-1)[0];
        double timeInterval = 0;
        while(timeInterval+1 < maxTTC) {
            ArrayList<double[]> timeIntervalCost = extractTuplesFromTimeInterval(sortedTtcos, timeInterval, timeInterval + .5);
            double[] intervalResults = new double[]{0,0,0};
            StandardDeviation sd = new StandardDeviation();
            double[] costs = extractCosts(timeIntervalCost);
            if (costs.length > 100) {
                intervalResults[0] = StatUtils.percentile(costs,5);
                intervalResults[1] = StatUtils.percentile(costs,50);
                intervalResults[2] = StatUtils.percentile(costs,95);
            } else if (costs.length > 3 && sd.evaluate(costs,StatUtils.mean(costs))>0) {
                EmpiricalDistribution ed = getEmpiricalDistrib(timeIntervalCost);
                try {
                    double[] samples = sampleEmpiricalDistrib(ed, 1000);
                    intervalResults[0] = samples[50];
                    intervalResults[1] = samples[500];
                    intervalResults[2] = samples[950];
                } catch (NotStrictlyPositiveException e) {
                    System.err.println("Standard dev Err. Sample size: "+costs.length);
                    System.err.println(e.getMessage()+"\n"+e.getMessage());
                    for(double cost: costs) System.out.print(cost+" ");
                    System.exit(1);
                }
            } else if (!timeIntervalCost.isEmpty()) {
                intervalResults[0] = timeIntervalCost.get(0)[1];
                intervalResults[1] = timeIntervalCost.get(timeIntervalCost.size() / 2)[1];
                intervalResults[2] = timeIntervalCost.get(timeIntervalCost.size() -1)[1];
            }
            statValues.add(intervalResults);
            timeInterval += .5;
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(createPercentileSerie(statValues, 0));
        dataset.addSeries(createPercentileSerie(statValues, 1));
        dataset.addSeries(createPercentileSerie(statValues, 2));
        String plotTitle = "Estimation of Security Risk";
        String xaxis = "Time (days)";
        String yaxis = "Cost ($)";
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean show = true;
        boolean toolTips = false;
        boolean urls = false;
        JFreeChart chart = ChartFactory.createHistogram( plotTitle, xaxis, yaxis,
                dataset, orientation, show, toolTips, urls);
        int width = 500;
        int height = 300;
        try {
            ChartUtilities.saveChartAsPNG(new File("histoCostv2.PNG"), chart, width, height);
        } catch (IOException e) {}
    }

    private static XYSeries createPercentileSerie(ArrayList<double[]> statValues, int index) {
        XYSeries serie = new XYSeries(index==0?"5%":index==1?"50%":"95%"+" percentile");
        for(int cpt=0;cpt<statValues.size();cpt++) {
            serie.add((double)cpt/2,statValues.get(cpt)[index]);
        }
        return serie;
    }

    private static EmpiricalDistribution getEmpiricalDistrib(ArrayList<double[]> ttcs) {
        double[] costs = extractCosts(ttcs);
        EmpiricalDistribution empDistrib = new EmpiricalDistribution();
        empDistrib.load(costs);
        return empDistrib;
    }

    private static double[] sampleEmpiricalDistrib(EmpiricalDistribution ed, int sampleSize) {
        double[] vals = new double[sampleSize];
        for(int i=0;i<sampleSize;i++) {
            vals[i] = ed.getNextValue();
        }
        Arrays.sort(vals);
        return vals;
    }

    private static double[] extractCosts(ArrayList<double[]> ttcs) {
        double[] costs = new double[ttcs.size()];
        for(int cpt=0; cpt<ttcs.size();cpt++) costs[cpt] =  ttcs.get(cpt)[1];
        return costs;
    }

    private static ArrayList<double[]> extractTuplesFromTimeInterval(ArrayList<double[]> tcos, double start, double end){
        ArrayList<double[]> intervalTtcos = new ArrayList<>();
        for(double[] tuple : tcos) {
            if (tuple[0]>end) break;
            if (tuple[0] >= start && tuple[0] < end) {
                intervalTtcos.add(tuple);
            }
        }
        return sortByCost(intervalTtcos);
    }

    private static ArrayList<double[]> sortByCost(ArrayList<double[]> ttcos) {
        Collections.sort(ttcos, (o1, o2) -> Double.compare(o1[1],o2[1]) < 0 ? -1 : Double.compare(o1[1],o2[1]) == 0? 0 : 1);
        return ttcos;
    }

    private static ArrayList<double[]> sumCostSortByTTCAndFlattenAll(Set<ArrayList<double[]>> ttcos) {
        ArrayList<double[]> sortedTtcos = new ArrayList<>();
        for(ArrayList<double[]> ttcs : ttcos) {
            ArrayList<double[]> sortedttcs = sortTtcAndSumCost(ttcs);
            sortedTtcos.addAll(sortedttcs);
        }
        Collections.sort(sortedTtcos, (o1, o2) -> Double.compare(o1[0],o2[0]) < 0 ? -1 : Double.compare(o1[0],o2[0]) == 0? 0 : 1);
        return sortedTtcos;
    }

    private static ArrayList<double[]> sortTtcAndSumCost(ArrayList<double[]> ttcs) {
        ArrayList<double[]> sortedTTCs = new ArrayList<>();
        sortedTTCs.addAll(ttcs);
        Collections.sort(sortedTTCs, (o1, o2) -> o1[0] < o2[0] ? -1 : 1);
        for (int i = 1; i < ttcs.size(); i++) {
            sortedTTCs.get(i)[1] += sortedTTCs.get(i - 1)[1];
        }
        return sortedTTCs;
    }

    private static void cumulateIntervalData(ArrayList<double[]> statValues) {
        for (int i = 1; i < statValues.size(); i++) {
            statValues.get(i)[0] += statValues.get(i - 1)[0];
            statValues.get(i)[1] += statValues.get(i - 1)[1];
            statValues.get(i)[2] += statValues.get(i - 1)[2];
        }
    }

    private static ArrayList<double[]> sortByTTC(ArrayList<double[]> ttcos) {
        Collections.sort(ttcos, (o1, o2) -> Double.compare(o1[0],o2[0]) < 0 ? -1 : Double.compare(o1[0],o2[0]) == 0? 0 : 1);
        return ttcos;
    }

    private static double[] removeLowerHalf(double[] values) {
        Arrays.sort(values);
        return Arrays.copyOfRange(values,values.length/2,values.length);
    }

    private static double[] removeHigherHalf(double[] values) {
        Arrays.sort(values);
        return Arrays.copyOfRange(values,0,values.length/2);
    }

    private static Set<ArrayList<double[]>> sortTtcAndSumCost(Set<ArrayList<double[]>> ttcos) {
        Set<ArrayList<double[]>> sortedTtcos = new HashSet<>();
        for(ArrayList<double[]> ttcs : ttcos) {
            sortedTtcos.add(sortTtcAndSumCost(ttcs));
        }
        return sortedTtcos;
    }

    public static UnivariateFunction interpolateDataset(ArrayList<double[]> ttcs) {
        double[][] ttc = extractAndStore(ttcs);
        UnivariateInterpolator interpolator = new LinearInterpolator();
        UnivariateFunction function = interpolator.interpolate(ttc[0], ttc[1]);
        return function;
    }

    private static double[][] extractAndStore(ArrayList<double[]> ttcs) {
        double[][] array = new double[2][ttcs.size()];
        for(int i=0;i<ttcs.size();i++) {
            array[0][i] = ttcs.get(i)[0];
            array[1][i] = ttcs.get(i)[1];
        }
        return array;
    }


    /**
     * All datasets must have the same highest ttc for their linear interpolation function to behave properly
     * @param ttcos
     */
    private static void equalize(Set<ArrayList<double[]>> ttcos) {
        double maxTTC = 0;
        for(ArrayList<double[]> arr : ttcos) {
            if (arr.get(arr.size()-1)[0] > maxTTC) maxTTC = arr.get(arr.size()-1)[0];
        }
        for(ArrayList<double[]> arr : ttcos) {
            if (arr.get(arr.size()-1)[0] < maxTTC) {
                arr.add(new double[]{maxTTC, arr.get(arr.size()-1)[1]});
            }
        }
    }

    private static void printAll(Set<ArrayList<double[]>> ttcos) {
        for(ArrayList<double[]> arr : ttcos) {
            printSample(arr);
        }
    }

    private static void printSample(ArrayList<double[]> ttc) {
        for(double[] arr : ttc) {
            System.out.print("["+arr[0]+";"+arr[1]+"]");
        }
        System.out.println();
    }

    private static XYSeries newXYSerie(ArrayList<double[]> ttcs) {
        XYSeries series = new XYSeries("spl "+new Random().nextInt());
        ttcs.forEach(doubles -> series.add(doubles[0],doubles[1]));
        return series;
    }
    /*
    public static void getInterpolatedCoS(Set<ArrayList<double[]>> ttcs) {
        Set<ArrayList<double[]>> sortedCos = sortTtcAndSumCost(ttcs);
        equalize(sortedCos);
        XYDataset dataset = new XYSeriesCollection();
        Set<UnivariateFunction> functions = new HashSet<>();
        ArrayList<double[]> a = sortedCos.iterator().next();
        double maxTTC = a.get(a.size()-1)[0];
        for (ArrayList<double[]> arr: sortedCos) {
            functions.add(interpolateDataset(arr));
        }
        ArrayList<double[]> interpolatedData = new ArrayList<>();
        double time = 0;
        while(time < maxTTC) {
            int cpt = 0;
            double[] interpolatedVals = new double[functions.size()];
            for(UnivariateFunction f: functions) {
                try {
                    interpolatedVals[cpt] = f.value(time);
                } catch (OutOfRangeException e) {
                    interpolatedVals[cpt] = 0;
                }
                cpt++;
            }
            interpolatedData.add(interpolatedVals);
            time += 0.2;
        }
        time = 0.0;
        XYSeries meanSeries = new XYSeries("Mean");
        XYSeries highTwentyfivePercentile = new XYSeries("High 50%");
        XYSeries lowTwentyfivePercentile = new XYSeries("Low 50%");
        for(double[] interps: interpolatedData) {
            double mean = StatUtils.mean(interps);
            meanSeries.add(time, mean);
            highTwentyfivePercentile.add(time, StatUtils.mean(removeHigherHalf(interps)));
            lowTwentyfivePercentile.add(time, StatUtils.mean(removeLowerHalf(interps)));
/*            StandardDeviation sd = new StandardDeviation();
            double sdValue = sd.evaluate(interps,mean);
            if (mean-sdValue >= 0) lowSDevSeries.add(cpt,mean-sdValue);
            else lowSDevSeries.add(cpt,0);
            highSDevSeries.add(cpt,mean+sdValue); */ /*
    time += 0.2;
}
((XYSeriesCollection)dataset).addSeries(meanSeries);
        ((XYSeriesCollection)dataset).addSeries(highTwentyfivePercentile);
        ((XYSeriesCollection)dataset).addSeries(lowTwentyfivePercentile);
        String plotTitle = "Cost of Security over Time";
        String xaxis = "Time (days)";
        String yaxis = "Cost of Security ($)";
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean show = true;
        boolean toolTips = false;
        boolean urls = false;
        JFreeChart chart = ChartFactory.createXYLineChart(plotTitle, xaxis, yaxis,
        dataset, orientation, show, toolTips, urls);
        int width = 500;
        int height = 300;
        try {
        ChartUtilities.saveChartAsPNG(new File("meanCoS.PNG"), chart, width, height);
        } catch (IOException e) {}
        }
    public static ArrayList<double[]> getMedianSerie(Set<ArrayList<double[]>> ttcs) {
        ArrayList<double[]> medianSerie = new ArrayList<>();
        int size = getLargestRun(ttcs);
        for(int i = 0; i < size; i++) {
            int sum = 0;
            for(ArrayList<double[]> sample : ttcs) {
                // now what?
            }
        }
        return medianSerie;
    }
    private static int getLargestRun(Set<ArrayList<double[]>> ttcs) {
        int size = 0;
        for(ArrayList<double[]> array : ttcs)
            if(array.size()>size) size = array.size();
        return size;
    }
    public static void printFittingMethod(ArrayList<double[]> values) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        values.forEach(tuple -> obs.add(tuple[0],tuple[1]));
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);
        final double[] coeff = fitter.fit(obs.toList());
        for (double elt : coeff) {
            System.out.print(elt+" ");
        }
    }
    */
}
