public class CostLogger {
    private int bestCost;
    private int currentCost;
    private static CostLogger costLogger;
    private int moneyTransactionCount;

    private CostLogger(){
        bestCost = 0;
        currentCost = 0;
        moneyTransactionCount = 0;
    }

    public static CostLogger getCostLogger() {
        if (CostLogger.costLogger == null){
            CostLogger.costLogger = new CostLogger();
        }
        return CostLogger.costLogger;
    }

    public void newOfferStarted(){
        currentCost = 0;
    }

    public void addIndividualCost(int cost){
        currentCost += cost;
    }

    public void newOfferWasBestOffer(){
        bestCost = currentCost;
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
}
