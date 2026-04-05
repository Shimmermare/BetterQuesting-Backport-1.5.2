package betterquesting.backport;

public interface Consumer<T> {
    void accept(T t);
}