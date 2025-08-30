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
     * Encrypts and stores the given byte array under the provided key in private SharedPreferences.
     *
     * The data is encrypted using a per-item AES-GCM key and stored as a Base64-encoded string
     * containing the concatenation of the 12-byte IV and the ciphertext.
     *
     * @param key Identifier used as the SharedPreferences entry name and as the per-item key suffix.
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
     * Retrieves securely stored data.
     * @param key The key of the data to retrieve.
     * @return The decrypted data, or null if not found.
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
     * Remove the securely stored value for the given logical key from the app's secure_prefs.
     *
     * This deletes only the Base64-encoded encrypted value from SharedPreferences; it does not delete
     * any per-item secret keys stored in the Android KeyStore.
     *
     * @param key The logical storage key whose associated encrypted value should be removed.
     */
    fun removeData(key: String) {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit {remove(key)}
    }

    /**
     * Removes all entries from the "secure_prefs" SharedPreferences used by SecureKeyStore.
     *
     * This irreversibly deletes all stored Base64-encoded encrypted values. Note that this does
     * not delete any keys from the AndroidKeyStore itself.
     */
    fun clearAllData() {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit {clear()}
    }

    /**
     * Retrieves a SecretKey from the AndroidKeyStore for the given alias, creating and storing
     * a new AES-GCM 256-bit key if the alias does not already exist.
     *
     * The generated key is created with:
     * - Algorithm: AES
     * - Block mode: GCM
     * - Padding: No padding
     * - Key size: 256 bits
     * - Randomized encryption required: true
     * - User authentication: not required
     *
     * @param keyAlias The alias under which the secret key is stored or will be created.
     * @return The existing or newly generated SecretKey associated with the alias.
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
