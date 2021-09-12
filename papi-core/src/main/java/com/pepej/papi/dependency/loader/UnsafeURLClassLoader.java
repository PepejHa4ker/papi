package com.pepej.papi.dependency.loader;

import com.pepej.papi.shadow.ClassTarget;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

class UnsafeURLClassLoader implements PapiURLClassLoader {


    @ClassTarget(Unsafe.class)
    private interface UnsafeShadow extends Shadow {

        @com.pepej.papi.shadow.Field
        Unsafe getTheUnsafe();
    }

    private static final sun.misc.Unsafe UNSAFE;

    private final ClassLoader classLoader;

    static {
        UnsafeShadow unsafeShadow = ShadowFactory.global().staticShadow(UnsafeShadow.class);
        UNSAFE = unsafeShadow.getTheUnsafe();
    }

    static boolean isSupported() {
        return UNSAFE != null;
    }

    private final Collection<URL> unopenedURLs;
    private final Collection<URL> pathURLs;

    @SuppressWarnings("unchecked")
    UnsafeURLClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;

        Collection<URL> unopenedURLs;
        Collection<URL> pathURLs;
        try {
            Object ucp = fetchField(URLClassLoader.class, classLoader, "ucp");
            unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
            pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
        } catch (Throwable e) {
            unopenedURLs = null;
            pathURLs = null;
        }
        this.unopenedURLs = unopenedURLs;
        this.pathURLs = pathURLs;
    }

    private static Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        long offset = UNSAFE.objectFieldOffset(field);
        return UNSAFE.getObject(object, offset);
    }

    @Override
    public void addURL(@NotNull URL url) {
        this.unopenedURLs.add(url);
        this.pathURLs.add(url);
    }



    @Override
    public @NotNull ClassLoader getClassLoader() {
        return this.classLoader;
    }


}
