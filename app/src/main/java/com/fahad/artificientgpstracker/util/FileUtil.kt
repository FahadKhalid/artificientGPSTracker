package com.fahad.artificientgpstracker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.fahad.artificientgpstracker.R
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileUtil {
    companion object {
        fun saveToFile(context: Context, content: String, fileName: String): Uri? {
            return try {
                // Validate input parameters
                if (content.isBlank()) {
                    return null
                }
                
                val sanitizedFileName = SecurityUtil(context).sanitizeFileName(fileName)
                val file = File(context.cacheDir, sanitizedFileName)
                FileWriter(file).use { writer ->
                    writer.write(content)
                }
                
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } catch (e: Exception) {
                null
            }
        }
        

        
        fun validateFileFormat(format: String): Boolean {
            return format.lowercase() in listOf("csv", "json")
        }
        
        fun shareFile(context: Context, uri: Uri, mimeType: String, title: String) {
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                val chooser = Intent.createChooser(intent, context.getString(R.string.export_share_trip_data))
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooser)
            } catch (e: Exception) {
                // Handle case where no app can handle the share intent
            }
        }
    }
} 