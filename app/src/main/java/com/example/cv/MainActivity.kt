package com.example.cv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.cv.databinding.ActivityMainBinding
import com.github.barteksc.pdfviewer.util.FitPolicy
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        previewCVPdf()
        onSendEmailClick()
    }

    private fun previewCVPdf() {
        binding.content.pdfView.fromAsset(Constants.NO_HEADER_PDF_FILE_NAME)
            .pageFitPolicy(FitPolicy.HEIGHT)
            .swipeHorizontal(true)
            .pageSnap(true)
            .autoSpacing(true)
            .pageFling(true)
            .enableSwipe(true)
            .load()
    }

    private fun onSendEmailClick() {
        binding.fabEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, Constants.SUBJECT)
            intent.putExtra(Intent.EXTRA_TEXT, Constants.BODY)

            val file = getFileFromAssets()
            // Get a content URI for the temporary file using FileProvider
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                file
            )
            intent.setDataAndType(uri, contentResolver.getType(uri))

            // Attach the PDF to the email intent
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val emailChooser = Intent.createChooser(intent, Constants.CHOSER_EMAIL_TITLE)

            if (emailChooser.resolveActivity(packageManager) != null) {
                startActivity(emailChooser)
            } else {
                Toast.makeText(
                    this@MainActivity,
                    Constants.NO_EMAIL_CLIENT,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun getFileFromAssets(): File {
        // Access the PDF file from assets
        val inputStream: InputStream = assets.open(Constants.HEADER_PDF_FILE_NAME)

        // Create a temporary file in the app's cache directory
        val tempFile = File.createTempFile(Constants.TEMP_CV_FILE_NAME, ".pdf", cacheDir)
        val outputStream = FileOutputStream(tempFile)
        val buffer = ByteArray(4 * 1024) // Adjust the buffer size as needed

        var read: Int
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()

        return tempFile
    }

}