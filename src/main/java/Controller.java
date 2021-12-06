import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Controller {
    public static void main(String[] args){
        int offerRuns = 50;
        HashMap<Integer, Agent> agents = createAgents();
        Mediator mediator = new Mediator(agents, 0.2);
        mediator.run(offerRuns, 100000, 10);
        CostLogger.getCostLogger().showResults();
    }

    private static HashMap<Integer, Agent> createAgents(){
        HashMap<Integer, Agent> agents = new HashMap<>();
        agents.put(0, new Agent(new File("src/main/resources/daten3A.txt"), 0));
        agents.put(1, new Agent(new File("src/main/resources/daten3B.txt"), 1));
        return agents;
    }
}
