package carbonite;

/** User: sritchie Date: 1/20/12 Time: 2:33 PM */

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

import java.nio.ByteBuffer;

public class ClojureMapSerializer extends Serializer {
    Var writeMap;
    Var readMap;
    Kryo kryo;

    public ClojureMapSerializer(Kryo k) {
        JavaBridge.requireCarbonite();
        writeMap = RT.var("carbonite.serializer", "write-map");
        readMap = RT.var("carbonite.serializer", "read-map");
        this.kryo = k;
    }

    @Override public void writeObjectData(ByteBuffer byteBuffer, Object o) {
        writeMap.invoke(kryo, byteBuffer, o);
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        return (T) readMap.invoke(kryo, byteBuffer);
    }
}
