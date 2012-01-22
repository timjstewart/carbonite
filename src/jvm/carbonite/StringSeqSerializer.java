package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Serializer;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/21/12 Time: 8:19 PM */
public class StringSeqSerializer extends Serializer {
    Var readStringSeq;
    Var printStringSeq;

    public StringSeqSerializer() {
        JavaBridge.requireCarbonite();
        readStringSeq = RT.var("carbonite.serializer", "read-string-seq");
        printStringSeq = RT.var("carbonite.serializer", "write-string-seq");
    }

    @Override public void writeObjectData(ByteBuffer byteBuffer, Object o) {
        printStringSeq.invoke(byteBuffer, o);
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        return (T) readStringSeq.invoke(byteBuffer);
    }
}
