package com.xpel.manager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import com.xpel.manager.account.R;

import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String appName = "";
        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo.activities.length > 0) {
                appName = packageInfo.activities[0].loadLabel(pm).toString();
            }

            // serviceName – the name of the Java cryptographic service (e.g., Signature, MessageDigest, Cipher, Mac, KeyStore). Note: this parameter is case-insensitive.
            Set<String> algorithms = Security.getAlgorithms("KeyStore");
            Set<String> al2 = Security.getAlgorithms("Signature");
            Set<String> al1 = Security.getAlgorithms("MessageDigest");
            Log.d("__TAG__", "MSG: ~~~~~");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 1. AccountManager
        String finalAppName = appName;
        findViewById(R.id.buttonAddAccount).setOnClickListener(v -> {
            String accountType = getString(R.string.account_type);
            Account account = new Account(finalAppName, accountType);
            AccountManager.get(this).addAccountExplicitly(account, "~~~~~", null);
        });

        findViewById(R.id.buttonCheckAccounts).setOnClickListener(v -> {
            String accountType = getString(R.string.account_type);
            Account[] accounts = AccountManager.get(this).getAccountsByType(accountType);
            for (Account account : accounts) {
                Log.d(getClass().getCanonicalName(), "Your account name is: " + account.name);
            }
        });

        // 2. KeyStore
        String ANDROID_KEY_STORE = "AndroidKeyStore";
        String KEY_ALIAS = "iSecurityAlias";
        String RSA = "RSA";
        findViewById(R.id.buttonAddKeyStore).setOnClickListener(v -> {
            try {
                KeyStore store = KeyStore.getInstance(ANDROID_KEY_STORE);
                store.load(null);

                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA, ANDROID_KEY_STORE);
                keyPairGenerator.initialize(
                        new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                                .build());
                keyPairGenerator.generateKeyPair();

                PublicKey publicKey = store.getCertificate(KEY_ALIAS).getPublicKey();
                Log.d("__TAG__", "publicKey finally is null or not? " + (publicKey == null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.buttonCheckKeyStore).setOnClickListener(v -> {
            try {
                KeyStore store = KeyStore.getInstance(ANDROID_KEY_STORE);
                store.load(null);

                Certificate certificate = store.getCertificate(KEY_ALIAS);
                PublicKey publicKey = null;
                if (certificate != null) {
                    publicKey = certificate.getPublicKey();
                }
                Log.d("__TAG__", "publicKey adding is null or not? " + (publicKey == null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 3. KeyChain
        findViewById(R.id.buttonAddKeyChain).setOnClickListener(v -> {
            try {
                byte[] keychainBytes = "KEY..........".getBytes(StandardCharsets.UTF_8);
                Intent intent = KeyChain.createInstallIntent();
                intent.putExtra(KeyChain.EXTRA_CERTIFICATE, keychainBytes);
                intent.putExtra(KeyChain.EXTRA_NAME, "NetworkDiagnosis CA Certificate");
                intent.putExtra(KeyChain.EXTRA_KEY_ALIAS, "__FORGET_IT__");
                startActivityForResult(intent, 3);
                Log.d("__TAG__", "KeyChain add done ~~~");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.buttonCheckKeyChain).setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    PrivateKey privateKey = KeyChain.getPrivateKey(MainActivity.this, "__FORGET_IT__");
                    Log.d("__TAG__", "KeyChain privateKey is null or not? " + (privateKey == null));
                    X509Certificate[] keyChainCert = KeyChain.getCertificateChain(MainActivity.this, "__FORGET_IT__");
                    Log.d("__TAG__", "KeyChain Certificate is null or not? " + (keyChainCert == null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

}