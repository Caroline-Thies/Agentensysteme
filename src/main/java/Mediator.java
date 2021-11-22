import java.io.File;
import java.util.*;

public class Mediator {
    List<int[]> allBestOffers;
    List<Agent> allAgents;
    int[] currentCostDeltas;
    int offersSinceLastImprovement;
    double[] acceptanceRates;
    double minAcceptanceRate;

    public static void main(String args[]){
        List<Agent> agents = new ArrayList<Agent>();
        agents.add(new Agent(new File("src/main/resources/daten3A.txt")));
        agents.add(new Agent(new File("src/main/resources/daten3B.txt")));
        Mediator mediator = new Mediator(agents, 0.2);
        int rounds = 500000;
        int[] bestOffer = mediator.run(rounds);
        int bestCost = CostLogger.getCostLogger().getBestCost();
        System.out.println("Best Offer: " + Arrays.toString(bestOffer));
        System.out.println("Total cost: " + bestCost);
        System.out.println("Es wurde " + CostLogger.getCostLogger().getMoneyTransactionCount() + " mal Geld ausgetauscht.");
    }

    public Mediator(List<Agent> allAgents, double minAcceptanceRate){
        this.allAgents = allAgents;
        this.minAcceptanceRate = minAcceptanceRate;
        this.allBestOffers = new ArrayList<int[]>();
        this.acceptanceRates = new double[allAgents.size()];
        this.currentCostDeltas = new int[allAgents.size()];
        this.offersSinceLastImprovement = 0;
    }

    public int[] run(int rounds){
        int round;
        for (round = 0; round < rounds; round++){
            round++;
            CostLogger.getCostLogger().newOfferStarted();
            int[] offer = null;
            if (allBestOffers.isEmpty()){
                offer = generateRandomOffer();
            } else {
                int[] lastBestOffer = allBestOffers.get(allBestOffers.size() - 1);
                offer = generateMutatedOffer(lastBestOffer);
            }
            boolean isAccepted = getTotalVote(offer, round);
            if (!isAccepted){
                offersSinceLastImprovement += 1;
                continue;
            }
            offersSinceLastImprovement = 0;
            CostLogger.getCostLogger().newOfferWasBestOffer();
            System.out.println("found new best offer in round " + round + " with costs " + CostLogger.getCostLogger().getBestCost());
            allBestOffers.add(offer);
            informAgents(offer);
        }
        return allBestOffers.get(allBestOffers.size() - 1);
    }

    boolean getTotalVote(int[] offer, int round){
        List<VoteResponse> responses = new ArrayList<VoteResponse>();
        int totalOfferedMoney = 0;
        int totalRequestedMoney = 0;
        int totalRejected = 0;
        boolean allAccepted = true;
        for (Agent agent: allAgents) {
            VoteResponse response = getAgentVote(agent, offer, round);
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
            System.out.println("Angeboten: " + totalOfferedMoney + ", Verlangt: " + totalRequestedMoney);
            CostLogger.getCostLogger().logMoneyTransaction();
            setCostDeltas(responses, totalRequestedMoney, totalOfferedMoney, totalRejected);
            System.out.println(Arrays.toString(currentCostDeltas));
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

    void adjustAcceptanceRate(Agent agent, boolean isAccepted, int round){
        int agentIndex = allAgents.indexOf(agent);
        int acceptedValue = isAccepted ? 1 : 0;
        double currentAcceptanceRate = acceptanceRates[agentIndex];
        acceptanceRates[agentIndex] = (currentAcceptanceRate*(round-1) + acceptedValue) / (double)round;
    }

    VoteResponse getAgentVote(Agent agent, int[] offer, int round){
        return agent.vote(offer);
        //int agentIndex = allAgents.indexOf(agent);
        //double agentAcceptanceRate = acceptanceRates[agentIndex];
        //if (agentAcceptanceRate >= minAcceptanceRate){
        //    return agent.vote(offer);
        //} else {
        //    return new VoteResponse(true);
        //}
    }

    void informAgents(int[] newBestOffer){
        for (int i = 0; i < allAgents.size(); i++) {
            Agent agent = allAgents.get(i);
            agent.setCurrentAcceptedOffer(newBestOffer, currentCostDeltas[i]);
        }
    }

    int[] generateMutatedOffer(int[] offer) {
        Random r = new Random();
        int[] mutatedOffer = offer.clone();
        int swapCount = 1;
        for(int i = 0; i < swapCount; i++) {
            int indexA = r.nextInt(offer.length - 1);
            int indexB = r.nextInt(offer.length - 1);
            while (indexA == indexB) {
                indexB = r.nextInt(offer.length - 1);
            }
            int mem = mutatedOffer[indexA];
            mutatedOffer[indexA] = mutatedOffer[indexB];
            mutatedOffer[indexB] = mem;
        }
        return mutatedOffer;
    }

    int[] generateRandomOffer(){
        int size = 200;
        List<Integer> sortedOfferList = new ArrayList<Integer>();
        for (int i = 0; i < size; i++){
            sortedOfferList.add(i);
        }
        Collections.shuffle(sortedOfferList);
        int[] shuffledOffer = sortedOfferList.stream().mapToInt(i->i).toArray();
        return shuffledOffer;
    }

    void evaluateResponses(VoteResponse[] responses) {

    }

    void adjustAcceptanceRate(){}
}
