package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Serializer;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/20/12 Time: 3:57 PM
 *
 * Define a serializer that utilizes the Clojure pr-str and
 * read-string functions to serialize/deserialize instances relying
 * solely on the printer/reader.  Probably not the most efficient but
 * likely to work in many cases.
 *
 */
public class ClojureReaderSerializer extends Serializer {
    Var cljRead;
    Var cljPrint;

    public ClojureReaderSerializer() {
        JavaBridge.requireCarbonite();
        cljRead = RT.var("carbonite.serializer", "clj-read");
        cljPrint = RT.var("carbonite.serializer", "clj-print");
    }

    @Override public void writeObjectData(ByteBuffer byteBuffer, Object o) {
        cljPrint.invoke(byteBuffer, o);
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        return (T) cljRead.invoke(byteBuffer);
    }
}
