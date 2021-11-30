import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Agent {
    private int[][] costMatrix;
    private HashMap<String, int[]> currentAcceptedOfferByRunId;
    private HashMap<String, Integer> currentCostByRunId;
    private int agentId;

    public Agent(File file, int agentId){
        readMatrix((file));
        currentAcceptedOfferByRunId = new HashMap<>();
        currentCostByRunId = new HashMap<>();
        this.agentId = agentId;
    }

    public int getAgentId() {
        return agentId;
    }


    public VoteResponse vote(int[] offer, String runId) {
        if(!currentCostByRunId.containsKey(runId)){
            currentCostByRunId.put(runId, Integer.MAX_VALUE);
        }
        int currentCost = currentCostByRunId.get(runId);
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

    public void addRunOffspring(String parentId, String offspringId){
        int currentParentCost = currentCostByRunId.get(parentId);
        currentCostByRunId.put(offspringId, currentParentCost);
        int[] currentParentAcceptedOffer = currentAcceptedOfferByRunId.get(parentId);
        currentAcceptedOfferByRunId.put(offspringId, currentParentAcceptedOffer);
    }

    private int calcOfferCost(int[] offer){
        int cost = 0;
        for(int i=0; i<offer.length-1; i++){
            cost += costMatrix[offer[i]][offer[i+1]];
        }
        return cost;
    }

    public void setCurrentAcceptedOffer(int[] currentAcceptedOffer, int costDelta, String runId) {
        this.currentAcceptedOfferByRunId.put(runId, currentAcceptedOffer);
        int currentCost = calcOfferCost(currentAcceptedOffer) + costDelta;
        this.currentCostByRunId.put(runId, currentCost);
    }

    public List<String> rankRunIdsByBestOffer(HashMap<String, int[]> bestOfferByRun) {
        List<String> runIdList = new ArrayList<>( bestOfferByRun.keySet());
        runIdList.sort((runId1, runId2) -> {
            int cost1 = getCurrentCostByRunId(runId1);
            int cost2 = getCurrentCostByRunId(runId2);
            return cost1 - cost2;
        });
        return runIdList;
    }

    private int getCurrentCostByRunId(String runId){
        return currentCostByRunId.getOrDefault(runId, Integer.MAX_VALUE);
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
}
