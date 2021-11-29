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

    public int[] run(int initialRunCount, int deleteInterval){
        initializeOfferRuns(initialRunCount);
        boolean validOfferRunFound = true;
        int totalRunIndex = 0;
        while (validOfferRunFound) {
            validOfferRunFound = false;
            if((totalRunIndex + 1) % deleteInterval == 0){
                setWorstHalfToSkip();
            }
            for (int runIndex = 0; runIndex < allOfferRuns.size(); runIndex++){
                OfferRun offerRun = allOfferRuns.get(runIndex);
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
            totalRunIndex++;
        }
        return lastRemoved.getBestOffer();
    }

    private void setWorstHalfToSkip(){
        List<int[]> allFinalOffers = new ArrayList<>();
        List<OfferRun> allFinalOfferRuns = new ArrayList<>();
        for (OfferRun offerRun : allOfferRuns){
            if(!offerRun.isToSkip()) {
                allFinalOffers.add(offerRun.getBestOffer());
                allFinalOfferRuns.add(offerRun);
            }
        }
        if(allFinalOfferRuns.size() == 1){
            allFinalOfferRuns.get(0).setToSkip(true);
            lastRemoved = allFinalOfferRuns.get(0);
        }
        List<Integer>[] rankedFinalOffersByAgent = new ArrayList[allAgents.size()];
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
        //finalOfferScores.forEach((key, value) -> System.out.println(key + " => " + value));

        List<Integer> scoreKeys = rankedFinalOffersByAgent[0];
        scoreKeys.sort((key1, key2) -> {
            int value1 = finalOfferScores.get(key1);
            int value2 = finalOfferScores.get(key2);
            return value1 - value2;
        });
        for (int i = scoreKeys.size() - 1; i >= scoreKeys.size() / 2; i--){
            int scoreKey = scoreKeys.get(i);
            //System.out.println("Setting Run " + scoreKey + " to skip");
            allFinalOfferRuns.get(scoreKey).setToSkip(true);
        }
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
