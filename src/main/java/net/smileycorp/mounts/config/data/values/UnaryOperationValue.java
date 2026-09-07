package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.atlas.api.data.UnaryOperation;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.SpawnContext;

public class UnaryOperationValue<T extends Number & Comparable<T>> implements Value<T> {
    
    private final UnaryOperation operation;
    private final Value<T> value;
    
    private UnaryOperationValue(UnaryOperation<T> operation, Value<T> value) {
        this.operation = operation;
        this.value = value;
    }

    @Override
    public T get(SpawnContext ctx) {
        return (T) operation.apply(value.get(entity));
    }

    public static Deserializer of(UnaryOperation operation) {
        return new Deserializer(operation);
    }

    public static class Deserializer {

        private final UnaryOperation operation;

        private Deserializer(UnaryOperation operation) {
            this.operation = operation;
        }

        public <T extends Comparable<T>> UnaryOperationValue deserialize(JsonObject obj, DataType<T> type) {
            try {
                Value<T> getter = DataRegistry.readValue(type, obj.get("value"));
                if (getter == null | !type.isNumber()) {
                    MountsLogger.logError("invalid value for " + operation.getName(), new ParsingException(obj.get("value").toString()));
                    return null;
                }
                return new UnaryOperationValue(operation, getter);
            } catch (Exception e) {
                MountsLogger.logError("invalid values for " + operation.getName(), new ParsingException("missing value"));
                return null;
            }
        }
    }
    
}
