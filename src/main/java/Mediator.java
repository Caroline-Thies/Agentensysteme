import java.util.*;

public class Mediator {
    HashMap<Integer, Agent> allAgents;
    int[] currentCostDeltas;
    HashMap<String, OfferRun> allOfferRuns;
    int lastRemovedId;

    public Mediator(HashMap<Integer, Agent> allAgents, double minAcceptanceRate){
        this.allAgents = allAgents;
        this.currentCostDeltas = new int[allAgents.size()];
        this.allOfferRuns = new HashMap<>();
    }

    public int[] run(int initialRunCount, int totalIterations, int replacementCount){
        initializeOfferRuns(initialRunCount);
        for(int totalRunIndex = 0; totalRunIndex < totalIterations; totalRunIndex++) {
            if(totalRunIndex % (totalIterations / replacementCount) == 0 && totalRunIndex > 0){
                replaceWorstHalf();
            }
            for (String runId : allOfferRuns.keySet()){
                OfferRun offerRun = allOfferRuns.get(runId);
                if (offerRun.isToSkip()){
                    continue;
                }
                CostLogger.getCostLogger().newOfferRunStarted(runId);
                int[] mutatedOffer = offerRun.getMutatedOffer();
                boolean isAccepted = getTotalVote(mutatedOffer, runId);
                offerRun.informAccepted(isAccepted);
                if (isAccepted){
                    CostLogger.getCostLogger().newOfferWasBestOffer();
                    informAgents(mutatedOffer, runId);
                }
            }
        }
        return new int[0];
    }

    private HashMap<String, Integer> getOfferIdRanking(){
        HashMap<String, int[]> bestOffersByActiveRunIds = new HashMap<>();
        for (String runId : allOfferRuns.keySet()){
            OfferRun offerRun = allOfferRuns.get(runId);
            if(!offerRun.isToSkip()){
                bestOffersByActiveRunIds.put(runId, offerRun.getBestOffer());
            }
        }
        HashMap<String, Integer> totalRankByRunId = new HashMap<>();
        for(int agentId : allAgents.keySet()){
            Agent agent = allAgents.get(agentId);
            List<String> runIdRanking = agent.rankRunIdsByBestOffer(bestOffersByActiveRunIds);
            for (int rankingIndex = 0; rankingIndex < runIdRanking.size(); rankingIndex++){
                String runId = runIdRanking.get(rankingIndex);
                if (totalRankByRunId.containsKey(runId)){
                    totalRankByRunId.put(runId, totalRankByRunId.get(runId) + rankingIndex);
                } else {
                    totalRankByRunId.put(runId, rankingIndex);
                }
            }
        }
        return totalRankByRunId;
    }

    private void replaceWorstHalf(){
        HashMap<String, Integer> totalRankByRunId = getOfferIdRanking();
        List<String> sortedRunIds = new ArrayList<>(totalRankByRunId.keySet());
        sortedRunIds.sort(Comparator.comparingInt(totalRankByRunId::get));
        for (int sortedIndex = 0; sortedIndex < sortedRunIds.size() / 2; sortedIndex++){
            String runId = sortedRunIds.get(sortedIndex);
            OfferRun successfulOfferRun = allOfferRuns.get(runId);
            OfferRun offerRunOffspring = new OfferRun(successfulOfferRun);
            for (Agent agent : allAgents.values()){
                agent.addRunOffspring(successfulOfferRun.getRunId(), offerRunOffspring.getRunId());
            }
            CostLogger.getCostLogger().addOfferRunOffspring(successfulOfferRun.getRunId(), offerRunOffspring.getRunId());
            allOfferRuns.put(offerRunOffspring.getRunId(), offerRunOffspring);
        }
        for (int sortedIndex = sortedRunIds.size() / 2; sortedIndex < sortedRunIds.size(); sortedIndex++){
            String runId = sortedRunIds.get(sortedIndex);
            OfferRun failedOfferRun = allOfferRuns.get(runId);
            failedOfferRun.setToSkip(true);
        }
    }

    private void initializeOfferRuns(int initialRunCount){
        for(int i = 0; i < initialRunCount; i++){
            OfferRun offerRun = new OfferRun(OfferRun.generateRandomOffer(), String.valueOf(i));
            this.allOfferRuns.put(offerRun.getRunId(), offerRun);
        }
    }

    boolean getTotalVote(int[] offer, String runId){
        List<VoteResponse> responses = new ArrayList<>();
        int totalOfferedMoney = 0;
        int totalRequestedMoney = 0;
        int totalRejected = 0;
        boolean allAccepted = true;
        for (Agent agent: allAgents.values()) {
            VoteResponse response = getAgentVote(agent, offer, runId);
            responses.add(response);
            totalOfferedMoney += response.getOfferedMoney();
            totalRequestedMoney += response.getRequestedMoney();
            if(!response.getIsAccepted()){
                totalRejected++;
                allAccepted = false;
            }
        }
        if (allAccepted){
            return true;
        } else if (totalRequestedMoney > totalOfferedMoney) {
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

    VoteResponse getAgentVote(Agent agent, int[] offer, String runIndex){
        return agent.vote(offer, runIndex);
    }

    void informAgents(int[] newBestOffer, String runId){
        for (int i = 0; i < allAgents.size(); i++) {
            Agent agent = allAgents.get(i);
            agent.setCurrentAcceptedOffer(newBestOffer, currentCostDeltas[i], runId);
        }
    }
}
