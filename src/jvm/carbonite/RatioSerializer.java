package carbonite;

import clojure.lang.Ratio;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.serialize.BigIntegerSerializer;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/** User: sritchie Date: 1/20/12 Time: 3:49 PM */
public class RatioSerializer extends Serializer {
    BigIntegerSerializer big  = new BigIntegerSerializer();

    @Override public void writeObjectData(ByteBuffer byteBuffer, Object o) {
        Ratio ratio = (Ratio) o;

        big.writeObjectData(byteBuffer, ratio.numerator);
        big.writeObjectData(byteBuffer, ratio.denominator);
    }

    @Override public <T> T readObjectData(ByteBuffer byteBuffer, Class<T> tClass) {
        BigInteger num = big.readObjectData(byteBuffer, null);
        BigInteger denom = big.readObjectData(byteBuffer, null);

        return (T) new Ratio(num, denom);

    }
}
