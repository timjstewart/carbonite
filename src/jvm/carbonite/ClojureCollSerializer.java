package carbonite;

import clojure.lang.RT;
import clojure.lang.Var;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/21/12 Time: 8:01 PM */
public abstract class ClojureCollSerializer extends Serializer {
    Var printCollection;
    Kryo kryo;
    
    public ClojureCollSerializer(Kryo k) {
        JavaBridge.requireCarbonite();
        printCollection = RT.var("carbonite.serializer", "print-collection");
        this.kryo = k;

    }

    @Override public void writeObjectData(ByteBuffer byteBuffer, Object o) {
        printCollection.invoke(kryo, byteBuffer, o);
    }
}
