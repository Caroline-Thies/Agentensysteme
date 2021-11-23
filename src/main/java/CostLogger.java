import java.util.ArrayList;
import java.util.List;

public class CostLogger {
    private int bestCost;
    private List<Integer> allCosts;
    private static CostLogger costLogger;
    private int moneyTransactionCount;

    private CostLogger(){
        bestCost = 0;
        moneyTransactionCount = 0;
        allCosts = new ArrayList<>();
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
        System.out.println("Neue beste Kosten von " + bestCost + " in Iteration " + allCosts.size() + " gefunden");
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
    }
}
