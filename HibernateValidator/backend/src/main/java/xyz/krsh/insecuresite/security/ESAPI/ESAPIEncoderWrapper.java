package xyz.krsh.insecuresite.security.ESAPI;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

//Singleton encoder
public class ESAPIEncoderWrapper {

    private Encoder encoder = ESAPI.encoder();

    public Encoder getEncoder() {
        return encoder;
    }

}
