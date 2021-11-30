import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OfferRun {
    private List<int[]> allBestOffers;
    private int offersSinceLastImprovement;
    private int[] lastOffer;
    private boolean toSkip;
    private String runId;
    private int childrenCount;
    private String parentId;

    public boolean isToSkip() {
        return toSkip;
    }

    public void setToSkip(boolean toSkip) {
        this.toSkip = toSkip;
    }

    public String getRunId() {
        return runId;
    }

    public String getParentId() {
        return parentId;
    }

    public OfferRun(OfferRun parentRun){
        this.allBestOffers = parentRun.allBestOffers;
        this.offersSinceLastImprovement = parentRun.offersSinceLastImprovement;
        this.lastOffer = parentRun.lastOffer;
        this.childrenCount = 0;
        this.toSkip = false;
        this.parentId = parentRun.getRunId();
        this.runId = parentId + "-" + parentRun.getNextChildId();
    }

    public OfferRun(int[] firstOffer, String runId){
        this.allBestOffers = new ArrayList<>();
        this.allBestOffers.add(firstOffer);
        this.offersSinceLastImprovement = 0;
        this.toSkip = false;
        this.runId = runId;
        childrenCount = 0;
        parentId = "";
    }

    public int getNextChildId(){
        int nextChildId = childrenCount;
        childrenCount++;
        return  nextChildId;
    }

    public int[] getMutatedOffer(){
        Random r = new Random();
        int[] offer = this.allBestOffers.get(this.allBestOffers.size() - 1);
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
        this.lastOffer = mutatedOffer;
        return mutatedOffer;
    }

    public int getOffersSinceLastImprovement() {
        return offersSinceLastImprovement;
    }

    public void informAccepted(boolean accepted){
        if (accepted){
            this.allBestOffers.add(this.lastOffer);
            this.offersSinceLastImprovement = 0;
        } else {
            offersSinceLastImprovement++;
        }
    }

    public int[] getBestOffer() {
        return this.allBestOffers.get(this.allBestOffers.size() - 1);
    }

    static int[] generateRandomOffer(){
        int size = 200;
        List<Integer> sortedOfferList = new ArrayList<>();
        for (int i = 0; i < size; i++){
            sortedOfferList.add(i);
        }
        Collections.shuffle(sortedOfferList);
        return sortedOfferList.stream().mapToInt(i->i).toArray();
    }
}
