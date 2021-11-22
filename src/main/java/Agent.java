import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Agent {
    int[][] costMatrix;
    int acceptedOffersCount;
    int rejectedOffersCount;
    float minAcceptanceRate;
    int[] currentAcceptedOffer;
    int currentCost;

    public Agent(File file){
        readMatrix((file));
        currentCost = Integer.MAX_VALUE;
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

    public VoteResponse vote(int[] offer) {
        int cost = calcOfferCost(offer);
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

    public void setCurrentAcceptedOffer(int[] currentAcceptedOffer, int costDelta) {
        this.currentAcceptedOffer = currentAcceptedOffer;
        this.currentCost = calcOfferCost(currentAcceptedOffer) + costDelta;
    }
}
