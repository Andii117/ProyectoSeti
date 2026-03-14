package com.acme.acme_order_api.SSLUtil;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLUtil {

    public static void disableSSLVerification() throws NoSuchAlgorithmException, KeyManagementException {

        // Creating a Trust Manager.
        TrustManager[] TrustMgr = new TrustManager[] {
                new X509TrustManager(){public X509Certificate[] getAcceptedIssuers(){return null;
                }

                    // No Checking required
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                    // No Checking required
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        }
                ;

        // Installing the Trast manager
                SSLContext SSLCont = SSLContext.getInstance("SSL");
                SSLCont.init(null, TrustMgr, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(SSLCont.getSocketFactory());

        // Creating an all-trusting verifier for Host Name
                HostnameVerifier ValidHost = new HostnameVerifier() {
                    public boolean verify(String HostName, SSLSession MySession) {
                        return true;
                    }
                };

        // Installing an all-trusting verifier for the HOST
                HttpsURLConnection.setDefaultHostnameVerifier(ValidHost);

    }
}
