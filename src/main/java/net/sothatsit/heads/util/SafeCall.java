package net.sothatsit.heads.util;

import com.google.common.base.Predicate;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SafeCall {

    private final ExceptionDetailer exceptionDetailer;

    private SafeCall() {
        exceptionDetailer = ExceptionDetailer.constructorDetailer();
    }

    protected RuntimeException fail(String message) {
        throw exceptionDetailer.detail(new IllegalStateException(message));
    }

    protected RuntimeException fail(String message, Throwable cause) {
        throw exceptionDetailer.detail(new IllegalStateException(message, cause));
    }

    public static <T, R> Function<T, R> function(String name, Function<T, R> function) {
        return new SafeFunction<>(function, name);
    }

    public static <T, R> Function<T, R> nonNullFunction(String name, Function<T, R> function) {
        return new NonNullSafeFunction<>(function, name);
    }

    public static <T> Predicate<T> predicate(String name, Predicate<T> predicate) {
        return new SafePredicate<>(predicate, name);
    }

    public static <T> Predicate<T> nonNullPredicate(String name, Predicate<T> predicate) {
        return new NonNullSafePredicate<>(predicate, name);
    }

    public static <V> SafeCallable<V> callable(String name, Callable<V> callable) {
        return new SafeCallable<>(callable, name);
    }

    public static <V> SafeCallable<V> nonNullCallable(String name, Callable<V> callable) {
        return new NonNullSafeCallable<>(callable, name);
    }

    public static <T> Consumer<T> consumer(String name, Consumer<T> consumer) {
        return new SafeConsumer<>(consumer, name);
    }

    public static <T> Consumer<T> nonNullConsumer(String name, Consumer<T> consumer) {
        return new NonNullSafeConsumer<>(consumer, name);
    }

    private static class SafeFunction<T, R> extends SafeCall implements Function<T, R> {

        private final Function<T, R> function;
        protected final String name;

        private SafeFunction(Function<T, R> function, String name) {
            Checks.ensureNonNull(function, "function");
            Checks.ensureNonNull(name, "name");

            this.function = function;
            this.name = name;
        }

        @Override
        public R apply(T t) {
            try {
                return function.apply(t);
            } catch(Exception e) {
                throw fail("Exception thrown when calling function " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + function + " (" + name + ")";
        }

    }

    private static class NonNullSafeFunction<T, R> extends SafeFunction<T, R> {

        private NonNullSafeFunction(Function<T, R> function, String name) {
            super(function, name);
        }

        @Override
        public R apply(T t) {
            Checks.ensureNonNull(t, "argument");

            R returnValue = super.apply(t);

            if(returnValue == null)
                throw fail(name + " function returned a null value");

            return returnValue;
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

    private static class SafePredicate<T> extends SafeCall implements Predicate<T> {

        private final Predicate<T> predicate;
        protected final String name;

        private SafePredicate(Predicate<T> predicate, String name) {
            Checks.ensureNonNull(predicate, "predicate");
            Checks.ensureNonNull(name, "name");

            this.predicate = predicate;
            this.name = name;
        }

        @Override
        public boolean apply(T t) {
            try {
                return predicate.apply(t);
            } catch(Exception e) {
                throw fail("Exception thrown when calling predicate " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + predicate + " (" + name + ")";
        }

    }

    private static class NonNullSafePredicate<T> extends SafePredicate<T> {

        private NonNullSafePredicate(Predicate<T> predicate, String name) {
            super(predicate, name);
        }

        @Override
        public boolean apply(T t) {
            Checks.ensureNonNull(t, "argument");

            return super.apply(t);
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

    public static class SafeCallable<V> extends SafeCall implements Callable<V> {

        private final Callable<V> callable;
        protected final String name;

        private SafeCallable(Callable<V> callable, String name) {
            Checks.ensureNonNull(callable, "callable");
            Checks.ensureNonNull(name, "name");

            this.callable = callable;
            this.name = name;
        }

        @Override
        public V call() {
            try {
                return callable.call();
            } catch(Exception e) {
                throw fail("Exception thrown when calling callable " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + callable + " (" + name + ")";
        }

    }

    private static class NonNullSafeCallable<V> extends SafeCallable<V> {

        private NonNullSafeCallable(Callable<V> callable, String name) {
            super(callable, name);
        }

        @Override
        public V call() {
            V returnValue = super.call();

            if(returnValue == null)
                throw fail(name + " callable returned a null value");

            return returnValue;
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

    private static class SafeConsumer<T> extends SafeCall implements Consumer<T> {

        private final Consumer<T> consumer;
        protected final String name;

        private SafeConsumer(Consumer<T> consumer, String name) {
            Checks.ensureNonNull(consumer, "consumer");
            Checks.ensureNonNull(name, "name");

            this.consumer = consumer;
            this.name = name;
        }

        @Override
        public void accept(T t) {
            try {
                consumer.accept(t);
            } catch(Exception e) {
                throw fail("Exception thrown when calling predicate " + name, e);
            }
        }

        @Override
        public String toString() {
            return "Safe " + consumer + " (" + name + ")";
        }

    }

    private static class NonNullSafeConsumer<T> extends SafeConsumer<T> {

        private NonNullSafeConsumer(Consumer<T> consumer, String name) {
            super(consumer, name);
        }

        @Override
        public void accept(T t) {
            Checks.ensureNonNull(t, "argument");

            super.accept(t);
        }

        @Override
        public String toString() {
            return "NonNull " + super.toString();
        }

    }

}
