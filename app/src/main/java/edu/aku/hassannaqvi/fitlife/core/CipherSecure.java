package edu.aku.hassannaqvi.fitlife.core;


import static edu.aku.hassannaqvi.fitlife.core.MainApp.IBAHC;
import static edu.aku.hassannaqvi.fitlife.core.MainApp.TRATS;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class CipherSecure {
    private static final String TAG = "CipherSecure";

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 16;

    public static String encryptGCM(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        byte[] iv = new byte[IV_LENGTH];
        (new SecureRandom()).nextBytes(iv);
        //  Log.d(TAG, "encryptGCM (encrypted IV): "+ new String(Base64.encode(iv, Base64.NO_WRAP)));

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec ivSpec = new GCMParameterSpec(TAG_LENGTH * Byte.SIZE, iv);
        //cipher.init(Cipher.ENCRYPT_MODE, skey, ivSpec);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(hashSHA384().getBytes(StandardCharsets.UTF_8), "AES"), ivSpec);

        byte[] ciphertext = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        //Log.d(TAG, "encryptGCM (ciphertext): "+ new String(Base64.encode(ciphertext, Base64.NO_WRAP)));

        byte[] encrypted = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
        String encoded = Base64.encodeToString(encrypted, Base64.NO_WRAP);

        return encoded;
    }

    public static String decryptGCM(String encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        byte[] decoded = Base64.decode(encrypted, Base64.NO_WRAP);
        // Log.d(TAG, "decryptGCM: (to Decrypt):"+new String(decoded));
        //Log.d(TAG, "decryptGCM: (to Decrypt):"+new String(Base64.encode(decoded, Base64.NO_WRAP)));
        byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
        //Log.d(TAG, "decryptGCM(iv): "+new String(Base64.encode(iv, Base64.NO_WRAP)));
        //Log.d(TAG, "decryptGCM(iv): "+new String(iv));
        //Log.d(TAG, "decryptGCM(key): "+IBAHC);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec ivSpec = new GCMParameterSpec(TAG_LENGTH * Byte.SIZE, iv);
        //cipher.init(Cipher.DECRYPT_MODE, skey, ivSpec);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hashSHA384().getBytes(StandardCharsets.UTF_8), "AES"), ivSpec);

        byte[] ciphertext = cipher.doFinal(decoded, IV_LENGTH, decoded.length - IV_LENGTH);

        String newString = new String(ciphertext, StandardCharsets.UTF_8);

        return newString;
    }

    /*public static String encrypt(String plain) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Log.d(TAG, "encrypt: " + IBAHC);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(IBAHC.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(iv));
        byte[] cipherText = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        byte[] ivAndCipherText = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, ivAndCipherText, 0, iv.length);
        System.arraycopy(cipherText, 0, ivAndCipherText, iv.length, cipherText.length);
        return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP);

    }*/

    /*public static String decrypt(String encoded) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        Log.d(TAG, "decrypt: encoded " + encoded);
        byte[] ivAndCipherText = Base64.decode(encoded, Base64.NO_WRAP);
        byte[] iv = Arrays.copyOfRange(ivAndCipherText, 0, 16);
        byte[] cipherText = Arrays.copyOfRange(ivAndCipherText, 16, ivAndCipherText.length);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(IBAHC.getBytes(StandardCharsets.UTF_8), "AES"), new IvParameterSpec(iv));
        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);

    }*/

    public static SSLSocketFactory buildSslSocketFactory(Context context) {
        try {


            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            AssetManager assetManager = context.getAssets();
            InputStream caInput = assetManager.open("star_aku_edu_2025.crt");
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
//                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
/*

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
            */
            // Create an SSLContext that uses our TrustManager
            SSLContext context1 = SSLContext.getInstance("TLSv1.2");
            context1.init(null, tmf.getTrustManagers(), null);
            return context1.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | CertificateException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean certIsValid(Certificate[] certs, Certificate ca) {
        for (Certificate cert : certs) {
//            System.out.println("Certificate is: " + cert);
            if (cert instanceof X509Certificate) {

                try {
                    ((X509Certificate) cert).checkValidity();

//                    System.out.println("Certificate is active for current date");
                    if (cert.equals(ca)) {

                        return true;
                    }
                    //  Toast.makeText(mContext, "Certificate is active for current date", Toast.LENGTH_SHORT).show();
                } catch (CertificateExpiredException | CertificateNotYetValidException e) {
                    e.printStackTrace();
                    return false;
                }
            }

        }
        return false;
    }


        public static String hashSHA384()
                throws NoSuchAlgorithmException {
            String input = IBAHC;
            Log.d(TAG, "hashSHA384 (input): "+ input);
            //input = input.substring(12,12+32);
            MessageDigest mDigest = MessageDigest.getInstance("SHA-384");
            Log.d(TAG, "hashSHA384 (mDigest): "+ mDigest);

            byte[] shaByteArr = mDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            Log.d(TAG, "hashSHA384 (shaByteArr): "+ Arrays.toString(shaByteArr));

         /*   StringBuilder hexStrBuilder = new StringBuilder();
            for (int i = 0; i < shaByteArr.length; i++) {
                hexStrBuilder.append(String.format("%02x", shaByteArr[i]));
            }*/
            // Log.d(TAG, "hashSHA256: "+ new String(shaByteArr).substring(TRATS,TRATS+32));
            //  return Base64.encodeToString(hexStrBuilder.toString().substring(12,12+32).getBytes(StandardCharsets.UTF_8),  Base64.NO_WRAP);
            String hash_base64 = Base64.encodeToString(shaByteArr, Base64.NO_WRAP).substring(TRATS, TRATS + 32);
            Log.d(TAG, "hashSHA384 (hash_base64): "+ hash_base64);
             return hash_base64;
        }

}