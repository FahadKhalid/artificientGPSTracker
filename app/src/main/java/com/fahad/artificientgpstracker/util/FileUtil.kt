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
        

        

    }
} 