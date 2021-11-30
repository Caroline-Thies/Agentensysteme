import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        //System.out.println("Transaktionen: " + moneyTransactionCount);
        addBestCostsToUI();
        UIApp.run();
    }

    public void addBestCostsToUI() {
        for (String runId : allCostsByRun.keySet()){
            List<Integer> allCosts = allCostsByRun.get(runId);
            List<Integer> bestCostIndices = bestCostIndicesByRun.get(runId);
            int[] bestCostArray = new int[allCosts.size()];
            int[] iterationArray = new int[bestCostArray.length];
            int lastBestCostIndex = 0;
            for (int bestCostIndex = 0; bestCostIndex < bestCostIndices.size(); bestCostIndex++){
                if (bestCostIndex == 0){
                    lastBestCostIndex = bestCostIndices.get(bestCostIndex);
                    continue;
                }
                int currentBestCostIndex = bestCostIndices.get(bestCostIndex);
                int bestCost = allCosts.get(currentBestCostIndex);
                for(int j = lastBestCostIndex; j <= currentBestCostIndex; j++){
                    bestCostArray[j] = bestCost;
                }
                if (bestCostIndex == bestCostIndices.size() - 1){
                    for(int j = currentBestCostIndex; j < bestCostArray.length; j++){
                        bestCostArray[j] = bestCost;
                    }
                }
                lastBestCostIndex = currentBestCostIndex;
            }
            for (int i = 0; i < bestCostArray.length; i++){
                iterationArray[i] = i+ 1;
            }
            UIApp.addDataset(iterationArray, bestCostArray, "Run " + runId);
        }
    }
}
