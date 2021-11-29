import java.io.File;
import java.util.*;

public class Mediator {
    List<Agent> allAgents;
    int[] currentCostDeltas;
    //double[] acceptanceRates;
    //double minAcceptanceRate;
    List<OfferRun> allOfferRuns;
    List<Integer> offerRunsToSkip;
    OfferRun lastRemoved;

    public Mediator(List<Agent> allAgents, double minAcceptanceRate){
        this.allAgents = allAgents;
        //this.minAcceptanceRate = minAcceptanceRate;
        //this.acceptanceRates = new double[allAgents.size()];
        this.currentCostDeltas = new int[allAgents.size()];
        this.allOfferRuns = new ArrayList<>();
    }

    public int[] run(int initialRunCount, int maxMutationsSinceLastImprovement){
        initializeOfferRuns(initialRunCount);
        boolean validOfferRunFound = true;
        while (validOfferRunFound) {
            validOfferRunFound = false;
            for (int runIndex = 0; runIndex < allOfferRuns.size(); runIndex++){
                OfferRun offerRun = allOfferRuns.get(runIndex);
                if (offerRun.getOffersSinceLastImprovement() > maxMutationsSinceLastImprovement){
                    offerRun.setToSkip(true);
                    this.lastRemoved = offerRun;
                }
                if (offerRun.isToSkip()){
                    continue;
                }
                CostLogger.getCostLogger().newOfferRunStarted(runIndex);
                validOfferRunFound = true;
                int[] mutatedOffer = offerRun.getMutatedOffer();
                boolean isAccepted = getTotalVote(mutatedOffer, runIndex);
                offerRun.informAccepted(isAccepted);
                if (isAccepted){
                    CostLogger.getCostLogger().newOfferWasBestOffer();
                    informAgents(mutatedOffer, runIndex);
                }
            }
        }
        return getBestOffer();
    }

    private int[] getBestOffer(){
        List<int[]> allFinalOffers = new ArrayList<>();
        //[offer1, offer2, offer3]
        for (OfferRun offerRun : allOfferRuns){
            allFinalOffers.add(offerRun.getBestOffer());
        }
        //[Agent1: [offer2, offer1, offer3], Agent2: [offer1, offer2, offer3]]
        List[] rankedFinalOffersByAgent = new List[allAgents.size()];
        for (int i = 0; i < allAgents.size(); i++){
            Agent agent = allAgents.get(i);
            rankedFinalOffersByAgent[i] = agent.rankOffers(allFinalOffers);
        }
        HashMap<Integer, Integer> finalOfferScores = new HashMap<>();
        for (int i = 0; i < rankedFinalOffersByAgent.length; i++){
            List<Integer> agentRanking = rankedFinalOffersByAgent[i];
            for (int j = 0; j < agentRanking.size(); j++){
                int offer_index = agentRanking.get(j);
                if (i == 0){
                    finalOfferScores.put(offer_index, j);
                } else {
                    finalOfferScores.put(offer_index, finalOfferScores.get(offer_index) + j);
                }
            }
        }
        System.out.println("---");
        finalOfferScores.forEach((key, value) -> System.out.println(key + " => " + value));

        int minKey = 0;
        int minValue = Integer.MAX_VALUE;
        for(int key : finalOfferScores.keySet()) {
            int value = finalOfferScores.get(key);
            if(value < minValue) {
                minValue = value;
                minKey = key;
            }
        }
        System.out.println("Run " + minKey + " won!");
        return allFinalOffers.get(minKey);
    }

    private void initializeOfferRuns(int initialRunCount){
        for(int i = 0; i < initialRunCount; i++){
            this.allOfferRuns.add(new OfferRun(OfferRun.generateRandomOffer()));
        }
    }

    boolean getTotalVote(int[] offer, int runIndex){
        List<VoteResponse> responses = new ArrayList<>();
        int totalOfferedMoney = 0;
        int totalRequestedMoney = 0;
        int totalRejected = 0;
        boolean allAccepted = true;
        for (Agent agent: allAgents) {
            VoteResponse response = getAgentVote(agent, offer, runIndex);
            responses.add(response);
            totalOfferedMoney += response.getOfferedMoney();
            totalRequestedMoney += response.getRequestedMoney();
            if(!response.getIsAccepted()){
                totalRejected++;
                allAccepted = false;
            }
        }
        if (allAccepted){
            //System.out.println("alle haben akzeptiert");
            return true;
        } else if (totalRequestedMoney > totalOfferedMoney) {
            //System.out.println("es wurde nicht genug Geld angeboten");
            return false;
        } else {
            CostLogger.getCostLogger().logMoneyTransaction();
            setCostDeltas(responses, totalRequestedMoney, totalOfferedMoney, totalRejected);
            return true;
        }
    }

    void setCostDeltas(List<VoteResponse> responses, int totalRequestedMoney, int totalOfferedMoney, int totalRejected){
        int extraMoney = totalOfferedMoney - totalRequestedMoney;
        for (int i = 0; i < allAgents.size(); i++){
            VoteResponse response = responses.get(i);
            if (response.getIsAccepted()){
                currentCostDeltas[i] = response.getOfferedMoney();
            }
            else {
                currentCostDeltas[i] = (response.getRequestedMoney() + (extraMoney / totalRejected)) * -1;
            }
        }
    }

    VoteResponse getAgentVote(Agent agent, int[] offer, int runIndex){
        return agent.vote(offer, runIndex);
    }

    void informAgents(int[] newBestOffer, int runIndex){
        for (int i = 0; i < allAgents.size(); i++) {
            Agent agent = allAgents.get(i);
            agent.setCurrentAcceptedOffer(newBestOffer, currentCostDeltas[i], runIndex);
        }
    }
}
