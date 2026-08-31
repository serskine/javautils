package javautils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Announcer provides a proxy of the listener interface that, when a method
 * is invoked on the proxy, forwards the invocation to all registered listeners.
 *
 * Usage:
 * Announcer<MyListener> a = new Announcer<>(MyListener.class);
 * a.addListener(listener1);
 * a.addListener(listener2);
 * a.announce().onEvent(...); // calls onEvent on listener1 and listener2
 */
public class Announcer<T extends EventListener> {

    private final LinkedList<T> listeners = new LinkedList<T>();
    private final T proxy;

    public Announcer(Class<T> listenerType) {
        // create a dynamic proxy implementing the listenerType that forwards
        // method calls to all registered listeners
        //noinspection unchecked
        this.proxy = (T) Proxy.newProxyInstance(
            listenerType.getClassLoader(),
            new Class<?>[] { listenerType },
            (proxy, method, args) -> {
                // handle Object methods locally
                if (method.getDeclaringClass() == Object.class) {
                    return handleObjectMethod(method, args);
                }

                // snapshot to avoid ConcurrentModificationException and to reduce
                // lock contention while invoking listeners
                final List<T> snapshot;
                synchronized (listeners) {
                    snapshot = new ArrayList<>(listeners);
                }

                for (T listener : snapshot) {
                    try {
                        method.invoke(listener, args);
                    } catch (InvocationTargetException e) {
                        // unwrap and rethrow runtime exceptions/errors, otherwise wrap
                        Throwable cause = e.getCause();
                        if (cause instanceof RuntimeException) {
                            throw (RuntimeException) cause;
                        } else if (cause instanceof Error) {
                            throw (Error) cause;
                        } else {
                            throw new RuntimeException(cause);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return null; // listener methods are assumed void; any return is ignored
            }
        );
    }

    private Object handleObjectMethod(Method method, Object[] args) {
        String name = method.getName();
        if ("toString".equals(name)) {
            return "Announcer proxy for listeners: " + listeners.toString();
        } else if ("hashCode".equals(name)) {
            return System.identityHashCode(this);
        } else if ("equals".equals(name)) {
            return proxy == args[0];
        }
        return null;
    }

    public final void addListener(final T listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public final void removeListener(final T listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Returns a proxy implementing the listener interface. Invoking any method
     * on the returned proxy will forward that invocation to all registered listeners.
     *
     * Example: announcer.announce().onEvent(x) will call onEvent(x) on every listener.
     */
    public final T announce() {
        return proxy;
    }

}
