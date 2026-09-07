package net.smileycorp.mounts.config.data.values;

import com.google.gson.JsonObject;
import net.smileycorp.atlas.api.data.BinaryOperation;
import net.smileycorp.atlas.api.data.DataType;
import net.smileycorp.mounts.common.MountsLogger;
import net.smileycorp.mounts.config.data.DataRegistry;
import net.smileycorp.mounts.config.data.ParsingException;
import net.smileycorp.mounts.config.data.SpawnContext;

public class BinaryOperationValue<T extends Number & Comparable<T>> implements Value<T> {
    
    private final BinaryOperation operation;
    private final Value<T> value1, value2;
    
    private BinaryOperationValue(BinaryOperation operation, Value<T> value1, Value<T> value2) {
        this.operation = operation;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public T get(SpawnContext ctx) {
        return (T) operation.apply(value1.get(entity), value2.get(entity));
    }

    public static Deserializer of(BinaryOperation operation) {
        return new Deserializer(operation);
    }

    public static class Deserializer {

        private final BinaryOperation operation;

        private Deserializer(BinaryOperation operation) {
            this.operation = operation;
        }

        public <T extends Comparable<T>> BinaryOperationValue deserialize(JsonObject obj, DataType<T> type) {
            try {
                Value<T> getter1 = DataRegistry.readValue(type, obj.get("value1"));
                Value<T> getter2 = DataRegistry.readValue(type, obj.get("value2"));
                if (getter1 == null || getter2 == null | !type.isNumber()) {
                    MountsLogger.logError("invalid values for " + operation.getName(), new ParsingException(
                            "value1: " + obj.get("value1") + ", value2: " + obj.get("value2")));
                    return null;
                }
                return new BinaryOperationValue(operation, getter1, getter2);
            } catch (Exception e) {
                MountsLogger.logError("invalid values for " + operation.getName(), new ParsingException("missing values"));
                return null;
            }
        }
    }
    
}
