import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Agent {
    int[][] costMatrix;
    List<int[]> currentAcceptedOfferByRun;
    List<Integer> currentCostByRun;
    public Agent(File file){
        readMatrix((file));
        currentAcceptedOfferByRun = new ArrayList<>();
        currentCostByRun = new ArrayList<>();
    }

    public void readMatrix(File file) {
        int[][] cost = new int[1][1];
        try{
            Scanner scanner = new Scanner(file);
            int dim         = scanner.nextInt();
            cost            = new int[dim][dim];
            for(int i=0;i<dim;i++){
                for(int j=0;j<dim;j++){
                    int x = scanner.nextInt();
                    cost[i][j] = x;
                }
            }
            scanner.close();
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        this.costMatrix = cost;
    }

    public VoteResponse vote(int[] offer, int runIndex) {
        int cost = calcOfferCost(offer);
        if(runIndex >= currentCostByRun.size()){
            currentCostByRun.add(Integer.MAX_VALUE);
        }
        int currentCost = currentCostByRun.get(runIndex);
        CostLogger.getCostLogger().addIndividualCost(cost);
        int costDelta = currentCost - cost;
        if (currentCost == Integer.MAX_VALUE){
            costDelta = 0;
        }
        if(costDelta >= 0) {
            return new VoteResponse(true, costDelta / 2);
        } else {
            costDelta *= -1;
            return new VoteResponse(false, costDelta + 1);
        }
    }

    private int calcOfferCost(int[] offer){
        int cost = 0;
        for(int i=0; i<offer.length-1; i++){
            cost += costMatrix[offer[i]][offer[i+1]];
        }
        return cost;
    }

    public void setCurrentAcceptedOffer(int[] currentAcceptedOffer, int costDelta, int runIndex) {
        if (runIndex >= currentAcceptedOfferByRun.size()){
            currentAcceptedOfferByRun.add(currentAcceptedOffer);
        }
        this.currentAcceptedOfferByRun.set(runIndex, currentAcceptedOffer);
        int currentCost = calcOfferCost(currentAcceptedOffer) + costDelta;
        this.currentCostByRun.set(runIndex, currentCost);
    }

    public List<Integer> rankOffers(List<int[]> offers) {
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++){
            indexList.add(i);
        }
        indexList.sort((index1, index2) -> {
            int[] offer1 = offers.get(index1);
            int[] offer2 = offers.get(index2);
            return calcOfferCost(offer1) - calcOfferCost(offer2);
        });
        System.out.println(indexList);
        return indexList;
    }
}
