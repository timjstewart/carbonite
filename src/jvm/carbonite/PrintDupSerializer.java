package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Serializer;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/21/12 Time: 7:57 PM */
public class PrintDupSerializer extends Serializer {
    Var cljRead;
    Var cljPrintDup;

    public PrintDupSerializer() {
        JavaBridge.requireCarbonite();
        cljRead = RT.var("carbonite.serializer", "clj-read");
        cljPrintDup = RT.var("carbonite.serializer", "clj-print-dup");
    }

    @Override public void writeObjectData(ByteBuffer byteBuffer, Object o) {
        cljPrintDup.invoke(byteBuffer, o);
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        return (T) cljRead.invoke(byteBuffer);
    }
}
