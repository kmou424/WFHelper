package moe.kmou424.WorldFlipper.Helper.Tools;

public class Constructor<T> {
    private final T obj;

    public Constructor(T obj) {
        this.obj = obj;
    }

    public T make() {
        return this.obj;
    }
}
