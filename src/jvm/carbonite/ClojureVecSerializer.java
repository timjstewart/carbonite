package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/21/12 Time: 8:13 PM */
public class ClojureVecSerializer extends ClojureCollSerializer {
    Var readVec;

    public ClojureVecSerializer(Kryo k) {
        super(k);
        readVec = RT.var("carbonite.serializer", "read-vector");
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        return (T) readVec.invoke(kryo, byteBuffer);
    }
}
