public class Simulator {
    public static void main(String[] args) {
        VacuumEnvironment e = new VacuumEnvironment();
        System.out.printf("New %dx%d grid with %f probability of dirt\n",
                e.gridWidth, e.gridHeight, e.dirtProbability);
        ReflexVacuumAgent r = new ReflexVacuumAgent();
        e.addAgent(r);
        System.out.println("Agent's percept:");
        System.out.println(e.getPerceptSeenBy(r).toString());
        e.executeAction(r, e.ACTION_MOVE_LEFT);
        System.out.println("Agent's percept after moving left:");
        System.out.println(e.getPerceptSeenBy(r).toString());
        VacuumEnvironment f = new VacuumEnvironment(5, 5, 0.9);
        System.out.printf("New %dx%d grid with %f probability of dirt\n",
                f.gridWidth, f.gridHeight, f.dirtProbability);
        EnvironmentState es = f.getCurrentState();
        System.out.println(es.toString());
        Boolean[][] grid = new Boolean[][]{
            {true, false},
            {false, true}
        };
        System.out.println("New 2x2 grid:\nDirty Clean\nClean Dirty");
        VacuumEnvironment g = new VacuumEnvironment(grid);
        g.setLocationState(0, 0, VacuumEnvironment.LocationState.Clean);
        System.out.println("Set state of location (0, 0) to Clean");
        System.out.println(g.getCurrentState().toString());
    }
}
