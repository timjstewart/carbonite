package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/21/12 Time: 8:09 PM */
public class ClojureSeqSerializer extends ClojureCollSerializer {
    Var readSeq;

    public ClojureSeqSerializer(Kryo k) {
        super(k);
        readSeq = RT.var("carbonite.serializer", "read-seq");
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        return (T) readSeq.invoke(kryo, byteBuffer);
    }
}
