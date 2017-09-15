package ua.rd.ioc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;

public class ClassProxyHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Fooooooooo");
        if (method.isAnnotationPresent(Benchmark.class)) {
            return methodInvocationWithExecutionTimeNoted(proxy, method, args);
        } else {
            return method.invoke(proxy, args);
        }
    }

    private Object methodInvocationWithExecutionTimeNoted(Object proxy, Method method, Object[] args) {
        Object objectAnnotatedMethodExecutionResult;
        Clock clock = Clock.systemDefaultZone();
        long startTime = clock.millis();

        try {
            objectAnnotatedMethodExecutionResult = method.invoke(proxy, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        long stopTime = clock.millis();
        long elapsedTimeInSeconds = (stopTime - startTime) / 1000;

        System.out.println("Method execution time: " + elapsedTimeInSeconds);
        return objectAnnotatedMethodExecutionResult;
    }
}

