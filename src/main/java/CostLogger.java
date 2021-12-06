import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;

public class CostLogger {
    private int bestCost;
    private HashMap<String, List<Integer>> allCostsByRun;
    private HashMap<String, List<Integer>> bestCostIndicesByRun;
    private static CostLogger costLogger;
    private int moneyTransactionCount;
    private String currentOfferRunId;

    private CostLogger(){
        bestCost = Integer.MAX_VALUE;
        moneyTransactionCount = 0;
        allCostsByRun = new HashMap<>();
        bestCostIndicesByRun = new HashMap<>();
    }

    public static CostLogger getCostLogger() {
        if (CostLogger.costLogger == null){
            CostLogger.costLogger = new CostLogger();
        }
        return CostLogger.costLogger;
    }

    public void newOfferRunStarted(String runId){
        if (!allCostsByRun.containsKey(runId)){
            allCostsByRun.put(runId, new ArrayList<>());
        }
        if (!bestCostIndicesByRun.containsKey(runId)){
            bestCostIndicesByRun.put(runId, new ArrayList<>());
        }
        allCostsByRun.get(runId).add(0);
        currentOfferRunId = runId;
    }

    public void addOfferRunOffspring(String parentRunId, String offspringRunId){
        List<Integer> allCosts = new ArrayList<>(allCostsByRun.get(parentRunId));
        List<Integer> bestCostIndices = new ArrayList<>(bestCostIndicesByRun.get(parentRunId));
        allCostsByRun.put(offspringRunId, allCosts);
        bestCostIndicesByRun.put(offspringRunId, bestCostIndices);
    }

    public void addIndividualCost(int cost){
        List<Integer> allCosts = allCostsByRun.get(currentOfferRunId);
        int lastIndex = allCosts.size() - 1;
        int lastCost = allCosts.get(lastIndex);
        allCosts.set(lastIndex, lastCost + cost);
    }

    public void newOfferWasBestOffer(){
        List<Integer> allCosts = allCostsByRun.get(currentOfferRunId);
        int lastIndex = allCosts.size() - 1;
        if (allCosts.get(lastIndex) < bestCost){
            bestCost = allCosts.get(lastIndex);
        }
        List<Integer> bestCostIndices = bestCostIndicesByRun.get(currentOfferRunId);
        bestCostIndices.add(lastIndex);
        //System.out.println("Neue beste Kosten von " + bestCost + " in Iteration " + allCosts.size() + " gefunden");
    }

    public int getBestCost() {
        return bestCost;
    }

    public void logMoneyTransaction(){
        this.moneyTransactionCount++;
    }

    public int getMoneyTransactionCount() {
        return moneyTransactionCount;
    }

    public void showResults() {
        System.out.println("Beste Kosten: " + bestCost);
        System.out.println("Transaktionen: " + moneyTransactionCount);
        addBestCostsToUI();
        if(bestCost <= 5184){
            UIApp.run();
        }
    }

    public void addBestCostsToUI() {
        HashMap<String, int[]> bestCostArrayByRun = new HashMap<>();
        HashMap<String, int[]> bestCostIndexArrayByRun = new HashMap<>();
        for (String runId : allCostsByRun.keySet()){
            List<Integer> allCosts = allCostsByRun.get(runId);
            List<Integer> bestCostIndices = bestCostIndicesByRun.get(runId);
            int[] bestCostArray = new int[bestCostIndices.size()];
            int[] bestCostIndexArray = new int[bestCostIndices.size()];
            for(int i = 0; i < bestCostIndices.size(); i++){
                int bestCostIndex = bestCostIndices.get(i);
                bestCostArray[i] = allCosts.get(bestCostIndex);
                bestCostIndexArray[i] = bestCostIndex;
            }
            bestCostArrayByRun.put(runId, bestCostArray);
            bestCostIndexArrayByRun.put(runId, bestCostIndexArray);
            //UIApp.addDataset(iterationArray, bestCostArray, "Run " + runId);
        }
        writeDataToOutput(bestCostIndexArrayByRun, bestCostArrayByRun);
    }

    public void writeDataToOutput(HashMap<String, int[]> xVals, HashMap<String, int[]> yVals){
        try {
            FileWriter fw = new FileWriter("data.json", false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("[\n");
            int i = 0;
            for (String runId : xVals.keySet()){
                int[] xEntry = xVals.get(runId);
                int[] yEntry = yVals.get(runId);
                JSONArray jsonXArray = new JSONArray(xEntry);
                JSONArray jsonYArray = new JSONArray(yEntry);
                bw.write(jsonXArray.toString());
                bw.write("\n,");
                bw.write(jsonYArray.toString());
                if (i < xVals.keySet().size() - 1){
                    bw.write(",\n");
                }
                i++;
            }
            bw.write("]");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
