package com.cloudpos.apidemo.util;

import android.util.Base64;

import com.cloudpos.sdk.util.Logger;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

public class CertHelper {
    private static volatile CertHelper instance = null;

    private CertHelper() {}

    public static CertHelper getInstance() {
        if (instance == null) {
            synchronized (CertHelper.class) {
                if (instance == null) {
                    instance = new CertHelper();
                }
            }
        }
        return instance;
    }

    public X509Certificate getCert(byte[] data){
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(data));
            return cert;
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        Logger.debug("getCert(%s)", cf);
        return null;
    }
    public PrivateKey getPriKey(){
        String pem = priKey;
        pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");  // 去掉换行和空格

        byte[] der = Base64.decode(pem, 0);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(der);
        KeyFactory kf = null; // "RSA" 或 "EC"
        try {
            kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.debug("getK(%s)", kf);
        return null;
    }


    public static final String priKey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtfIgi3i34Eg9C\n" +
            "pAjTuID56MCtiRWpbOvTYKluRjrFh5vf1JfTH6hY+wGcvh1az+Cf3i5c0P0Npvqp\n" +
            "b2yLAH8c4CB4ur7FlZ/MY2RtyPUJUOh3rD4vhMgCItPWp6uoVi5+1/xz23biBmJw\n" +
            "I+uO+AIHmXpRw/QKjCKLFczYFwuGIKIFQie2adHRK4saEUNtm5hGr7TLrfiBLar5\n" +
            "zkcKEEtfB4hcEwlcJisE1LcQiCUh5qlVqHETlwvZtJizbGzDnwG0rCs3/Gd+iMs7\n" +
            "UG9myn7JNIO9pwxEihTP1qBq/ya/biWhtLa1y5wBVgoHoEbxKobGjpIr3wW9XTbg\n" +
            "VLtZv0wNAgMBAAECggEACNl0XEr3D1NkgeK6LIqZ0aWkxnVarITI1sR0gBKHBvJu\n" +
            "6hD/GZ7gyIEtSOCNwMcfzyfXN5bVga/F04TChail00Q/g8kK2CtUtmlPgyYgEbSV\n" +
            "1xzrIyNjzHAYXDe7Ut/qiT/dOc+1Fbx7ovrCFk3iVJ7olWkodq1FSdD33QH37YSA\n" +
            "86AFS11ZyMGc1LgvYnny9t7eeGoBADJ/S/FYsM3oM1osBg30whsdqoL+FxWgedG4\n" +
            "4zfJxgNTTJ1Amd6LXVQfSldQWP+2OOudWtFngk1dZUFq4rGd9YGEVyqcfzLjmGsM\n" +
            "YOesjcpTzrgOILdjjT3XncgyOhfFL6nR3rztU43JZQKBgQDu/5ARrzXvOLMpX58D\n" +
            "xi0PnaymLzkKmwNixkHSBJCS4XQBzAY2o1e+Whg3FGdUSXYtENnf6SsHJEWXIrfd\n" +
            "O9OA3L8Yp0ml7Ks0VCVpL8g8rPfxP6Z8JKlx4f/FHZFitvqFldJwQB7dGO+SWWIa\n" +
            "npBybiyuyiOQEkfokJGD3m3UwwKBgQC50+wPy7qTBd6lF2kgvhLCfYdH8kOobXCy\n" +
            "JsmYKMFslPsHu3iowKB+xJG5LsSStOReynXQW5PQo+596yH4/OwAYwWZ7lk4N5e6\n" +
            "tVWTmaaeHAVs4kfiNOZkPGlz3DeQePjPTG9fiqqORvwJMjlAahTEqAmrdS7q89Tw\n" +
            "iZgszPEO7wKBgAp5OJ8NxpdNLMzLW/SWYrMkfcRqnV1RnbxeY9QITy2go6zQfBU4\n" +
            "40d8O0NuqXiQH5Wp/2JInZIajNIqu1P+oy4qflVeP2P+EyKf7WPGrEe8bMEtwOX/\n" +
            "1U0gUk0ZFp301tWz0x0IMlIOHbGUKTBxcnMRUPP398cAhhYy/61/ueBxAoGAY9V3\n" +
            "6FCSjvRDVjyKjT2whG6+JCgCs1UAi6WM/sM+j2BGsTPFshuy3ggrJer7TroCSu7I\n" +
            "dsUMV7YKpfmSCduPyvFoC/fiVPKBAJz1OdP9kMoLHCFBb0TuVgGiwc0YyPRE79Hn\n" +
            "r9Omp8N5GlOyMGoNeE2yzDurliyjRQsXB1KfW18CgYEAj5MQuui0JFA5aiBzAYKx\n" +
            "+wZNZpC+H1iEOZWS/mv1yDxFg0KmV1npR7HnCvMH/7vqczttrWhu97qem2TUi71j\n" +
            "0+wZqotVgniHLV/XXbIPgpk5GHHzuYjUXVL9fWYa4OUUqUoST9hPG9ovQZ+9aLA/\n" +
            "Jaj4wS3wT8kv+lvvhylMmm0=\n" +
            "-----END PRIVATE KEY-----\n";
    public static final String pubKey = "-----BEGIN CERTIFICATE-----\n" +
            "MIIFdjCCA16gAwIBAgIUTHv+OYUlFCX9ax5Ny7MbZE8xS+4wDQYJKoZIhvcNAQEL\n" +
            "BQAwgaUxCzAJBgNVBAYTAlZFMRAwDgYDVQQIDAdDYXJhY2FzMRAwDgYDVQQHDAdD\n" +
            "YXJhY2FzMRAwDgYDVQQKDAdUcmFucmVkMRcwFQYDVQQLDA50cmFucmVkLmNvbS52\n" +
            "ZTEXMBUGA1UEAwwOdHJhbnJlZC5jb20udmUxLjAsBgkqhkiG9w0BCQEWH3NlZ3Vy\n" +
            "aWRhZGFjY2Vzb3NAdHJhbnJlZC5jb20udmUwHhcNMjUwOTA4MTQyODIwWhcNMzUw\n" +
            "OTA2MTQyODIwWjCBpTELMAkGA1UEBhMCVkUxEDAOBgNVBAgMB0NhcmFjYXMxEDAO\n" +
            "BgNVBAcMB0NhcmFjYXMxEDAOBgNVBAoMB1RyYW5yZWQxFzAVBgNVBAsMDnRyYW5y\n" +
            "ZWQuY29tLnZlMRcwFQYDVQQDDA50cmFucmVkLmNvbS52ZTEuMCwGCSqGSIb3DQEJ\n" +
            "ARYfc2VndXJpZGFkYWNjZXNvc0B0cmFucmVkLmNvbS52ZTCCASIwDQYJKoZIhvcN\n" +
            "AQEBBQADggEPADCCAQoCggEBAK18iCLeLfgSD0KkCNO4gPnowK2JFals69NgqW5G\n" +
            "OsWHm9/Ul9MfqFj7AZy+HVrP4J/eLlzQ/Q2m+qlvbIsAfxzgIHi6vsWVn8xjZG3I\n" +
            "9QlQ6HesPi+EyAIi09anq6hWLn7X/HPbduIGYnAj6474AgeZelHD9AqMIosVzNgX\n" +
            "C4YgogVCJ7Zp0dErixoRQ22bmEavtMut+IEtqvnORwoQS18HiFwTCVwmKwTUtxCI\n" +
            "JSHmqVWocROXC9m0mLNsbMOfAbSsKzf8Z36IyztQb2bKfsk0g72nDESKFM/WoGr/\n" +
            "Jr9uJaG0trXLnAFWCgegRvEqhsaOkivfBb1dNuBUu1m/TA0CAwEAAaOBmzCBmDAd\n" +
            "BgNVHQ4EFgQUacWSeXA1dXsyQX8I8qeKvbE299EwDAYDVR0TAQH/BAIwADAjBgNV\n" +
            "HREEHDAagglnYnQubG9jYWyCDXZwbi5nYnQubG9jYWwwDgYDVR0PAQH/BAQDAgWg\n" +
            "MBMGA1UdJQQMMAoGCCsGAQUFBwMBMB8GA1UdIwQYMBaAFLes4+Orgy3VAyB5Mz0Y\n" +
            "bADNEGscMA0GCSqGSIb3DQEBCwUAA4ICAQBVk18/sn3Qmxk4yaGElECnHzI0FysC\n" +
            "Cp6u/JvdkcpIAUK8HxMrsEY9LstC8kvF2fevkzRkKi1UQSwEwdkZBqBCaqdQVrIK\n" +
            "FFKv+dtO0z03QJegbPAuVvy4sidC0J8LBddUkjzSWv+ds2J2/qBOEwqiJpjJBuXo\n" +
            "XMkorA5Odm4Qj3peB8hzb6P8RhsUruUIAOv7gR1jbTzy5wWeu/3mKjgrcJ8fIjdY\n" +
            "v4olQ6Y8E4YH7b/eGtgx+aoeLCcHqawkmdRQXqk44hJKA+q3xlW1HXSU5pa8QZvU\n" +
            "fR+SZQkkGtOhExvOCFOabbOyVkDThvaUQIV0yrXwVFJvp5lQC7gLAhL9uvph8ky1\n" +
            "FHo6yuciN/RCS0hSp7QgEuhtCm/rH5iOfafVf0tSEQPZadzrOHEzi/MgXJuc7Ozu\n" +
            "ktUy5HFyQW/NO5dH9KhkFq5eqRdN/wpodOoVTLu6pbrEhj2AO8at36hWhL0BW9/z\n" +
            "qSfb14qu+9yOJ8uWxel47R5uOccdCC3mRL34u7U2MANSytcg6Irkcx1UP2vQ0rTU\n" +
            "UDr88rYkLjDXM2JtMJNBPOj7DNH8eIHOG3wEW+8gV14xoesR/Nn0yuDr2gxVTZiJ\n" +
            "Z/2jW0yBNPnqRx5Z4dd3NqP0WIXtJwoxGoDXX2jS9Y+o5B15qrFE/zfY3V3YqiI3\n" +
            "rNSMH6ci0IfKKQ==\n" +
            "-----END CERTIFICATE-----";
    public static final String ca = "-----BEGIN CERTIFICATE-----\n" +
            "MIIGLTCCBBWgAwIBAgIUI3W5Mjpr6sEDj8oSe6maKei5DNAwDQYJKoZIhvcNAQEL\n" +
            "BQAwgaUxCzAJBgNVBAYTAlZFMRAwDgYDVQQIDAdDYXJhY2FzMRAwDgYDVQQHDAdD\n" +
            "YXJhY2FzMRAwDgYDVQQKDAdUcmFucmVkMRcwFQYDVQQLDA50cmFucmVkLmNvbS52\n" +
            "ZTEXMBUGA1UEAwwOdHJhbnJlZC5jb20udmUxLjAsBgkqhkiG9w0BCQEWH3NlZ3Vy\n" +
            "aWRhZGFjY2Vzb3NAdHJhbnJlZC5jb20udmUwHhcNMjUwOTA4MTQyNTI4WhcNMzUw\n" +
            "OTA2MTQyNTI4WjCBpTELMAkGA1UEBhMCVkUxEDAOBgNVBAgMB0NhcmFjYXMxEDAO\n" +
            "BgNVBAcMB0NhcmFjYXMxEDAOBgNVBAoMB1RyYW5yZWQxFzAVBgNVBAsMDnRyYW5y\n" +
            "ZWQuY29tLnZlMRcwFQYDVQQDDA50cmFucmVkLmNvbS52ZTEuMCwGCSqGSIb3DQEJ\n" +
            "ARYfc2VndXJpZGFkYWNjZXNvc0B0cmFucmVkLmNvbS52ZTCCAiIwDQYJKoZIhvcN\n" +
            "AQEBBQADggIPADCCAgoCggIBALPzKp0zkEI0Hx8wscEtYzvem72WxD4jmbiF5QjN\n" +
            "KelM8KhxgGG+wuZk+mk+67vk7D0n4wnNjsSqSszAelwkKCHXXFzIhWBT1bF30y8x\n" +
            "xjK/HQ34eFB0KmvHOEyM+AhhcaCPIJzh78jYEV2oTrwjFUjLAj88K0jYPA9g/zKD\n" +
            "COzgooi7uJEoPKQDph+Eg+hcPsajYLUmJmGh/5qEvBTmAjRidXBNV70Uwz+FHF1u\n" +
            "/KQiaMIWO02ptiTKT+4OAcBLjHF9SzHD95S5eaKr49i7EfuduNEp0MDwCQCY/rIa\n" +
            "YDyLIqzxzTIIcyKmLB8tLb9jcfJlOMLZtMcYZPGKOcNyOtm8QT1oEnKN0gGamgsX\n" +
            "PQ9ld64P9PgGG1O9rR1CXFTlKZ463y/wh4YVzH11hrs6pjfLnlSyM6tcBE9lcMeI\n" +
            "3B44AyJKlpmzTOneyHPHzaC7T76BErKEh/s1gTnwCLz5VXQVZxv1Da0VTceMUerI\n" +
            "qI7EpHY656K3rZBj2YnCD7B/CT0VflwPCOB1HQ6a2RhCG+jJ3vc3hUyHdvg+BQZF\n" +
            "brkm3MukzNn4p+ut607SpAaHiVR1HJ4PCUnrDYVMiZhUlv156Jg3oB73u9nXyfVy\n" +
            "+IZAOc4gmu6n0F2a74QExiUMw1d/J/ojoE3srCbIfrPLStEGQZwcsGfdjOshY9Or\n" +
            "HFdjAgMBAAGjUzBRMB0GA1UdDgQWBBS3rOPjq4Mt1QMgeTM9GGwAzRBrHDAfBgNV\n" +
            "HSMEGDAWgBS3rOPjq4Mt1QMgeTM9GGwAzRBrHDAPBgNVHRMBAf8EBTADAQH/MA0G\n" +
            "CSqGSIb3DQEBCwUAA4ICAQBjV+3qMdMfjS+H4yOA8OFpX9allB+LQvenB8omTjy3\n" +
            "kV6nN5O7CnmaG7MXeORZeWaqs2NwoBze0BRGrzvW72apsiBN5cAL5fgk6JoBMD4u\n" +
            "t3+L6gMhej9hdF1j8bIj38bJylYgUs1K9ZWR+vVQqxs7O3iWrmZA7pbjo1SnbBZO\n" +
            "o3GZdl08FxZb32EXM5y7uwy6djUfaTurEhJ72EemLEtL2LE73GxteWHhD6f3GCNP\n" +
            "9/W4U7kJuY6MoH1UTUGfyNyF5d9nwqjrrTfoE9THzW1UNYsmYveB19Ijixuhsd4k\n" +
            "T9Op/ccVh0mbvOWXuxdvVSDV+L8nxZgd4xtdwWbZ7LkMFqRXyOuA/ycmcSJBEQGH\n" +
            "YHfW4TFyTuC6JzyxQHtMB8wamIHr7F4b7tG40t/zum5STk2kUTKPJzgAYbkFBIAB\n" +
            "owmL5K2scvrMvPLXuv7/ErCBmLRQVfr91fewYtCoLSOZ9OolxDuCSFgtbm2vQFD9\n" +
            "ekYRCAgl8JehZrv0S0W2cgr2EubPI3Zjw7AY8uBwo2xycnqPrg6LpcWmhuKOSImn\n" +
            "8VpaLnSQ8LMcqoneasq/7RkY4E8pvLPl0pBANq6yNOjiCA8GDg+z0uMD/MVze1UL\n" +
            "o2k/Yun+bkC+HYQXxWFnDVubSCtAAKrH/OALd87PbHrWgUrp5ugmn/kSx7OCS36E\n" +
            "cw==\n" +
            "-----END CERTIFICATE-----\n";

}
