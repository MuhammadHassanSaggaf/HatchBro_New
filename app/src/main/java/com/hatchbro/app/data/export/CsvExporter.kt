package com.hatchbro.app.data.export

import android.content.Context
import com.hatchbro.app.data.local.entity.BatchEntity
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class CsvExporter @Inject constructor() {
    
    fun exportBatches(context: Context, batches: List<BatchEntity>): File {
        val fileName = "hatchbro_export_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        FileWriter(file).use { writer ->
            writer.append("ID,TrayID,Born,EggsSet,HatchDate,Status\n")
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            
            batches.forEach { batch ->
                writer.append("${batch.id},")
                writer.append("${batch.trayId},")
                writer.append("${dateFormat.format(batch.startDate)},")
                writer.append("${batch.eggsSet},")
                writer.append("${dateFormat.format(batch.expectedHatchDate)},")
                writer.append("${batch.status}\n")
            }
        }
        return file
    }
}
