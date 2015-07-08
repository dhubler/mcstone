package org.conf2.minecraft;

import org.conf2.minecraft.event.NewUserEvent;
import org.conf2.minecraft.event.RemovedUserEvent;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EventSource implements Runnable {
    private BufferedReader in;
    private OutputStream out;
    private boolean done;
    private static Logger logger = Logger.getLogger("org.conf2.minecraft");
    private static Map<Class,Pattern> patterns = new HashMap<Class, Pattern>();
    static {
        registerEvent(NewUserEvent.class, Pattern.compile("^(\\S+) joined the game$"));
        registerEvent(RemovedUserEvent.class, Pattern.compile("^(\\S+) left the game$"));
    }
    private Map<Class, List<CraftListener>> listeners = new HashMap<Class,List<CraftListener>>();

    public EventSource(InputStream in, OutputStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = out;
    }

    public static EventSource replaceStdOut() throws IOException {
        PipedInputStream pipe = new PipedInputStream(2048);
        OutputStream proxy = new PipedOutputStream(pipe);
        EventSource events = new EventSource(pipe, System.out);
        System.setOut(new PrintStream(proxy, true));
        return events;
    }

    static void registerEvent(Class c, Pattern p) {
        patterns.put(c, p);
    }

    @Override
    public void run() {
        done = false;
        while (!done) {
            try {
                String line = this.in.readLine();
                if (line == null) {
                    logger.log(Level.INFO, "No more input to read from");
                    break;
                }
                for (Map.Entry<Class,Pattern> e : patterns.entrySet()) {
                    int begin = line.indexOf("]: ");
                    if (begin < 0) {
                        continue;
                    }
                    Matcher m = e.getValue().matcher(line.substring(begin + 3));
                    if (m.matches()) {
                        MatchResult matchResult = m.toMatchResult();
                        try {
                            Constructor<?> ctor = ((Class<?>)e.getKey()).getConstructor(MatchResult.class);
                            fire(e.getKey(), (CraftEvent) ctor.newInstance(m));
                        } catch (ReflectiveOperationException e1) {
                            logger.log(Level.SEVERE, "Could not create event", e1);
                        }
                    }
                }
                if (out != null) {
                    out.write(line.getBytes());
                    out.write('\n');
                    out.flush();
                }
            } catch (IOException e) {
                throw new CraftError("Error reading server output");
            }
        }
    }

    public void addListener(Class eventType, CraftListener listener) {
        List<CraftListener> eventListeners = listeners.get(eventType);
        if (eventListeners == null) {
            eventListeners = new ArrayList<CraftListener>();
            listeners.put(eventType, eventListeners);
        }
        eventListeners.add(listener);
    }

    @SuppressWarnings("unchecked")
    void fire(Class c, CraftEvent e) {
        logger.log(Level.INFO, "firing event " + c.getName());
        List<CraftListener> eventListeners = listeners.get(e.getClass());
        if (eventListeners != null) {
            for (CraftListener l : eventListeners) {
                l.onCraftEvent(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        EventSource events = EventSource.replaceStdOut();
        events.addListener(NewUserEvent.class, new CraftListener<NewUserEvent>() {
            @Override
            public void onCraftEvent(NewUserEvent e) {
                System.out.println("got new user event");
            }
        });
        new Thread(events).start();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = in.readLine();
            System.out.println(line);
        }
    }
}
