package carbonite;

import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.StringSerializer;

import java.nio.ByteBuffer;

/** User: sritchie Date: 1/20/12 Time: 3:11 PM */
public class URISerializer extends Serializer {

    @Override public void writeObjectData(ByteBuffer byteBuffer, Object o) {
        StringSerializer.put(byteBuffer, o.toString());
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        String s = StringSerializer.get(byteBuffer);
        return (T) java.net.URI.create(s);
    }
}
