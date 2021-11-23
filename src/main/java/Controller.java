import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public static void main(String[] args){
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(new File("src/main/resources/daten3A.txt")));
        agents.add(new Agent(new File("src/main/resources/daten3B.txt")));
        Mediator mediator = new Mediator(agents, 0.2);
        int rounds = 200000;
        int[] bestOffer = mediator.run(rounds);
        CostLogger.getCostLogger().showResults();
    }
}
