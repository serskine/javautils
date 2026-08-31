package javautils.search;

import javautils.Graph;
import javautils.Logger;
import javautils.search.fringe.DepthFirstFringe;
import javautils.search.history.SetHistory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class SearchTest {

    private Graph<State, Action> g;

    public enum State {
        A, B, C, D, E, F, G, H, I
    }

    public enum Action {
        WALK, RUN
    }

    private Rules<State, Action> rules = new Rules<State, Action>() {
        @Override
        public Iterator<Path<State, Action>> expand(Path<State, Action> current) {
            final State source = current.state;
            final Map<State, Action> map = g.getPathFrom(source);
            final List<Path<State, Action>> paths = new ArrayList<Path<State, Action>>();
            for(final Map.Entry<State, Action> entry : map.entrySet()) {
                final State target = entry.getKey();
                final Action action = entry.getValue();
                final Path path = new  Path<State, Action>(target, action, current);
                paths.add(path);
            }
            return paths.iterator();
        }
    };

    private History<State, Action> history = new SetHistory<>();
    private Fringe<State, Action> fringe = new DepthFirstFringe<>();

    private Search<State, Action> search;

    @Before
    public void onSetup() {
        g = new Graph<>();
        g.link(State.A, State.B, Action.WALK);
        g.link(State.A, State.C, Action.WALK);
        g.link(State.B, State.D, Action.WALK);
        g.link(State.B, State.E, Action.WALK);
        g.link(State.C, State.D, Action.WALK);
        g.link(State.E, State.F, Action.WALK);
        g.link(State.D, State.G, Action.WALK);
        g.link(State.E, State.G, Action.WALK);
        g.link(State.E, State.H, Action.WALK);
        g.link(State.G, State.I, Action.WALK);
        g.link(State.H, State.I, Action.WALK);
        g.link(State.I, State.A, Action.RUN);

        search = new Search<>(rules, history, fringe);
        search.push(new Path<>(State.A));
    }


    @After
    public void onTearDown() {

    }

    @Test
    public void testSearch() {
        int i=0;
        while(search.hasNext() && i<100) {
            final Path<State, Action> path = search.next();
            Logger.info("path[" + i + "]: " + path.toString());
            i++;
        }
        Logger.info("Found " + i +  " paths\n");
    }
}
