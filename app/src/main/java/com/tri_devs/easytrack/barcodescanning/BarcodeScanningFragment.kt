package com.tri_devs.easytrack.barcodescanning

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tri_devs.easytrack.databinding.FragmentBarcodeScanningBinding
import com.tri_devs.easytrack.entities.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

typealias BarcodeListener = (barcode: String) -> Unit

class BarcodeScanningFragment : Fragment() {
    lateinit var binding: FragmentBarcodeScanningBinding
    val args: BarcodeScanningFragmentArgs by navArgs()
    private var processingBarcode = AtomicBoolean(false)
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    val db = Firebase.firestore
    val productsRef = db.collection("products")
    val TAG = "BARSCAN"
    private lateinit var scan: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBarcodeScanningBinding.inflate(this.layoutInflater, container, false)

        scan = args.scan

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        return binding.root
    }


    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        context?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1, it)
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        if (processingBarcode.compareAndSet(false, true)) {
                            searchBarcode(barcode)
                        }
                    })
                }


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis)

                // Getting the CameraControl instance from the camera
                val cameraControl = camera.cameraControl

                // Listen to tap events on the viewfinder and set them as focus regions
                val taptoFocus = binding.viewFinder.setOnTouchListener{ _: View, motionEvent: MotionEvent ->
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            binding.viewFinder.performClick()
                            return@setOnTouchListener true
                        }
                        MotionEvent.ACTION_UP -> {
                            // Get the MeteringPointFactory from PreviewView
                            val factory = binding.viewFinder.meteringPointFactory

                            // Create a MeteringPoint from the tap coordinates
                            val point = factory.createPoint(motionEvent.x, motionEvent.y)

                            // Create a MeteringAction from the MeteringPoint, you can configure it to specify the metering mode
                            val action = FocusMeteringAction.Builder(point).build()

                            // Trigger the focus and metering. The method returns a ListenableFuture since the operation
                            // is asynchronous. You can use it get notified when the focus is successful or if it fails.
                            cameraControl.startFocusAndMetering(action)
                            binding.viewFinder.performClick()
                            return@setOnTouchListener true
                        }
                        else -> {
                            binding.viewFinder.performClick()
                            return@setOnTouchListener false
                        }
                    }
                }

                val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector): Boolean {
                        // Get the camera's current zoom ratio
                        val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio ?: 0F

                        // Get the pinch gesture's scaling factor
                        val delta = detector.scaleFactor

                        // Update the camera's zoom ratio. This is an asynchronous operation that returns
                        // a ListenableFuture, allowing you to listen to when the operation completes.
                        cameraControl.setZoomRatio(currentZoomRatio * delta)

                        // Return true, as the event was handled
                        return true
                    }
                }
                val scaleGestureDetector = ScaleGestureDetector(requireContext(), listener)

                // Attach the pinch gesture listener to the viewfinder
                binding.viewFinder.setOnTouchListener { _, event ->
                    scaleGestureDetector.onTouchEvent(event)
                    binding.viewFinder.performClick()
                    return@setOnTouchListener true
                }
            }
            catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }



        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun searchBarcode(barcode: String) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            Log.d(TAG, "upcNumber: $barcode")
            val docRef = productsRef.document(barcode)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        if(scan == "salesScan"){
                            getProductInfo(barcode, document)
                        }
                        if (scan == "deptScan"){
                            val input = "scan"
                            val action = BarcodeScanningFragmentDirections.
                            actionBarcodeScanningFragmentToProductInformationDetailsFragment(input,
                                barcode)
                            findNavController().navigate(action)
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }

    fun getProductInfo(barcode: String, document : DocumentSnapshot){
        val productInfo = Product(
            document.get("name").toString(),
            document.get("description").toString(),
            document.get("quantity").toString().toInt(),
            document.get("upcNumber").toString().toLong(),
            document.get("price").toString(),
            document.get("salesPrice").toString(),
            document.get("startSalesDate").toString(),
            document.get("endSalesDate").toString()
        )

        if(productInfo != null){
            val action = BarcodeScanningFragmentDirections.goToProductInfoSearch(productInfo)
            findNavController().navigate(action)
        }
        else{
            Toast.makeText(activity, "UPC number not saved in the db", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(activity,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


}