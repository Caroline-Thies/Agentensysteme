import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public static void main(String[] args){
        int rounds = 500000;
        List<Agent> agents = createAgents();
        Mediator mediator = new Mediator(agents, 0.2);
        mediator.run(rounds);
        int bestCost = CostLogger.getCostLogger().getBestCost();
        System.out.println(bestCost);
        CostLogger.getCostLogger().showResults();
    }

    private static List<Agent> createAgents(){
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(new File("src/main/resources/daten3A.txt")));
        agents.add(new Agent(new File("src/main/resources/daten3B.txt")));
        return agents;
    }
}
