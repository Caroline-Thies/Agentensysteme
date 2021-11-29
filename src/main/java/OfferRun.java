import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OfferRun {
    private List<int[]> allBestOffers;
    private int offersSinceLastImprovement;
    private int[] lastOffer;
    private boolean toSkip;

    public boolean isToSkip() {
        return toSkip;
    }

    public void setToSkip(boolean toSkip) {
        this.toSkip = toSkip;
    }

    public OfferRun(int[] firstOffer){
        this.allBestOffers = new ArrayList<>();
        this.allBestOffers.add(firstOffer);
        this.offersSinceLastImprovement = 0;
        this.toSkip = false;
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
