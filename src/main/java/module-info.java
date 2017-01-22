module ro.derbederos.hamcrest {
    exports ro.derbederos.hamcrest;

    requires java.base;
    requires jdk.unsupported;
    requires transitive hamcrest.all;
    requires streamsupport;
}
