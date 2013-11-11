package protocol_packages;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: markdev
 * Date: 11/11/13
 * Time: 5:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChangeCipherSpec {
    private BigInteger modulus;
    private BigInteger pubExp;

    public ChangeCipherSpec(BigInteger modulus, BigInteger pubExp) {
        this.modulus = modulus;
        this.pubExp = pubExp;
    }

    public BigInteger getModulus() {
        return modulus;
    }

    public BigInteger getPubExp() {
        return pubExp;
    }

    @Override

    public String toString() {
        return "protocol_packages.ChangeCipherSpec{" +
                "modulus=" + modulus +
                ", pubExp=" + pubExp +
                '}';
    }
}
