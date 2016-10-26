module ro.derbederos.hamcrest {
    exports ro.derbederos.hamcrest;

    requires java.base;
    requires java.instrument;
    requires jdk.unsupported;
    requires transitive hamcrest.all;
    requires streamsupport;
    requires asm;
    requires asm.tree;
}
