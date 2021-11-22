public class VoteResponse {
    private boolean isAccepted;
    private int offeredMoney;
    private int requestedMoney;

    public VoteResponse(boolean isAccepted){
        this.isAccepted = isAccepted;
        this.offeredMoney = 0;
        this.requestedMoney = 0;
    }

    public VoteResponse(boolean isAccepted, int money) {
        this.isAccepted = isAccepted;
        if (isAccepted){
            offeredMoney = money;
        } else {
            requestedMoney = money;
        }
    }

    public boolean getIsAccepted() {
        return this.isAccepted;
    }

    public int getOfferedMoney() {
        return this.offeredMoney;
    }

    public int getRequestedMoney() {
        return this.requestedMoney;
    }
}
