import java.util.Random;
import java.util.Arrays;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): pg 58.<br>
 * <br>
 * Let the world contain just two locations. Each location may or may not
 * contain dirt, and the agent may be in one location or the other. There are 8
 * possible world states, as shown in Figure 3.2. The agent has three possible
 * actions in this version of the vacuum world: <em>Left</em>, <em>Right</em>,
 * and <em>Suck</em>. Assume for the moment, that sucking is 100% effective. The
 * goal is to clean up all the dirt.
 * 
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 * @author Mike Stampone
 */
public class VacuumEnvironment extends AbstractEnvironment {
	// Allowable Actions within the Vacuum Environment
	public static final Action ACTION_MOVE_LEFT = new DynamicAction("Left");
	public static final Action ACTION_MOVE_RIGHT = new DynamicAction("Right");
    public static final Action ACTION_MOVE_UP = new DynamicAction("Up");
    public static final Action ACTION_MOVE_DOWN = new DynamicAction("Down");
	public static final Action ACTION_SUCK = new DynamicAction("Suck");
    public static final String LOCATION_A = "A";
    public static final String LOCATION_B = "B";
    public static final String LOCATION_FORMAT = "%d,%d";
    public static final int MAX_DIMENSION = 1024;

	public enum LocationState {
		Clean, Dirty
	};

	//
	protected VacuumEnvironmentState envState = null;
	protected boolean isDone = false;
    protected int gridWidth = 0;
    protected int gridHeight = 0;
    protected double dirtProbability = 0;

    /**
      * Constructs a vacuum environment with a random grid size and random
      * probability that dirt will be on each tile (default)
      */
    public VacuumEnvironment() {
        Random r = new Random();
        gridWidth = r.nextInt(MAX_DIMENSION) + 1;
        gridHeight = r.nextInt(MAX_DIMENSION) + 1;
        dirtProbability = r.nextDouble();
        envState = new VacuumEnvironmentState();
        for (int i = 0; i < gridWidth; ++i) {
            for (int j = 0; j < gridHeight; ++j) {
                envState.setLocationState(String.format(LOCATION_FORMAT, i, j),
                        r.nextDouble() < dirtProbability ? LocationState.Dirty :
                        LocationState.Clean);
            }
        }
    }

    /**
      * Constructs a vacuum environment with a random grid size and specified
      * dirt probability
      */
    public VacuumEnvironment(double probability) {
        Random r = new Random();
        gridWidth = r.nextInt(MAX_DIMENSION) + 1;
        gridHeight = r.nextInt(MAX_DIMENSION) + 1;
        dirtProbability = probability;
        envState = new VacuumEnvironmentState();
        for (int i = 0; i < gridWidth; ++i) {
            for (int j = 0; j < gridHeight; ++j) {
                envState.setLocationState(String.format(LOCATION_FORMAT, i, j),
                        r.nextDouble() < dirtProbability ? LocationState.Dirty :
                        LocationState.Clean);
            }
        }
    }

    /**
      * Constructs a vacuum environment with a grid of specified width, height,
      * and dirt probability
      */
    public VacuumEnvironment(int width, int height, double probability) {
        Random r = new Random();
        gridWidth = width;
        gridHeight = height;
        dirtProbability = probability;
        envState = new VacuumEnvironmentState();
        for (int i = 0; i < gridWidth; ++i) {
            for (int j = 0; j < gridHeight; ++j) {
                envState.setLocationState(String.format(LOCATION_FORMAT, i, j),
                        r.nextDouble() < dirtProbability ? LocationState.Dirty :
                        LocationState.Clean);
            }
        }
    }

    /**
      * Constructs a vacuum environment with a pre-defined grid with dirt
      * locations specified by a 2-dimensinoal array of Booleans
      */
    public VacuumEnvironment(Boolean[][] grid) {
        int dirts = 0;
        gridWidth = grid.length;
        gridHeight = (gridWidth > 0 ? grid[0].length : 0);
        envState = new VacuumEnvironmentState();
        for (int i = 0; i < gridWidth; ++i) {
            for (int j = 0; j < gridHeight; ++j) {
                envState.setLocationState(String.format(LOCATION_FORMAT, i, j),
                        grid[i][j] ? LocationState.Dirty : LocationState.Clean);
                if (grid[i][j]) {
                    dirts++;
                }
            }
        }
        dirtProbability = (gridWidth > 0 && gridHeight > 0 ? 1.0 * dirts /
                (gridWidth * gridHeight) : 0);
    }

	@Override
	public EnvironmentState getCurrentState() {
		return envState;
	}

    /**
      * Execute an action with an agent to navigate and clean a 2-dimensional
      * grid of locations
      */
	@Override
	public EnvironmentState executeAction(Agent a, Action agentAction) {
        String agentLocation = envState.getAgentLocation(a);
        String[] agentCoordinates = agentLocation.split(",");
        int x = Integer.parseInt(agentCoordinates[0]);
        int y = Integer.parseInt(agentCoordinates[1]);
		if (ACTION_MOVE_RIGHT == agentAction) {
            x = (x + 1 < gridWidth ? x + 1 : x);
			envState.setAgentLocation(a, String.format(LOCATION_FORMAT, x, y));
			updatePerformanceMeasure(a, -1);
		} else if (ACTION_MOVE_LEFT == agentAction) {
            x = (x - 1 >= 0 ? x - 1 : x);
			envState.setAgentLocation(a, String.format(LOCATION_FORMAT, x, y));
			updatePerformanceMeasure(a, -1);
        } else if (ACTION_MOVE_UP == agentAction) {
            y = (y + 1 < gridHeight ? y + 1 : y);
			envState.setAgentLocation(a, String.format(LOCATION_FORMAT, x, y));
			updatePerformanceMeasure(a, -1);
        } else if (ACTION_MOVE_DOWN == agentAction) {
            y = (y - 1 >= 0 ? y - 1 : y);
			envState.setAgentLocation(a, String.format(LOCATION_FORMAT, x, y));
			updatePerformanceMeasure(a, -1);
		} else if (ACTION_SUCK == agentAction) {
			if (LocationState.Dirty == envState.getLocationState(envState
					.getAgentLocation(a))) {
				envState.setLocationState(envState.getAgentLocation(a),
						LocationState.Clean);
				updatePerformanceMeasure(a, 10);
			}
		} else if (agentAction.isNoOp()) {
			// In the Vacuum Environment we consider things done if
			// the agent generates a NoOp.
			isDone = true;
		}
		return envState;
	}

	@Override
	public Percept getPerceptSeenBy(Agent anAgent) {
		if (anAgent instanceof NondeterministicVacuumAgent) {
    		// Note: implements FullyObservableVacuumEnvironmentPercept
    		return new VacuumEnvironmentState(this.envState);
    	}
		String agentLocation = envState.getAgentLocation(anAgent);
		return new LocalVacuumEnvironmentPercept(agentLocation,
				envState.getLocationState(agentLocation));
	}

	@Override
	public boolean isDone() {
		return super.isDone() || isDone;
	}

	@Override
	public void addAgent(Agent a) {
        Random r = new Random();
		int x = r.nextInt(gridWidth);
        int y = r.nextInt(gridHeight);
		envState.setAgentLocation(a, String.format(LOCATION_FORMAT, x, y));
		super.addAgent(a);
	}

    /**
      * Add agent using LOCATION_FORMAT'd location string
      */
	public void addAgent(Agent a, String location) {
		// Ensure the agent state information is tracked before
		// adding to super, as super will notify the registered
		// EnvironmentViews that is was added.
		envState.setAgentLocation(a, location);
		super.addAgent(a);
	}

    /**
      * Add agent using x and y coordinates
      */
    public void addAgent(Agent a, int x, int y) {
        envState.setAgentLocation(a, String.format(LOCATION_FORMAT, x, y));
        super.addAgent(a);
    }

	public LocationState getLocationState(String location) {
		return envState.getLocationState(location);
	}

	public String getAgentLocation(Agent a) {
		return envState.getAgentLocation(a);
	}

    /**
      * Add dirt to location specified by LOCATION_FORMAT'd location string
      */
    public void addDirt(String location) {
        envState.setLocationState(location, LocationState.Dirty);
    }

    /**
      * Add dirt to location specified by x and y coordinates
      */
    public void addDirt(int x, int y) {
        envState.setLocationState(String.format(LOCATION_FORMAT, x, y),
                LocationState.Dirty);
    }

    /**
      * Remove dirt from location specified by LOCATION_FORMAT'd location string
      */
    public void removeDirt(String location) {
        envState.setLocationState(location, LocationState.Clean);
    }

    /**
      * Remove dirt from location specified by x and y coordinates
      */
    public void removeDirt(int x, int y) {
        envState.setLocationState(String.format(LOCATION_FORMAT, x, y),
                LocationState.Clean);
    }

    /**
      * Set state of location specified by LOCATION_FORMAT'd location string
      */
    public void setLocationState(String location, LocationState state) {
        envState.setLocationState(location, state);
    }

    /**
      * Set state of location specified by x and y coordinates
      */
    public void setLocationState(int x, int y, LocationState state) {
        envState.setLocationState(String.format(LOCATION_FORMAT, x, y), state);
    }

    /**
      * Change all locations to a given set of states
      */
    public void setLocationStates(Boolean[][] grid) {
        for (int i = 0; i < gridWidth; ++i) {
            for (int j = 0; j < gridHeight; ++j) {
                envState.setLocationState(String.format(LOCATION_FORMAT, i, j),
                        grid[i][j] ? LocationState.Dirty : LocationState.Clean);
            }
        }
    }
}
