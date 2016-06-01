package org.razorfish.storefront.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.springframework.core.NestedIOException;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.ConfigurableObjectInputStream;

public class DefaultDeserializer implements Deserializer<Object> {
    private final ClassLoader classLoader;

    public DefaultDeserializer() {
        this.classLoader = getClass().getClassLoader();
    }

    public DefaultDeserializer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    @SuppressWarnings("resource")
    public Object deserialize(InputStream inputStream) throws IOException {
        ObjectInputStream objectInputStream = new ConfigurableObjectInputStream(inputStream, classLoader);
        try {
            return objectInputStream.readObject();
        }
        catch (ClassNotFoundException ex) {
            throw new NestedIOException("Failed to deserialize object type", ex);
        }
    }

}
