import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    public static void main(String[] args){
        int offerRuns = 20;
        List<Agent> agents = createAgents();
        Mediator mediator = new Mediator(agents, 0.2);
        mediator.run(offerRuns, 100000, 10);
        CostLogger.getCostLogger().showResults();
    }

    private static List<Agent> createAgents(){
        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent(new File("src/main/resources/daten3A.txt")));
        agents.add(new Agent(new File("src/main/resources/daten3B.txt")));
        return agents;
    }
}
