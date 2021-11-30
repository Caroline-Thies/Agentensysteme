import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CostLogger {
    private int bestCost;
    private List<List<Integer>> allCostsByRun;
    private List<List<Integer>> bestCostIndicesByRun;
    private static CostLogger costLogger;
    private int moneyTransactionCount;
    private int currentOfferRunIndex;

    private CostLogger(){
        bestCost = Integer.MAX_VALUE;
        moneyTransactionCount = 0;
        allCostsByRun = new ArrayList<>();
        bestCostIndicesByRun = new ArrayList<>();
    }

    public static CostLogger getCostLogger() {
        if (CostLogger.costLogger == null){
            CostLogger.costLogger = new CostLogger();
        }
        return CostLogger.costLogger;
    }

    public void newOfferRunStarted(int runIndex){
        while (runIndex >= allCostsByRun.size()){
            allCostsByRun.add(new ArrayList<>());
        }
        while(runIndex >= bestCostIndicesByRun.size()){
            bestCostIndicesByRun.add(new ArrayList<>());
        }
        allCostsByRun.get(runIndex).add(0);
        currentOfferRunIndex = runIndex;
    }

    public void addCloneOfferRun(int sourceIndex){
        List<Integer> allCosts = new ArrayList<>(allCostsByRun.get(sourceIndex));
        List<Integer> bestCostIndices = new ArrayList<>(bestCostIndicesByRun.get(sourceIndex));
        allCostsByRun.add(allCosts);
        bestCostIndicesByRun.add(bestCostIndices);
    }

    public void addIndividualCost(int cost){
        List<Integer> allCosts = allCostsByRun.get(currentOfferRunIndex);
        int lastIndex = allCosts.size() - 1;
        int lastCost = allCosts.get(lastIndex);
        allCosts.set(lastIndex, lastCost + cost);
    }

    public void newOfferWasBestOffer(){
        List<Integer> allCosts = allCostsByRun.get(currentOfferRunIndex);
        int lastIndex = allCosts.size() - 1;
        if (allCosts.get(lastIndex) < bestCost){
            bestCost = allCosts.get(lastIndex);
        }
        List<Integer> bestCostIndices = bestCostIndicesByRun.get(currentOfferRunIndex);
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
        for (int runIndex = 0; runIndex < allCostsByRun.size(); runIndex++){
            List<Integer> allCosts = allCostsByRun.get(runIndex);
            List<Integer> bestCostIndices = bestCostIndicesByRun.get(runIndex);
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
            UIApp.addDataset(iterationArray, bestCostArray, "Run " + runIndex);
        }
    }
}
