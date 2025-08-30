package dev.aurakai.auraframefx.securecomm.keystore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A secure key store implementation using Android's KeyStore system.
 * Provides secure storage and retrieval of cryptographic keys.
 */
@Singleton
class SecureKeyStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    companion object {
        const val KEY_ALIAS = "aura_secure_key"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_SIZE = 256
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 128
    }

    /**
     * Encrypts plaintext with a per-entry AES-GCM key and persists the result in app-private SharedPreferences.
     *
     * The per-entry key alias is derived from the class-level KEY_ALIAS and the provided `key`. The stored value is
     * the concatenation of the GCM IV (12 bytes) followed by the ciphertext, Base64-encoded with `Base64.NO_WRAP`,
     * saved under `key` in the "secure_prefs" preferences. Calling this with an existing `key` overwrites the previous entry.
     *
     * @param key Identifier used both to derive the per-entry encryption key and as the SharedPreferences entry key.
     * @param data Plaintext bytes to encrypt and persist.
     */
    fun storeData(key: String, data: ByteArray) {
        val encryptedData = encryptData(key, data)
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit {
                putString(key, Base64.encodeToString(encryptedData, Base64.NO_WRAP))
            }
    }

    /**
     * Retrieves and decrypts data previously stored under the given key.
     *
     * Looks up a Base64-encoded IV+ciphertext value in the app-private "secure_prefs",
     * decodes it, and decrypts it using the per-entry AndroidKeyStore key derived from
     * the provided key identifier. Returns null if no entry exists or if decoding/decryption fails.
     *
     * @param key Identifier for the stored entry; also used to derive the per-entry keystore alias.
     * @return Decrypted plaintext bytes, or null if the entry is missing or cannot be decrypted.
     */
    fun retrieveData(key: String): ByteArray? {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val encryptedData = prefs.getString(key, null) ?: return null
        return try {
            decryptData(key, Base64.decode(encryptedData, Base64.NO_WRAP))
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Removes the encrypted entry stored under the given key from the app's secure preferences.
     *
     * This deletes only the SharedPreferences entry in "secure_prefs"; it does not delete
     * the underlying SecretKey stored in the AndroidKeyStore for that key alias.
     *
     * @param key The preferences key identifying the stored encrypted value.
    fun removeData(key: String) {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit {remove(key)}
    }

    /**
     * Removes all entries from the secure storage backing (the "secure_prefs" SharedPreferences).
     *
     * This clears only the stored encrypted blobs in preferences; it does not delete per-key
     * cryptographic keys from the AndroidKeyStore. Use key management helpers separately if
     * you also need to remove keystore entries.
     */
    fun clearAllData() {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit {clear()}
    }

    /**
     * Retrieve or create a SecretKey stored in AndroidKeyStore for the given alias.
     *
     * If a key for the alias exists, it is returned. Otherwise a new AES-256 key configured for
     * AES/GCM/NoPadding and both encryption/decryption is generated and persisted in the AndroidKeyStore.
     *
     * @param keyAlias Alias used to look up or create the key in AndroidKeyStore.
     * @return The SecretKey associated with the provided alias.
     */
    private fun getOrCreateSecretKey(keyAlias: String): SecretKey {
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )

            val builder = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setKeySize(KEY_SIZE)
                setRandomizedEncryptionRequired(true)
                setUserAuthenticationRequired(false)
            }

            keyGenerator.init(builder.build())
            return keyGenerator.generateKey()
        }

        val entry = keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    private fun encryptData(key: String, data: ByteArray): ByteArray {
        val secretKey = getOrCreateSecretKey("${KEY_ALIAS}_$key")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encrypted = cipher.doFinal(data)

        // Combine IV and encrypted data
        val combined = ByteArray(iv.size + encrypted.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encrypted, 0, combined, iv.size, encrypted.size)

        return combined
    }

    private fun decryptData(key: String, encryptedData: ByteArray): ByteArray {
        val secretKey = getOrCreateSecretKey("${KEY_ALIAS}_$key")

        // Extract IV and encrypted data
        if (encryptedData.size <= GCM_IV_LENGTH) {
            throw IllegalArgumentException("Invalid encrypted data format")
        }

        val iv = encryptedData.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = encryptedData.copyOfRange(GCM_IV_LENGTH, encryptedData.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return cipher.doFinal(encrypted)
    }
}
