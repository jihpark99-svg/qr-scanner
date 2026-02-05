package com.example.qrscanner

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) { // Force Dark Theme for "Modern" look
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppValidation()
                }
            }
        }
    }
}

@Composable
fun MainAppValidation() {
    // Permission Handling
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        QRAppScaffold()
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission needed for scanning.")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRAppScaffold() {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Scan, 1: Create

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.QrCodeScanner, "Scan") },
                    label = { Text("Scan") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.QrCode, "Create") },
                    label = { Text("Create") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (selectedTab == 0) {
                ScannerScreen(isActive = true) // Pass isActive to control camera
            } else {
                GeneratorScreen()
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 1: Scanner Logic
// -----------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(isActive: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var scannedCode by remember { mutableStateOf<String?>(null) }
    var autoOpen by remember { mutableStateOf(true) } // Auto Open Default ON
    
    // Auto Actions Logic
    LaunchedEffect(scannedCode) {
        scannedCode?.let { code ->
            // 1. Auto Copy
            clipboardManager.setPrimaryClip(ClipData.newPlainText("QR Code", code))
            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()

            // 2. Auto Open
            if (autoOpen && (code.startsWith("http://") || code.startsWith("https://"))) {
                 try {
                     val intent = Intent(Intent.ACTION_VIEW, Uri.parse(code))
                     context.startActivity(intent)
                 } catch (e: Exception) {
                     e.printStackTrace()
                 }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isActive) {
            val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
            
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }
                        
                        val analysisExecutor = Executors.newSingleThreadExecutor()
                        val analyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(analysisExecutor, QRCodeAnalyzer { code ->
                                    if (scannedCode != code) { 
                                        scannedCode = code
                                    }
                                })
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analyzer
                            )
                        } catch (e: Exception) { e.printStackTrace() }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            
            DisposableEffect(Unit) {
                onDispose {
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        cameraProvider.unbindAll()
                    } catch (e: Exception) { e.printStackTrace() }
                }
            }
        }

        // Overlay UI
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
             Row(
                 verticalAlignment = Alignment.CenterVertically,
                 modifier = Modifier
                     .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                     .padding(horizontal = 16.dp, vertical = 8.dp)
             ) {
                 Text("Auto Open", color = Color.White)
                 Spacer(modifier = Modifier.width(8.dp))
                 Switch(checked = autoOpen, onCheckedChange = { autoOpen = it })
             }
        }

        // Result Bottom Sheet (Simple version for display)
        if (scannedCode != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Scanned: $scannedCode", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { /* Already Copied */ }) {
                        Icon(Icons.Default.ContentCopy, "Copy")
                        Text("  Copied!")
                    }
                    Button(onClick = { scannedCode = null }) {
                        Text("Scan Again")
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// Tab 2: Generator Logic
// -----------------------------------------------------------------------------------------
@Composable
fun GeneratorScreen() {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var text by remember { mutableStateOf("") }
    var qrColor by remember { mutableStateOf(android.graphics.Color.BLACK) }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    val scope = rememberCoroutineScope()

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        logoUri = uri
    }

    // Color Palette
    val colors = listOf(
        android.graphics.Color.BLACK,
        android.graphics.Color.RED,
        android.graphics.Color.BLUE,
        android.graphics.Color.parseColor("#006400"), // Dark Green
        android.graphics.Color.parseColor("#800080"), // Purple
        android.graphics.Color.parseColor("#FF8C00")  // Dark Orange
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Entry Text or URL") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { resizeAndGenerate(text, qrColor, logoUri, context) { generatedBitmap = it }; keyboardController?.hide(); focusManager.clearFocus() })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color Picker
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(color))
                            .border(
                                width = if (qrColor == color) 2.dp else 0.dp,
                                color = if (qrColor == color) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { qrColor = color }
                    )
                }
            }
            // Logo Button
            IconButton(onClick = { logoPicker.launch("image/*") }) {
                Icon(Icons.Default.AddPhotoAlternate, "Add Logo", tint = if (logoUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                resizeAndGenerate(text, qrColor, logoUri, context) { generatedBitmap = it }
                keyboardController?.hide()
                focusManager.clearFocus() 
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate QR Code")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Preview
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.DarkGray, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (generatedBitmap != null) {
                Image(
                    bitmap = generatedBitmap!!.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            } else {
                Text("Enter text and click generate", color = Color.LightGray)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                 generatedBitmap?.let { saveImageToGallery(context, it) }
            },
            enabled = generatedBitmap != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)) // Green
        ) {
            Icon(Icons.Default.Save, "Save")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save to Gallery")
        }
    }
}

// -----------------------------------------------------------------------------------------
// Helper Functions
// -----------------------------------------------------------------------------------------
fun resizeAndGenerate(text: String, color: Int, logoUri: Uri?, context: Context, onResult: (Bitmap?) -> Unit) {
    if (text.isEmpty()) return
    
    // Run on background thread
    // Note: Compose 'LaunchedEffect' or ViewModel is better, but this is a simple port
    val executor = Executors.newSingleThreadExecutor()
    executor.execute {
        val qr = generateQRCode(text, color, logoUri, context)
        // Switch back to Main
        ContextCompat.getMainExecutor(context).execute {
            onResult(qr)
        }
    }
}

fun generateQRCode(text: String, color: Int, logoUri: Uri?, context: Context): Bitmap? {
    try {
        val size = 1024
        val hints = hashMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.ERROR_CORRECT] = ErrorCorrectionLevel.H // High error correction for LOGO
        hints[EncodeHintType.MARGIN] = 1

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) color else android.graphics.Color.WHITE)
            }
        }

        // Add Logo if exists
        logoUri?.let { uri ->
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val originalLogo = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalLogo != null) {
                    val logoSize = size / 5
                    val scaledLogo = Bitmap.createScaledBitmap(originalLogo, logoSize, logoSize, false)
                    
                    val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(combined)
                    canvas.drawBitmap(bmp, 0f, 0f, null)
                    
                    val left = (width - logoSize) / 2f
                    val top = (height - logoSize) / 2f
                    canvas.drawBitmap(scaledLogo, left, top, null)
                    return combined
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
        
        return bmp
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val filename = "QR_${System.currentTimeMillis()}.jpg"
    var fos: OutputStream? = null
    var imageUri: Uri? = null
    
    try {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }
        
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = imageUri?.let { contentResolver.openOutputStream(it) }
        
        if (fos != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        fos?.close()
    }
}

// Logic Re-use
class QRCodeAnalyzer(private val onQRCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) barcode.rawValue?.let { onQRCodeScanned(it) }
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}
