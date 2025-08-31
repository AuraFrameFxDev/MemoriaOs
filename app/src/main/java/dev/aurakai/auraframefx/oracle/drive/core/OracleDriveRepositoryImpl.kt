package dev.aurakai.auraframefx.oracle.drive.core

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext // Added import
import dev.aurakai.auraframefx.oracle.drive.api.OracleCloudApi
import dev.aurakai.auraframefx.oracle.drive.model.OracleDriveFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

// Assuming OracleDriveRepository is an interface defined elsewhere
// interface OracleDriveRepository {
//     suspend fun listFiles(bucketName: String, prefix: String?): List<OracleDriveFile>
//     suspend fun uploadFile(bucketName: String, objectName: String, filePath: String): Boolean
//     suspend fun downloadFile(bucketName: String, objectName: String, destinationPath: String): File?
//     suspend fun deleteFile(bucketName: String, objectName: String): Boolean
// }

class OracleDriveRepositoryImpl @Inject constructor(
    private val oracleCloudApi: OracleCloudApi,
    @ApplicationContext private val context: Context // Added @ApplicationContext
): OracleDriveRepository {

    override suspend fun listFiles(bucketName: String, prefix: String?): List<OracleDriveFile> = withContext(Dispatchers.IO) {
        try {
            val response = oracleCloudApi.listFiles(bucketName = bucketName, prefix = prefix)
            if (response.isSuccessful) {
                response.body()?.objects?.map { OracleDriveFile(it.name, it.size, it.timeCreated) } ?: emptyList()
            } else {
                // Handle error, log, throw custom exception etc.
                emptyList()
            }
        } catch (e: Exception) {
            // Handle error
            emptyList()
        }
    }

    override suspend fun uploadFile(bucketName: String, objectName: String, filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) return@withContext false

            val requestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val response = oracleCloudApi.uploadFile(
                bucketName = bucketName,
                objectName = objectName,
                body = requestBody
            )
            response.isSuccessful
        } catch (e: Exception) {
            // Handle error
            false
        }
    }

    override suspend fun downloadFile(bucketName: String, objectName: String, destinationPath: String): File? = withContext(Dispatchers.IO) {
        try {
            val response = oracleCloudApi.downloadFile(bucketName = bucketName, objectName = objectName)
            if (response.isSuccessful && response.body() != null) {
                val file = File(destinationPath, objectName) // Ensure destinationPath is a directory
                file.parentFile?.mkdirs() // Create parent directories if they don't exist
                response.body()!!.byteStream().use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                file
            } else {
                null
            }
        } catch (e: Exception) {
            // Handle error
            null
        }
    }

    override suspend fun deleteFile(bucketName: String, objectName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = oracleCloudApi.deleteFile(bucketName = bucketName, objectName = objectName)
            response.isSuccessful
        } catch (e: Exception) {
            // Handle error
            false
        }
    }
}
