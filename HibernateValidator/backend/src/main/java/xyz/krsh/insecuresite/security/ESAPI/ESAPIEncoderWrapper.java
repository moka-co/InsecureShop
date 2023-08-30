package xyz.krsh.insecuresite.security.ESAPI;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

//Singleton encoder
public class ESAPIEncoderWrapper {

    private static Encoder encoder = ESAPI.encoder();

    public static Encoder getEncoder() {
        return encoder;
    }

}
