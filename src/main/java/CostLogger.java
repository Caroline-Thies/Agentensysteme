import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CostLogger {
    private int bestCost;
    private List<Integer> allCosts;
    private List<Integer> bestCostIndices;
    private static CostLogger costLogger;
    private int moneyTransactionCount;

    private CostLogger(){
        bestCost = 0;
        moneyTransactionCount = 0;
        allCosts = new ArrayList<>();
        bestCostIndices = new ArrayList<>();
    }

    public static CostLogger getCostLogger() {
        if (CostLogger.costLogger == null){
            CostLogger.costLogger = new CostLogger();
        }
        return CostLogger.costLogger;
    }

    public void newOfferStarted(){
        allCosts.add(0);
    }

    public void addIndividualCost(int cost){
        int lastIndex = allCosts.size() - 1;
        int lastCost = allCosts.get(lastIndex);
        allCosts.set(lastIndex, lastCost + cost);
    }

    public void newOfferWasBestOffer(){
        int lastIndex = allCosts.size() - 1;
        bestCost = allCosts.get(lastIndex);
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
        addAllCostsToUI();
        UIApp.run();
    }

    public void reset(){
        bestCost = 0;
        allCosts = new ArrayList<>();
        bestCostIndices = new ArrayList<>();
        moneyTransactionCount = 0;
    }

    private void addAllCostsToUI() {
        int[] allCostsArray = allCosts.stream().mapToInt(i -> i).toArray();
        int[] iterationArray = new int[allCostsArray.length];
        for(int i = 0; i < iterationArray.length; i++){
            iterationArray[i] = i+1;
        }
        UIApp.addDataset(iterationArray, allCostsArray, "Alle Kosten");
    }

    private void addBestCostsToUI() {
        int[] bestCostArray = new int[allCosts.size()];
        int[] iterationArray = new int[bestCostArray.length];
        int lastBestCostIndex = 0;
        for (int i = 0; i < bestCostIndices.size(); i++){
            if (i == 0){
                lastBestCostIndex = bestCostIndices.get(i);
                continue;
            }
            int currentBestCostIndex = bestCostIndices.get(i);
            int bestCost = allCosts.get(currentBestCostIndex);
            for(int j = lastBestCostIndex; j <= currentBestCostIndex; j++){
                bestCostArray[j] = bestCost;
            }
            if (i == bestCostIndices.size() - 1){
                for(int j = currentBestCostIndex; j < bestCostArray.length; j++){
                    bestCostArray[j] = bestCost;
                }
            }
            lastBestCostIndex = currentBestCostIndex;
        }
        for (int i = 0; i < bestCostArray.length; i++){
            iterationArray[i] = i+ 1;
        }
        UIApp.addDataset(iterationArray, bestCostArray, "Beste Kosten");
    }
}
