package pro.doczilla.common.api.interfaces;

public interface IInitialize {
    default void preInit() {

    }

     void init();

    default void postInit() {

    }
}