package betterquesting.backport;

public interface Function<T, R> {
    R apply(T t);
}