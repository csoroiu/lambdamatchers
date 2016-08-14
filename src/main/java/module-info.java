module ro.derbederos.hamcrest {
    exports ro.derbederos.hamcrest;

    requires java.base;
    requires transitive scala.library;
    requires jdk.unsupported;
    requires transitive org.hamcrest;
    uses sun.misc.Unsafe;
}
