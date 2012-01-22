package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/21/12 Time: 8:13 PM */
public class ClojureSetSerializer extends ClojureCollSerializer {
    Var readSet;

    public ClojureSetSerializer(Kryo k) {
        super(k);
        readSet = RT.var("carbonite.serializer", "read-set");
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        return (T) readSet.invoke(kryo, byteBuffer);
    }
}
