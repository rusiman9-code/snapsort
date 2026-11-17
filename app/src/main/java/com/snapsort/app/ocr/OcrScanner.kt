package com.snapsort.app.ocr

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class TextBlockItem(
    val text: String,
    val boundingBox: Rect?
)

data class OcrScanResult(
    val fullText: String,
    val blocks: List<TextBlockItem>
)

class OcrScanner(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun processImage(imageUri: Uri): OcrScanResult = suspendCancellableCoroutine { continuation ->
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val blocks = mutableListOf<TextBlockItem>()
                    for (block in visionText.textBlocks) {
                        for (line in block.lines) {
                            blocks.add(
                                TextBlockItem(
                                    text = line.text,
                                    boundingBox = line.boundingBox
                                )
                            )
                        }
                    }
                    continuation.resume(
                        OcrScanResult(
                            fullText = visionText.text,
                            blocks = blocks
                        )
                    )
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            continuation.resumeWithException(e)
        }
    }
}
